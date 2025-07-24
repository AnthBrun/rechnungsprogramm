package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.GUI.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        Parent root = loader.load();

        // Zugriff auf Controller, um Testdaten zu setzen
        MainController controller = loader.getController();
        controller.setRootDirectory(createMockTestDirectory());

        primaryStage.setTitle("Rechnungs- und Angebotsverwaltung");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private File createMockTestDirectory() throws IOException {
        File tempRoot = Files.createTempDirectory("mockRoot").toFile();

        File unterordner1 = new File(tempRoot, "Angebote");
        File unterordner2 = new File(tempRoot, "Rechnungen");
        unterordner1.mkdir();
        unterordner2.mkdir();

        new File(unterordner1, "angebot1.tex").createNewFile();
        new File(unterordner2, "rechnung1.pdf").createNewFile();
        new File(unterordner2, "rechnung2.tex").createNewFile();

        return tempRoot;
    }
}
