package models;

import java.sql.SQLException;

public interface DBConnectionable {
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/gardens_dataBase";
    static final String USER = "ADMIN";
    static final String PASSWORD = "ADMIN";
    void insertIntoDB() throws SQLException;
    void updateIntoDB() throws SQLException;
    void deleteFromDB() throws SQLException;

}
