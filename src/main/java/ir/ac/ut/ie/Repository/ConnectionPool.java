//package ir.ac.ut.ie.Repository;
//
//import org.apache.commons.dbcp.BasicDataSource;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//
//public class ConnectionPool {
//    private final static String dbURL = "jdbc:mysql://localhost:3306/IEMDB?useSSL=false";
//    private final static String dbUsername = "root";
//    private final static String dbPassword = "@Hamid78";
//
//    private static BasicDataSource ds = new BasicDataSource();
//
//    public static Connection getConnection() throws SQLException {
//        return ds.getConnection();
//    }
//
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
//        ds.setUrl(dbURL);
//        ds.setUsername(dbUsername);
//        ds.setPassword(dbPassword);
//        ds.setMinIdle(5);
//        ds.setMaxIdle(10);
//        ds.setMaxOpenPreparedStatements(100);
//        setEncoding();
//    }
//
//    public static void setEncoding(){
//        try {
//            Connection connection = getConnection();
//            Statement statement = connection.createStatement();
//            statement.execute("ALTER DATABASE IEMDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
//            connection.close();
//            statement.close();
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}