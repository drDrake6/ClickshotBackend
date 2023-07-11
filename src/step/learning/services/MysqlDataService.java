package step.learning.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;

@Singleton
public class MysqlDataService implements DataService{
    private final LoadConfigService loadConfigService;
    private final RealPathService realPathService;

    @Inject
    public MysqlDataService(LoadConfigService loadConfigService, RealPathService realPathService){
        this.loadConfigService = loadConfigService;
        //this.dbConnection = this.loadConfigService.load().getJSONObject("dbConnection");

        this.realPathService = realPathService;
    }
    public Connection connection;
    public Connection getConnection(){
        try{
            if(connection == null){
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(
                            this.loadConfigService.load(realPathService.getRealPath()).getJSONObject("dbConnection").getString("connectionString"),
                            this.loadConfigService.load(realPathService.getRealPath()).getJSONObject("dbConnection").getString("dbUser"),
                            this.loadConfigService.load(realPathService.getRealPath()).getJSONObject("dbConnection").getString("dbPass")
                    );
            }
        } catch (Exception ex){
            System.out.println("Exception MysqlDataService::getConnection " +
                    ex.getMessage());
        }
        return connection;
    }
}
