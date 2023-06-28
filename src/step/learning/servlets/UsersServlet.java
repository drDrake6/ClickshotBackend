package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.LoadConfigService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class UsersServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;

    @Inject
    private LoadConfigService loadConfigService;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        int amount;
        int from;

        try {
            amount = Integer.parseInt(req.getParameter("amount"));
            from = Integer.parseInt(req.getParameter("from"));
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters");
            return;
        }

        List<User> users = userDAO.getSomeUsers(from, amount);
        JSONArray jaUsers = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            jaUsers.put(i, users.get(i).ToViewJSON());
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaUsers.toString());
    }
}
