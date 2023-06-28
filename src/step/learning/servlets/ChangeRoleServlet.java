package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
public class ChangeRoleServlet extends HttpServlet {

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);

        String adminLogin = body.getString("adminLogin");
        String login = body.getString("login");
        char role = body.getString("role").charAt(0);

        if(role != 'b' && role != 'u')
        {
            res.getWriter().write("2: unknown role");
            return;
        }

        if(userDAO.isAdmin(adminLogin))
        {
            User user = userDAO.getUser(login);
            user.setRole(role);
            userDAO.update(user, user.getLogin());
            res.getWriter().write("0: role successfully changed");
        }
        else
            res.getWriter().write("1: access denied");
    }
}
