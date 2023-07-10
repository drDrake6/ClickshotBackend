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
public class RestorePasswordServlet extends HttpServlet {

    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        User user = null;
        String code;
        try {
            user = userDAO.getUser(body.getString("login"));
            code = body.getString("code");
            if(user == null){
                throw  new Exception("2: incorrect login");
            }
            if(!code.equals(user.getEmail_code().substring(0, 6)))
                throw  new Exception("1: incorrect code");
        }catch (Exception ex){
            res.getWriter().write("6: " + ex.getMessage());
            return;
        }

        String newPassword = "";
        String repeatPassword = "";

        try {
            newPassword = body.getString("newPassword");
            repeatPassword = body.getString("repeatPassword");
        }catch (JSONException ex){
            res.getWriter().write("6: " + ex.getMessage());
            return;
        }

        if(newPassword.equals("")) {
            res.getWriter().write("3: password can not be empty");
            return;
        }

        if(!newPassword.equals(repeatPassword)){
            res.getWriter().write("4: passwords are not equal");
        }
        else{
            if(userDAO.isPrevPassword(user, newPassword)){
                res.getWriter().write("5: passwords can not match previous one");
                return;
            }

            userDAO.makePassword(user, newPassword);
            user.setEmail_code(null);
            userDAO.update(user, user.getId());
            res.getWriter().write("0: password is changed successfully");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        String login = req.getParameter("login");
        String code = req.getParameter("code");

        User user = userDAO.getUser(login);

        if(user.getEmail_attempt() >= 6) {
            res.getWriter().write("2: access denied, attempts is out, ask admins of clickshot");
            return;
        }

        if(code.equals(user.getEmail_code())){
            user.setEmail_code(user.getEmail_code() + "_allowed");
            user.setEmail_attempt(0);
            userDAO.update(user, user.getId());
            res.getWriter().write("0: access allowed");
        }
        else{
            if(user.getEmail_attempt() < 6){
                user.setEmail_attempt(user.getEmail_attempt() + 1);
                userDAO.update(user, user.getId());
            }
            res.getWriter().write("1: access denied, attempts is rest: " + (6 - user.getEmail_attempt()));
        }

        //res.sendRedirect(req.getRequestURI());
    }
}
