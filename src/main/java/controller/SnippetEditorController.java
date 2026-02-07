package controller;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import service.VersionService;
import service.SnippetService;
import service.CollectionService;
import service.AutoSaveService;
import model.Version;
import util.JSONUtil;
import util.DateTimeUtil;

public class SnippetEditorController {

    @FXML private TextField titleField;
    @FXML private TextArea codeField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> choiceBox1;
    @FXML private ComboBox<String> choiceBox2;
    @FXML private ListView<String> backlinksListView;

    private Stage primaryStage;
    private Scene scene;
    private Parent root;
    private String currentFile = null;  //track currently open file
    private CollectionService collectionService;
    private AutoSaveService autoSaveService;

    @FXML
    public void initialize() {
        collectionService = new CollectionService();
        autoSaveService = new AutoSaveService();
        
        //example programming language and tag options for demo purpose
        if (choiceBox1 != null) {
            choiceBox1.getItems().addAll("Java", "Python", "C++", "JavaScript");
        }
        if (choiceBox2 != null) {
            choiceBox2.getItems().addAll("Dockerfile", "Java", "Sorting", "Algorithm", "Config", "Script");
        }
    }
    
    //method to load a snippet file (can be called from other controllers)
    public void loadSnippetFile(String filename) {
        this.currentFile = filename;
        
        //load the latest version of the snippet into editor fields
        Version latestVer = SnippetService.getLatestVersion(filename);
        if (latestVer != null) {
            titleField.setText(latestVer.getTitle());
            codeField.setText(latestVer.getCode());
            descriptionField.setText(latestVer.getDescription());
            
            //set language dropdown
            if (choiceBox1 != null && latestVer.getLanguage() != null) {
                choiceBox1.setValue(latestVer.getLanguage());
            }
            
            //set tags dropdown (just setting first tag for now)
            if (choiceBox2 != null && latestVer.getTags() != null && !latestVer.getTags().isEmpty()) {
                choiceBox2.setValue(latestVer.getTags());
            }
            
            //load backlinks for this snippet
            loadBacklinks(latestVer);
        }

        // Start or restart auto-save for this file
        if (autoSaveService != null && currentFile != null) {
            autoSaveService.startTimer(currentFile, () -> makeCurrentSnippetVersion());
        }
    }
    
    //load and display backlinks for the currently viewed snippet
    private void loadBacklinks(Version currentSnippet) {
        ObservableList<String> backlinks = FXCollections.observableArrayList();
        
        if (backlinksListView == null) return;
        
        //get all snippet files to check for backlinks
        ArrayList<File> files = SnippetService.getSnippetFiles();
        
        for (File file : files) {
            ArrayList<Version> versions = SnippetService.getVersions(file.getName());
            if (!versions.isEmpty()) {
                Version latest = versions.get(versions.size() - 1);
                
                //check if this snippet has a backlink that points to our current snippet
                //we need to find if collectionService has stored a backlink from 'latest' to 'currentSnippet'
                Version linkedSnippet = collectionService.getDestinationSnippet(latest);
                
                if (linkedSnippet != null && linkedSnippet.getTitle().equals(currentSnippet.getTitle())) {
                    //this snippet links to our current snippet
                    backlinks.add("→ " + latest.getTitle() + " (" + file.getName() + ")");
                }
            }
        }
        
        if (backlinks.isEmpty()) {
            backlinks.add("(No backlinks)");
        }
        
        backlinksListView.setItems(backlinks);
    }


