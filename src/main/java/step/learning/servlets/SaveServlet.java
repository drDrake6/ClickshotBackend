package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.SavesDAO;
import step.learning.dao.UserDAO;
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

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        if(userDAO.getUserByToken(body.getString("token")) == null)
        {
            res.getWriter().write("2: access denied");
            return;
        }

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
        res.setHeader("Access-Control-Allow-Origin","*");
        String postId;
        String login;
        try {
            postId = req.getParameter("postId");
            login = req.getParameter("login");
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters");
            return;
        }

        JSONObject answer = new JSONObject();
        answer.put("isSaved", savesDAO.isSavedByUser(postId, login, false));

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(answer.toString());
    }
}
