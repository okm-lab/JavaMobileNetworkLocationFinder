package LBSGeo;

import com.mysql.cj.conf.ConnectionUrlParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
public class QueryManager {
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public HashMap<Long, HashMap<String, Number>> sendQuery(String query) {
        HashMap<Long, HashMap<String, Number>> towers = new HashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://0.tcp.ngrok.io:17440/cell", "ro1", "y71mM7!l257G5Q60");
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                //mnc - 3, mcc - 3, lac - 6, cellid - 7
                Long key = resultSet.getLong("mnc") * (long) 1e16 +
                        resultSet.getLong("mcc") * (long) 1e13 +
                        resultSet.getLong("lac") * (long) 1e7  +
                        resultSet.getLong("cellid");

                towers.put(key, new HashMap<>(){{
                    put("lat", resultSet.getDouble("lat"));
                    put("lon", resultSet.getDouble("lon"));
                }});
            }
        } catch (SQLException | ClassNotFoundException sx) {
            System.out.println("Exception when sending a request!");
        } finally {
            close();
        }
        return towers;
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (Exception ignored) {

        }
    }
}
