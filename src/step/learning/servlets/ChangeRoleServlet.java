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

        if(body.isNull("token")){
            res.getWriter().write("1: access denied");
            return;
        }

        String token = body.getString("token");
        String login = body.getString("login");
        char role = body.getString("role").charAt(0);



        if(role != 'b' && role != 'u')
        {
            res.getWriter().write("2: unknown role");
            return;
        }

        User admin = userDAO.getUserByToken(token);

        if(admin == null){
            res.getWriter().write("1: access denied");
            return;
        }

        if(admin.getRole() == 'a')
        {
            User user = userDAO.getUser(login);
            user.setRole(role);
            if(role == 'b')
                user.setToken(null);
            userDAO.update(user, user.getId());

            res.getWriter().write("0: role successfully changed");
        }
        else
            res.getWriter().write("1: access denied");
    }
}
