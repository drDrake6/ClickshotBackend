package step.learning.services;

import org.json.JSONObject;

import java.io.FileNotFoundException;

public interface LoadConfigService {
    JSONObject load(String realPath) throws FileNotFoundException;
}
