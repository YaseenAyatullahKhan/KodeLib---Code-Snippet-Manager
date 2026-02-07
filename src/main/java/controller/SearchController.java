package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import model.Version;
import service.SnippetService;
import service.SearchService;
import service.FilterandSortService;
import util.DateTimeUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchController {

    //FXML attributes
    @FXML private TextField searchBar;
    @FXML private CheckBox caseToggle;
    @FXML private ComboBox<String> langFilter;
    @FXML private ComboBox<String> tagFilter;
    @FXML private DatePicker createdStartDate;
    @FXML private DatePicker createdEndDate;
    @FXML private DatePicker modifiedStartDate;
    @FXML private DatePicker modifiedEndDate;
    @FXML private ComboBox<String> sortBox;
    @FXML private TableView<SnippetItem> tableView;
    @FXML private TableColumn<SnippetItem, String> colName;
    @FXML private TableColumn<SnippetItem, String> colLang;
    @FXML private TableColumn<SnippetItem, String> colTags;
    @FXML private TableColumn<SnippetItem, LocalDate> colCreated;
    @FXML private TableColumn<SnippetItem, LocalDate> colModified;
    @FXML private TableColumn<SnippetItem, Void> colActions;

    private final ObservableList<SnippetItem> masterData = FXCollections.observableArrayList();
    private FilterAndSortController filterController;
    //for scene navigation
    private Stage primaryStage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        //initialize filter controller
        filterController = new FilterAndSortController();
        injectFilterFields();
        
        //load all snippets first
        loadAllSnippets();

        //set up table columns
        colName.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getName()));
        colLang.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getLanguage()));
        colTags.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getTagsString()));
        colCreated.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getCreatedDate()));
        colModified.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getModifiedDate()));
        
        addActionButtonToTable();

        //set up search bar listener
        searchBar.textProperty().addListener((obs, old, newVal) -> performSearch(newVal));
        
        //initialize filters
        filterController.initializeFilters(masterData);
        
        //set up filter listeners to re-trigger search
        langFilter.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));
        tagFilter.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));
        createdStartDate.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));
        createdEndDate.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));
        modifiedStartDate.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));
        modifiedEndDate.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));

        //setting up case sensitivity toggle
        caseToggle.selectedProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));

        //sortBox listener - re-trigger search to apply new sort order
        sortBox.valueProperty().addListener((obs, old, newVal) -> performSearch(searchBar.getText()));

        tableView.setItems(masterData);
        tableView.setPlaceholder(new Label("Use the search bar above to find snippets!"));
    }
    
    //Bring in FXML fields into FilterAndSortController since it's not directly bound to FXML
    private void injectFilterFields() {
        try {
            java.lang.reflect.Field langField = FilterAndSortController.class.getDeclaredField("langFilter");
            langField.setAccessible(true);
            langField.set(filterController, langFilter);
            
            java.lang.reflect.Field tagField = FilterAndSortController.class.getDeclaredField("tagFilter");
            tagField.setAccessible(true);
            tagField.set(filterController, tagFilter);
            
            java.lang.reflect.Field sortField = FilterAndSortController.class.getDeclaredField("sortBox");
            sortField.setAccessible(true);
            sortField.set(filterController, sortBox);
            
            java.lang.reflect.Field createdStartField = FilterAndSortController.class.getDeclaredField("createdStartDate");
            createdStartField.setAccessible(true);
            createdStartField.set(filterController, createdStartDate);
            
            java.lang.reflect.Field createdEndField = FilterAndSortController.class.getDeclaredField("createdEndDate");
            createdEndField.setAccessible(true);
            createdEndField.set(filterController, createdEndDate);
            
            java.lang.reflect.Field modifiedStartField = FilterAndSortController.class.getDeclaredField("modifiedStartDate");
            modifiedStartField.setAccessible(true);
            modifiedStartField.set(filterController, modifiedStartDate);
            
            java.lang.reflect.Field modifiedEndField = FilterAndSortController.class.getDeclaredField("modifiedEndDate");
            modifiedEndField.setAccessible(true);
            modifiedEndField.set(filterController, modifiedEndDate);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize filters: " + e.getMessage());
        }
    }

    //to load all snippets using SearchService
    private void loadAllSnippets() {
        masterData.clear();
        ArrayList<Version> allSnippets = SearchService.getAllSnippets();
        
        for (Version snippet : allSnippets) {
            //get file info to extract filename and creation date
            ArrayList<File> files = SnippetService.getSnippetFiles();
            for (File file : files) {
                ArrayList<Version> versions = SnippetService.getVersions(file.getName());
                if (!versions.isEmpty() && versions.get(versions.size() - 1).equals(snippet)) {
                    Version first = versions.get(0);
                    
                    SnippetItem item = new SnippetItem(snippet.getTitle(), snippet.getLanguage(), parseStringToList(snippet.getTags()), DateTimeUtil.parseTimestamp(first.getTimestamp()), DateTimeUtil.parseTimestamp(snippet.getTimestamp()), file.getName());
                    masterData.add(item);
                    break;
                }
            }
        }
    }

    //perform search using SearchService, then filter, limit, and sort
    private void performSearch(String keyword) {
        masterData.clear();
        
        ArrayList<Version> results;
        if (keyword == null || keyword.trim().isEmpty()) {
            //no search keyword - get all snippets
            results = SearchService.getAllSnippets();
        } else {
            //search with keyword and case sensitivity
            boolean caseInsensitive = !caseToggle.isSelected();
            results = SearchService.searchSnippets(keyword, caseInsensitive);
        }
        
        //step 1: Apply filters using FilterAndSortController
        ArrayList<Version> filteredResults = filterController.applyFilters(results);
        
        //step 2: Convert to SnippetItems for display
        ArrayList<SnippetItem> snippetItems = new ArrayList<>();
        for (Version snippet : filteredResults) {
            //find the file info for each result
            ArrayList<File> files = SnippetService.getSnippetFiles();
            for (File file : files) {
                ArrayList<Version> versions = SnippetService.getVersions(file.getName());
                if (!versions.isEmpty()) {
                    Version latest = versions.get(versions.size() - 1);
                    if (latest.getTitle().equals(snippet.getTitle()) && 
                        latest.getCode().equals(snippet.getCode())) {
                        Version first = versions.get(0);
                        
                        SnippetItem item = new SnippetItem(
                            snippet.getTitle(),
                            snippet.getLanguage(),
                            parseStringToList(snippet.getTags()),
                            DateTimeUtil.parseTimestamp(first.getTimestamp()),
                            DateTimeUtil.parseTimestamp(snippet.getTimestamp()),
                            file.getName()
                        );
                        snippetItems.add(item);
                        break;
                    }
                }
            }
        }
        
        //step 3: Limit to 25 results
        if (snippetItems.size() > 25) {
            snippetItems = new ArrayList<>(FilterandSortService.limitSnippetItemResults(snippetItems));
        }
        masterData.addAll(snippetItems);
        
        //step 4: Apply sorting using FilterAndSortController
        String selectedSort = sortBox.getValue();
        if (selectedSort != null && !selectedSort.isEmpty()) {
            filterController.applySorting(masterData, selectedSort);
        }
    }

    //"Open" button for each row in the Actions column
    private void addActionButtonToTable() {
        colActions.setCellFactory(param -> new TableCell<SnippetItem, Void>() {
            private final Button openBtn = new Button("Open");

            {
                openBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
                openBtn.setOnAction(e -> {
                    SnippetItem item = getTableView().getItems().get(getIndex());
                    openSnippet(item.getFilename());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : openBtn);
            }
        });
    }

    //open the selected snippet in editor for viewing or editing
    private void openSnippet(String filename) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Editor.fxml"));
            Parent root = loader.load();
            
            //pass the filename to editor controller to load the snippet
            SnippetEditorController snippetAdder = loader.getController();
            snippetAdder.loadSnippetFile(filename);
            
            primaryStage = (Stage) tableView.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open snippet: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Home.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @FXML
    public void switchToCollections(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Collections.fxml"));
        primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //helper method to convert comma-separated tag string to List (for easier processing)
    private List<String> parseStringToList(String tagString) {
        if (tagString == null || tagString.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(tagString.split(","));
    }

    //class for use locally in this file to represent a row of snippet in search results display
    public static class SnippetItem {
        private final String name;
        private final String language;
        private final List<String> tags;
        private final LocalDate createdDate;
        private final LocalDate modifiedDate;
        private final String filename;  //stored to load snippet when "Open" is clicked

        public SnippetItem(String name, String language, List<String> tags, LocalDate created, LocalDate modified, String filename) {
            this.name = name;
            this.language = language;
            this.tags = tags;
            this.createdDate = created;
            this.modifiedDate = modified;
            this.filename = filename;
        }

        public String getName() { return name; }
        public String getLanguage() { return language; }
        public List<String> getTags() { return tags; }
        public String getTagsString() { return String.join(", ", tags); }
        public LocalDate getCreatedDate() { return createdDate; }
        public LocalDate getModifiedDate() { return modifiedDate; }
        public String getFilename() { return filename; }
    }
}