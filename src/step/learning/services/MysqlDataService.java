package step.learning.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;

@Singleton
public class MysqlDataService implements DataService{
    private final LoadConfigService loadConfigService;

    @Inject
    public MysqlDataService(LoadConfigService loadConfigService){
        this.loadConfigService = loadConfigService;
        //this.dbConnection = this.loadConfigService.load().getJSONObject("dbConnection");

    }
    public Connection connection;
    public Connection getConnection(){
        try{
            if(connection == null || connection.isClosed()){
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(
                            this.loadConfigService.load().getJSONObject("dbConnection").getString("connectionString"),
                            this.loadConfigService.load().getJSONObject("dbConnection").getString("dbUser"),
                            this.loadConfigService.load().getJSONObject("dbConnection").getString("dbPass")
                    );
            }
        } catch (Exception ex){
            System.out.println("Exception MysqlDataService::getConnection " +
                    ex.getMessage());
        }
        return connection;
    }
}
