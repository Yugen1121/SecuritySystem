package application;
	
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Create a 3D box
        Box box = new Box(150, 150, 150);
        PhongMaterial material = new PhongMaterial(Color.CORNFLOWERBLUE);
        box.setMaterial(material);

        // Add box to a Group
        Group root = new Group(box);

        // Camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-600);

        // Scene with depth buffer enabled
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        // Show stage
        stage.setScene(scene);
        stage.setTitle("JavaFX 3D Box");
        stage.show();

        // Ensure clean exit
        stage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

 