package assets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY_VALUE = "piXMchSCpSgCMErm".getBytes();
    private static final Key key = generateKey();

    private static Key generateKey() {
        return new SecretKeySpec(KEY_VALUE, ALGORITHM);
    }

    public static byte[] encrypt(byte[] dataToEncrypt) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(dataToEncrypt);
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        return c.doFinal(encryptedData);
    }
}
