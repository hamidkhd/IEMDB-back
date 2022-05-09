//package ir.ac.ut.ie.Repository;
//
//import ir.ac.ut.ie.Entities.Movie;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class IEMDBRepository {
//    private static IEMDBRepository adInstance;
//
//
//    public static IEMDBRepository getInstance() throws SQLException {
//        if (adInstance == null) {
//            try {
//                adInstance = new IEMDBRepository();
//            } catch (SQLException e) {
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//            }
//        }
//        return adInstance;
//    }
//
//    private IEMDBRepository() throws SQLException {
//        Connection con = ConnectionPool.getConnection();
//        con.setAutoCommit(false);
//        Statement stmt = con.createStatement();
//        stmt.addBatch("CREATE TABLE IF NOT EXISTS Movie(id INT, name CHAR(200), summary CHAR(200), releaseDate CHAR(20), director CHAR(20)," +
//                "                    imdbRate FLOAT, duration INT, ageLimit INT, rating FLOAT, image CHAR(200), coverImage CHAR(200)," +
//                "                    PRIMARY KEY(id));");
//
//        stmt.executeBatch();
//        stmt.close();
//        con.close();
//    }
//
//    public void insertMovies(Movie[] moviesList) throws SQLException {
//        Connection con = ConnectionPool.getConnection();
//        con.setAutoCommit(false);
//
//        PreparedStatement stm = con.prepareStatement("INSERT INTO Movie VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on duplicate key update id = id");
//
//        for (Movie movie: moviesList) {
//            try {
//                stm.setInt(1, movie.getId());
//                stm.setString(2, movie.getName());
//                stm.setString(3, movie.getSummary());
//                stm.setString(4, movie.getReleaseDate());
//                stm.setString(5, movie.getDirector());
//                stm.setFloat(6, movie.getImdbRate());
//                stm.setInt(7, movie.getDuration());
//                stm.setInt(8, movie.getAgeLimit());
//                stm.setFloat(9, movie.getRating());
//                stm.setString(10, movie.getImage());
//                stm.setString(11, movie.getCoverImage());
//                stm.addBatch();
//            } catch (SQLException e) {
//                System.out.println(e.getMessage());
//            }
//
//            stm.executeBatch();
//            stm.close();
//            con.close();
//        }
//    }
//
//
//}
