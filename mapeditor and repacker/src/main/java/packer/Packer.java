import com.utils.file.EncryptionUtils;
import org.json.simple.JSONObject; // Make sure to have a JSON library
import java.io.*;
import java.util.Base64;

public class Packer {
  public static void packDataToFile(Object data, String filePath) {
    try {
      // Serialize data to JSON (for example, you can use a more suitable format)
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("mapData", data); // Assuming 'data' can be put in a JSONObject directly
      String serializedData = jsonObject.toJSONString();
      // Encrypt the serialized data
      byte[] encryptedData = EncryptionUtils.encrypt(serializedData.getBytes());
      // Base64-encode the encrypted data
      String base64EncodedData = Base64.getEncoder().encodeToString(encryptedData);
      // Write the encoded data to the file
      try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")) {
        writer.write(base64EncodedData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Object unpackDataFromFile(String filePath) {
    try {
      // Read the Base64-encoded encrypted data from the file
      String base64EncodedData;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
        base64EncodedData = reader.readLine();
      }
      // Base64-decode the data to get the encrypted byte array
      byte[] encryptedData = Base64.getDecoder().decode(base64EncodedData);
      // Decrypt the data
      byte[] decryptedData = EncryptionUtils.decrypt(encryptedData);
      // Deserialize the decrypted data back into an object
      // Implement your deserialization logic here, for example, using JSON
      String json = new String(decryptedData);
      // Assuming you have a method that can parse the JSON string back into an
      // object:
      Object data = deserializeJSON(json);
      return data;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Object deserializeJSON(String jsonString) {
    // Implement JSON deserialization logic according to your application's needs
    // Assume we retun parsed object here
    return null;
  }
}