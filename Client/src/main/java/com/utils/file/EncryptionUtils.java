package com.utils.file;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils {

  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES";
  private static final String DIGEST_ALGORITHM = "SHA-256";
  private static final byte[] KEY = "OCuT9eMhUhXosv3BTFc1Bw==".getBytes();

  public static byte[] encrypt(byte[] dataToEncrypt) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      SecretKeySpec secretKey = new SecretKeySpec(getKeySpec(), ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);

      return cipher.doFinal(dataToEncrypt);
    } catch (Exception e) {
      throw new RuntimeException("Error encrypting data", e);
    }
  }

  public static byte[] decrypt(byte[] encryptedData) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      SecretKeySpec secretKey = new SecretKeySpec(getKeySpec(), ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);

      return cipher.doFinal(encryptedData);
    } catch (Exception e) {
      throw new RuntimeException("Error decrypting data", e);
    }
  }

  private static byte[] getKeySpec() {
    try {
      byte[] keySpec = Arrays.copyOf(KEY, 16); // AES key size is 16 bytes
      MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
      keySpec = sha.digest(keySpec);
      keySpec = Arrays.copyOf(keySpec, 16); // use only first 128 bits
      return keySpec;
    } catch (Exception e) {
      throw new RuntimeException("Error creating key spec", e);
    }
  }

  public static String calculateChecksum(byte[] data) {
    try {
      MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
      byte[] encodedhash = digest.digest(data);

      StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
      for (byte b : encodedhash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("Error calculating checksum", e);
    }
  }
}
