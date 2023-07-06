package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.CommentDAO;
import step.learning.entities.Comment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class GetCommentByIdServlet extends HttpServlet {

    @Inject
    private CommentDAO commentDAO;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        Comment comment = commentDAO.getCommentById(req.getParameter("commentId"));
        if(comment == null){
            res.getWriter().write("1: internal error");
            return;
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(new JSONObject(comment).toString());
    }
}