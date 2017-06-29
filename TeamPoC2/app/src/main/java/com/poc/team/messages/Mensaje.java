package com.poc.team.messages;

import android.util.Base64;

import com.poc.team.cipher.CipherAES;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;


public abstract class Mensaje {
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
    protected static final int INTSIZE = 4;
    protected static final int KEYSIZE = 256 / 8;
    protected static final int ONEMINUTE = 3600;
    private final int type;
    private final int tag;
    private static int taggen;
    DataOutputStream outstream = null;
    Socket socket = null;
    Boolean debug = true;
    int[] lastNonces = new int[5];

    public Mensaje(int t, int msg) {
        type = msg;
        tag = t;
    }

    protected static int newTag() {
        taggen++;
        return taggen;
    }

    protected int getTag() {
        return tag;
    }

    public int getType() {
        int Type;
        Type = type;
        return Type;
    }

    public void writeTo(OutputStream o) throws Exception {
        DataOutputStream output = new DataOutputStream(o);
        output.write(marshallInt(type));
        output.write(marshallInt(tag));
    }

    protected abstract void readFrom(InputStream i) throws Exception;

    public static Mensaje readMsg(InputStream i) throws Exception {
        Mensaje msg = null;
        int type = 7;
        int tag = 0;
        DataInputStream incon = new DataInputStream(i);
        byte[] buffer = new byte[INTSIZE];
        incon.read(buffer);
        type = unmarshallInt(buffer);
        buffer = new byte[INTSIZE];
        incon.read(buffer);
        tag = unmarshallInt(buffer);
        switch (type) {
            case TGETCONTACT:
                msg = new Mensaje.TGetContact(tag);
                break;
            case RGETCONTACT:
                msg = new Mensaje.RGetContact(tag);
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
            case TSENDKEY:
                msg = new Mensaje.TSendKey(tag);
                break;
            case TSCREENSHOT:
                msg = new Mensaje.TScreenshot(tag);
                break;
            case RSCREENSHOT:
                msg = new Mensaje.RScreenshot(tag);
                break;
            case ROK:
                msg = new Mensaje.ROk(tag);
                break;
            case RERR:
                msg = new Mensaje.RErr(tag);
                break;
            case TLISTIMAGES:
                msg = new Mensaje.TListImages(tag);
                break;
            case RLISTIMAGES:
                msg = new Mensaje.RListImages(tag);
                break;
            case TIMAGE:
                msg = new Mensaje.TImage(tag);
                break;
            case RIMAGE:
                msg = new Mensaje.RImage(tag);
                break;
            default:
                System.out.println("Mensaje desconocido");
        }
        msg.readFrom(incon);
        return msg;
    }

    public String toString() {
        String tp;
        switch (type) {
            case TGETCONTACT:
                tp = "TGET";
                break;
            case RGETCONTACT:
                tp = "RGET";
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
            case TSCREENSHOT:
                tp = "TSCREENSHOT";
                break;
            case RSCREENSHOT:
                tp = "RSCREENSHOT";
                break;
            case ROK:
                tp = "ROK";
                break;
            case RERR:
                tp = "RERR";
                break;
            case TLISTIMAGES:
                tp = "TLISTIMAGES";
                break;
            case RLISTIMAGES:
                tp = "RLISTIMAGES";
                break;
            case TIMAGE:
                tp = "TIMAGE";
                break;
            case RIMAGE:
                tp = "RIMAGE";
                break;
            default:
                tp = "Mensaje Desconocido";
        }
        return tp;
    }

    public static byte[] marshallString(String s) throws UnsupportedEncodingException {
        byte[] b;
        b = s.getBytes("UTF-8");
        int size = b.length;
        byte[] marshall = new byte[size + INTSIZE];
        System.arraycopy(marshallInt(size), 0, marshall, 0, INTSIZE);
        System.arraycopy(b, 0, marshall, INTSIZE, size);
        return marshall;
    }

    public static String unmarshallString(byte[] b) throws UnsupportedEncodingException {
        String unmarshall = new String(b, "UTF-8");
        return unmarshall;
    }

    public static byte[] marshallInt(int value) {
        byte[] marshall;
        byte b3 = (byte) ((value >> 24) & 0xFF);
        byte b2 = (byte) ((value >> 16) & 0xFF);
        byte b1 = (byte) ((value >> 8) & 0xFF);
        byte b0 = (byte) (value & 0xFF);
        marshall = new byte[]{b0, b1, b2, b3};
        return marshall;
    }

    public static int unmarshallInt(byte[] value) {
        int a, b, c, d;
        a = (value[3] & 0xFF) << 24;
        b = (value[2] & 0xFF) << 16;
        c = (value[1] & 0xFF) << 8;
        d = value[0] & 0xFF;
        return a | b | c | d;
    }

