package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.entities.Post;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class PostsServlet extends HttpServlet {
    @Inject
    private PostDAO postDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin","*");
        int amount;
        int from;

        try {
            amount = Integer.parseInt(req.getParameter("amount"));
            from = Integer.parseInt(req.getParameter("from"));
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters");
            return;
        }


        List<Post> posts = postDAO.getSomePosts(from, amount);

        JSONArray jposts = new JSONArray();
        for (Post post : posts) {
            jposts.put(new JSONObject(post));
        }

        res.setContentType("application/json");
        res.setHeader("Hello","World");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jposts.toString());
    }
}
