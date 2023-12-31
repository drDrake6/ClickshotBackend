package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.SubscribersDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class IsSubscribedServlet extends HttpServlet {
    @Inject
    private SubscribersDAO subscribersDAO;

    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        String login = body.getString("login");
        String author = body.getString("author");

        if(subscribersDAO.isSubscribedTo(author, login, false)){
            res.getWriter().write("true");
        }
        else{
            res.getWriter().write("false");
        }
    }
}
