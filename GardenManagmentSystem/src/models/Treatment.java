package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Treatment implements DBConnectionable{

    private int treatmentId;
    private String treatmentName, description;

    public Treatment(String treatmentName, String description){
        setTreatmentName(treatmentName);
        setDescription(description);
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * CRUD
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
            String sql = "INSERT INTO `gardens_database`.`treatments` ( `treatment_name`, `treatment_desc`) " +
                    "VALUES (?,?);\n";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);



            //4. Bind the values to the parameters
            preparedStatement.setString(1, getTreatmentName());
            preparedStatement.setString(2, getDescription());


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
            String sql = "UPDATE `gardens_database`.`treatments` " +
                    "SET `treatment_name` = ?, `treatment_desc` = ?" +
                    " WHERE (`treatment_id` = ?);\n";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);





            //4. bind the parameters
            preparedStatement.setString(1, getTreatmentName());
            preparedStatement.setString(2,getDescription());

            preparedStatement.setInt(3,getTreatmentId());



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
            String sql = "DELETE FROM `gardens_database`.`treatments` WHERE (`treatment_id` = ?);\n";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object


            //5. bind the parameters
            preparedStatement.setInt(1, getTreatmentId());




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
        return getTreatmentName();
    }
}
