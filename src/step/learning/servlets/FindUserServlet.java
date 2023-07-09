package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;
import step.learning.services.LoadConfigService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Singleton
public class FindUserServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private LoadConfigService loadConfigService;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        JSONObject body = bodyParseService.parseBody(req);

        int amount;
        int from;
        JSONObject params;
        try {
            amount = Integer.parseInt(body.getString("amount"));
            from = Integer.parseInt(body.getString("from"));
            params = body.getJSONObject("params");
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters " + ex.getMessage());
            return;
        }


        try{
            if(!params.isNull("birthday") &&
                    Timestamp.valueOf(params.getJSONObject("birthday").getString("from") + " 00:00:00")
                    .after(Timestamp.valueOf(params.getJSONObject("birthday").getString("to") + " 00:00:00"))){
                String tmp = params.getJSONObject("birthday").getString("from");
                JSONObject addDate = params.getJSONObject("birthday");
                addDate.put("from", addDate.getString("to"));
                addDate.put("to", tmp);
                params.put("birthday", addDate);
            }
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters " + ex.getMessage());
            return;
        }

        List<User> users = userDAO.findSomeUsers(from, amount, params);
        JSONArray jUsers = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            jUsers.put(i, userDAO.getPublicUserInfo(users.get(i).getLogin()));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jUsers.toString());
    }
}
