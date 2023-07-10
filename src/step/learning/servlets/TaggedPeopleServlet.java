package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.TaggedPeopleDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class TaggedPeopleServlet extends HttpServlet {

    @Inject
    private PostDAO postDAO;

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private TaggedPeopleDAO taggedPeopleDAO;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        String postId;
        try {
            postId = req.getParameter("postId");
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters");
            return;
        }

        List<String> logins = taggedPeopleDAO.getTaggedPeople(postId);
        JSONObject jLogins = new JSONObject();
        for (int i = 0; i < logins.size(); i++) {
            jLogins.put(String.valueOf(i), logins.get(i));
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jLogins.toString());
    }
}
