package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.CommentDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.Comment;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class ChangeCommentServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private CommentDAO commentDAO;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);
        String token = body.getString("token");
        Comment comment = null;
        try {
            comment = new Comment(body.getJSONObject("comment"));
        } catch (Exception ex) {
            res.getWriter().write("1: internal error, " + ex.getMessage());
            return;
        }

        User user = userDAO.getUserByToken(token);
        if(user == null){
            res.getWriter().write("2: access denied");
            return;
        }

        Boolean has = commentDAO.authorHasComment(comment.getId(), user.getLogin());

        if(has == null){
            res.getWriter().write("1: internal error");
            return;
        }
        else if (!has){
            res.getWriter().write("2: access denied");
            return;
        }

        commentDAO.updateComment(comment);
        res.getWriter().write("0: successfully changed");
    }
}
