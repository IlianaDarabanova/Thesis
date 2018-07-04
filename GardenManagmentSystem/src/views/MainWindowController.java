package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import models.Plant;
import models.SprayEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public Button waterBtn;
    public Button sprayBtn;
    private SceneChanger sc = new SceneChanger();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if(isExpectingWater()){

                waterBtn.getStyleClass().add("red");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(isWaitingSpray(LocalDate.now().toString())){
                sprayBtn.getStyleClass().add("red");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public boolean isExpectingWater() throws SQLException {
        ObservableList<Plant> plants = FXCollections.observableArrayList();
        String query = String.format("select plants.plant_id, plants.plant_name,plants.last_water_date,plants.next_water_date,gardens.name,plants.water_period\n" +
                "from plants\n" +
                "inner join gardens\n" +
                "on plants.garden_id = gardens.id\n" +
                "where plants.next_water_date = date(now());");
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
                Plant plant = new Plant(resultSet.getString("plant_name"),
                        resultSet.getString("name"),

                        resultSet.getDate("last_water_date").toLocalDate(),
                        resultSet.getDate("next_water_date").toLocalDate()
                );
                plant.setPlantId(resultSet.getInt("plant_id"));
                plant.setWaterPeriod(resultSet.getInt("water_period"));


                plants.add(plant);
            }


        } catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();

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
        if(plants.size()>0){
            return true;
        }
        return false;
    }

    public boolean isWaitingSpray(String selectedDate) throws SQLException {
        ObservableList<SprayEvent> sprayEvents = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();
            String sql = String.format("select gardens.name,gardens.id, plants.plant_name,plants.plant_id,problems.problem_name,problems.problem_id,treatments.treatment_name,treatments.treatment_id, sevents.spray_date,sevents.event_id\n" +
                    "from sevents join\n" +
                    "plants on sevents.plant_id = plants.plant_id\n" +
                    "join gardens \n" +
                    "on sevents.garden_id = gardens.id\n" +
                    "join problems\n" +
                    "on problems.problem_id = sevents.problem_id\n" +
                    "join treatments \n" +
                    "on treatments.treatment_id = sevents.treatment_id\n" +
                    "where spray_date = '%s';",selectedDate);





            //3.  create the SQL query
            resultSet = statement.executeQuery(sql);

            //4.  create plant objects from each record
            while (resultSet.next()) {

                SprayEvent newSevent = new SprayEvent(resultSet.getInt("id"), resultSet.getInt("plant_id"),
                        resultSet.getInt("problem_id"), resultSet.getInt("treatment_id"), resultSet.getDate("spray_date").toLocalDate());
                newSevent.setPlantName(resultSet.getString("plant_name"));
                newSevent.setGardenName(resultSet.getString("name"));
                newSevent.setProblemName(resultSet.getString("problem_name"));
                newSevent.setTreatmentName(resultSet.getString("treatment_name"));
                newSevent.setEventID(resultSet.getInt("event_id"));
                sprayEvents.add(newSevent);


            }


        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
        if(sprayEvents.size()>0){
            return true;
        }
        return false;
    }

    public void listUpcomingWater(ActionEvent event) {
        try {
            sc.changeScenes(event,"HomePage.fxml","Предстоящи поливания");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listUpcomingSpray(ActionEvent event) {
        try {
            sc.changeScenes(event,"SprayEventsList.fxml","Предстоящи пръскания");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listGardens(ActionEvent event) {
        try {
            sc.changeScenes(event,"ListGardens.fxml","Списък градини");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listPlants(ActionEvent event) {
        try {
            sc.changeScenes(event,"ListPlants.fxml","Списък растения");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listProblems(ActionEvent event) {
        try {
            sc.changeScenes(event,"ListProblems.fxml","Вредители");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listTreatments(ActionEvent event) {
        try {
            sc.changeScenes(event,"ListTreatments.fxml","Методи за обработка на растения");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configure(ActionEvent event) {
        try {
            sc.changeScenes(event,"ConfigureTreatmentsProblems.fxml","Конфигуриране на препарати");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
