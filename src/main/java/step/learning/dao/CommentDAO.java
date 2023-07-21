package step.learning.dao;

import com.google.inject.Inject;
import step.learning.entities.Response;
import step.learning.entities.Comment;
import step.learning.services.DataService;
import step.learning.services.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentDAO {
    private final DataService dataService;
    private final LoggerService loggerService;
    @Inject
    public CommentDAO(DataService dataService, LoggerService loggerService)
    {
        this.dataService = dataService;
        this.loggerService = loggerService;
    }

    public String makeComment(Comment comment){
        if(comment.getId() == null)
            comment.setId(UUID.randomUUID().toString());
        String sql = "INSERT INTO Comments (" +
                "id, " +
                "date, " +
                "author, " +
                "content, " +
                "postId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            int param = 1;
            prep.setString(param,  comment.getId());
            param++;
            prep.setTimestamp(param, comment.getDate());
            param++;
            prep.setString(param, comment.getAuthor());
            param++;
            prep.setString(param, comment.getContent());
            param++;
            prep.setString(param, comment.getPostId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::makeComment() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return comment.getId();
    }
    public void updateComment(Comment comment){
        String sql = "UPDATE Comments SET ";
        if(comment.getDate() != null) sql += "date = ?, ";
        if(comment.getAuthor() != null) sql += "author = ?, ";
        if(comment.getContent() != null) sql += "content = ?, ";
        if(comment.getPostId() != null) sql += "postId = ?, ";
        if(sql.endsWith(", ")) sql = sql.substring(0, sql.lastIndexOf(','));
        sql += " WHERE id = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            int param = 1;
            if(comment.getDate() != null) {
                prep.setTimestamp(param, comment.getDate());
                param++;
            }
            if(comment.getAuthor() != null) {
                prep.setString(param, comment.getAuthor());
                param++;
            }
            if(comment.getContent() != null) {
                prep.setString(param, comment.getContent());
                param++;
            }
            if(comment.getPostId() != null) {
                prep.setString(param, comment.getPostId());
                param++;
            }

            prep.setString(param, comment.getId());

            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::updateComment() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }
    public Boolean deleteCommentById(String id){
        String sql = "UPDATE Comments SET deleted = NOW() WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            return prep.executeUpdate() != 0;
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::deleteCommentById() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public Boolean deleteCommentByAuthor(String login){
        String sql = "UPDATE Comments SET deleted = NOW() WHERE author = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, login);

            return prep.executeUpdate() != 0;
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::deleteCommentByAuthor() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public Boolean restoreComment(String id){
        String sql = "UPDATE Comments SET deleted = null WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::restoreComment() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }

        return true;
    }
    public Boolean isAnswer(String id){
        String sql = "SELECT * FROM Responses WHERE id_response = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){

            prep.setString(1, id);
            ResultSet res = prep.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::isAnswer() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public String getCommentIdOfAnswer(String id){
        String sql = "SELECT id_comment FROM Responses WHERE id_response = ? AND deleted IS NULL";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            if(res.next())
                return res.getString("id_comment");
            else
                return null;
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::getCommentIdOfAnswer() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public Boolean authorHasComment(String id,
                                    String author){
        String sql = "SELECT * FROM Comments WHERE author = ? AND id = ? AND deleted IS NULL";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            prep.setString(2, id);
            ResultSet res = prep.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::authorHasComment() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
    public Comment getCommentById(String id){
        String sql = "SELECT * FROM Comments WHERE id = ? AND deleted IS NULL";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, id);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            if(res.next()){
                return new Comment(res);
            }
        } catch (Exception ex) {
            loggerService.log("CommentDAO::getCommentById() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
    public void giveAnswer(Response response){
        Comment comment = response.getAnswerComment();
        comment.setId(makeComment(comment));
        response.setAnswerComment(comment);

        String sql = "INSERT INTO Responses (" +
                "id_comment, " +
                "id_response) VALUES (?, ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, response.getCommentId());
            prep.setString(2, response.getAnswerComment().getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::giveAnswer() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
    }
    public List<Comment> getSomeCommentsByPost(
                                        String postId,
                                        int from,
                                        int amount){
        String sql = "SELECT * FROM Comments WHERE postId = ? AND deleted IS NULL ORDER BY date ASC LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while(res.next()){
                comments.add(new Comment(res));
            }
            return comments;
        } catch (Exception ex) {
            loggerService.log("CommentDAO::getSomeCommentsByPost() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }

    public Boolean hasAnswer(String id){
        String sql = "SELECT * FROM Responses WHERE id_comment = ? AND deleted IS NULL";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::hasAnswer() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public String getAnswer(String id){
        String sql = "SELECT id_response FROM Responses WHERE id_comment = ? AND deleted IS NULL";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            if(res.next())
                return res.getString("id_response");
            else
                return null;
        } catch (SQLException ex) {
            loggerService.log("CommentDAO::getAnswer() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
            return null;
        }
    }
    public List<Comment> getCommentsByUser(String login, int from, int amount){
        String sql = "SELECT * FROM Comments WHERE author = ? AND deleted IS NULL ORDER BY date ASC LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while(res.next()){
                comments.add(new Comment(res));
            }
            return comments;
        } catch (Exception ex) {
            loggerService.log("CommentDAO::getCommentsByUser() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
    public List<Comment> getAllCommentsOfPost(String postId, int from, int amount){
        String sql = "SELECT * FROM Comments WHERE postId = ? ORDER BY date ASC LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while(res.next()){
                comments.add(new Comment(res));
            }
            return comments;
        } catch (Exception ex) {
            loggerService.log("CommentDAO::getAllCommentsOfPost() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
    public List<Comment> getSomeComments(int from, int amount){
        String sql = "SELECT * FROM Comments WHERE deleted IS NULL ORDER BY date LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setInt(1, from);
            prep.setInt(2, amount);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while(res.next()){
                comments.add(new Comment(res));
            }
            return comments;
        } catch (Exception ex) {
            loggerService.log("CommentDAO::getSomeComments() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
}
