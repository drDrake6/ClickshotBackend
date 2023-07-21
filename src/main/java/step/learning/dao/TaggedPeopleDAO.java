package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;
import step.learning.services.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaggedPeopleDAO {
    private final DataService dataService;
    private final LoggerService loggerService;
    @Inject
    public TaggedPeopleDAO(DataService dataService, LoggerService loggerService)
    {
        this.dataService = dataService;
        this.loggerService = loggerService;
    }

    public void setTaggedPeople(String postId, String[] logins){
        String sql = "INSERT INTO TaggedPeople (postId, userId) VALUES (?, (SELECT id FROM Users WHERE login = ?))";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            for (String login : logins) {
                prep.setString(2,  login);
                prep.executeUpdate();
            }
        } catch (SQLException ex) {
            loggerService.log("TaggedPeopleDAO::setTaggedPeople() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }
    public List<String> getTaggedPeople(String postId){

        String sql = "SELECT login FROM Users JOIN TaggedPeople ON Users.id = TaggedPeople.userId WHERE TaggedPeople.postId = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            ResultSet res = prep.executeQuery();
            List<String> taggedPeople = new ArrayList<>();
            while(res.next()){
                taggedPeople.add(res.getString("login"));
            }
            return taggedPeople;
        } catch (SQLException ex) {
            loggerService.log("TaggedPeopleDAO::getTaggedPeople() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }

    public void unTaggedPeople(String login){

        String sql = "UPDATE TaggedPeople SET deleted = NOW() WHERE userId = (SELECT id FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  login);
            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("TaggedPeople::unTaggedPeople() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }
}
