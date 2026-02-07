package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import model.Version;


public class JSONUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static ArrayList<Version> reader(File file){
        try{
            if (file.exists() && file.length() > 0) {
                return mapper.readValue(file, new TypeReference<ArrayList<Version>>() {});
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //for writing snippets to specified JSON file
    public static void writer(File file, ArrayList<Version> snippets){
        try{
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, snippets);
        }catch(Exception e){
        e.printStackTrace();
        }
    }
    
    //to create a new JSON file
    public static File JSONcreator(){
        try{
            String directory = "data/snippets/";
            int snipcounter = 1;
            File file;
            while (true) {
                String fileName = directory + "snippet" + snipcounter + ".json";
                file = new File(fileName);
                if (!file.exists()) {
                    break; 
                }
                snipcounter++;
            }
            return file;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
