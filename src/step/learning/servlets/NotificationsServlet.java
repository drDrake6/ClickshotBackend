package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import step.learning.dao.NotificationDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class NotificationsServlet extends HttpServlet {
    @Inject
    private NotificationDAO notificationDAO;

    @Inject
    private BodyParseService bodyParseService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int amount = Integer.parseInt(req.getParameter("amount"));
        int from = Integer.parseInt(req.getParameter("from"));
        String login = req.getParameter("login");

        JSONArray jsonArray = notificationDAO.getSomeNotifications(login, from, amount);

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jsonArray.toString());
    }
}
