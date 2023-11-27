import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import com.client.ui.LoginUI;
import com.utils.file.EncryptionUtils;
import com.utils.net.NetworkUtils;
import java.util.Map;

public class Client extends ApplicationAdapter {
  private Channel channel;
  private LoginUI loginUI;

  @Override
  public void create() {
    // Update the IP address and port number to point to your login server
    String loginServerIP = "127.0.0.1"; // Replace with the login server's IP address
    int loginServerPort = 12345; // Replace with the login server's port number

    // Connect to the login server using the IP and port
    ChannelFuture connectFuture = NetworkUtils.connectToServer(loginServerIP, loginServerPort);

    // Add a listener to handle the completion of the connection attempt
    connectFuture.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
          // Connection is established
          channel = future.channel();
          // Proceed with loading files and verification
          loadFilesAndVerifyWithServer(() -> {
            // After verification, we show the login screen within the rendering thread
            Gdx.app.postRunnable(() -> {
              loginUI = new LoginUI(channel, Client.this::onLoginSuccess);
              loginUI.show();
            });
          });
        } else {
          // Connection failed, handle it accordingly
          System.err.println("Failed to connect to the login server.");
        }
      }
    });
  }

  private void onLoginSuccess() {
    Map<String, Boolean> worldStatus = NetworkUtils.getWorldStatusFromLoginServer();
    Gdx.app.postRunnable(() -> {
      // Assuming loginUI has a method to update the world server list called
      // updateWorldList
      loginUI.updateWorldList(worldStatus);
    });
  }

  private void loadFilesAndVerifyWithServer(Runnable onVerificationComplete) {
    // Update the filesLoaded to reflect any loading errors
    boolean filesLoaded = true; // Start assuming all files are loaded

    // Define all the files that need to be loaded and verified
    String[] filesToLoad = new String[] {
        "Equip.ss",
        "Items.ss",
        "Etc.ss",
        "Use.ss",
        "skin.ss",
        "Effects.ss",
        "Maps.ss",
        "Mobs.ss",
        "Skills.ss",
        "NPCs.ss",
        "Quest.ss",
        "Sound.ss",
        "Context.ss",
        "UI.ss",
    };

    for (String filename : filesToLoad) {
      // If any file fails to load and verify, set filesLoaded to false
      if (!loadAndVerify(filename)) {
        filesLoaded = false;
        break; // Exit the loop early if a file verification fails
      }
    }

    if (filesLoaded) {
      // If all files are loaded and verified successfully, proceed to call
      // onVerificationComplete
      onVerificationComplete.run();
    } else {
      // Handle a failure in loading or verification here
    }
  }

  private boolean loadAndVerify(String filename) {
    boolean isVerified = false;
    try {
      byte[] encryptedData = loadFile(filename); // Load the encrypted file
      byte[] decryptedData = EncryptionUtils.decrypt(encryptedData); // Decrypt data
      String fileChecksum = EncryptionUtils.calculateChecksum(decryptedData); // Calculate checksum
      String fileID = extractFileID(filename); // Extract unique ID based on filename

      isVerified = verifyWithServer(fileID, fileChecksum); // Request server to verify
    } catch (Exception e) {
      // Handle decryption error or verification failure
    }
    return isVerified;
  }

  private byte[] loadFile(String filename) {
    // Placeholder for file loading
    // Actual file-loading logic would depend on your application's specific
    // requirements
    return new byte[0]; // Placeholder
  }

  private String extractFileID(String filename) {
    // Extract the unique ID from the filename, assuming IDs are numeric parts of
    // the filename
    return filename.replaceAll("\\D+", ""); // Placeholder
  }

  private boolean verifyWithServer(String fileID, String fileChecksum) {
    // Placeholder for server communication
    // Actual verification logic will use YourNetworkUtils to interact with your
    // server
    return NetworkUtils.verifyFileWithServer(fileID, fileChecksum); // Placeholder
  }

  @Override
  public void render() {
    if (loginUI != null) {
      loginUI.render();
    }
  }

  @Override
  public void dispose() {
    if (loginUI != null) {
      loginUI.dispose();
    }
  }
}
