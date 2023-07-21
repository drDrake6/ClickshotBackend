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
import java.util.List;

@Singleton
public class CommentsServlet extends HttpServlet {
    @Inject
    private CommentDAO commentDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin","*");
        int amount = Integer.parseInt(req.getParameter("amount"));
        int from = Integer.parseInt(req.getParameter("from"));
        String postId = req.getParameter("postId");

        List<Comment> comments = commentDAO.getSomeCommentsByPost(postId, from, amount);

        JSONArray jcomments = new JSONArray();
        for (Comment comment : comments) {
            if(commentDAO.isAnswer(comment.getId())){
                jcomments.put(
                        new JSONObject(
                                new Response(
                                        commentDAO.getCommentIdOfAnswer(comment.getId()),
                                        comment)
                        )
                );
            }
            else
                jcomments.put(new JSONObject(comment));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jcomments.toString());
    }
}
