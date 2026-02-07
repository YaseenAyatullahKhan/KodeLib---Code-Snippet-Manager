package util;

import java.util.Base64;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtil {

    //to create a Salt
    public static String saltHasher() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    //to hash a password with a given salt using SHA-256 algorithm
    public static String hashPassword(String password, String salt) {
        try {
            String hashedPassword = password + salt;

            MessageDigest locker = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = locker.digest(hashedPassword.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes);
            
        } catch (Exception e) {
            return null; 
        }
    }

    public static boolean checkPassword(String passwordInput, String savedHash, String savedSalt) {
        String combinedPassword = hashPassword(passwordInput, savedSalt);
        return combinedPassword.equals(savedHash);
    }
}
