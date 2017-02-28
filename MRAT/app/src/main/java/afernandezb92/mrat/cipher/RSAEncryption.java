package afernandezb92.mrat.cipher;


import android.util.Base64;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.encodings.PKCS1Encoding;
import org.spongycastle.crypto.engines.RSAEngine;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PublicKeyFactory;

import java.security.Security;


public class RSAEncryption {

    private String secrectKey = "prueba";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYBjV6H7fIfmOCQKDnd14B0lEH8j1Qe0xE5sDWzhAU/ZGJIT4c0M7tB5bwkxjIMFKpb5fGUYhOWAgDT+Oqg4MdmgzbeVxyK1hRXrhBOwjUQPNlN8z8Xr6qvi3PkhxvmfwlLUXdYroGaVuBvF7YYohYLpspwNPwDaZxUHkie8gaKwIDAQAB";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public String encrypt() {
        try {
            Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
            AsymmetricKeyParameter pKey =
                    (AsymmetricKeyParameter) PublicKeyFactory.createKey(Base64.decode(publicKey, Base64.DEFAULT));
            AsymmetricBlockCipher e = new RSAEngine();
            e = new PKCS1Encoding(e);
            e.init(true, pKey);
            byte[] messageBytes = secrectKey.getBytes();
            byte[] hexEncodedCipher = e.processBlock(messageBytes, 0, messageBytes.length);
            return getHexString(hexEncodedCipher);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

}