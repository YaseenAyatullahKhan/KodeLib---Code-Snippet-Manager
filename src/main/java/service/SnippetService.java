package service;

import java.io.File;
import java.util.ArrayList;

import model.Version;
import util.DateTimeUtil;
import util.JSONUtil;


public class SnippetService {
    //for creating new snippet from GUI - returns new snippet file's name
    public static String createFromGUI(String title, String language, String tags, 
                                       String packages, String description, String code) {
        try {
            File file = JSONUtil.JSONcreator();
            String timestamp = DateTimeUtil.getFormattedDateTimeNow();
            
            ArrayList<Version> versions = new ArrayList<>();
            Version initialVersion = new Version(title, language, tags, packages, description, code, 0, timestamp);
            versions.add(initialVersion);
            
            JSONUtil.writer(file, versions);
            return file.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void update(String filename, Version currentSnippet) {
        //make File object using filename in data/snippets folder
        File file = new File("data/snippets/" + filename);
        
        if (file.exists()) {
            ArrayList<Version> versions = JSONUtil.reader(file);
            if (!versions.isEmpty()) {
                Version lastEntry = versions.get(versions.size() - 1);
                
                //compare new snippet data with last known data
                if (hasChanged(lastEntry, currentSnippet)) {
                    //create new version object with the next version number
                    int nextVersionNumber = versions.size();
                    String timestamp = DateTimeUtil.getFormattedDateTimeNow();
                    Version newVersion = new Version(
                        currentSnippet.getTitle(), 
                        currentSnippet.getLanguage(), 
                        currentSnippet.getTags(), 
                        currentSnippet.getPackages(), 
                        currentSnippet.getDescription(), 
                        currentSnippet.getCode(), 
                        nextVersionNumber, 
                        timestamp
                    );
                    
                    versions.add(newVersion);
                    JSONUtil.writer(file, versions);
                }
            }
        }
    }

    public static void delete(String filename) {
        try {
            File file = new File("data/snippets/" + filename);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //helper methods:

    //to load latest version of snippet for editing
    public static Version getLatestVersion(String filename) {
        try {
            File file = new File("data/snippets/" + filename);
            if (file.exists()) {
                ArrayList<Version> versions = JSONUtil.reader(file);
                if (!versions.isEmpty()) {
                    return versions.get(versions.size() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //to get all versions for specified snippet
    public static ArrayList<Version> getVersions(String filename) {
        try {
            File file = new File("data/snippets/" + filename);
            if (file.exists()) {
                return JSONUtil.reader(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    //to get all snippet files of current user
    public static ArrayList<File> getSnippetFiles() {
        ArrayList<File> snippetFiles = new ArrayList<>();
        File dir = new File("data/snippets/");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    snippetFiles.add(file);
                }
            }
        }
        return snippetFiles;
    }

    public static boolean hasChanged(Version oldV, Version newV) {
        // check every field to see if at least one has changed or not
        return !safeEquals(oldV.getTitle(), newV.getTitle()) ||
               !safeEquals(oldV.getLanguage(), newV.getLanguage()) ||
               !safeEquals(oldV.getTags(), newV.getTags()) ||
               !safeEquals(oldV.getPackages(), newV.getPackages()) ||
               !safeEquals(oldV.getDescription(), newV.getDescription()) ||
               !safeEquals(oldV.getCode(), newV.getCode());
    }
    //small helper method to safely compare two string values
    private static boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}