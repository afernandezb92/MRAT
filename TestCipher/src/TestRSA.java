import org.bouncycastle.crypto.InvalidCipherTextException;


public class TestRSA {

	public static void main(String[] args) throws InvalidCipherTextException
    {
		
		String inputEncrypt, inputDecrypt;
		String input = "hola";
        GenerateRSAKeys generateRSAKeys = new GenerateRSAKeys();
        generateRSAKeys.generate();
        System.out.println("keys generated");
        String pub = generateRSAKeys.getPublicKey();
        String priv = generateRSAKeys.getPrivateKey();
        System.out.println("keys generated 2");
 
        //System.out.println("PubKey : " + generateRSAKeys.getPublicKey());
        //System.out.println("PrivKey : " + generateRSAKeys.getPrivateKey());
        inputEncrypt = RSAEncryption.encrypt(pub, input);
        System.out.println("inputEncrypt: " + inputEncrypt);
        inputDecrypt = RSADecryption.decrypt(priv, inputEncrypt);
        System.out.println("inputDecrypt: " + inputDecrypt);
    }
}
