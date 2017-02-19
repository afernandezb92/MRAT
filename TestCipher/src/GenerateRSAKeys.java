/*
 * This is a test program for probe RSA + AES cipher 
 */


//package com.as400samplecode;



import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;


public class GenerateRSAKeys{
	private String publicKey;
	private String privateKey;

	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void generate (){
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
			Base64.Encoder b64 =  Base64.getEncoder();
			SecureRandom random = createFixedRandom();
			generator.initialize(1024, random);
			KeyPair pair = generator.generateKeyPair();
			Key pubKey = pair.getPublic();
			Key privKey = pair.getPrivate();
			publicKey = b64.encodeToString(pubKey.getEncoded());
			privateKey = b64.encodeToString(privKey.getEncoded());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static SecureRandom createFixedRandom() {
		return new FixedRand();
	}

	private static class FixedRand extends SecureRandom {
		MessageDigest sha;
		byte[] state;
		FixedRand() {
			try {
				this.sha = MessageDigest.getInstance("SHA-1");
				this.state = sha.digest();
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("can't find SHA-1!");
			}
		}

		public void nextBytes(byte[] bytes){
			int    off = 0;
			sha.update(state);
			while (off < bytes.length){                
				state = sha.digest();
				if (bytes.length - off > state.length){
					System.arraycopy(state, 0, bytes, off, state.length);
				} else {
					System.arraycopy(state, 0, bytes, off, bytes.length - off);
				}
				off += state.length;
				sha.update(state);
			}
		}
	}
	
}