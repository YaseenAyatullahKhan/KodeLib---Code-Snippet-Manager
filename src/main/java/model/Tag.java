package model;

public class Tag {
    private String tag;
//Constructor 
    public Tag(String tag){
        this.tag=tag.trim();
    }
public String getTag(){
    return tag;
}
// check if search result match with the tag present
public boolean matches(String tagSearch){
    return this.tag.startsWith(tagSearch);
}
public String getParentTag(){
    int endIndex=tag.lastIndexOf("/");
    if(endIndex == -1){
        return null;
    }
    return tag.substring(0, endIndex);
}
}
