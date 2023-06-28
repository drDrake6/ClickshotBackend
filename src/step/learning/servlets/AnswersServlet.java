package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.CommentDAO;
import step.learning.entities.Comment;
import step.learning.entities.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AnswersServlet extends HttpServlet {
    @Inject
    private CommentDAO commentDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String commentId = req.getParameter("commentId");
        int amount = Integer.parseInt(req.getParameter("amount"));
        int from = Integer.parseInt(req.getParameter("from"));

        List<Response> responses = new ArrayList<>(); //commentDAO.getSomeAnswers(commentId, from, amount);

        JSONArray jcomments = new JSONArray();
        for (Response response : responses) {
            jcomments.put(new JSONObject(response));
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jcomments.toString());
    }
}
