package DiscordBot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.json.JSONObject;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {

   private static final Dotenv dotenv = Dotenv.load();


    public static String get(String key) {
        String d = dotenv.get(key.toUpperCase());
        return d;
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
