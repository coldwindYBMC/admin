package transfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    static final String jdbc = "jdbc:mysql://%s:%s/%s?characterEncoding=utf-8&user=%s&password=%s";
    
    static Connection connect(Conf conf) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(String.format(jdbc, conf.host, conf.port, conf.name, conf.user, conf.pass));
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
