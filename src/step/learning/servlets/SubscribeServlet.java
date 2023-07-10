package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.SubscribersDAO;
import step.learning.dao.UserDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class SubscribeServlet extends HttpServlet {

    @Inject
    private SubscribersDAO subscribersDAO;

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        if(userDAO.getUserByToken(body.getString("token")) == null)
        {
            res.getWriter().write("2: access denied");
            return;
        }

        String subscriber;
        String author;
        boolean isSubscribing;
        try {
            subscriber = body.getString("subscriber");
            author    = body.getString("author");
            isSubscribing = body.getBoolean("isSubscribing");
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters");
            return;
        }

        if(isSubscribing){
            subscribersDAO.subscribe(author, subscriber);
        }
        else{
            subscribersDAO.unsubscribe(author, subscriber);
        }

        res.getWriter().write("");
    }


}
