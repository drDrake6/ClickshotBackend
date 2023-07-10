package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.CommentDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.Comment;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class AddCommentServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;
    @Inject
    private CommentDAO commentDAO;

    @Inject
    private UserDAO userDAO;
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        try {
            JSONObject body = bodyParseService.parseBody(req);
            if(userDAO.getUserByToken(body.getString("token")) == null)
            {
                res.getWriter().write("2: access denied");
                return;
            }
            Comment comment = new Comment(body);
            commentDAO.makeComment(comment);
        }
        catch (Exception ex){
            res.getWriter().write("1: internal error, " + ex.getMessage());
            return;
        }
        res.getWriter().write("0: comment added successfully");
    }
}
