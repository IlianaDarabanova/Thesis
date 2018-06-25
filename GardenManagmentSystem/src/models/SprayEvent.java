package models;

import java.sql.*;
import java.time.LocalDate;

public class SprayEvent implements DBConnectionable{

    private int eventID;
    private int gardenID;
    private int plantID;
    private int problemID;
    private int treatmentID;
    private String gardenName;
    private String plantName;
    private String problemName;
    private String treatmentName;
    private LocalDate eventDate;

    public SprayEvent(int gardenID, int plantID, int problemID, int treatmentID, LocalDate eventDate) {
        setGardenID(gardenID);
        setPlantID(plantID);
        setProblemID(problemID);
        setTreatmentID(treatmentID);
        setEventDate(eventDate);
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getGardenID() {
        return gardenID;
    }

    public void setGardenID(int gardenID) {
        this.gardenID = gardenID;
    }

    public int getPlantID() {
        return plantID;
    }

    public void setPlantID(int plantID) {
        this.plantID = plantID;
    }

    public int getProblemID() {
        return problemID;
    }

    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    public int getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(int treatmentID) {
        this.treatmentID = treatmentID;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getGardenName() {
        return gardenName;
    }

    public void setGardenName(String gardenName) {
        this.gardenName = gardenName;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public void insertIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //1. Connect to the database
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2. Create a String that holds the query with ? as user inputs
            String sql = "    INSERT INTO `gardens_database`.`sevents` (`garden_id`, `plant_id`, `problem_id`, `treatment_id`, `spray_date`) " +
                    "VALUES (?,?,?,?,?);\n";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);

            //4. Convert the LocalDate into a SQL date
            Date eventDate = Date.valueOf(getEventDate());


            //5. Bind the values to the parameters
            preparedStatement.setInt(1, getGardenID());
            preparedStatement.setInt(2, getPlantID());
            preparedStatement.setInt(3, getProblemID());
            preparedStatement.setInt(4, getTreatmentID());
            preparedStatement.setDate(5,eventDate);



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

    public void updateIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);


            String sql = "update sevents \n" +
                    "set garden_id=?, plant_id=?, problem_id=?, treatment_id=?, spray_date=?\n" +
                    "where event_id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object
            Date cd = Date.valueOf(getEventDate());

            preparedStatement.setInt(1,getGardenID());
            preparedStatement.setInt(2,getPlantID());
            preparedStatement.setInt(3,getProblemID());
            preparedStatement.setInt(4,getTreatmentID());
            preparedStatement.setDate(5,cd);
            preparedStatement.setInt(6,getEventID());



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
            String sql = "delete from sevents where event_id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object


            //5. bind the parameters
            preparedStatement.setInt(1, getEventID());




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
        return getEventDate().toString();
    }
}
