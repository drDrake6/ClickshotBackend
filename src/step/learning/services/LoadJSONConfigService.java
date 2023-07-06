package step.learning.services;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadJSONConfigService implements LoadConfigService{

    private final String path = "clickshotConfig/configs.json";
    @Override
    public JSONObject load(){

        File configFile = new File(path);

        if (configFile.isFile()) {
            String content;
            StringBuilder sb;
            try (InputStream reader = Files.newInputStream(Paths.get(configFile.getPath()))) {
                int symbol;
                sb = new StringBuilder();
                while ((symbol = reader.read()) != -1) {
                    sb.append((char) symbol);
                }
                content = new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return null;
            }
            return new JSONObject(content);
        } else {
            System.out.println("FileNotFoundException");
            return null;
        }
    }
}
