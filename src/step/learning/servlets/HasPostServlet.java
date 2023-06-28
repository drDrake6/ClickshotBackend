package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.PostDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class HasPostServlet extends HttpServlet {

    @Inject
    private PostDAO postDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String login = req.getParameter("login");
        String postId = req.getParameter("postId");
        Boolean has = postDAO.authorHasPost(postId, login);
        if(has != null){
            if(has) res.getWriter().write("true");
            else res.getWriter().write("false");
        }
    }

}
