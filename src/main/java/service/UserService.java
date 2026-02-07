package service;

import java.util.Optional;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

import java.io.File;

public class UserService {
    private static final String FILENAME = "data/users.json";
    private final ObjectMapper mapper=new ObjectMapper();
    //load users from the JSON file
    public ArrayList<User> loadUsers() {
        try {
            File file = new File(FILENAME);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            
            return mapper.readValue(file, new TypeReference<ArrayList<User>>() {});
        } catch (Exception e) {
            return new ArrayList<>(); 
        }
    }

    //save the list back to the JSON file
    private void saveUsers(ArrayList<User> users) {
        try {
            mapper.writeValue(new File(FILENAME), users);
        } catch (Exception e) {
            System.err.println("Could not save: " + e.getMessage());
        }
    }

    //register a new user
    public boolean registerUser(String username, String password) {
        ArrayList<User> users = loadUsers();

        //check if username is already taken
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return false;
        }


        if (!ValidationUtil.isValidPassword(password)) return false;


        String salt = PasswordUtil.saltHasher();
        String hash = PasswordUtil.hashPassword(password, salt);

        users.add(new User(username, hash, salt));
        saveUsers(users);
        return true;
    }

    //login authentication using optional class so it can return empty if not found
    public Optional<User> authenticate(String username, String password) {
        ArrayList<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {

                if (PasswordUtil.checkPassword(password, user.getPasswordHash(), user.getSalt())) {
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}
