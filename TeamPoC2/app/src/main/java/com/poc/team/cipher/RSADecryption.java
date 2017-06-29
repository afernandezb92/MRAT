package com.poc.team.cipher;

import android.util.Base64;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.encodings.PKCS1Encoding;
import org.spongycastle.crypto.engines.RSAEngine;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PrivateKeyFactory;

import java.security.Security;


public class RSADecryption {

    private static String privateKey = "MIICdAIBADANBgkqhkiG9w0BAQEFAASCAl4wggJaAgEAAoGBAJgGNXoft8h+Y4JAoOd3XgHSUQfyPVB7TETmwNbOEBT9kYkhPhzQzu0HlvCTGMgwUqlvl8ZRiE5YCANP46qDgx2aDNt5XHIrWFFeuEE7CNRA82U3zPxevqq+Lc+SHG+Z/CUtRd1iugZpW4G8XthiiFgumynA0/ANpnFQeSJ7yBorAgMBAAECfwkNng5VpOl3JJyJzaXK0cSvb4lW46ujrBKLVGlPtYh7+4Dc94PX8FQQRkieP/O1pIishtrn9msximpuHYr7fxbt5IQMtmxLKUY0NoxN5dYDGPBVQPm8nWDupADy11pKA8FsUb2psrcSQ9yoGvQmW7plncYhuXaAlPhsX4PySwECQQDMtyMTl60zU1xY1r/OoZhYk5jm/eDEbPT9Xk4PKf0W7/F+LSpPf/ZZtBBkMkSkg7NxSguKzvxMSXTdNMXo1zZbAkEAvhvewKp0tNyweZQ+cFKAlsyphfiAMk7SRY5dUehRly0JKgmW3AOOi94bh52TQpmz8D8KXNuZxQvcXLTqN74UcQJABfjx1Qh/zReJgi4Buo2MXEkyFMsjW5eyLhIqRNb8w0aMzRmUOm2JSmSudb3hsssE2TFH1Ozk/3TFLA72FyzwMQJBAIAI8Sq9IkC06T3Ys3yec/AcAogx5tT69O7XhM4nMtwn/qYLM0kWNCjK+6uIWqdeMSu6qVYEqDlnVZAyYBQOtmECQFBseqlqIXBxSDma0BDKcet2zc/IbC32Kz/g4aboV/KWX33jlDi/JUpD9IKVXyVagzBVj4iyCdTyI6RkAmiuVvw=";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static String decrypt(String encryptedData) {
        String outputData = null;
        try {
            Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
            AsymmetricKeyParameter privKey =
                    (AsymmetricKeyParameter) PrivateKeyFactory.createKey(Base64.decode(privateKey, Base64.DEFAULT));
            AsymmetricBlockCipher e = new RSAEngine();
            e = new PKCS1Encoding(e);
            e.init(false, privKey);
            byte[] messageBytes = hexStringToByteArray(encryptedData);
            byte[] hexEncodedCipher = e.processBlock(messageBytes, 0, messageBytes.length);
            outputData = new String(hexEncodedCipher);
        } catch (Exception e) {
            System.out.println(e);
        }
        return outputData;
    }

    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}