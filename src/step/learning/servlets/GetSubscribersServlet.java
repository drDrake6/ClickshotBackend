package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.SubscribersDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class GetSubscribersServlet extends HttpServlet {
    @Inject
    private SubscribersDAO subscribersDAO;

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);
        String login = body.getString("login");
        int from = Integer.parseInt(body.getString("from"));
        int amount = Integer.parseInt(body.getString("amount"));

        List<User> subscriber;

        try {
            subscriber = subscribersDAO.getSubscribers(login, from, amount, false);
        }catch (Exception ex){
            res.getWriter().write("1: " + ex.getMessage());
            return;
        }

        JSONArray jaUsers = new JSONArray();
        for (int i = 0; i < subscriber.size(); i++) {
            jaUsers.put(i, userDAO.getPublicUserInfo(subscriber.get(i).getLogin()));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaUsers.toString());
    }
}
