package GUI.mockups;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.GUI.MainController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainControllerTest {

    private static File tempRootDir;

    @BeforeAll
    public static void setup() throws IOException {
        // JavaFX initialisieren (in Headless-Tests nötig)
        new JFXPanel();

        // Temporären Ordner mit Unterordnern und Dateien erzeugen
        tempRootDir = Files.createTempDirectory("testRootDir").toFile();

        File subFolder = new File(tempRootDir, "subfolder");
        subFolder.mkdir();

        new File(tempRootDir, "file1.txt").createNewFile();
        new File(subFolder, "file2.txt").createNewFile();
    }

    @Test
    public void testLoadFolderStructure() throws InterruptedException {
        Platform.runLater(() -> {
            MainController controller = new MainController();

            // Manuelles Setzen von TreeView und ListView (ohne FXML laden)
            controller.folderTreeView = new TreeView<>();
            controller.documentsListView = new ListView<>();

            // Setze Root-Ordner und initiiere TreeView
            controller.setRootDirectory(tempRootDir);

            // Prüfe, ob TreeView Root gesetzt ist
            assertNotNull(controller.folderTreeView.getRoot());

            // Optional: weitere Assertions oder Interaktionen mit TreeView/ListView
        });

        // Kurze Pause, um Platform.runLater zu ermöglichen
        Thread.sleep(1000);
    }
}
