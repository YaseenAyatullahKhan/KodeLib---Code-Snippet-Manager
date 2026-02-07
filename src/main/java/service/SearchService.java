package service;

import java.io.File;

import java.util.ArrayList;
import model.Version;
import model.Tag;

public class SearchService {
    
    //search through all snippets based on keyword and case sensitivity toggle
    //searches through title, tags, language, description, code body, packages
    //for tags, also matches nested tag hierarchies (Algorithm/Sorting matches Algorithm/Sorting/QuickSort)
    public static ArrayList<Version> searchSnippets(String keyword, boolean caseInsensitive) {
        ArrayList<Version> searchResults = new ArrayList<>();
        
        //return empty if no keyword provided
        if (keyword == null || keyword.isBlank()) {
            return searchResults;
        }
        
        //get all snippet files
        ArrayList<File> files = SnippetService.getSnippetFiles();
        
        for (File file : files) {
            ArrayList<Version> versions = SnippetService.getVersions(file.getName());
            if (!versions.isEmpty()) {
                //search through latest version only
                Version latest = versions.get(versions.size() - 1);
                //using a helper method to find all correct matches
                if (matchFound(latest, keyword, caseInsensitive)) {
                    searchResults.add(latest);
                }
            }
        }
        return searchResults;
    }
    
    //to check if a version matches the search keyword (with nested tag support)
    private static boolean matchFound(Version snippet, String keyword, boolean caseInsensitive) {
        String searchTerm = caseInsensitive ? keyword.toLowerCase() : keyword;
        
        //snippet attributes to search through
        String title = snippet.getTitle();
        String tags = snippet.getTags();
        String language = snippet.getLanguage();
        String description = snippet.getDescription();
        String code = snippet.getCode();
        String packages = snippet.getPackages();
        
        if (caseInsensitive) {
            //perform case insensitive search
            boolean basicMatch = (title != null && title.toLowerCase().contains(searchTerm)) ||
                   (language != null && language.toLowerCase().contains(searchTerm)) ||
                   (description != null && description.toLowerCase().contains(searchTerm)) ||
                   (code != null && code.toLowerCase().contains(searchTerm)) ||
                   (packages != null && packages.toLowerCase().contains(searchTerm));
            
            //check tags with nested hierarchy support
            boolean tagsMatch = false;
            if (tags != null) {
                tagsMatch = tags.toLowerCase().contains(searchTerm) || matchesNestedTag(tags, searchTerm, caseInsensitive);
            }
            
            return basicMatch || tagsMatch;
        } else {
            //perform case sensitive search
            boolean basicMatch = (title != null && title.contains(searchTerm)) ||
                   (language != null && language.contains(searchTerm)) ||
                   (description != null && description.contains(searchTerm)) ||
                   (code != null && code.contains(searchTerm)) ||
                   (packages != null && packages.contains(searchTerm));
            
            //check tags with nested hierarchy support
            boolean tagsMatch = false;
            if (tags != null) {
                tagsMatch = tags.contains(searchTerm) || matchesNestedTag(tags, searchTerm, caseInsensitive);
            }
            
            return basicMatch || tagsMatch;
        }
    }
    
    //check if any tag in the comma-separated tags string matches the search term using nested tag logic
    //example: searching for "Algorithm/Sorting" should match "Algorithm/Sorting/QuickSort"
    private static boolean matchesNestedTag(String tagsString, String searchTerm, boolean caseInsensitive) {
        if (tagsString == null || tagsString.isBlank()) {
            return false;
        }
        
        //split by comma to get individual tags
        String[] tagArray = tagsString.split(",");
        
        for (String tagStr : tagArray) {
            Tag tag = new Tag(tagStr.trim());
            if (caseInsensitive) {
                //for case insensitive, compare lowercase versions
                if (tag.getTag().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    return true;
                }
            } else {
                //for case sensitive, use the Tag.matches() method directly
                if (tag.matches(searchTerm)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    //get latest version of all snippets
    public static ArrayList<Version> getAllSnippets() {
        ArrayList<Version> allSnippets = new ArrayList<>();
        ArrayList<File> files = SnippetService.getSnippetFiles();
        
        for (File file : files) {
            ArrayList<Version> versions = SnippetService.getVersions(file.getName());
            if (!versions.isEmpty()) {
                //get latest version
                Version latest = versions.get(versions.size() - 1);
                allSnippets.add(latest);
            }
        }
        
        return allSnippets;
    }
}