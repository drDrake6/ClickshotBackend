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
import java.util.Scanner;
import java.util.UUID;

@Singleton
public class AuthorizeServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject credentialsObj = bodyParseService.parseBody(req);
        String login = "";
        String password = "";
        try {
            login = credentialsObj.getString("login");
            password  = credentialsObj.getString("password");
            System.out.println(login);
        } catch (JSONException ex) {
            res.getWriter().write("3: " + ex.getClass().getName() + "\n" + ex.getMessage());
            return;
        }

        User user = userDAO.getUser(login);
        if(user != null
                && userDAO.CheckCredentials(user, password)
                && user.getRole() != 'b'){
            if(user.getEmail_code() != null && !user.getEmail_code().equals("not_confirmed"))
                user.setEmail_code(null);
            user.setEmail_attempt(0);
            user.setToken(UUID.randomUUID().toString());
            userDAO.update(user, user.getId());
            res.getWriter().write("0: " + user.getToken());
            System.out.println("access allowed");
        }
        else{
            res.getWriter().write( "1: access denied");
            System.out.println("access denied");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {
        String login = req.getParameter("login");
        User user = userDAO.getUser(login);
        if(user != null){
            res.getWriter().write("exists");
        }
        else{
            res.getWriter().write("");
        }
    }
}
