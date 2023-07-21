package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.SavesDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class GetSavedPostsServlet extends HttpServlet {

    @Inject
    private PostDAO postDAO;

    @Inject
    private SavesDAO savesDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin","*");
        int amount = Integer.parseInt(req.getParameter("amount"));
        int from = Integer.parseInt(req.getParameter("from"));
        String login = req.getParameter("login");

        List<String> postIds = savesDAO.getSaves(from, amount, login);

        JSONArray jposts = new JSONArray();
        for (String postId : postIds) {
            jposts.put(new JSONObject(postDAO.getPostByID(postId)));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jposts.toString());
    }
}
