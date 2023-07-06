package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LikesDAO {

    private final DataService dataService;

    @Inject
    public LikesDAO(DataService dataService)
    {
        this.dataService = dataService;
    }

    public Boolean putLike(String postId, String login){
        Boolean isLiked = isLikedByUser(postId, login, true);
        String sql;
        if(isLiked != null){
            if(isLiked)
                sql = "UPDATE LikedBy SET deleted = null, date = NOW() WHERE postId = ? AND userId = (SELECT id FROM Users WHERE login = ?)";
            else
                sql = "INSERT INTO LikedBy (postId, userId) VALUES (?, (SELECT id FROM Users WHERE login = ?))";
        }
        else
            return null;
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("LikedDAO::putLike() " + ex.getMessage()
                    + "\n" + sql);
            return null;
        }
        return true;
    }

    public Boolean unLike(String postId, String login){
        String sql = "UPDATE LikedBy SET deleted = NOW() WHERE postId = ? AND userId = (SELECT id FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  postId);
            prep.setString(2,  login);
            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("LikedDAO::unLike() " + ex.getMessage()
                    + "\n" + sql);
            return null;
        }
        return true;
    }

    public List<String> getLiked(String postId){

        String sql = "SELECT login FROM Users JOIN LikedBy ON Users.id = LikedBy.userId WHERE LikedBy.postId = ?";
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
            System.out.println("LikedDAO::getLiked() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<String> getLikedByUser(String login){

        String sql = "SELECT postId FROM LikedBy WHERE userId = (SELECT userId FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  login);
            ResultSet res = prep.executeQuery();
            List<String> likedPosts = new ArrayList<>();
            while(res.next()){
                likedPosts.add(res.getString("postId"));
            }
            return likedPosts;
        } catch (SQLException ex) {
            System.out.println("LikedDAO::getLikedByOne() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public Boolean isLikedByUser(String postId, String login, boolean includeDeleted){

        String sql = "SELECT * FROM LikedBy WHERE postId = ? AND userId = (SELECT userId FROM Users WHERE login = ?)";
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
            System.out.println("LikedDAO::isLikedByUser() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public int getLikesCount(String postId){
        String sql = "SELECT COUNT(*) as 'count' FROM LikedBy WHERE postId = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return res.getInt("count");
            }
            return -1;
        } catch (SQLException ex) {
            System.out.println("LikedDAO::deleteLikeByPost() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return -1;
        }
    }
}
