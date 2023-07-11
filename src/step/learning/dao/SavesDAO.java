package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;
import step.learning.services.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SavesDAO {

    private final DataService dataService;
    private final LoggerService loggerService;

    @Inject
    public SavesDAO(DataService dataService, LoggerService loggerService)
    {
        this.dataService = dataService;
        this.loggerService = loggerService;
    }

    public void addSave(String postId, String login){
        Boolean isSaved = isSavedByUser(postId, login, true);
        String sql;
        if(isSaved != null){
            if(isSaved)
                sql = "UPDATE SavedBy SET deleted = null, date = NOW() WHERE postId = ? AND userId = (SELECT id FROM Users WHERE login = ?)";
            else
                sql = "INSERT INTO SavedBy (postId, userId) VALUES (?, (SELECT id FROM Users WHERE login = ?))";
        }
        else
            return;

        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("SavesDAO::addSave() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }

    public void unSave(String postId, String login){
        String sql = "UPDATE SavedBy SET deleted = NOW() WHERE postId = ? AND userId = (SELECT id FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("SavesDAO::unSave() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }

    public List<String> getSaves(int from, int amount, String login){

        String sql = "SELECT postId FROM SavedBy WHERE SavedBy.userId = (SELECT id FROM Users WHERE login = ?) AND postId IN (SELECT id FROM posts WHERE posts.deleted IS NULL) AND SavedBy.deleted IS NULL ORDER BY date LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  login);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<String> saves = new ArrayList<>();
            while(res.next()){
                saves.add(res.getString("postId"));
            }
            return saves;
        } catch (SQLException ex) {
            loggerService.log("SavesDAO::getSaves() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }

    public List<String> getSavers(String postId){

        String sql = "SELECT login FROM Users JOIN SavedBy WHERE deleted IS NULL ON Users.id = SavedBy.userId WHERE SavedBy.postId = ?";
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
            loggerService.log("SavesDAO::getSavers() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }

    public List<String> getSaveById(String postId){

        String sql = "SELECT postId FROM SavedBy WHERE deleted IS NULL ON Users.id = SavedBy.userId WHERE SavedBy.postId = ?";
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
            loggerService.log("SavesDAO::getSaveById() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }

    public Boolean isSavedByUser(String postId, String login, boolean includeDeleted){
        String sql = "SELECT * FROM SavedBy WHERE postId = ? AND userId = (SELECT userId FROM Users WHERE login = ?)";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";

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
            loggerService.log("SavesDAO::isSavedByUser() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
}
