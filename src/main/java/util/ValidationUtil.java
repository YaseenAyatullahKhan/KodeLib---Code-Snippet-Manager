package util;

public class ValidationUtil {
   public static boolean isValidPassword(String password){
    if (password==null || password.length()<8){
        return false;
    }
    boolean hasUppercase=false;
    boolean hasLowercase=false;
    boolean hasDigit=false;
    boolean hasSpecialChar=false;   
    String specialChars="!@#$%^&*()-+";

    //check each character
    for(char c:password.toCharArray()){
        if(Character.isUpperCase(c)){
            hasUppercase=true;
        }else if(Character.isLowerCase(c)){
            hasLowercase=true;
        }else if(Character.isDigit(c)){
            hasDigit=true;
        }else if(specialChars.contains(String.valueOf(c))){
            hasSpecialChar=true;
        }
    }
    if (hasDigit && hasLowercase && hasUppercase && hasSpecialChar){
        return true;
    }
    return false;
    }
}