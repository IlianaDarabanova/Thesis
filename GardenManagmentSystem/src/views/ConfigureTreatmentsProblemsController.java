package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Problem;
import models.Treatment;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ConfigureTreatmentsProblemsController implements Initializable {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/gardens_dataBase";
    private static final String USER = "ADMIN";
    private static final String PASSWORD = "ADMIN";

    @FXML private ListView<Problem> allProblemsListView;
    @FXML private ListView<Problem> selectedProblemsListView;
    @FXML private ComboBox<Treatment> treatmentComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadTreatments();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        treatmentComboBox.getSelectionModel().selectFirst();
        try {
            loadUnselcetedProblems(treatmentComboBox.getSelectionModel().getSelectedItem().getTreatmentId());
            loadSelectedProblems(treatmentComboBox.getSelectionModel().getSelectedItem().getTreatmentId());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
    public void treatmentSelected(ActionEvent event) {
        Treatment selected = treatmentComboBox.getValue();
        int treatmentId = selected.getTreatmentId();
        try {
            loadSelectedProblems(treatmentId);
            loadUnselcetedProblems(treatmentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void includeToSelected(ActionEvent event) throws SQLException {
        Treatment selectedTr = treatmentComboBox.getSelectionModel().getSelectedItem();
        Problem problemToInsert =  allProblemsListView.getSelectionModel().getSelectedItem();
        int problemID= problemToInsert.getProblemId();
        int treatmentID = selectedTr.getTreatmentId();
        insertIntoDB(problemID,treatmentID);
        loadUnselcetedProblems(treatmentID);
        loadSelectedProblems(treatmentID);

    }

    public void excludeFromSelected(ActionEvent event) {
        Treatment selectedTr = treatmentComboBox.getSelectionModel().getSelectedItem();
        Problem problemToInsert =  selectedProblemsListView.getSelectionModel().getSelectedItem();
        int problemID= problemToInsert.getProblemId();
        int treatmentID = selectedTr.getTreatmentId();
        try {
            deleteFromDB(problemID,treatmentID);
        } catch (SQLException e) {
            //TODO add label in wich to inform the user
        }
        try {
        loadSelectedProblems(treatmentID);
        loadUnselcetedProblems(treatmentID);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }



    public void saveConfiguration(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event,"MainWindow.fxml","Начална страница");
    }

    public void loadTreatments() throws SQLException {

        ObservableList<Treatment> treatments = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM treatments");

            //4.  create plant objects from each record
            while (resultSet.next())
            {   Treatment newTreatment = new Treatment(resultSet.getString("treatment_name"),resultSet.getString("treatment_desc"));
                newTreatment.setTreatmentId(resultSet.getInt("treatment_id"));
                treatments.add(newTreatment);
            }


        } catch (Exception e)
        {
            System.err.println(e);
        }
        finally
        {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
        this.treatmentComboBox.getItems().addAll(treatments);

    }

    public void loadSelectedProblems(int treatmentId) throws SQLException {

        ObservableList<Problem> problems = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();
            String formatedSql = String.format("select problems.problem_name, problems.problem_id, problems.type, problem_desc\n" +
                    "        from problems join problem_treatment\n" +
                    "        on problems.problem_id = problem_treatment.problem_id\n" +
                    "        join treatments\n" +
                    "        on problem_treatment.treatment_id = treatments.treatment_id\n" +
                    "        where treatments.treatment_id = %d;",treatmentId);

            //3.  create the SQL query
            resultSet = statement.executeQuery(formatedSql);

            //4.  create plant objects from each record
            while (resultSet.next())

            {
                Problem selectedProblem = new Problem(resultSet.getString("problem_name"),resultSet.getString("type"),resultSet.getString("problem_desc"));
                selectedProblem.setProblemId(resultSet.getInt("problem_id"));
                problems.add(selectedProblem);
            }


        } catch (Exception e)
        {
            System.err.println(e);
        }
        finally
        {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
        this.selectedProblemsListView.getItems().setAll(problems);



    }

    public void loadUnselcetedProblems(int treatmentId) throws SQLException {
        ObservableList<Problem> problems = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();
            String formatedSql = String.format("select * from problems\n" +
                    "where not problem_id in(\n" +
                    "select problem_id from problem_treatment\n" +
                    "join treatments\n" +
                    "on treatments.treatment_id = problem_treatment.treatment_id\n" +
                    "where treatments.treatment_id = %d);",treatmentId);

            //3.  create the SQL query
            resultSet = statement.executeQuery(formatedSql);

            //4.  create plant objects from each record
            while (resultSet.next())

            {
                Problem unselectedProblem = new Problem(resultSet.getString("problem_name"),resultSet.getString("type"),resultSet.getString("problem_desc"));
                unselectedProblem.setProblemId(resultSet.getInt("problem_id"));
                problems.add(unselectedProblem);
            }


        } catch (Exception e)
        {
            System.err.println(e);
        }
        finally
        {
            if (conn != null)
                conn.close();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
        this.allProblemsListView.getItems().setAll(problems);


    }

    public void insertIntoDB(int problemId,int treatmentId) throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //1. Connect to the database
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2. Create a String that holds the query with ? as user inputs
            String sql = "INSERT INTO `gardens_database`.`problem_treatment` (`problem_id`, `treatment_id`)" +
                    " VALUES (?,?);\n";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);




            //5. Bind the values to the parameters
            preparedStatement.setInt(1, problemId);
            preparedStatement.setInt(2, treatmentId);


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

    private void deleteFromDB(int problemID, int treatmentID) throws SQLException {

        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //1. Connect to the database
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2. Create a String that holds the query with ? as user inputs
            String sql = "DELETE FROM `gardens_database`.`problem_treatment` " +
                    "WHERE (`problem_id` = ?) and (`treatment_id` = ?);";
            //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);

            //5. Bind the values to the parameters
            preparedStatement.setInt(1, problemID);
            preparedStatement.setInt(2, treatmentID);


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



}
