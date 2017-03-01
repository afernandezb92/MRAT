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


public class AESDecryption {

	private static byte[] key;
	public final static int IV_SIZE_DES = 64 / 8;
	public final static int IV_SIZE_TRIPLEDES = 64 / 8;
	public final static int IV_SIZE_AES = 128 / 8;
	public final static int IV_SIZE_TWOFISH = 128 / 8;
	public final static int NONCE_SIZE_GCM = 96 / 8;
	private final static int GCM_MODE_MAC_SIZE_BITS = 128;



	public AESDecryption(byte[] key) {
		this.key = key;
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


}
