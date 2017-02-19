import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;


public class Main {
	private static final String PLAINTEXT = "This is a simple symmetric cryptography test using the bouncy castle library";
	private static final int KEY_LENGTH_AES_256 = 256 / 8;
	
	@SuppressWarnings({ "restriction", "static-access" })
	public static void main(String[] args) throws InvalidCipherTextException
    {
		
		String inputEncrypt, inputDecrypt;
		String input = "hola";
        GenerateRSAKeys generateRSAKeys = new GenerateRSAKeys();
        generateRSAKeys.generate();
        //System.out.println("PubKey : " + generateRSAKeys.getPublicKey());
        //System.out.println("PrivKey : " + generateRSAKeys.getPrivateKey());
        inputEncrypt = RSAEncryption.encrypt(generateRSAKeys.getPublicKey(), input);
        System.out.println("inputEncrypt: " + inputEncrypt);
        inputDecrypt = RSADecryption.decrypt(generateRSAKeys.getPrivateKey(), inputEncrypt);
        System.out.println("inputDecrypt: " + inputDecrypt);
		// AES - GCM
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
