
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Security;
 
import java.util.Base64;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
 
 
public class RSAEncryption {
 
    @SuppressWarnings("restriction")
	public static String encrypt (String publicKey, String input){
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Base64.Decoder b64 = Base64.getDecoder();
            AsymmetricKeyParameter pKey = 
                (AsymmetricKeyParameter) PublicKeyFactory.createKey(b64.decode(publicKey));
            AsymmetricBlockCipher e = new RSAEngine();
            e = new org.bouncycastle.crypto.encodings.PKCS1Encoding(e);
            e.init(true, pKey);
            byte[] messageBytes = input.getBytes();
            byte[] hexEncodedCipher = e.processBlock(messageBytes, 0, messageBytes.length);
            return getHexString(hexEncodedCipher);
 
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
 
    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
 
}