package step.learning.dao;

import com.google.inject.Inject;
import step.learning.entities.Response;
import step.learning.entities.Comment;
import step.learning.services.DataService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentDAO {
    private final DataService dataService;
    @Inject
    public CommentDAO(DataService dataService)
    {
        this.dataService = dataService;
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
            System.out.println("CommentDAO::makeComment() " + ex.getMessage()
                    + "\n" + sql);
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
            System.out.println("CommentDAO::updateComment() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

    public Boolean deleteCommentById(List<String> deletedIds, String id){
        while (hasAnswer(id)){
            deleteCommentById(deletedIds, getAnswer(id));
        }

        if(іsAnswer(id)){
            deleteResponse(id);
        }

        String sql = "DELETE FROM Comments WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            if(prep.executeUpdate() == 0){
                return false;
            }
            else{
                deletedIds.add(id);
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("CommentDAO::deleteCommentById() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }
    }

    public Boolean іsAnswer(String id){
        String sql = "SELECT * FROM Responses WHERE id_response = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){

            prep.setString(1, id);
            ResultSet res = prep.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            System.out.println("CommentDAO::іsAnswer() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }
    }

    public String getCommentIdOfAnswer(String id){
        String sql = "SELECT id_comment FROM Responses WHERE id_response = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            if(res.next())
                return res.getString("id_comment");
            else
                return null;
        } catch (SQLException ex) {
            System.out.println("CommentDAO::getCommentIdOfAnswer() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }
    }

    public Boolean hasAnswer(String id){
        String sql = "SELECT * FROM Responses WHERE id_comment = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            System.out.println("CommentDAO::deleteComment() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }
    }

    public String getAnswer(String id){
        String sql = "SELECT id_response FROM Responses WHERE id_comment = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            ResultSet res = prep.executeQuery();
            if(res.next())
                return res.getString("id_response");
            else
                return null;
        } catch (SQLException ex) {
            System.out.println("CommentDAO::getAnswer() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }
    }

    public Boolean authorHasComment(String id, String author){
        String sql = "SELECT * FROM Comments WHERE author = ? AND id = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            prep.setString(2, id);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("CommentDAO::authorHasComment() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public Comment getCommentById(String id){
        String sql = "SELECT * FROM Comments WHERE id = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, id);
            ResultSet res = prep.executeQuery();
            List<Comment> comments = new ArrayList<>();
            if(res.next()){
                return new Comment(res);
            }
        } catch (Exception ex) {
            System.out.println("PostDAO::getSomePosts() " + ex.getMessage()
                    + "\n" + sql);
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
            System.out.println("CommentDAO::makeComment() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

//
//    public List<Comment> getCommentsByUser(String id){
//
//    }
//
//    public List<Comment> getAllComments(){
//
//    }

    public List<Comment> getSomeComments(int from, int amount){
        String sql = "SELECT * FROM Comments ORDER BY date LIMIT ?, ?";
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
            System.out.println("PostDAO::getSomePosts() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Comment> getSomeCommentsByPost(String postId, int from, int amount){
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
            System.out.println("PostDAO::getSomeCommentsByPost() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public Boolean deleteResponse(String id){

        String sql = "DELETE FROM Responses WHERE id_response = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, id);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("CommentDAO::deleteCommentById() " + ex.getMessage()
                    + "\n" + sql + " -- " + id);
            return null;
        }

        return true;
    }
}
