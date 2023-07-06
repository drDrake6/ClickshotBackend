package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

@Singleton
public class AddUserServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject bodyUser = bodyParseService.parseBody(req);

        try {
            bodyUser.put("birthday", bodyUser.getString("birthday") + " 00:00:00");
        }
        catch (Exception ex){
            res.getWriter().write("2: birthday can not be empty");
            return;
        }

        User user = null;

        try {
            user = new User(bodyUser);
        }
        catch (Exception ex){
            res.getWriter().write(ex.getMessage());
            return;
        }

        try {
            if(userDAO.getUser(user.getLogin()) != null){
                res.getWriter().write("1: user with such login exists");
                return;
            }
        } catch (JSONException ex) {
            res.getWriter().write(ex.getMessage());
            return;
        }

        user.setAvatar("user_icon_without_photo.PNG");

        try {
            user.setToken(UUID.randomUUID().toString());
            user.setEmail_code("not_confirmed");
            userDAO.add(user);
        }
        catch (Exception ex){
            res.getWriter().write(ex.getMessage());
            return;
        }

        res.getWriter().write("0: " + user.getToken());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {
        res.sendRedirect(req.getRequestURI());
    }
}
