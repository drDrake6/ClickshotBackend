package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class ChangeUserServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        String token = body.getString("token");
        User user = userDAO.getUserByToken(token);

        if(user == null){
            res.getWriter().write("2: access denied");
            return;
        }

        try {
            User.updateUser(user, body.getJSONObject("user"));
        }
        catch (Exception ex){
            res.getWriter().write("1: internal error" + ex.getClass().getName() +  ex.getMessage());
            return;
        }


        try {
            User otherUser = userDAO.getUser(user.getLogin());
            if(otherUser != null && !(otherUser.getToken().equals(token))){
                res.getWriter().write("4: user with such login exists");
                return;
            }
        } catch (JSONException ex) {
            res.getWriter().write(ex.getMessage());
            return;
        }

        userDAO.update(user, user.getId());
        res.getWriter().write("0: profile updated successfully");
    }
}
