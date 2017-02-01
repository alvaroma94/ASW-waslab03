package wallOfTweets;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.*;

public class AESencr {
    
    private static final String algoritme = "AES";
   private static final byte[] clau = 
       new byte[] { 'a', 's', 'w', 'c', 'l', 'a', 'v',

'e', 's', 'e', 'c','r', 'e', 't', 'a', '1' };


public static String encrypt(String Data) throws Exception {
       Key key = generateKey();
       Cipher c = Cipher.getInstance(algoritme);
       c.init(Cipher.ENCRYPT_MODE, key);
       byte[] encVal = c.doFinal(Data.getBytes());
       String encryptedValue = new BASE64Encoder().encode(encVal);
       return encryptedValue;
   }

   public static String decrypt(String encryptedData) throws Exception {
       Key key = generateKey();
       Cipher c = Cipher.getInstance(algoritme);
       c.init(Cipher.DECRYPT_MODE, key);
       byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
       byte[] decValue = c.doFinal(decordedValue);
       String decryptedValue = new String(decValue);
       return decryptedValue;
   }
   private static Key generateKey() throws Exception {
       Key key = new SecretKeySpec(clau, algoritme);
       return key;
}

}

