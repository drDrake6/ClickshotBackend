package step.learning.services;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Scanner;

public class BodyParseJSONService implements BodyParseService{
    @Override
    public JSONObject parseBody(HttpServletRequest req) throws IOException {
        Scanner s = new Scanner(req.getInputStream(), "UTF-8").useDelimiter("\\A");
        String bodyText = s.hasNext() ? s.next() : null;
        if(bodyText == null) {
            System.out.println("AddUserServlet::doPost | body was null");
            //res.getWriter().write("body was null");
            return null;
        }
        JSONObject bodyObj = new JSONObject(bodyText);
        return bodyObj;
    }
}
