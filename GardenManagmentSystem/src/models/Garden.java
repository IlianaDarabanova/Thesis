package models;

import java.sql.*;
import java.time.LocalDate;

public class Garden implements DBConnectionable{


    private int id;
    private String name,description;
    private LocalDate creatingDate;

    public Garden(String name, String description,LocalDate creatingDate){
        setName(name);
        setDescription(description);
        setCreatingDate(creatingDate);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatingDate() {
        return creatingDate;
    }

    public void setCreatingDate(LocalDate creatingDate) {
        this.creatingDate = creatingDate;
    }

    /**
     * This method will write the instance of the garden into the database
     */
    public void insertIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //1. Connect to the database
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2. Create a String that holds the query with ? as user inputs
            String sql = "insert into gardens(name,description,creating_date) " +
                    "values (?,?,?)";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);

            //4. Convert the LocalDate into a SQL date
            Date cd = Date.valueOf(creatingDate);


            //5. Bind the values to the parameters
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, cd);

            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (preparedStatement != null)
                preparedStatement.close();

            if (conn != null)
                conn.close();
        }
    }


    /**
     * This will update the garden in the database
     */
    public void updateIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2.  create a String that holds our SQL update command with ? for user inputs
            String sql = "update gardens \n" +
                    "set name = ?, description=?, creating_date=?\n" +
                    "where id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object
            Date cd = Date.valueOf(creatingDate);


            //5. bind the parameters
            preparedStatement.setString(1, name);
            preparedStatement.setString(2,description);
            preparedStatement.setDate(3,cd);
            preparedStatement.setInt(4,id);



            //6. run the command on the SQL server
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }

    }

    public void deleteFromDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2.  create a String that holds our SQL update command with ? for user inputs
            String sql = "delete from gardens where id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object


            //5. bind the parameters
            preparedStatement.setInt(1, getId());




            //6. run the command on the SQL server
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }

    }

    @Override
    public String toString() {
        return getName();
    }
}
