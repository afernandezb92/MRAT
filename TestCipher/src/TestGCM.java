import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;


public class TestGCM {

	private static final String PLAINTEXT = "hola";
	private static final int KEY_LENGTH_AES_256 = 256 / 8;
	
	public static void main(String[] args) throws InvalidCipherTextException
    {
		byte[] key = getRandomBytes(KEY_LENGTH_AES_256);
		AESEngine engine = new AESEngine();
        
        CipherAES cipherAes = new CipherAES();
        byte[] cipherText = cipherAes.cipherInGCMMode(getPlaintextBytes(PLAINTEXT), key, getRandomBytes(cipherAes.NONCE_SIZE_GCM), engine);
        engine.reset();
        System.out.println("CipherText: " + cipherText);
        String plaintext = cipherAes.decipherInGCMMode(cipherText, key, engine);
        System.out.println("PlainText: " + plaintext);
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
