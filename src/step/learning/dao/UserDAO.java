package step.learning.dao;

import com.google.inject.Inject;
import javafx.util.Pair;
import org.json.JSONObject;
import step.learning.entities.User;
import step.learning.services.DataService;
import step.learning.services.EmailService;
import step.learning.services.HashService;
import step.learning.services.LoadConfigService;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    private final DataService dataService;
    private final HashService hashService;
    private final EmailService emailService;
    private final TaggedPeopleDAO taggedPeopleDAO;
    private final LikesDAO likedDAO;
    private final SavesDAO savesDAO;
    private final LoadConfigService loadConfigService;

    @Inject
    public UserDAO(DataService dataService,
                   HashService hashService,
                   EmailService emailService,
                   TaggedPeopleDAO taggedPeopleDAO,
                   LikesDAO likedDAO,
                   SavesDAO savesDAO,
                   LoadConfigService loadConfigService)
    {
        this.dataService = dataService;
        this.hashService = hashService;
        this.emailService = emailService;
        this.taggedPeopleDAO = taggedPeopleDAO;
        this.likedDAO = likedDAO;
        this.savesDAO = savesDAO;
        this.loadConfigService = loadConfigService;
    }
    public User setAuthorizeToken(String token, String userId){
        String sql = "UPDATE Users SET token = ? WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, token);
            prep.setString(2, userId);
            ResultSet res = prep.executeQuery();
            if(res.next()) return new User(res);
        } catch (SQLException ex) {
            System.out.println("UserDAO::setAuthorizeToken() " + ex.getMessage()
                    + "\n" + sql + " -- " + userId);
        }
        return null;
    }
    public User getUserById(String userId){
        String sql = "SELECT * FROM Users WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, userId);
            ResultSet res = prep.executeQuery();
            if(res.next()) return new User(res);
        } catch (SQLException ex) {
            System.out.println("UserDAO::getUserById() " + ex.getMessage()
                    + "\n" + sql + " -- " + userId);
        }
        return null;
    }

    public User getUserByToken(String token){
        String sql = "SELECT * FROM Users WHERE token = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, token);
            ResultSet res = prep.executeQuery();
            if(res.next()) return new User(res);
        } catch (SQLException ex) {
            System.out.println("UserDAO::getUserByToken() " + ex.getMessage()
                    + "\n" + sql + " -- " + token);
        }
        return null;
    }

    public List<User> getAllUsers(){
        String sql = "SELECT * FROM Users";
        try(Statement statement =
                    dataService.getConnection().createStatement()){
            ResultSet res = statement.executeQuery(sql);
            List<User> users = new ArrayList<>();
            while(res.next()){
                users.add(new User(res));
            }
            return users;
        } catch (SQLException ex) {
            System.out.println("UserDAO::getAllUsers() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<User> getSomeUsers(int from, int amount){
        String sql = "SELECT * FROM Users ORDER BY login LIMIT ?, ?";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setInt(1, from);
            prep.setInt(2, amount);
            ResultSet res = prep.executeQuery();
            List<User> users = new ArrayList<>();
            while(res.next()){
                users.add(new User(res));
            }
            return users;
        } catch (SQLException ex) {
            System.out.println("UserDAO::getSemeUsers() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<User> findSomeUsers(int from, int amount, JSONObject params){
        String sql = "SELECT * FROM Users WHERE";
                if(!params.isNull("login")) sql += " login LIKE ? AND";
        if(!params.isNull("name")) sql += " name LIKE ? AND";
        if(!params.isNull("surname")) sql += " surname LIKE ? AND";
        if(!params.isNull("email")) sql += " email LIKE ? AND";
        if(!params.isNull("bio")) sql += "bio LIKE ? AND";
        if(!params.isNull("birthday")) sql += " birthday BETWEEN ? AND ?";
        if(sql.endsWith("AND")) sql = sql.substring(0, sql.lastIndexOf('A'));
        sql += " ORDER BY login LIMIT ?, ?";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            int param = 1;
            if(!params.isNull("login")){
                prep.setString(param, "%" + params.getString("login") + "%");
                param++;
            }
            if(!params.isNull("name")){
                prep.setString(param, "%" + params.getString("name") + "%");
                param++;
            }
            if(!params.isNull("surname")){
                prep.setString(param, "%" + params.getString("surname") + "%");
                param++;
            }
            if(!params.isNull("email")){
                prep.setString(param, "%" + params.getString("email") + "%");
                param++;
            }
            if(!params.isNull("bio")){
                prep.setString(param, "%" + params.getString("bio") + "%");
                param++;
            }
            if(!params.isNull("birthday")){
                prep.setString(param, params.getJSONObject("birthday").getString("from"));
                param++;
                prep.setString(param, params.getJSONObject("birthday").getString("to"));
                param++;
            }

            prep.setInt(param, from);
            param++;
            prep.setInt(param, amount);

            ResultSet res = prep.executeQuery();
            List<User> users = new ArrayList<>();
            while(res.next()){
                users.add(new User(res));
            }
            return users;
        } catch (SQLException ex) {
            System.out.println("UserDAO::FindSomeUsers() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public User getUser(String login){
        String sql = "SELECT * FROM Users WHERE login = ?";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            User user = null;
            if(res.next()){
                user = new User(res);
            }
            return user;
        } catch (SQLException ex) {
            System.out.println("UserDAO::getUser() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    private String genSalt(){
        return hashService.hash(UUID.randomUUID().toString());
    }

    public void makePassword(User user, String password){
        user.setSalt(genSalt());
        user.setPassword(this.makePasswordHash(password, user.getSalt()));
        this.update(user, user.getId());
    }

    public String add(User user){
        user.setId(UUID.randomUUID().toString());
        user.setSalt(genSalt());
        user.setEmail_code(/*UUID.randomUUID().toString().substring(0, 6)*/null);
        user.setEmail_attempt(/*UUID.randomUUID().toString().substring(0, 6)*/0);
        String sql = "INSERT INTO Users (" +
                "id, " +
                "login, " +
                "password, " +
                "name, " +
                "surname, " +
                "salt, " +
                "phone, " +
                "email, " +
                "email_code, " +
                "email_attempt, " +
                "avatar, " +
                "birthday, " +
                "bio," +
                "token ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, user.getId());
            prep.setString(2, user.getLogin());
            prep.setString(3, this.makePasswordHash(user.getPassword(), user.getSalt()));
            prep.setString(4, user.getName());
            prep.setString(5, user.getSurname());
            prep.setString(6, user.getSalt());
            prep.setString(7, user.getPhone());
            prep.setString(8, user.getEmail());
            prep.setString(9, user.getEmail_code());
            prep.setInt(10, user.getEmail_attempt());
            prep.setString(11, user.getAvatar());
            prep.setTimestamp(12, user.getBirthday());
            prep.setString(13, user.getBio());
            prep.setString(14, user.getToken());

            prep.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("add | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
            return null;
        }
        return user.getId();
    }

    public void update(User user, String id){
        String sql = "UPDATE Users SET ";
        if(user.getPassword() != null) sql += "password = ?, salt = ?, ";
        if(user.getName() != null) sql += "name = ?, ";
        if(user.getSurname() != null) sql += "surname = ?, ";
        if(user.getPhone() != null) sql += "phone = ?, ";
        if(user.getAvatar() != null) sql += "avatar = ?, ";
        if(user.getEmail() != null) sql += "email = ?, email_code = ?, email_attempt = ?, ";
        if(user.getBirthday() != null) sql += "birthday = ?, ";
        if(user.getBio() != null) sql += "bio = ?, ";
        if(user.getToken() != null) sql += "token = ?, ";
        if(user.getRole() == 'u' || user.getRole() == 'a') sql += "role = ?, ";
        if(sql.endsWith(", ")) sql = sql.substring(0, sql.lastIndexOf(','));
        sql += " WHERE id = ?";

        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            if(prep.getParameterMetaData().getParameterCount() == 0) return;
            int param = 1;
            if(user.getPassword() != null){
                prep.setString(param, user.getPassword());
                param++;
                prep.setString(param, user.getSalt());
                param++;
            }
            if(user.getName() != null) {
                prep.setString(param, user.getName());
                param++;
            }

            if(user.getSurname() != null) {
                prep.setString(param, user.getSurname());
                param++;
            }

            if(user.getPhone() != null) {
                prep.setString(param, user.getPhone());
                param++;
            }

            if(user.getAvatar() != null) {
                prep.setString(param, user.getAvatar());
                param++;
            }
            if(user.getEmail() != null) {
                prep.setString(param, user.getEmail());
                param++;
                prep.setString(param, user.getEmail_code());
                param++;
                prep.setInt(param, user.getEmail_attempt());
                param++;
            }
            if(user.getBirthday() != null) {
                prep.setTimestamp(param, user.getBirthday());
                param++;
            }

            if(user.getBio() != null) {
                prep.setString(param, user.getBio());
                param++;
            }

            if(user.getToken() != null) {
                prep.setString(param, user.getToken());
                param++;
            }

            if(user.getRole() == 'u' || user.getRole() == 'a') {
                prep.setString(param, Character.toString(user.getRole()));
                param++;
            }

            prep.setString(param, id);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("update | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
        }
    }

    public boolean deleteUserById(String userId){
        String sql = "DELETE FROM Users WHERE id = ?";
        try(PreparedStatement prep =
                    dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, userId);

            if(prep.executeUpdate() == 0){
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("UserDAO::deleteUserById() " + ex.getMessage()
                    + "\n" + sql + " -- " + userId);
            return false;
        }



        return cascade(userId);
    }

    private boolean cascade(String userId){
        if(taggedPeopleDAO.deleteTaggedPeopleByAuthor(userId) != null &&
                likedDAO.deleteLikeByUser(userId) != null &&
                savesDAO.deleteSavesByUser(userId) != null)
            return true;
        else
            return false;
    }

    public boolean sendConfirmCode(String email, String param, String value, String code, String link) throws FileNotFoundException {
        if(email != null){
            return emailService.send(email, "Email confirmation code",
                    String.format(
                            "<h2>Hello</h2><p>To confirm your E-mail type a code <b>%s</b></p>" +
                                    "<p>Or follow this <a href='" + loadConfigService.load().getString("domen") + link + "?" + param + "=%s&code=%s'>link</a></p>",
                            code, value, code));
        }
        return false;
    }

    private String makePasswordHash(String password, String salt){
        return hashService.hash(salt + password + salt);
    }

    public boolean isPrevPassword(User user, String password){
        if(makePasswordHash(password, user.getSalt()).equals(user.getPassword())) return true;
        else return false;
    }

    public boolean CheckCredentials(User user, String password){
        String pass = makePasswordHash(password, user.getSalt());
        if(pass.equals(user.getPassword())) return true;
        else return false;
    }

    public boolean setEmailCode(User user, String code){
        if(user == null || user.getId() == null) return false;

        String sql = "UPDATE Users SET email_code = ? WHERE id = ?";
        try(PreparedStatement prep = dataService.getConnection().prepareStatement(sql)){
            prep.setString(1, code);
            prep.setString(2, user.getId());
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("setEmailCode() | Query error: " + ex.getMessage());
            System.out.println("sql: " + sql);
            return false;
        }
        //user.setEmail_code(null);
        return true;
    }

    public boolean isAdmin(String login){
        User user = getUser(login);

        if(user.getRole() == 'a')
            return true;
        else
            return false;
    }

    public int postsAmount(String login){
        String sql = "SELECT COUNT(*) as 'count' FROM Posts WHERE author = ?";

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

    public JSONObject getPublicUserInfo(String login){
        User user = getUser(login);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", user.getLogin());
        jsonObject.put("name", user.getName());
        jsonObject.put("surname", user.getSurname());
        jsonObject.put("bio", user.getBio());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("birthday", user.getBirthday());
        jsonObject.put("postsAmount", postsAmount(login));

        return jsonObject;
    }
}
