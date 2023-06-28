package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.util.Pair;
import org.json.JSONException;
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
import java.util.UUID;

@Singleton
public class ConfirmEmailServlet extends HttpServlet {

    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        JSONObject body = bodyParseService.parseBody(req);
        User user = null;
        String code = "";
        try {
            user = userDAO.getUser(body.getString("login"));
        }
        catch (JSONException ex){
            res.getWriter().write("2: " + ex.getClass().getName() + "\n" + ex.getMessage());
            return;
        }
        if(user.getEmail_code() != null
                && !user.getEmail_code().equals("not_confirmed")
                && body.isNull("newEmail"))
        {
            res.getWriter().write( "1: code has been sent already, please, try later");
            return;
        }
        code = UUID.randomUUID().toString().substring(0, 6);
        if(!userDAO.setEmailCode(user, code)){
            res.getWriter().write( "2: error accused during setting code");
            return;
        }

        if(!body.isNull("newEmail") && !body.isNull("token")){
            String newEmail = body.getString("newEmail");
            userDAO.sendConfirmCode(newEmail, "token", body.getString("token"), code, "/changeEmail");
            res.getWriter().write("0: code is being sent");
        }
        else{
            userDAO.sendConfirmCode(user.getEmail(), "login", user.getLogin(), code, "/restore");
            res.getWriter().write("0: code is being sent");
        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String login = req.getParameter("login");

        User user = userDAO.getUser(login);

        try {
            if((user.getEmail_code().substring(6)).equals("_allowed"))
                res.getWriter().write("0: " + user.getEmail_code().substring(0, 6));
            else
                res.getWriter().write("1: false");
        }catch (Exception ex){
            res.getWriter().write("2: false " + ex.getMessage());
        }
    }
}
