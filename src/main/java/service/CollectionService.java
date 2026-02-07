package service;

import model.Version;
import model.CollectionGroup;
import java.util.ArrayList;

public class CollectionService {

    private ArrayList<Version> initialList;
    private ArrayList<Version> destinationList;
    private ArrayList<CollectionGroup> collections;

    //initialize the service with empty lists for collections and backlinks
    public CollectionService() {
        collections = new ArrayList<>();
        initialList = new ArrayList<>();      
        destinationList = new ArrayList<>();  
    }

    //to allow creation of backlink relationship between two snippet versions
    public void backlinkSnippets(Version initial, Version destination) {
        initialList.add(initial);
        destinationList.add(destination);
    }

    //retrieve destination snippet that is backlinked to the given initial snippet
    public Version getDestinationSnippet(Version initial) {
        for (int i = 0; i < initialList.size(); i++) {
            if (initialList.get(i).equals(initial)) {
                return destinationList.get(i);
            }
        }
        //return null if no backlink exists for the initial snippet
        return null; 
    }

    //for creating new collection group with the given name to organize snippets
    public void groupSnippetsByCollection(String name) {
        collections.add(new CollectionGroup(name));
    }

    //to assign a snippet to a specific collection and remove it from all other collections, so one snippet is only in one collection at a time
    public void assignSnippetToOneCollection(Version snippet, String collectionName) {
        for (CollectionGroup c : collections) {
            //remove snippet from all collections first
            c.removeSnippet(snippet);
            if (c.getCollectionName().equals(collectionName)) {
                //now add to target collection
                c.addSnippet(snippet);
            }
        }
    }
}
