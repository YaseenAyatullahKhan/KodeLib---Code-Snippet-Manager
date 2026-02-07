package model;

public class Version {
    private String title;
    private String language;
    private String tags;
    private String packages;
    private String description;
    private String code;
    private int version;
    private String timestamp;
    public Version(){}
    public Version(String Title, String Language,String Tags, String Packages, String Description,String code,int version,String timestamp){
            this.title = Title;
            this.language = Language;
            this.tags = Tags;
            this.packages = Packages;
            this.description = Description;
            this.code = code;
            this.version=version;
            this.timestamp=timestamp;
    }
    public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }

        public String getPackages() { return packages; }
        public void setPackages(String packages) { this.packages = packages; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
