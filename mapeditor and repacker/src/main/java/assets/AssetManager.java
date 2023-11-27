import mapeditor.AssetManager;  // Import the AssetManager class assuming it's in this package
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.Base64;

import mapeditor.AssetManager;  // Import the AssetManager class assuming it's in this package
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.Base64;
// Assuming the EncryptionUtils is in the package 'com.utils.file'
import com.utils.file.EncryptionUtils;
// JSON parsing library
import org.json.simple.JSONObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class MapEditor extends Application {
    private AssetManager assetManager;
    private ListView<String> listViewMaps;
    public MapEditor(String assetsPath) {
        assetManager = new AssetManager(assetsPath);
    }
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Create the file menu
        Menu fileMenu = new Menu("File");
        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(event -> createNewMap()); // You'll need to implement createNewMap() method
        fileMenu.getItems().add(newMap);

        // Create the menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        listViewMaps = new ListView<>();
        updateMapList(); // Call the method that updates the list with current maps
        VBox sideBar = new VBox(listViewMaps);

        root.setTop(menuBar);
        root.setLeft(sideBar);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Map Editor");
        stage.setScene(scene);
        stage.show();
    }

    private void updateMapList() {
        // Assuming that all maps are in a single directory and have a specific extension, e.g., ".map"
        try {
            Files.newDirectoryStream(Paths.get(assetManager.getAssetsPath()),
                path -> path.toString().endsWith(".map"))
                .forEach(mapPath -> listViewMaps.getItems().add(mapPath.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        try {
            Object map = assetManager.loadAsset("Maps.ss"); // Load the map using AssetManager
            // Continue with your logic for the map
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors
        }
    }

    private void createNewMap() {
      // Create a new map file
    }

    private void saveMap(File file, Object mapData) throws Exception {
        String mapContent = serializeMap(mapData); // Convert your map data to a String
        byte[] encryptedData = EncryptionUtils.encrypt(mapContent.getBytes());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(Base64.getEncoder().encode(encryptedData));
        }
    }

    private Object loadMap(File file) throws Exception {
        byte[] encryptedData;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = fis.readAllBytes();
            encryptedData = Base64.getDecoder().decode(data);
        }
        byte[] decryptedData = EncryptionUtils.decrypt(encryptedData);
        String mapContent = new String(decryptedData);
        return deserializeMap(mapContent); // Convert the string back to your map data object
    }

    private String serializeMap(Object mapData) {
        // Serialize your map data to a JSON string format
        if(mapData instanceof MapSerializable) {
            return ((MapSerializable)mapData).toJSONString();
        }
        throw new IllegalArgumentException("The map data does not implement MapSerializable.");
    }

    private Object deserializeMap(String mapContent) {
        // Deserialize string back to your map data object
        // This is a stub, replace with your actual deserialization logic
        return null;
    }

    // Interface for objects that can be serialized into a JSON string
    public interface MapSerializable {
        String toJSONString();
    }

    // Example implementation of the MapSerializable interface
    public static class MyMapData implements MapSerializable {
        private String name;
        private int width;
        private int height;
        // other properties and methods

        @Override
        public String toJSONString() {
            JSONObject obj = new JSONObject();
            obj.put("name", this.name);
            obj.put("width", this.width);
            obj.put("height", this.height);
            // add other properties to JSON object
            return obj.toJSONString();
        }

        // Constructor, getters, setters, and other methods...
    }

    public static void main(String[] args) {
        launch(args);
    }
}