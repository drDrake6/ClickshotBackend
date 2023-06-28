package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;
import step.learning.services.HashService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaggedPeopleDAO {
    private final DataService dataService;

    @Inject
    public TaggedPeopleDAO(DataService dataService)
    {
        this.dataService = dataService;
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
            System.out.println("TaggedPeopleDAO::setTaggedPeople() " + ex.getMessage()
                    + "\n" + sql);
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
            System.out.println("TaggedPeopleDAO::getTaggedPeople() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }
    public Boolean deleteTaggedPeopleByPost(String postId){
        String sql = "DELETE FROM TaggedPeople WHERE postId = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("TaggedPeopleDAO::deleteTaggedPeopleByPost() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return null;
        }
        return true;
    }

    public Boolean deleteTaggedPeopleByAuthor(String authorId){
        String sql = "DELETE FROM TaggedPeople WHERE userId = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, authorId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("TaggedPeopleDAO::deleteTaggedPeopleByAuthor() " + ex.getMessage()
                    + "\n" + sql + " -- " + authorId);
            return null;
        }
        return true;
    }

}
