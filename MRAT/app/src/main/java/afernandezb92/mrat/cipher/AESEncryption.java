package afernandezb92.mrat.cipher;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class AESEncryption {

    private static byte[] key;
    public final static int IV_SIZE_DES = 64 / 8;
    public final static int IV_SIZE_TRIPLEDES = 64 / 8;
    public final static int IV_SIZE_AES = 128 / 8;
    public final static int IV_SIZE_TWOFISH = 128 / 8;
    public final static int NONCE_SIZE_GCM = 96 / 8;
    private final static int GCM_MODE_MAC_SIZE_BITS = 128;

    public AESEncryption(byte[] key) {
        this.key = key;
    }

    public static byte[] cipherInGCMMode(String plaintext) throws InvalidCipherTextException {
        byte[] plaintextBytes = getPlaintextBytes(plaintext);
        AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);
        // Init the cipherer with key, mac size and nonce
        KeyParameter keyParameter = new KeyParameter(key);
        byte [] nonce = getRandomBytes(NONCE_SIZE_GCM);
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
