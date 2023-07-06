package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.SavesDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class SaveServlet extends HttpServlet {

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private SavesDAO savesDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);

        String postId = body.getString("postId");
        String login = body.getString("login");
        boolean isSaved = body.getBoolean("isSaved");

        if(isSaved){
            savesDAO.addSave(postId, login);
        }
        else{
            savesDAO.unSave(postId, login);
        }

        res.getWriter().write("");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String postId = req.getParameter("postId");
        String login = req.getParameter("login");

        JSONObject answer = new JSONObject();
        answer.put("isSaved", savesDAO.isSavedByUser(postId, login, false));

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(answer.toString());
    }
}
