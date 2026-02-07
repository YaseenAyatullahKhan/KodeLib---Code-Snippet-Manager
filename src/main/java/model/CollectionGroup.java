package model;
import java.util.ArrayList;

public class CollectionGroup {
    private String collectionName;
    private ArrayList<Version> snippets; 

    public CollectionGroup(String collectionName){
        this.collectionName = collectionName;
        this.snippets = new ArrayList<>();
    }

    public String getCollectionName(){
        return collectionName;
    }

    public ArrayList<Version> getSnippets(){
        return snippets;
    }

    public void addSnippet(Version snippet){ 
        if (!this.snippets.contains(snippet))
            this.snippets.add(snippet);
    }

    public void removeSnippet(Version snippet){
        this.snippets.remove(snippet);
    }
}