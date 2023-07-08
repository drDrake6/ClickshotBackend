package step.learning.dao;

import com.google.inject.Inject;
import org.json.JSONObject;
import step.learning.entities.Post;
import step.learning.services.DataService;
import step.learning.services.MimeService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostDAO {
    private final DataService dataService;
    private final TaggedPeopleDAO taggedPeopleDAO;
    private final LikesDAO likedDAO;
    private final SavesDAO savesDAO;
    private final MimeService mimeService;
    @Inject
    public PostDAO(DataService dataService, TaggedPeopleDAO taggedPeopleDAO, LikesDAO likedDAO, SavesDAO savesDAO, MimeService mimeService)
    {
        this.dataService = dataService;
        this.taggedPeopleDAO = taggedPeopleDAO;
        this.likedDAO = likedDAO;
        this.savesDAO = savesDAO;
        this.mimeService = mimeService;
    }

    public boolean canShowPost(Post post){
        return post.isBaned() == null
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
                "addDate) VALUES (?,?,?,?,?,?,?)";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, post.getId());
            prep.setTimestamp(2, post.getPostponePublication());
            prep.setString(3, post.getAuthor());
            prep.setString(4, post.getDescription());
            prep.setString(5, post.getMediaUrl());
            prep.setString(6, post.getMetadata());
            prep.setTimestamp(7, post.getAddDate());

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
        if(post.getAddDate() != null) sql += "addDate = ?, ";
        sql += "baned = ?, ";
        if(sql.endsWith(", ")) sql = sql.substring(0, sql.lastIndexOf(','));
        sql += " WHERE id = ? AND deleted IS null";

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
            if(post.getAddDate() != null) {
                prep.setTimestamp(param, post.getAddDate());
                param++;
            }

                prep.setTimestamp(param, post.isBaned());
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
        String sql = "UPDATE Posts SET deleted = ? WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            prep.setString(2, postId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("UserDAO::deletePostById() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return null;
        }

        return true;

        //return cascade(postId);
    }

    public Boolean restorePost(String postId){
        String sql = "UPDATE Posts SET deleted = null WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, postId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("UserDAO::restorePost() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
            return null;
        }

        return true;
    }

    public boolean deletePostByAuthor(String login){
        List<Post> posts = getPostsByAuthor(login);

        for (int i = 0; i < posts.size(); i++) {
            if(!deletePostById(posts.get(i).getId()))
                return false;
        }
        return true;
    }

    public boolean restorePostByAuthor(String login){
        List<Post> posts = getDeletedPostsByAuthor(login);

        for (int i = 0; i < posts.size(); i++) {
            if(!restorePost(posts.get(i).getId()))
                return false;
        }
        return true;
    }

    public Post getPostByID(String postId) {
        String sql = "SELECT * FROM Posts WHERE id = ? AND deleted IS null AND baned IS null";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                    return new Post(res);
            }
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostByID() " + ex.getMessage()
                    + "\n" + sql + " -- " + postId);
        }
        return null;
    }
    public List<Post> getPostsByAuthor(String author){
        String sql = "SELECT * FROM Posts WHERE author = ? AND deleted IS null AND baned IS null";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                    posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public List<Post> getBanedPostsByAuthor(String author){
        String sql = "SELECT * FROM Posts WHERE author = ? AND deleted IS null";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getBanedPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public List<Post> getDeletedPostsByAuthor(String author){
        String sql = "SELECT * FROM Posts WHERE author = ? AND baned IS null";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getDeletedPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql + " -- " + author);
        }
        return null;
    }

    public Boolean authorHasPost(String id, String author){
        String sql = "SELECT * FROM Posts WHERE author = ? AND id = ? AND deleted IS null";
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
        String sql = "SELECT * FROM Posts WHERE deleted IS null AND baned IS null ORDER BY addDate LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setInt(1, from);
            prep.setInt(2, amount);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                    posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getSomePosts() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Post> findSomePosts(int from, int amount, JSONObject params){
        String sql = "SELECT * FROM Posts WHERE deleted IS null AND baned IS null AND";
        if(!params.isNull("author")) sql += " author LIKE ? AND";
        if(!params.isNull("description")) sql += " description LIKE ? AND";
        if(!params.isNull("onlyMedia")) sql += " mediaUrl REGEXP ? AND";
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
            if(!params.isNull("onlyMedia"))
            {
                String par = "";
                JSONObject jmediaTypes = params.getJSONObject("onlyMedia");
                for (int i = 0; i < jmediaTypes.length(); i++) {
                    List<String> mediaTypes = mimeService.getMediaTypes(mimeService.getMediaType(jmediaTypes.getString(String.valueOf(i))));
                    for (int j = 0; j < mediaTypes.size(); j++) {
                        par += mediaTypes.get(j) + "$|";
                    }
                }
                par = par.substring(0, par.lastIndexOf('|'));
                prep.setString(param,  par);
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
            System.out.println("PostDAO::FindSomePosts() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<Post> getSomePostsByAuthor(String login, int from, int amount){
        String sql = "SELECT * FROM Posts WHERE author = ? AND deleted IS null AND baned IS null ORDER BY addDate LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(res.next()){
                    posts.add(new Post(res));
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
                    posts.add(new Post(res));
            }
            return posts;
        } catch (SQLException ex) {
            System.out.println("PostDAO::getPostsByAuthor() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public int postsAmount(String login, boolean includeDeleted){
        String sql = "SELECT COUNT(*) as 'count' FROM Posts WHERE author = ?";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";

        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            if(res.next())
                return res.getInt("count");
            else
                return -1;
        }
        catch (SQLException ex) {
            System.out.println("setEmailCode() | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
            return -1;
        }
    }

    public Boolean banPosts(String postId, boolean ban) {
        String sql;

        if (ban)
            sql = "UPDATE Posts SET baned = NOW() WHERE id = ?";
        else
            sql = "UPDATE Posts SET baned = null WHERE id = ?";

        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, postId);
            return prep.executeUpdate() != 0;
        } catch (SQLException ex) {
            System.out.println("PostDAO::banPosts()" + ex.getMessage()
                    + "\n" + sql);
            return null;
        }
    }
}