    public boolean isValid(Timestamp t) {
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        System.out.println("TimeNow: " + timeNow.getTime());
        System.out.println("TimeMsg: " + t.getTime());
        if (t.getTime() - timeNow.getTime() > ONEMINUTE) {
            return false;
        }
        return true;
    }

    public boolean isValid(Timestamp t, int nonce, int lastNonce) {
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        System.out.println("TimeNow: " + timeNow.getTime());
        System.out.println("TimeMsg: " + t.getTime());
        if ((t.getTime() - timeNow.getTime() > ONEMINUTE) || (nonce != lastNonce + 1)) {
            return false;
        }
        return true;
    }

    public static class TGetContact extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public TGetContact(int tag) {
            super(tag, TGETCONTACT);
        }

        public TGetContact(int n, Timestamp ts, CipherAES c) {
            super(newTag(), TGETCONTACT);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }


    public static class RGetContact extends Mensaje {
        String[] names;
        String[] numbers;
        int numContacts;
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public RGetContact(int tag) {
            super(tag, RGETCONTACT);
        }

        public RGetContact(TGetContact t, String[] nam, String[] num, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RGETCONTACT);
            names = nam;
            numbers = num;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public String[][] getValor() {
            String[][] contacts = new String[2][numContacts];
            contacts[0] = names;
            contacts[1] = numbers;
            return contacts;
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            int sizes = 0;
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int numApps = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            names = new String[numApps];
            numbers = new String[numApps];
            sizes = INTSIZE;
            for (int i = 0; i < numApps; i++) {
                int nameSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizes, sizes + INTSIZE));
                sizes = sizes + INTSIZE;
                names[i] = unmarshallString(Arrays.copyOfRange(decipherMsg, sizes, sizes + nameSize));
                sizes = sizes + nameSize;
                int numberSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizes, sizes + INTSIZE));
                sizes = sizes + INTSIZE;
                numbers[i] = unmarshallString(Arrays.copyOfRange(decipherMsg, sizes, sizes + numberSize));
                sizes = sizes + numberSize;
            }
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizes, sizes + INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE + sizes];
            tsBytes = Arrays.copyOfRange(decipherMsg, sizes + INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public byte[] marshallMsg(String[] names, String[] numbers, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(names.length));
            for (int i = 0; i < names.length; i++) {
                outputStream.write(marshallString(names[i]));
                outputStream.write(marshallString(numbers[i]));
            }
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(names, numbers, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString() + names + numbers;
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class TGetInstalledApps extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public TGetInstalledApps(int tag) {
            super(tag, TGETINSTALLEDAPPS);
        }

        public TGetInstalledApps(int n, Timestamp ts, CipherAES c) {
            super(newTag(), TGETINSTALLEDAPPS);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }

    public static class RGetInstalledApps extends Mensaje {
        String[] appsNames;
        int numApps;
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public RGetInstalledApps(int tag) {
            super(tag, RGETINSTALLEDAPPS);
        }

        public RGetInstalledApps(TGetInstalledApps t, String[] apps, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RGETINSTALLEDAPPS);
            appsNames = apps;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public String[] getValor() {
            return appsNames;
        }

        public byte[] marshallMsg(String[] appsNames, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(appsNames.length));
            for (int i = 0; i < appsNames.length; i++) {
                outputStream.write(marshallString(appsNames[i]));
            }
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            int sizes = 0;
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int numApps = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            appsNames = new String[numApps];
            sizes = INTSIZE;
            for (int i = 0; i < numApps; i++) {
                int nameSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizes, sizes + INTSIZE));
                sizes = sizes + INTSIZE;
                appsNames[i] = unmarshallString(Arrays.copyOfRange(decipherMsg, sizes, sizes + nameSize));
                sizes = sizes + nameSize;
            }
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizes, sizes + INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE + sizes];
            tsBytes = Arrays.copyOfRange(decipherMsg, sizes + INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(appsNames, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString() + appsNames;
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class TGetInfo extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public TGetInfo(int tag) {
            super(tag, TGETINFO);
        }

        public TGetInfo(int n, Timestamp ts, CipherAES c) {
            super(newTag(), TGETINFO);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }

    public static class RGetInfo extends Mensaje {
        String info;
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public RGetInfo(int tag) {
            super(tag, RGETINFO);
        }

        public RGetInfo(TGetInfo t, String inf, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RGETINFO);
            info = inf;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public String getValor() {
            return info;
        }


        public byte[] marshallMsg(String info, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallString(info));
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int sizeInfo = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            info = unmarshallString(Arrays.copyOfRange(decipherMsg, INTSIZE, sizeInfo));
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, INTSIZE + sizeInfo, INTSIZE));
            byte[] tsBytes = new byte[size - 2 * INTSIZE + sizeInfo];
            tsBytes = Arrays.copyOfRange(decipherMsg, 2 * INTSIZE + sizeInfo, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("Info: " + info);
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(info, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println("info length " + info.length());
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString() + info;
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class TSendKey extends Mensaje {
        byte[] keyMaster, msgBytes;
        Timestamp timestamp;
        int nonce;
        CipherAES cipher;

        public TSendKey(int tag) {
            super(tag, TSENDKEY);
        }

        public TSendKey(CipherAES c, byte[] km, Timestamp t, int n) {
            super(newTag(), TSENDKEY);
            cipher = c;
            keyMaster = km;
            timestamp = t;
            nonce = n;
        }

        public byte[] getKeyMaster() {
            return keyMaster;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            keyMaster = new byte[KEYSIZE];
            keyMaster = Arrays.copyOfRange(decipherMsg, 0, KEYSIZE);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, KEYSIZE, KEYSIZE + INTSIZE));
            byte[] tsBytes = new byte[size - (KEYSIZE + INTSIZE)];
            tsBytes = Arrays.copyOfRange(decipherMsg, KEYSIZE + INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("keyMaster: " + new String(Base64.encodeToString(keyMaster, Base64.DEFAULT)));
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public boolean isValid() {
            return isValid(timestamp);
        }

        public byte[] marshallMsg(byte[] keyMaster, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(keyMaster);
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(keyMaster, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            output.write(msgCipher);
        }

    }

    public static class TSendId extends Mensaje {
        int id;

        public TSendId(int i) {
            super(newTag(), TSENDID);
            id = i;
        }

        public TSendId(Mensaje t, int i) {
            super(t.getTag(), TSENDID);
            id = i;
        }

        public int getId() {
            return id;
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            id = incon.readInt();
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            output.writeInt(id);
        }

        public String toString() {
            return super.toString() + " ID: " + id;
        }
    }

    public static class TScreenshot extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public TScreenshot(int tag) {
            super(tag, TSCREENSHOT);
        }

        public TScreenshot(int n, Timestamp ts, CipherAES c) {
            super(newTag(), TSCREENSHOT);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }

    public static class RScreenshot extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        int size;
        byte[] screenshot;
        byte[] msgBytes;

        public RScreenshot(int tag) {
            super(tag, RSCREENSHOT);
        }

        public RScreenshot(TScreenshot t, byte[] screen, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RSCREENSHOT);
            screenshot = screen;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public byte[] getScreenshot() {
            return screenshot;
        }

        public byte[] marshallMsg(byte[] screenshot, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(screenshot.length));
            outputStream.write(screenshot);
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int sizeScreenshot = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            screenshot = new byte[sizeScreenshot];
            screenshot = Arrays.copyOfRange(decipherMsg, INTSIZE, sizeScreenshot);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, INTSIZE + sizeScreenshot, INTSIZE));
            byte[] tsBytes = new byte[size - 2 * INTSIZE + sizeScreenshot];
            tsBytes = Arrays.copyOfRange(decipherMsg, 2 * INTSIZE + sizeScreenshot, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(screenshot, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println("Screenshot length " + screenshot.length);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString() + screenshot;
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class ROk extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public ROk(int tag) {
            super(tag, ROK);
        }

        public ROk(Mensaje t, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), ROK);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class RErr extends Mensaje {
        String error;
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public RErr(int tag) {
            super(tag, RERR);
        }

        public RErr(Mensaje t, String e, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RERR);
            error = e;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public String getError() {
            return error;
        }

        public byte[] marshallMsg(String error, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallString(error));
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int errorSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            error = unmarshallString(Arrays.copyOfRange(decipherMsg, INTSIZE, INTSIZE + errorSize));
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, INTSIZE + errorSize, 2 * INTSIZE + errorSize));
            byte[] tsBytes = new byte[size - (2 * INTSIZE + errorSize)];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(error, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString() + " Error: " + error;
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class TListImages extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;

        public TListImages(int tag) {
            super(tag, TLISTIMAGES);
        }

        public TListImages(int n, Timestamp ts, CipherAES c) {
            super(newTag(), TLISTIMAGES);
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public byte[] marshallMsg(int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }

    public static class RListImages extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        int size;
        String[] imagesPath;
        byte[] msgBytes;

        public RListImages(int tag) {
            super(tag, RLISTIMAGES);
        }

        public RListImages(TListImages t, String[] imgPath, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RLISTIMAGES);
            imagesPath = imgPath;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public String[] getImagesPath() {
            return imagesPath;
        }

        public byte[] marshallMsg(String[] imagesPath, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(imagesPath.length));
            for(int i = 0; i < imagesPath.length; i++){
                outputStream.write(marshallString(imagesPath[i]));
            }
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int numImages = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            imagesPath = new String[numImages];
            int sizeAdded = INTSIZE;
            for(int i = 0; i < numImages; i++){
                int sizePathImage = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizeAdded, sizeAdded + INTSIZE));
                sizeAdded = sizeAdded + INTSIZE;
                imagesPath[i] = Mensaje.unmarshallString(Arrays.copyOfRange(decipherMsg, sizeAdded, sizeAdded + sizePathImage));
                sizeAdded = sizeAdded + sizePathImage;
            }
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, sizeAdded, sizeAdded + INTSIZE));
            byte[] tsBytes = new byte[size - INTSIZE + sizeAdded];
            tsBytes = Arrays.copyOfRange(decipherMsg, INTSIZE + sizeAdded, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.readFully(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(imagesPath, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString();
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

    public static class TImage extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        byte[] msgBytes;
        String imagePath;

        public TImage(int tag) {
            super(tag, TIMAGE);
        }

        public TImage(int n, Timestamp ts, CipherAES c, String imgPath) {
            super(newTag(), TIMAGE);
            timestamp = ts;
            nonce = n;
            cipher = c;
            imagePath = imgPath;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public int getNonce() {
            return nonce;
        }

        public String getImagePath() { return imagePath; }

        public byte[] marshallMsg(int nonce, Timestamp ts, String imagePath) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallString(imagePath));
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.read(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int imagePathSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            imagePath = Mensaje.unmarshallString(Arrays.copyOfRange(decipherMsg, INTSIZE, INTSIZE + imagePathSize));
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, INTSIZE + imagePathSize, 2*INTSIZE + imagePathSize));
            byte[] tsBytes = new byte[size - 2*INTSIZE + imagePathSize];
            tsBytes = Arrays.copyOfRange(decipherMsg, 2*INTSIZE + imagePathSize, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(nonce, timestamp, imagePath);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public boolean isValid() {
            return isValid(timestamp);
        }
    }

    public static class RImage extends Mensaje {
        CipherAES cipher;
        Timestamp timestamp;
        int nonce;
        int size;
        byte[] image;
        byte[] msgBytes;

        public RImage(int tag) {
            super(tag, RIMAGE);
        }

        public RImage(TImage t, byte[] img, int n, Timestamp ts, CipherAES c) {
            super(t.getTag(), RIMAGE);
            image = img;
            timestamp = ts;
            nonce = n;
            cipher = c;
        }

        public byte[] getImage() {
            return image;
        }

        public byte[] marshallMsg(byte[] image, int nonce, Timestamp ts) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(marshallInt(image.length));
            outputStream.write(image);
            outputStream.write(marshallInt(nonce));
            outputStream.write(ts.toString().getBytes("UTF-8"));
            return outputStream.toByteArray();
        }

        public void decipherMsg(byte[] buffer, int size) throws Exception {
            byte[] decipherMsg = new byte[size];
            decipherMsg = CipherAES.decipherInGCMMode(buffer);
            int imageSize = unmarshallInt(Arrays.copyOfRange(decipherMsg, 0, INTSIZE));
            image = new byte[imageSize];
            image = Arrays.copyOfRange(decipherMsg, INTSIZE, INTSIZE + imageSize);
            nonce = unmarshallInt(Arrays.copyOfRange(decipherMsg, INTSIZE + imageSize, 2*INTSIZE + imageSize));
            byte[] tsBytes = new byte[size - 2 * INTSIZE + imageSize];
            tsBytes = Arrays.copyOfRange(decipherMsg, 2 * INTSIZE + imageSize, size);
            timestamp = Timestamp.valueOf(new String(tsBytes, "UTF-8"));
            if (debug) {
                System.out.println("nonce: " + nonce);
                System.out.println("ts: " + timestamp);
            }
        }

        protected void readFrom(InputStream i) throws Exception {
            DataInputStream incon = new DataInputStream(i);
            byte[] buffer = new byte[INTSIZE];
            incon.read(buffer);
            int size = unmarshallInt(buffer);
            System.out.println("Size " + size);
            buffer = new byte[size];
            incon.readFully(buffer);
            System.out.println("Msg: " + new String(Base64.encodeToString(buffer, Base64.DEFAULT)));
            decipherMsg(buffer, size);
        }

        public void writeTo(OutputStream o) throws Exception {
            super.writeTo(o);
            DataOutputStream output = new DataOutputStream(o);
            msgBytes = marshallMsg(image, nonce, timestamp);
            byte[] msgCipher = cipher.cipherInGCMMode(msgBytes);
            System.out.println("Nonce " + nonce);
            System.out.println("Ts " + timestamp);
            System.out.println(msgCipher.length);
            output.write(marshallInt(msgCipher.length));
            System.out.println(Base64.encodeToString(msgCipher, Base64.DEFAULT));
            output.write(msgCipher);
        }

        public String toString() {
            return super.toString();
        }

        public boolean isValid(int lastNonce) {
            return isValid(timestamp, nonce, lastNonce);
        }
    }

}