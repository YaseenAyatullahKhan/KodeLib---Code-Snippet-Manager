package service;

import java.io.File;
import java.util.ArrayList;

import model.Version;
import util.JSONUtil;


public class VersionService {
    
    //revert to previous version by removing the latest version
    //returns true if successful, false if only one version exists or error occurs
    public static boolean revert(File file) {
        try {
            ArrayList<Version> versions = JSONUtil.reader(file);
            int size = versions.size();
            
            if (size <= 1) {
                return false;
            }
            
            versions.remove(versions.size() - 1);
            JSONUtil.writer(file, versions);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //compare two specific versions and return comparison result
    //returns a string with formatted comparison data, or null if invalid versions or insufficient data
    public static String compareVersions(File file, int versionNum1, int versionNum2) {
        try {
            if (!file.exists() || file.length() == 0) {
                return null;
            }

            ArrayList<Version> versions = JSONUtil.reader(file);

            if (versions.size() < 2) {
                return null;
            }

            Version v1 = null;
            Version v2 = null;
            
            //find the two versions to compare
            for (Version v : versions) {
                if (v.getVersion() == versionNum1) {
                    v1 = v;
                }
                if (v.getVersion() == versionNum2) {
                    v2 = v;
                }
            }

            if (v1 == null || v2 == null) {
                return null;
            }

            //format both versions for comparison
            String first = formatVersionData(v1);
            String second = formatVersionData(v2);

            return "--- Version " + v1.getVersion() + " ---\n" + first + 
                   "\n--- Version " + v2.getVersion() + " ---\n" + second;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //helper method to format version data as string
    private static String formatVersionData(Version v) {
        return "version:" + v.getVersion() + "\n"
                + "title:" + v.getTitle() + "\n"
                + "language:" + v.getLanguage() + "\n"
                + "tags:" + v.getTags() + "\n"
                + "packages:" + v.getPackages() + "\n"
                + "code body:" + v.getCode() + "\n"
                + "timestamp:" + v.getTimestamp() + "\n";
    }
    
    //get all versions for a file (returns list for viewing version history)
    public static ArrayList<Version> getAllVersions(File file) {
        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
                //returning empty arraylist
            }
            return JSONUtil.reader(file);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
            //returning empty arraylist
        }
    }
}