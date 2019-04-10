
package scheduler;

import java.sql.*;
import java.time.ZoneId;
import java.util.Properties;
import java.util.TimeZone;

public class Db {

    private static Connection conn = null;

    public static void init() throws ClassNotFoundException {

        String driver = "com.mysql.jdbc.Driver";
        String db = "database name here";
        String url = "jdbc:mysql:// db ip here" + db;
        String user = "username here";
        String pass = "pass here";

        Properties p = new Properties();
        p.setProperty("user", user);
        p.setProperty("password", pass);
        p.setProperty("useTimezone", "true");
        p.setProperty("useJDBCCompliantTimezoneShift", "true");
        p.setProperty("useLegacyDatetimeCode", "false");
        p.setProperty("serverTimezone", ZoneId.systemDefault().toString());

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, p);
            System.out.println("Connected to database : " + db);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

        System.out.println("Session timezone: " + TimeZone.getDefault().getID());

    }

    public static Connection getConn() {
        return conn;
    }

    public static void close() {

        try {

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Connection closed.");
        }

    }

    public static boolean validateLogin(String user, String pass) {

        try {

            String query = "select * from user where userName = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("userId");
                String userName = rs.getString("userName");
                String password = rs.getString("password");

                if (userName == null || userName.equals("")) {

                    return false;
                } else {

                    if (pass.equals(password)) {

                        String qy = "set @cuser = ?";
                        PreparedStatement st = conn.prepareStatement(qy);
                        st.setString(1, user);

                        ResultSet r = st.executeQuery();

                        return true;

                    } else {
                        return false;
                    }
                }

            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

}
