package cipher;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class Keys {
	
	private static final int KEY_LENGTH_AES_256 = 256 / 8;
	private static String[] keys ={"LXx8pMOhIPojiTQz9GNNmssdgXIdracNlKt9CJLqIjs=",
    		"Iz0IpKNztdpULEJ0tkVh0UxwQoLiHd8b0k1RKbSlHQs=",
    		"KOWLaQ0hU0lJpMqYAWrnzhIzdDWxn7+EqodLb1pf/hc="	
    };
	
	public static byte[] getKey(int id){
		return Base64.getDecoder().decode(keys[id]);
	}
	
	public static byte[] generateKey(){
		return getRandomBytes(KEY_LENGTH_AES_256);
	}
	
	private static byte[] getRandomBytes(int length) {
    	SecureRandom random = new SecureRandom();
    	byte[] result = new byte[length];
    	random.nextBytes(result);
    	return result;
    }
	
	public static int generateNonce(){
		Random r = new Random();
		return r.nextInt(9)*1000 + r.nextInt(9)*100 + r.nextInt(9)*10 + r.nextInt(9);
	}
}
