package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class IsLoginFreeServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);

        String login = body.getString("login");

        if(userDAO.getUser(login) == null){
            res.getWriter().write("true");
        }
        else{
            res.getWriter().write("false");
        }
    }
}
