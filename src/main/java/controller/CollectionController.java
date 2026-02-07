package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import model.Version;
import service.CollectionService;
import service.SnippetService;

import java.io.File;
import java.util.ArrayList;

public class CollectionController {

    //FXML annotations to bind them to UI components in Login.fxml file
    @FXML private TextField collectionNameField;
    @FXML private ListView<String> collectionsListView;
    @FXML private ComboBox<String> snippetComboBox;
    @FXML private ComboBox<String> collectionComboBox;
    
    //for scene navigation
    private Stage primaryStage;
    private Scene scene;
    private Parent root;
    
    private CollectionService collectionService;
    private ObservableList<String> collectionsData;

    @FXML
    public void initialize() {
        collectionService = new CollectionService();
        collectionsData = FXCollections.observableArrayList();
        
        //load collections and snippets
        loadCollections();
        loadSnippets();
        loadCollectionsList();
        
        //set up list view to display collections
        collectionsListView.setItems(collectionsData);
        collectionsListView.setPrefHeight(300);
    }

    //load all available snippets into the combobox
    private void loadSnippets() {
        ArrayList<File> files = SnippetService.getSnippetFiles();
        ObservableList<String> snippets = FXCollections.observableArrayList();
        
        for (File file : files) {
            ArrayList<Version> versions = SnippetService.getVersions(file.getName());
            if (!versions.isEmpty()) {
                Version latest = versions.get(versions.size() - 1);
                snippets.add(latest.getTitle() + " (" + file.getName() + ")");
            }
        }
        
        snippetComboBox.setItems(snippets);
    }

    //load collection names into the collection combobox
    private void loadCollectionsList() {
        //get all collection names from service (simulated storage)
        collectionsData.clear();
        
        //load from a simple list maintained during the session
        if (collectionService != null) {
            //collections are stored in the service and displayed on during session, no json
            collectionsListView.setItems(collectionsData);
        }
    }

    //load existing collections from storage
    private void loadCollections() {
        //collections are managed in-memory during the session
    }

    @FXML
    public void handleCreateCollection(ActionEvent event) {
        String collectionName = collectionNameField.getText().trim();
        
        if (collectionName.isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please enter a collection name.");
            return;
        }
        
        //check if collection already exists
        if (collectionsData.contains(collectionName)) {
            showAlert(AlertType.WARNING, "Duplicate", "Collection '" + collectionName + "' already exists.");
            return;
        }
        
        //create new collection
        collectionService.groupSnippetsByCollection(collectionName);
        collectionsData.add(collectionName);
        collectionComboBox.setItems(FXCollections.observableArrayList(collectionsData));
        
        collectionNameField.clear();
        showAlert(AlertType.INFORMATION, "Success", "Collection '" + collectionName + "' created successfully.");
    }

    @FXML
    public void handleAssignSnippet(ActionEvent event) {
        String selectedSnippet = snippetComboBox.getValue();
        String selectedCollection = collectionComboBox.getValue();
        
        if (selectedSnippet == null || selectedCollection == null) {
            showAlert(AlertType.WARNING, "Selection Error", "Please select both a snippet and a collection.");
            return;
        }
        
        //extract filename from the selected snippet string
        String filename = selectedSnippet.substring(selectedSnippet.lastIndexOf("(") + 1, selectedSnippet.lastIndexOf(")"));
        
        try {
            //load the snippet version
            ArrayList<Version> versions = SnippetService.getVersions(filename);
            if (versions.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Snippet not found.");
                return;
            }
            
            Version snippet = versions.get(versions.size() - 1);
            
            //assign to collection
            collectionService.assignSnippetToOneCollection(snippet, selectedCollection);
            
            showAlert(AlertType.INFORMATION, "Success", 
                "Snippet '" + snippet.getTitle() + "' assigned to collection '" + selectedCollection + "'.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Failed to assign snippet: " + e.getMessage());
        }
    }
    //for alert message
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //helper method for switching to Home scene
    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Home.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
