package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import model.Version;
import service.FilterandSortService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//controller to handle filtering and sorting operations for search results
//separated from SearchController to keep code organized and maintainable

public class FilterAndSortController {

    @FXML private ComboBox<String> langFilter;
    @FXML private ComboBox<String> tagFilter;
    @FXML private ComboBox<String> sortBox;
    @FXML private DatePicker createdStartDate;
    @FXML private DatePicker createdEndDate;
    @FXML private DatePicker modifiedStartDate;
    @FXML private DatePicker modifiedEndDate;

    private ObservableList<SearchController.SnippetItem> masterData;

    //initialize filter/sort dropdowns with data from loaded snippets
    //called by SearchController after snippets are loaded
    public void initializeFilters(ObservableList<SearchController.SnippetItem> data) {
        this.masterData = data;
        updateLanguageFilter();
        updateTagFilter();
        initializeSortOptions();
    }
    
    //fill up language filter dropdown with unique languages from snippets
    private void updateLanguageFilter() {
        if (langFilter == null || masterData == null) return;
        
        List<String> languages = new ArrayList<>();
        languages.add("All");
        masterData.forEach(item -> {
            if (item.getLanguage() != null && !languages.contains(item.getLanguage())) {
                languages.add(item.getLanguage());
            }
        });
        langFilter.getItems().clear();
        langFilter.getItems().addAll(languages);
        langFilter.getSelectionModel().select("All");
    }

    //fill up tag filter dropdown with unique tags from snippets
    private void updateTagFilter() {
        if (tagFilter == null || masterData == null) return;
        
        List<String> tags = new ArrayList<>();
        tags.add("All");
        masterData.forEach(item -> {
            for (String tag : item.getTags()) {
                if (!tags.contains(tag.trim())) {
                    tags.add(tag.trim());
                }
            }
        });
        tagFilter.getItems().clear();
        tagFilter.getItems().addAll(tags);
        tagFilter.getSelectionModel().select("All");
    }

    //initialize sort dropdown with all sort options per spec:
    //Title (A-Z, Z-A)
    //Language (A-Z, Z-A)
    //Tags (A-Z, Z-A)
    //Created (Oldest, Newest)
    //Modified (Oldest, Newest)
    private void initializeSortOptions() {
        if (sortBox == null) return;
        
        sortBox.setItems(FXCollections.observableArrayList(
            "Title A–Z",
            "Title Z–A",
            "Language A–Z",
            "Language Z–A",
            "Tags A–Z",
            "Tags Z–A",
            "Created: Oldest First",
            "Created: Newest First",
            "Modified: Oldest First",
            "Modified: Newest First"
        ));
    }

    //sorting the SnippetItem list based on the selected sort option (called after filtering and limiting to 25 results)
    public void applySorting(ObservableList<SearchController.SnippetItem> items, String sortOption) {
        if (sortOption == null || sortOption.isEmpty() || items == null || items.isEmpty()) {
            return;
        }

        Comparator<SearchController.SnippetItem> comparator = null;

        switch (sortOption) {
            case "Title A–Z":
                comparator = Comparator.comparing(SearchController.SnippetItem::getName, 
                    String.CASE_INSENSITIVE_ORDER);
                break;
            case "Title Z–A":
                comparator = Comparator.comparing(SearchController.SnippetItem::getName, 
                    String.CASE_INSENSITIVE_ORDER).reversed();
                break;
            case "Language A–Z":
                comparator = Comparator.comparing(SearchController.SnippetItem::getLanguage, 
                    String.CASE_INSENSITIVE_ORDER);
                break;
            case "Language Z–A":
                comparator = Comparator.comparing(SearchController.SnippetItem::getLanguage, 
                    String.CASE_INSENSITIVE_ORDER).reversed();
                break;
            case "Tags A–Z":
                comparator = Comparator.comparing(SearchController.SnippetItem::getTagsString, 
                    String.CASE_INSENSITIVE_ORDER);
                break;
            case "Tags Z–A":
                comparator = Comparator.comparing(SearchController.SnippetItem::getTagsString, 
                    String.CASE_INSENSITIVE_ORDER).reversed();
                break;
            case "Created: Oldest First":
                comparator = Comparator.comparing(SearchController.SnippetItem::getCreatedDate);
                break;
            case "Created: Newest First":
                comparator = Comparator.comparing(SearchController.SnippetItem::getCreatedDate).reversed();
                break;
            case "Modified: Oldest First":
                comparator = Comparator.comparing(SearchController.SnippetItem::getModifiedDate);
                break;
            case "Modified: Newest First":
                comparator = Comparator.comparing(SearchController.SnippetItem::getModifiedDate).reversed();
                break;
        }

        if (comparator != null) {
            FXCollections.sort(items, comparator);
        }
    }

    //apply filters to the search results based on current filter values
    public ArrayList<Version> applyFilters(ArrayList<Version> searchResults) {
        //get filter values
        String selectedLang = (langFilter != null && langFilter.getValue() != null) 
            ? langFilter.getValue() : "All";
        String selectedTag = (tagFilter != null && tagFilter.getValue() != null) 
            ? tagFilter.getValue() : "All";
        
        LocalDate createdStart = (createdStartDate != null) ? createdStartDate.getValue() : null;
        LocalDate createdEnd = (createdEndDate != null) ? createdEndDate.getValue() : null;
        LocalDate modifiedStart = (modifiedStartDate != null) ? modifiedStartDate.getValue() : null;
        LocalDate modifiedEnd = (modifiedEndDate != null) ? modifiedEndDate.getValue() : null;

        //apply filters using filter and sort service
        return FilterandSortService.applyFilters(
            searchResults,
            selectedLang,
            selectedTag,
            createdStart,
            createdEnd,
            modifiedStart,
            modifiedEnd
        );
    }

    //get all filter/sort field references for setting up the listeners in SearchController
    public ComboBox<String> getLangFilter() { return langFilter; }
    public ComboBox<String> getTagFilter() { return tagFilter; }
    public ComboBox<String> getSortBox() { return sortBox; }
    public DatePicker getCreatedStartDate() { return createdStartDate; }
    public DatePicker getCreatedEndDate() { return createdEndDate; }
    public DatePicker getModifiedStartDate() { return modifiedStartDate; }
    public DatePicker getModifiedEndDate() { return modifiedEndDate; }
}
