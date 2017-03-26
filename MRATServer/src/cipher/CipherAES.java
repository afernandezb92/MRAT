package cipher;


import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;


public class CipherAES {

    public final static int IV_SIZE_DES = 64 / 8;
    public final static int IV_SIZE_TRIPLEDES = 64 / 8;
    public final static int IV_SIZE_AES = 128 / 8;
    public final static int IV_SIZE_TWOFISH = 128 / 8;
    public final static int NONCE_SIZE_GCM = 96 / 8;
    private final static int GCM_MODE_MAC_SIZE_BITS = 128;
    private static final int KEY_LENGTH_AES_256 = 256 / 8;
    private static byte[] key;

	public CipherAES(byte[] k){
    	key = k;
    }
	
	public String getKeyString(){
		return new String(key);
	}

    public byte[] cipherInGCMMode(String plaintext) throws InvalidCipherTextException {
    	byte[] plaintextBytes = getPlaintextBytes(plaintext);
    	AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);
        byte[] nonce = getRandomBytes(NONCE_SIZE_GCM);
        
        // Init the cipherer with key, mac size and nonce
        KeyParameter keyParameter = new KeyParameter(key);
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
    
    public byte[] cipherInGCMMode(byte[] plaintextBytes) throws InvalidCipherTextException {
    	AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);
        byte[] nonce = getRandomBytes(NONCE_SIZE_GCM);
        
        // Init the cipherer with key, mac size and nonce
        KeyParameter keyParameter = new KeyParameter(key);
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


    public static String decipherInGCMMode(byte[] ciphertextBytesWithNonce) throws InvalidCipherTextException {
    	AESEngine engine = new AESEngine();
        AEADBlockCipher cipher = new GCMBlockCipher(engine);

        // Split nonce and ciphertext
        byte[] nonce = new byte[NONCE_SIZE_GCM];
        byte[] ciphertext = new byte[ciphertextBytesWithNonce.length - NONCE_SIZE_GCM];
        System.arraycopy(ciphertextBytesWithNonce, 0, nonce, 0, NONCE_SIZE_GCM);
        System.arraycopy(ciphertextBytesWithNonce, NONCE_SIZE_GCM, ciphertext, 0, ciphertext.length);

        KeyParameter keyParameter = new KeyParameter(key);
        AEADParameters parameters = new AEADParameters(keyParameter, GCM_MODE_MAC_SIZE_BITS, nonce);
        cipher.init(false, parameters);

        int maxOutputSize = cipher.getOutputSize(ciphertext.length);
        byte[] plaintextBytes = new byte[maxOutputSize];

        int decipheredBytesSize = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintextBytes, 0);
        cipher.doFinal(plaintextBytes, decipheredBytesSize);

        String plaintext;
        try {
            plaintext = new String(plaintextBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            plaintext = new String(plaintextBytes);
        }

        return plaintext;
    }
    
    private static byte[] getRandomBytes(int length) {
    	SecureRandom random = new SecureRandom();
    	byte[] result = new byte[length];
    	random.nextBytes(result);
    	return result;
    }
    
    private static byte[] getPlaintextBytes(String plaintext) {
        try {
            return plaintext.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return plaintext.getBytes();
        }
    }

}