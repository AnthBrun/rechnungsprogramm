package com.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class MainController {

    @FXML
    public TreeView<File> folderTreeView;

    @FXML
    public ListView<File> documentsListView;

    private File rootDirectory;

    private ImageView pdfImageView;

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        initTreeView();
    }

    @FXML
    public void initialize() {
        // initial empty, call setRootDirectory() in test or main app to load real data
        try {
            loadMockData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        folderTreeView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
            if (newValue != null) {
                File selected = newValue.getValue();
                if (selected.isFile() && selected.getName().endsWith(".pdf")) {
                    renderPdfToImage(selected);
                }
            }
        });   
    }

    private void initTreeView() {
        if (rootDirectory == null || !rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Root directory ist ungültig");
        }

        TreeItem<File> rootItem = createNode(rootDirectory);
        System.out.println("Root directory: " + rootDirectory.getAbsolutePath());
        System.out.println("Root item: " + rootItem.getValue().getAbsolutePath());
        folderTreeView.setRoot(rootItem);
        System.out.println("TreeView initialized with root: " + folderTreeView.getRoot().getValue().getAbsolutePath());
        folderTreeView.setShowRoot(true);

        folderTreeView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<TreeItem<File>>() {
                @Override
                public void changed(ObservableValue<? extends TreeItem<File>> observable,
                                    TreeItem<File> oldValue,
                                    TreeItem<File> newValue) {
                    if (newValue != null) {
                        File selectedFolder = newValue.getValue();
                        if (selectedFolder.isDirectory()) {
                            File[] files = selectedFolder.listFiles();
                            if (files != null) {
                                documentsListView.setItems(
                                    FXCollections.observableArrayList(
                                        java.util.Arrays.stream(files)
                                            .filter(File::isFile)
                                            .toList()
                                    ));
                            } else {
                                documentsListView.setItems(FXCollections.emptyObservableList());
                            }
                        }
                    }
                }
            }
        );
    }

    private TreeItem<File> createNode(final File f) {
        return new TreeItem<>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }

            @Override
            public javafx.collections.ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            private javafx.collections.ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
                File f = TreeItem.getValue();
                if (f != null && f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        javafx.collections.ObservableList<TreeItem<File>> children =
                            javafx.collections.FXCollections.observableArrayList();

                        for (File childFile : files) {
                            // Füge sowohl Ordner als auch Dateien ein
                            children.add(createNode(childFile));
                        }
                        return children;
                    }
                }
                return javafx.collections.FXCollections.emptyObservableList();
            }
        };
    }

    @FXML
    public void handleExit() {
        // Handle exit logic, e.g., saving state or closing the application
        System.exit(0);
    }

    private void renderPdfToImage(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 150); // erste Seite
            Image fxImage = SwingFXUtils.toFXImage(image, null);
            pdfImageView.setImage(fxImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadMockData() throws IOException {
        // Temporären Ordner erstellen
        Path tempRoot = Files.createTempDirectory("mockRoot");

        // Unterordner anlegen
        Path subfolder1 = Files.createDirectory(tempRoot.resolve("Folder1"));
        Path subfolder2 = Files.createDirectory(tempRoot.resolve("Folder2"));

        // Dateien anlegen
        Files.createFile(tempRoot.resolve("rootFile1.txt"));
        Files.createFile(subfolder1.resolve("file1.txt"));
        Files.createFile(subfolder1.resolve("file2.txt"));
        Files.createFile(subfolder2.resolve("file3.txt"));

        // Setze den temporären Ordner als Root
        setRootDirectory(tempRoot.toFile());

        // Optional: Temporäre Dateien nicht sofort löschen (sonst GUI zeigt nichts)
        tempRoot.toFile().deleteOnExit();
        subfolder1.toFile().deleteOnExit();
        subfolder2.toFile().deleteOnExit();
    }
}