    @FXML
    public void handleSave(ActionEvent event) {
        if (isEditorEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter a name or content for your snippet.");
            return;
        }
        
        String title = titleField.getText();
        String code = codeField.getText();
        String desc = descriptionField.getText();
        String lang = choiceBox1.getValue();
        String tag = choiceBox2.getValue();
        
        if (currentFile == null) {
            //creating new snippet
            String newFile = SnippetService.createFromGUI(
                title, 
                lang != null ? lang : "",
                tag != null ? tag : "",
                "",
                desc,
                code
            );
            if (newFile != null) {
                currentFile = newFile;
                showAlert(AlertType.INFORMATION, "Success", "Snippet saved as " + newFile);
                //start auto-save timer for the newly created snippet
                if (autoSaveService != null) {
                    autoSaveService.startTimer(currentFile, () -> makeCurrentSnippetVersion());
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save snippet.");
            }
        } else {
            //updating existing snippet
            String timestamp = DateTimeUtil.getFormattedDateTimeNow();
            File file = new File("data/snippets/" + currentFile);
            ArrayList<model.Version> versions = util.JSONUtil.reader(file);
            int nextVer = versions.size();
            
            model.Version newVersion = new model.Version(
                title,
                lang != null ? lang : "",
                tag != null ? tag : "",
                "",  //packages
                desc,
                code,
                nextVer,
                timestamp
            );
            versions.add(newVersion);
            JSONUtil.writer(file, versions);
            showAlert(AlertType.INFORMATION, "Success", "Snippet updated (Version " + nextVer + ")");
            //ensure auto-save is active for the current file
            if (autoSaveService != null) {
                autoSaveService.startTimer(currentFile, () -> makeCurrentSnippetVersion());
            }
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        //check if Home.fxml exists
        System.out.println("Switching to Home.fxml...");
        //stop auto-save if leaving the editor view
        if (autoSaveService != null) {
            autoSaveService.stopTimer();
        }
        root = FXMLLoader.load(getClass().getResource("/view/Home.fxml"));
        if (root == null) {
            System.err.println("Home.fxml not found! Check file location.");
            return;
        }
        primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    @FXML
    public void handleNew(ActionEvent event) {
        //stop auto-save if starting a new (unsaved) snippet
        if (autoSaveService != null) {
            autoSaveService.stopTimer();
        }
        currentFile = null;  //reset to 'Create new snippet' mode
        titleField.clear();
        codeField.clear();
        descriptionField.clear();
        if (choiceBox1 != null) {
            choiceBox1.getSelectionModel().clearSelection();
        }
        if (choiceBox2 != null) {
            choiceBox2.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        if (isEditorEmpty()) {
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to clear the editor?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Delete Content");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            handleNew(event);
        }
    }

    private boolean isEditorEmpty() {
        return (titleField.getText() == null || titleField.getText().trim().isEmpty()) &&
               (codeField.getText() == null || codeField.getText().trim().isEmpty()) &&
               (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty());
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //to make a Version object from current editor fields for autosave checks
    private Version makeCurrentSnippetVersion() {
        String title = titleField != null ? titleField.getText() : "";
        String code = codeField != null ? codeField.getText() : "";
        String desc = descriptionField != null ? descriptionField.getText() : "";
        String lang = (choiceBox1 != null) ? choiceBox1.getValue() : "";
        String tag = (choiceBox2 != null) ? choiceBox2.getValue() : "";

        String timestamp = DateTimeUtil.getFormattedDateTimeNow();
        //version number is ignored by SnippetService.update
        return new Version(
            title != null ? title : "",
            lang != null ? lang : "",
            tag != null ? tag : "",
            "",
            desc != null ? desc : "",
            code != null ? code : "",
            0,
            timestamp
        );
    }
    
    //open version comparison pop-up window
    @FXML
    public void checkVersions(ActionEvent event) {
        if (currentFile == null || currentFile.isEmpty()) {
            showAlert(AlertType.WARNING, "No File", "Please open a snippet first to view versions.");
            return;
        }
        
        File file = new File("data/snippets/" + currentFile);
        ArrayList<Version> versionList = VersionService.getAllVersions(file);
        
        if (versionList.size() < 2) {
            showAlert(AlertType.INFORMATION, "Not Enough Versions", "You need at least 2 versions to compare.");
            return;
        }
        
        //create pop-up window
        Stage popup = new Stage();
        popup.setTitle("Compare Versions");
        
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        //dropdowns for picking versions
        ComboBox<Integer> firstVer = new ComboBox<>();
        ComboBox<Integer> secondVer = new ComboBox<>();
        
        for (Version v : versionList) {
            firstVer.getItems().add(v.getVersion());
            secondVer.getItems().add(v.getVersion());
        }
        
        firstVer.setPromptText("Pick First Version");
        secondVer.setPromptText("Pick Second Version");
        
        Button goBtn = new Button("Compare");
        TextArea resultBox = new TextArea();
        resultBox.setEditable(false);
        resultBox.setPrefHeight(300);
        
        //when user clicks compare versions button
        goBtn.setOnAction(e -> {
            Integer ver1 = firstVer.getValue();
            Integer ver2 = secondVer.getValue();
            
            if (ver1 == null || ver2 == null) {
                showAlert(AlertType.WARNING, "Missing Selection", "Please pick both versions to compare.");
                return;
            }
            
            String result = VersionService.compareVersions(file, ver1, ver2);
            if (result != null) {
                resultBox.setText(result);
            } else {
                showAlert(AlertType.ERROR, "Comparison Failed", "Could not compare the versions.");
            }
        });
        
        box.getChildren().addAll(
            new Label("First Version:"), firstVer,
            new Label("Second Version:"), secondVer,
            goBtn,
            new Label("Comparison Result:"), resultBox
        );
        
        Scene popupScene = new Scene(box, 500, 450);
        popup.setScene(popupScene);
        popup.show();
    }
}