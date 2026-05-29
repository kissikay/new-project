package com.academic.database;

import com.academic.model.User;
import com.academic.model.Mark;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:academic_report.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // CREATE IF NOT EXISTS — never drop, so data survives restarts
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "index_number TEXT NOT NULL," +
                    "programme TEXT NOT NULL," +
                    "level INTEGER NOT NULL," +
                    "semester INTEGER NOT NULL," +
                    "course_count INTEGER NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS marks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "course_code TEXT NOT NULL," +
                    "course_name TEXT NOT NULL," +
                    "credit_hours INTEGER NOT NULL," +
                    "score REAL NOT NULL," +
                    "grade TEXT NOT NULL," +
                    "remark TEXT NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean registerUser(String username, String password, String indexNumber,
                                        String programme, int level, int semester, int courseCount) {
        String sql = "INSERT INTO users(username, password, index_number, programme, level, semester, course_count) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, indexNumber);
            pstmt.setString(4, programme);
            pstmt.setInt(5, level);
            pstmt.setInt(6, semester);
            pstmt.setInt(7, courseCount);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("index_number"),
                    rs.getString("programme"),
                    rs.getInt("level"),
                    rs.getInt("semester"),
                    rs.getInt("course_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addMark(int userId, String courseCode, String courseName,
                                int creditHours, double score, String grade, String remark) {
        String sql = "INSERT INTO marks(user_id, course_code, course_name, credit_hours, score, grade, remark) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, courseCode);
            pstmt.setString(3, courseName);
            pstmt.setInt(4, creditHours);
            pstmt.setDouble(5, score);
            pstmt.setString(6, grade);
            pstmt.setString(7, remark);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Mark> getUserMarks(int userId) {
        List<Mark> marks = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                marks.add(new Mark(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credit_hours"),
                    rs.getDouble("score"),
                    rs.getString("grade"),
                    rs.getString("remark")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marks;
    }

    /** Returns true if the user already has marks saved in the database. */
    public static boolean hasMarks(int userId) {
        String sql = "SELECT COUNT(*) FROM marks WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Deletes all marks for a given user (used before re-submission). */
    public static void deleteUserMarks(int userId) {
        String sql = "DELETE FROM marks WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
