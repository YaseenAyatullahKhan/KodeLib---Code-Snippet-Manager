package model;

public class User {
    private String username;
    private String passwordHash;
    private String salt;
    //default constructor
    public User(){}
    //parameterized constructor
    public User(String username, String passwordHash,String salt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    //getters for username and password
    public String getUsername() {
        return username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public String getSalt() {
        return salt;
    }

    //setters for username and password
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
}