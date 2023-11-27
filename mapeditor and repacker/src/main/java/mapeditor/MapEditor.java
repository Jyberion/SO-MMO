import assets.AssetManager; // Import the AssetManager class assuming it's in this package
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.Base64;
import com.utils.file.EncryptionUtils;
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

    MenuItem saveMapItem = new MenuItem("Save Map");
    saveMapItem.setOnAction(event -> {
      try {
        File mapFile = // ... get the current map file
            // We assume `currentMap` holds the map data that's been modified
            saveMap(mapFile, currentMap);
      } catch (Exception e) {
        e.printStackTrace();
        // Handle save exceptions, possibly with an alert dialog
      }
    });

    // Create the file menu
    Menu fileMenu = new Menu("File");
    MenuItem newMap = new MenuItem("New Map");
    newMap.setOnAction(event -> createNewMap());
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
    listViewMaps.setOnMouseClicked(event -> {
      String selectedMap = listViewMaps.getSelectionModel().getSelectedItem();
      if (selectedMap != null) {
        try {
          File mapFile = new File(assetManager.getAssetsPath(), selectedMap);
          Object map = loadMap(mapFile); // Replace Object with your actual map class
          displayMap(map); // Implement this method to display the map
        } catch (Exception e) {
          e.printStackTrace();
          // Handle errors, possibly with an alert dialog
        }
      }
    });
  }

  private void createNewMap() {
    // Prompt the user for the new map name
    TextInputDialog dialog = new TextInputDialog("NewMap");
    dialog.setTitle("Create New Map");
    dialog.setHeaderText("Create a New Map");
    dialog.setContentText("Enter map name:");

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      String mapName = result.get();
      // Sanitize and validate mapName if necessary
      try {
        // Try creating a new map with the given name and default settings
        MyMapData newMapData = new MyMapData(); // Customize this with your map class
        newMapData.setName(mapName);
        // Set any default data required for newMapData
        // TODO: Serialize and save the new map to a file
        File newMapFile = getUniqueMapFile(mapName);
        saveMap(newMapFile, newMapData); // Already implemented saveMap method
        // Update UI with new map
        updateMapList();
      } catch (IOException e) {
        e.printStackTrace();
        // Display an error message to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to create the new map");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
      }
    }
  }
  private File getUniqueMapFile(String baseName) throws IOException {
    File mapDirectory = new File(assetManager.getAssetsPath());
    File mapFile = new File(mapDirectory, baseName + ".json");

    int counter = 1;
    while (mapFile.exists()) {
      mapFile = new File(mapDirectory, baseName + "(" + counter + ").json");
      counter++;
    }

    mapFile.createNewFile(); // Create the new unique file
    return mapFile;
  }

  private void displayMap(MyMapData map) {
    // Clear previous map display
    BorderPane mapDisplayPane = new BorderPane();
    // Assuming MyMapData has a method getImages that returns a list of Image objects
    for (Image image : map.getImages()) {
      ImageView imageView = new ImageView(image);
      // Set up the image view properties and event handlers as needed
      setupDragAndDrop(imageView); // Implement this method based on your drag and drop logic
      mapDisplayPane.getChildren().add(imageView);
    }

    // Assuming MyMapData has a method getFootholds that returns a list of Foothold objects
    for (Foothold foothold : map.getFootholds()) {
      // Create visual representation for footholds.
      // This might be a simple line or shape added to the mapDisplayPane.
    }

    // Set the mapDisplayPane with all its children to the center of the root pane
    // so it gets displayed in the main Scene.
    BorderPane root = (BorderPane) stage.getScene().getRoot();
    root.setCenter(mapDisplayPane);
  }

  private void setupDragAndDrop(ImageView imageView) {
    // Implement drag and drop handling
    imageView.setOnDragDetected(event -> {
      Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.putImage(imageView.getImage());
      db.setContent(content);
      event.consume();
    });
    imageView.setOnDragDone(event -> {
      // Finish the drag and drop operation
      if (event.getTransferMode() == TransferMode.MOVE) {
        imageView.setImage(null); // If the image was successfully moved, clear the source.
      }
      event.consume();
    });
    // You can handle more events such as dragOver, dragEntered, dragExited and drop
    // to manage the drag-and-drop behavior as per your requirements.
    // For example, if we drop the image on the map display pane, we can handle the
    // drop like this (assuming a draggedImageView member to temporarily hold the dragged view):
    BorderPane mapDisplayPane = new BorderPane(); // This would be your map display
    mapDisplayPane.setOnDragOver(dragEvent -> {
      if (dragEvent.getDragboard().hasImage()) {
        dragEvent.acceptTransferModes(TransferMode.MOVE);
      }
      dragEvent.consume();
    });
    mapDisplayPane.setOnDrop(dropEvent -> {
      Dragboard db = dropEvent.getDragboard();
      boolean success = false;
      if (db.hasImage()) {
        ImageView droppedImageView = new ImageView(db.getImage());
        // Add logic to position the dropped image view on the map
        mapDisplayPane.getChildren().add(droppedImageView);
        success = true;
      }
      dropEvent.setDropCompleted(success);
      dropEvent.consume();
    });
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
    return deserializeMap(mapContent);
  }

  private String serializeMap(Object mapData) {
    // Serialize your map data to a JSON string format
    if (mapData instanceof MapSerializable) {
      return ((MapSerializable) mapData).toJSONString();
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