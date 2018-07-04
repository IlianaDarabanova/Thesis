package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Problem implements DBConnectionable{


    private int problemId;
    private String name, description;
    private ProblemType problemType;



    public Problem(String name, String problemType, String description){
        setName(name);
        setProblemType(problemType);
        setDescription(description);



    }
    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
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

    public String getProblemType() {
            String name = problemType.name().toLowerCase();
            String pType = "";
            switch (name) {
                case "bug":
                   pType = "Насекомо";
                   break;
                case "disease":
                   pType = "Болест";
                    break;
                case "animal":
                    pType = "Животно";
                    break;
                default:
                    throw new IllegalArgumentException("There is no such ProblemType!");
            }
            return pType;

    }



    public void setProblemType(String problemType) {
        switch (problemType.toLowerCase()){
            case "bug": this.problemType = ProblemType.Bug;
            break;
            case "disease": this.problemType = ProblemType.Disease;
            break;
            case "animal": this.problemType = ProblemType.Animal;
            break;
            default: throw new IllegalArgumentException("There is no such ProblemType!");
        }

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
            String sql = "INSERT INTO `gardens_database`.`problems` (`type`, `problem_name`, `problem_desc`)" +
                    " VALUES (?,?,?);\n";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);



            //4. Bind the values to the parameters
            preparedStatement.setString(1, getProblemType());
            preparedStatement.setString(2, getName());
            preparedStatement.setString(3, getDescription());

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
            String sql = "UPDATE `gardens_database`.`problems` " +
                    "SET `type` = ?, `problem_name` = ?, `problem_desc` = ?" +
                    " WHERE (`problem_id` = ?);\n";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);





            //4. bind the parameters
            preparedStatement.setString(1, getProblemType());
            preparedStatement.setString(2,getName());
            preparedStatement.setString(3,getDescription());
            preparedStatement.setInt(4,getProblemId());



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
            String sql = "DELETE FROM `gardens_database`.`problems` WHERE (`problem_id` = ?);";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object


            //5. bind the parameters
            preparedStatement.setInt(1, getProblemId());




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
