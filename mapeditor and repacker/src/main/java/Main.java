import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button btnOpenMapEditor = new Button("Open Map Editor");
        btnOpenMapEditor.setOnAction(event -> {
            MapEditor mapEditor = new MapEditor();
            mapEditor.start(new Stage()); // Open the map editor in a new stage
        });
        StackPane root = new StackPane();
        root.getChildren().add(btnOpenMapEditor);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Main Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}