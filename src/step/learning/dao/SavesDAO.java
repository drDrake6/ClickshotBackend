package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;
import step.learning.services.HashService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SavesDAO {

    private final DataService dataService;
    private final HashService hashService;

    @Inject
    public SavesDAO(DataService dataService, HashService hashService)
    {
        this.dataService = dataService;
        this.hashService = hashService;
    }

    public void addSave(String postId, String login){
        String sql = "INSERT INTO SavedBy (postId, userId) VALUES (?, (SELECT id FROM Users WHERE login = ?))";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SavesDAO::addSave() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

    public void unSave(String postId, String login){
        String sql = "DELETE FROM SavedBy WHERE postId = ? AND userId = (SELECT id FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SavesDAO::unSave() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

    public Boolean deleteSavesByUser(String login){
        String sql = "DELETE FROM SavedBy WHERE userId = (SELECT userId FROM Users WHERE login = ?)";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, login);
            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SavesDAO::deleteSavesByUser() " + ex.getMessage()
                    + "\n" + sql + " -- " + login);
            return null;
        }

        return true;




    }

    public Boolean deleteSavesByPost(String postId){
        String sql = "DELETE FROM SavedBy WHERE postId = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);
            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SavesDAO::deleteSavesByPost() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return null;
        }

        return true;
    }

    public Boolean deleteOneSave(String postId, String login){
        String sql = "DELETE FROM SavedBy WHERE postId = ? AND userId = (SELECT userId FROM Users WHERE login = ?)";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);
            prep.setString(2, login);
            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SavesDAO::deleteOneSave() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId + " " + login);
            return null;
        }

        return true;
    }

    public List<String> getSaves(String login){

        String sql = "SELECT postId FROM SavedBy JOIN Users ON Users.id = SavedBy.userId WHERE Users.login = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  login);
            ResultSet res = prep.executeQuery();
            List<String> saves = new ArrayList<>();
            while(res.next()){
                saves.add(res.getString("postId"));
            }
            return saves;
        } catch (SQLException ex) {
            System.out.println("SavesDAO::getSaves() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<String> getSavers(String postId){

        String sql = "SELECT login FROM Users JOIN SavedBy ON Users.id = SavedBy.userId WHERE SavedBy.postId = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            ResultSet res = prep.executeQuery();
            List<String> savers = new ArrayList<>();
            while(res.next()){
                savers.add(res.getString("login"));
            }
            return savers;
        } catch (SQLException ex) {
            System.out.println("SavesDAO::getSavers() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<String> getSaveById(String postId){

        String sql = "SELECT postId FROM SavedBy WHERE SavedBy ON Users.id = SavedBy.userId WHERE SavedBy.postId = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            ResultSet res = prep.executeQuery();
            List<String> savers = new ArrayList<>();
            while(res.next()){
                savers.add(res.getString("login"));
            }
            return savers;
        } catch (SQLException ex) {
            System.out.println("SavesDAO::getSaveById() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public Boolean isLikedByUser(String postId, String login){

        String sql = "SELECT * FROM SavedBy WHERE postId = ? AND userId = (SELECT userId FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return true;
            }
            else return false;
        } catch (SQLException ex) {
            System.out.println("LikedDAO::isLikedByUser() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }
}
