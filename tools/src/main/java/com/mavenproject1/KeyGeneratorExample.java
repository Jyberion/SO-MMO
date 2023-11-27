import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyGeneratorExample {
    public static void main(String[] args) {
        try {
            // Specify the provider explicitly
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            // Initialize the KeyGenerator
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, secureRandom); // 128-bit key size for AES

            // Generate a secret key
            SecretKey secretKey = keyGenerator.generateKey();

            // Get the key bytes
            byte[] keyBytes = secretKey.getEncoded();

            // Convert the key bytes to a string representation
            String base64Key = java.util.Base64.getEncoder().encodeToString(keyBytes);

            // Print the generated key
            System.out.println("Generated Key: " + base64Key);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}