package com.poc.team.cipher;

import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class CipherAES {

    public final static int IV_SIZE_DES = 64 / 8;
    public final static int IV_SIZE_TRIPLEDES = 64 / 8;
    public final static int IV_SIZE_AES = 128 / 8;
    public final static int IV_SIZE_TWOFISH = 128 / 8;
    public final static int NONCE_SIZE_GCM = 96 / 8;
    private final static int GCM_MODE_MAC_SIZE_BITS = 128;
    private final static String key = "LXx8pMOhIPojiTQz9GNNmssdgXIdracNlKt9CJLqIjs=";
    private static byte[] keyMaster;

    public CipherAES(byte[] key) {
        this.keyMaster = key;
    }

    public static byte[] cipherInGCMMode(byte[] plaintextBytes) throws InvalidCipherTextException {
        AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);
        // Init the cipherer with key, mac size and nonce
        KeyParameter keyParameter = new KeyParameter(Base64.decode(key));
        byte[] nonce = getRandomBytes(NONCE_SIZE_GCM);
        AEADParameters parameters = new AEADParameters(keyParameter, GCM_MODE_MAC_SIZE_BITS, nonce);
        cipher.init(true, parameters);
        int maxOutputSize = cipher.getOutputSize(plaintextBytes.length);
        byte[] cipherText = new byte[maxOutputSize];
        int outputSize = cipher.processBytes(plaintextBytes, 0, plaintextBytes.length, cipherText, 0);
        cipher.doFinal(cipherText, outputSize);
        // Append the nonce at the beginning of the ciphertext
        byte[] cipherTextWithNonce = new byte[nonce.length + cipherText.length];
        System.arraycopy(nonce, 0, cipherTextWithNonce, 0, nonce.length);
        System.arraycopy(cipherText, 0, cipherTextWithNonce, nonce.length, cipherText.length);
        return cipherTextWithNonce;
    }

    public static byte[] decipherInGCMMode(byte[] ciphertextBytesWithNonce) throws InvalidCipherTextException {
        AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);

        // Split nonce and ciphertext
        byte[] nonce = new byte[NONCE_SIZE_GCM];
        byte[] ciphertext = new byte[ciphertextBytesWithNonce.length - NONCE_SIZE_GCM];
        System.arraycopy(ciphertextBytesWithNonce, 0, nonce, 0, NONCE_SIZE_GCM);
        System.arraycopy(ciphertextBytesWithNonce, NONCE_SIZE_GCM, ciphertext, 0, ciphertext.length);

        KeyParameter keyParameter = new KeyParameter(Base64.decode(key));
        AEADParameters parameters = new AEADParameters(keyParameter, GCM_MODE_MAC_SIZE_BITS, nonce);
        cipher.init(false, parameters);

        int maxOutputSize = cipher.getOutputSize(ciphertext.length);
        byte[] plaintextBytes = new byte[maxOutputSize];

        int decipheredBytesSize = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintextBytes, 0);
        cipher.doFinal(plaintextBytes, decipheredBytesSize);

        return plaintextBytes;
    }

    private static byte[] getPlaintextBytes(String plaintext) {
        try {
            return plaintext.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return plaintext.getBytes();
        }
    }

    private static byte[] getRandomBytes(int length) {
        SecureRandom random = new SecureRandom();
        byte[] result = new byte[length];
        random.nextBytes(result);
        return result;
    }

}
