package DiscordBot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import org.json.JSONObject;

import java.io.*;

public class Configuration {

    public static String get(String key) {
        StackTraceElement[] str = Thread.currentThread().getStackTrace();
        StackTraceElement stack = str[str.length-1];
        String requester = stack.getFileName()+":"+stack.getLineNumber();
        System.out.println("[ENV] Retrieving ("+key+") for ("+requester+")");
        if (System.getenv(key.toUpperCase()) != null) {
            System.out.println("[ENV] Success");
            return System.getenv(key.toUpperCase());
        } else {
            System.out.println("[ENV] System var: ("+key+") not found. Attempting .env");
            String d = null;
            try {
                Dotenv dotenv = Dotenv.load();
                d = dotenv.get(key.toUpperCase());
                System.out.println("[ENV] Success");
            } catch (Exception e) {
                System.out.println("[ENV] Failed. Returning blank");
                return "";
            }
            return d;
        }
    }



    public static void setJsonConfig(JSONObject updatedJsonConfig) {
        String fileLoc = "jsonData.json";
//      JSONObject class creates a json object

        JSONObject obj= updatedJsonConfig;
//      provides a put function to insert the details into json object

        try{

//          File Writer creates a file in write mode at the given location

            FileWriter file = new FileWriter(fileLoc);

//          Here we convert the obj data to string and put/write it inside the json file

            file.write(obj.toString());
            file.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static JSONObject getJsonConfig(JDA jda) {
        String fileLoc = "jsonData.json";
        File file = new File(fileLoc);
        if (!file.exists()) {
            final JSONObject[] json = {new JSONObject("{}")};
            setJsonConfig(json[0]);
            return json[0];
        }

        BufferedReader reader;
        String StringJson = "";
        try {
            reader = new BufferedReader(new FileReader(fileLoc));
            String line = reader.readLine();
            while (line != null) {
                StringJson += line+"\n";
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject(StringJson);
        return json;
    }


}
