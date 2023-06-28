package step.learning.dao;

import com.google.inject.Inject;
import com.mysql.cj.protocol.a.LocalDateTimeValueEncoder;
import org.json.JSONObject;
import step.learning.entities.Post;
import step.learning.entities.User;
import step.learning.services.DataService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostDAO {
    private final DataService dataService;
    private final TaggedPeopleDAO taggedPeopleDAO;
    private final LikesDAO likedDAO;
    private final SavesDAO savesDAO;
    @Inject
    public PostDAO(DataService dataService, TaggedPeopleDAO taggedPeopleDAO, LikesDAO likedDAO, SavesDAO savesDAO)
    {
        this.dataService = dataService;
        this.taggedPeopleDAO = taggedPeopleDAO;
        this.likedDAO = likedDAO;
        this.savesDAO = savesDAO;
    }

    public boolean canShowPost(Post post){
        return !post.isBanned()
                && !post.getPostponePublication().after(Timestamp.valueOf(LocalDateTime.now()));
    }


    public String add(Post post){
        if(post.getId() == null)
            post.setId(UUID.randomUUID().toString());
        String sql = "INSERT INTO Posts (" +
                "id, " +
                "postponePublication, " +
                "author, " +
                "description, " +
                "mediaUrl, " +
                "metadata, " +
                "likes, " +
                "addDate, " +
                "banned) VALUES (?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, post.getId());
            prep.setTimestamp(2, post.getPostponePublication());
            prep.setString(3, post.getAuthor());
            prep.setString(4, post.getDescription());
            prep.setString(5, post.getMediaUrl());
            prep.setString(6, post.getMetadata());
            prep.setInt(7, post.getLikes());
            prep.setTimestamp(8, post.getAddDate());
            prep.setBoolean(9, false);

            prep.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("add | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
            return null;
        }
        return post.getId();
    }

    public void update(Post post, String id){
        String sql = "UPDATE Posts SET ";
        if(post.getPostponePublication() != null) sql += "postponePublication = ?, ";
        if(post.getAuthor() != null) sql += "authorId = ?, ";
        if(post.getDescription() != null) sql += "description = ?, ";
        if(post.getMediaUrl() != null) sql += "mediaUrl = ?, ";
        if(post.getMetadata() != null) sql += "metadata = ?, ";
        if(post.getLikes() >= 0) sql += "likes = ?, ";
        if(post.getAddDate() != null) sql += "addDate = ?, ";
        sql += "banned = ?, ";
        if(sql.endsWith(", ")) sql = sql.substring(0, sql.lastIndexOf(','));
        sql += " WHERE id = ?";

        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            if(prep.getParameterMetaData().getParameterCount() == 0) return;
            int param = 1;
            if(post.getPostponePublication() != null) {
                prep.setTimestamp(param, post.getPostponePublication());
                param++;
            }
            if(post.getAuthor() != null){
                prep.setString(param, post.getAuthor());
                param++;
            }
            if(post.getDescription() != null) {
                prep.setString(param, post.getDescription());
                param++;
            }
            if(post.getMediaUrl() != null) {
                prep.setString(param, post.getMediaUrl());
                param++;
            }
            if(post.getMetadata() != null) {
                prep.setString(param, post.getMetadata());
                param++;
            }
            if(post.getLikes() >= 0) {
                prep.setInt(param, post.getLikes());
                param++;
            }
            if(post.getAddDate() != null) {
                prep.setTimestamp(param, post.getAddDate());
                param++;
            }

                prep.setBoolean(param, post.isBanned());
                param++;

            prep.setString(param, id);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("update | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
        }
    }
    public Boolean deletePostById(String postId){
        String sql = "DELETE FROM Posts WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("CarDao::deletePostById() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return null;
        }

        return cascade(postId);
    }

    public boolean deletePostByAuthor(String login){
        List<Post> posts = getPostsByAuthor(login);

        for (int i = 0; i < posts.size(); i++) {
            if(!deletePostById(posts.get(i).getId()))
                return false;
        }
        return true;
    }

    private boolean cascade(String postId){
        if(taggedPeopleDAO.deleteTaggedPeopleByPost(postId) != null &&
                likedDAO.deleteLikeByPost(postId) != null &&
                savesDAO.deleteSavesByPost(postId) != null)
            return true;
        else
            return false;
    }
    public Post getPostByID(String postId) {
        String sql = "SELECT * FROM Posts WHERE id = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                Post post = new Post(res);
                if(canShowPost(post))
                    return post;
            }
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostByID() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
        }
        return null;
    }
    public List<Post> getPostsByAuthor(String author){
        String sql = "SELECT * FROM Posts WHERE author = ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                Post post = new Post(res);
                if(canShowPost(post))
                    posts.add(post);
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public Boolean authorHasPost(String id, String author){
        String sql = "SELECT * FROM Posts WHERE author = ? AND id = ?";
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
            System.out.println("PostDAO::AuthorHasPost() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public List<Post> getSomePosts(int from, int amount){
        String sql = "SELECT * FROM Posts ORDER BY addDate LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setInt(1, from);
            prep.setInt(2, amount);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                Post post = new Post(res);
                if(canShowPost(post))
                    posts.add(post);
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getSomePosts() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Post> findSomePosts(int from, int amount, JSONObject params){
        String sql = "SELECT * FROM Posts WHERE";
        if(!params.isNull("author")) sql += " author LIKE ? AND";
        if(!params.isNull("description")) sql += " description LIKE ? AND";
        if(!params.isNull("addDate")) sql += " addDate BETWEEN ? AND ?";
        if(sql.endsWith("AND")) sql = sql.substring(0, sql.lastIndexOf('A'));
        sql += " ORDER BY addDate LIMIT ?, ?";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            int param = 1;
            if(!params.isNull("author")){
                prep.setString(param, "%" + params.getString("author") + "%");
                param++;
            }
            if(!params.isNull("description")){
                prep.setString(param, "%" + params.getString("description") + "%");
                param++;
            }
            if(!params.isNull("addDate")){
                prep.setString(param,  params.getJSONObject("addDate").getString("from"));
                param++;
                prep.setString(param, params.getJSONObject("addDate").getString("to"));
                param++;
            }

            prep.setInt(param, from);
            param++;
            prep.setInt(param, amount);

            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("UserDAO::FindSomePosts() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Post> getSomePostsByAuthor(String login, int from, int amount){
        String sql = "SELECT * FROM Posts WHERE author = ? ORDER BY addDate LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                Post post = new Post(res);
                if(canShowPost(post))
                    posts.add(post);
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getSomePostsByAuthor() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Post> getAllPosts(){
        String sql = "SELECT * FROM Posts ORDER BY addDate";
        try (Statement statement =
                     dataService.getConnection().createStatement()) {
            ResultSet res = statement.executeQuery(sql);
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                Post post = new Post(res);
                if(canShowPost(post))
                    posts.add(post);
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public Boolean banPosts(List<String> banPosts, List<String> unbanPosts){
        StringBuilder sql = new StringBuilder("UPDATE Posts SET banned = TRUE WHERE id IN (");
        boolean res1 = false;
        boolean res2 = false;

        try {
            for (int i = 0; i < banPosts.size(); i++) {
                if (!this.getPostByID(banPosts.get(i)).isBanned()) {
                    sql.append("?, ");
                }
                else{
                    banPosts.remove(banPosts.get(i));
                    i--;
                }
            }

            if (sql.toString().endsWith(", ")) {
                sql.delete(sql.length() - 2, sql.length());
                sql.append(")");

                try (PreparedStatement prep =
                             dataService.getConnection().prepareStatement(sql.toString())) {
                    for (int i = 0; i < banPosts.size(); i++) {
                        prep.setString(i + 1, banPosts.get(i));
                    }
                    res1 = prep.executeUpdate() != 0;
                } catch (SQLException ex) {
                    System.out.println("PostDAO::banPosts() ban " + ex.getMessage()
                            + "\n" + sql);
                    return null;
                }
            }
        }catch (Exception ex) {
            System.out.println("PostDAO::banPosts() ban " + ex.getMessage());
            return null;
        }

        sql = new StringBuilder("UPDATE Posts SET banned = FALSE WHERE id IN (");

        try {
            for (int i = 0; i < unbanPosts.size(); i++) {
                if(this.getPostByID(unbanPosts.get(i)).isBanned()){
                    sql.append("?, ");
                }
                else{
                    unbanPosts.remove(unbanPosts.get(i));
                    i--;
                }
            }

            if(sql.toString().endsWith(", ")) {
                sql.delete(sql.length() - 2, sql.length());
                sql.append(")");

                try (PreparedStatement prep =
                             dataService.getConnection().prepareStatement(sql.toString())) {
                    for (int i = 0; i < unbanPosts.size(); i++) {
                        prep.setString(i + 1, unbanPosts.get(i));
                    }
                    res2 = prep.executeUpdate() != 0;
                } catch (SQLException ex) {
                    System.out.println("PostDAO::banPosts() unban " + ex.getMessage()
                            + "\n" + sql);
                    return null;
                }
            }

        }catch (Exception ex) {
            System.out.println("PostDAO::banPosts() unban " + ex.getMessage());
            return null;
        }

        return (res1 || res2);
    }
}
