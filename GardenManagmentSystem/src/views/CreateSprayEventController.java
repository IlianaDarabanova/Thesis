package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import models.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class CreateSprayEventController implements Initializable, EventControllerClass {
    @FXML
    private Label headerLabel;
    @FXML
    private DatePicker sprayDatePicker;
    @FXML
    private ComboBox<Garden> gardenComboBox;
    @FXML
    private ComboBox<Plant> plantComboBox;
    @FXML
    private ComboBox<Problem> problemComboBox;
    @FXML
    private ComboBox<Treatment> treatmentComboBox;

    private SprayEvent sprayEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadGardens();
            loadProblems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preloadData(SprayEvent sEvent) {
        this.sprayEvent = sEvent;
        this.headerLabel.setText("Промени напоняне");
        sprayDatePicker.setValue(sprayEvent.getEventDate());

        List<Garden> gardens = gardenComboBox.getItems();
        Garden gardenToSelect = null;
        for (Garden g : gardens) {
            if (g.getId() == sprayEvent.getGardenID()) {
                gardenToSelect = g;
                break;
            }

        }
        gardenComboBox.getSelectionModel().select(gardenToSelect);
        try {
            loadPlants(gardenToSelect.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Plant> plants = plantComboBox.getItems();

        Plant plantToSelect = null;
        for (Plant p : plants
                ) {
            if (p.getPlantId() == sprayEvent.getPlantID()) {
                plantToSelect = p;
                break;
            }

        }
        plantComboBox.getSelectionModel().select(plantToSelect);


        List<Problem> problems = problemComboBox.getItems();
        Problem problemToSelect = null;
        for (Problem p : problems) {
            if (p.getProblemId() == sprayEvent.getProblemID()) {
                problemToSelect = p;
                break;
            }
        }
        problemComboBox.getSelectionModel().select(problemToSelect);
        try {
            loadTreatments(problemToSelect.getProblemId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Treatment> treats = treatmentComboBox.getItems();
        Treatment treatToSelect = null;
        for (Treatment t : treats) {
            if (t.getTreatmentId() == sprayEvent.getTreatmentID()) {
                treatToSelect = t;
                break;
            }
        }
        treatmentComboBox.getSelectionModel().select(treatToSelect);
    }

    public void gardenSelected(ActionEvent event) {
        Garden selectedGarden = gardenComboBox.getValue();
        try {
            loadPlants(selectedGarden.getId());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        plantComboBox.getSelectionModel().selectFirst();

    }

    public void problemSelected(ActionEvent event) {
        Problem selectedProblem = problemComboBox.getValue();
        try {
            loadTreatments(selectedProblem.getProblemId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        treatmentComboBox.getSelectionModel().selectFirst();

    }

    public void saveEvent(ActionEvent event) throws SQLException {
        try {
            if (sprayEvent != null) //we need to edit/update an existing volunteer
            {

                try {
                    updateSprayEvent();
                    sprayEvent.updateIntoDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else    //we need to create a new plant
            {

                sprayEvent = new SprayEvent(gardenComboBox.getSelectionModel().getSelectedItem().getId(),
                        plantComboBox.getSelectionModel().getSelectedItem().getPlantId(),
                        problemComboBox.getSelectionModel().getSelectedItem().getProblemId(),
                        treatmentComboBox.getSelectionModel().getSelectedItem().getTreatmentId(),
                        sprayDatePicker.getValue());

                //do not show errors if creating Volunteer was successful
                sprayEvent.insertIntoDB();

            }
        } catch (Exception e)

        {
            System.err.println(e.getMessage());
        }

        back(event);

    }

    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"SprayEventsList.fxml","Напомняния");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGardens() throws SQLException {
        ObservableList<Garden> gardens = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM gardens");

            //4.  create plant objects from each record
            while (resultSet.next())
            {
               Garden myGarden = new Garden(resultSet.getString("name"),resultSet.getString("description"),resultSet.getDate("creating_date").toLocalDate());
               myGarden.setId(resultSet.getInt("id"));
                gardens.add(myGarden);
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
        gardenComboBox.getItems().addAll(gardens);
    }

    private void loadPlants(int gardenId) throws SQLException {
        ObservableList<Plant> plants = FXCollections.observableArrayList();
        String query = String.format("select * from plants where garden_id = %s",gardenId);
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery(query);

            //4.  create plant objects from each record
            while (resultSet.next())
            {
                Plant newPlant = new Plant(resultSet.getString("plant_name"),
                        resultSet.getString("description"),
                        resultSet.getDate("planting_date").toLocalDate(),
                        resultSet.getDate("last_water_date").toLocalDate(),
                        resultSet.getInt("water_period"),
                        resultSet.getInt("garden_id"));
                newPlant.setNextWaterDate();
                newPlant.setPlantId(resultSet.getInt("plant_id"));
                newPlant.setImageFile(new File(resultSet.getString("imageFile")));

                plants.add(newPlant);
            }

            plantComboBox.getItems().setAll(plants);

        } catch (Exception e)
        {
            System.err.println(e.getMessage());

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
    }

    private void loadProblems() throws SQLException {
        ObservableList<Problem> problems = FXCollections.observableArrayList();
        String query = "select * from problems";
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery(query);

            //4.  create plant objects from each record
            while (resultSet.next())
            {
                Problem newProblem = new Problem(resultSet.getString("problem_name"),resultSet.getString("type"),resultSet.getString("problem_desc"));
                newProblem.setProblemId(resultSet.getInt("problem_id"));
                problems.add(newProblem);

            }

            problemComboBox.getItems().setAll(problems);

        } catch (Exception e)
        {
            System.err.println(e.getMessage());

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
    }

    public void loadTreatments(int problemId) throws SQLException {
        ObservableList<Treatment> treatments = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();
            String formatedSql = String.format("select treatments.treatment_id, treatments.treatment_name, treatments.treatment_desc\n" +
                    "from treatments join problem_treatment \n" +
                    "on treatments.treatment_id = problem_treatment.treatment_id\n" +
                    "join problems\n" +
                    "on problem_treatment.problem_id = problems.problem_id\n" +
                    "where problems.problem_id = %d;",problemId);

            //3.  create the SQL query
            resultSet = statement.executeQuery(formatedSql);

            //4.  create plant objects from each record
            while (resultSet.next())

            {
                Treatment myTreat = new Treatment(resultSet.getString("treatment_name"),resultSet.getString("treatment_desc"));
                myTreat.setTreatmentId(resultSet.getInt("treatment_id"));
                treatments.add(myTreat);
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
        this.treatmentComboBox.getItems().setAll(treatments);
    }

    private void updateSprayEvent() {
        sprayEvent.setEventDate(sprayDatePicker.getValue());
        sprayEvent.setProblemID(problemComboBox.getSelectionModel().getSelectedItem().getProblemId());
        sprayEvent.setTreatmentID(treatmentComboBox.getSelectionModel().getSelectedItem().getTreatmentId());
        sprayEvent.setPlantID(plantComboBox.getSelectionModel().getSelectedItem().getPlantId());
        sprayEvent.setGardenID(gardenComboBox.getSelectionModel().getSelectedItem().getId());
    }
    }
