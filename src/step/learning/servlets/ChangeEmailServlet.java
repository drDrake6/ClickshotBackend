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
public class ChangeEmailServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);

        User user = null;
        String code;
        try {
            user = userDAO.getUserByToken(body.getString("token"));
            code = body.getString("code");
            if(user == null){
                throw  new Exception("2: incorrect token");
            }
            if(!code.equals(user.getEmail_code().substring(0, 6))){
                user.setEmail_code(null);
                throw  new Exception("1: incorrect code");
            }

        }catch (Exception ex){
            res.getWriter().write("5: " + ex.getMessage());
            return;
        }

        String newEmail = "";

        try {
            newEmail = body.getString("newEmail");
        }catch (JSONException ex){
            res.getWriter().write("5: " + ex.getMessage());
            return;
        }

        if(newEmail.equals("")) {
            res.getWriter().write("3: Email can not be empty");
        }

        else{
            if(user.getEmail().equals(newEmail)){
                res.getWriter().write("4: Email can not match previous one");
                return;
            }

            user.setEmail(newEmail);
            userDAO.update(user, user.getId());
            res.getWriter().write("0: Email is changed successfully");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {

        String token = req.getParameter("token");
        String code = req.getParameter("code");

        User user = userDAO.getUserByToken(token);

        if(code.equals(user.getEmail_code())){
            user.setEmail_code(user.getEmail_code() + "_allowed");
            userDAO.update(user, user.getId());
            res.getWriter().write("0: access allowed");
        }
        else{
            res.getWriter().write("1: access denied");
        }

        //res.sendRedirect(req.getRequestURI());
    }
}
