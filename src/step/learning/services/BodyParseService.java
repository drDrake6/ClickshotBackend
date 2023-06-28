package step.learning.services;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BodyParseService {
    public JSONObject parseBody (HttpServletRequest req) throws IOException;
}
