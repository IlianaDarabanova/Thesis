package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SprayEventsListController implements Initializable {
    @FXML private DatePicker datePicker;
    @FXML private Button deleteSprayEvent;
    @FXML private Button updateSprayEvent;
    @FXML private TableView<SprayEvent> tableView;
    @FXML private TableColumn<SprayEvent,LocalDate> dateColumn;
    @FXML private TableColumn<SprayEvent,String> gardenColumn;
    @FXML private TableColumn<SprayEvent,String> plantColumn;
    @FXML private TableColumn<SprayEvent,String> problemColumn;
    @FXML private TableColumn<SprayEvent,String> treatmentColumn;
    SceneChanger sc  = new SceneChanger();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        deleteSprayEvent.setDisable(true);
        updateSprayEvent.setDisable(true);

        dateColumn.setCellValueFactory(new PropertyValueFactory<SprayEvent, LocalDate>("eventDate"));
        gardenColumn.setCellValueFactory(new PropertyValueFactory<SprayEvent, String>("gardenName"));
        plantColumn.setCellValueFactory(new PropertyValueFactory<SprayEvent, String>("plantName"));
        problemColumn.setCellValueFactory(new PropertyValueFactory<SprayEvent,String>("problemName"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<SprayEvent,String>("treatmentName"));
        try {
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void loadEvents() throws SQLException {

        ObservableList<SprayEvent> sprayEvents = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();
            String sql = "select gardens.name,gardens.id, plants.plant_name,plants.plant_id,problems.problem_name,problems.problem_id,treatments.treatment_name,treatments.treatment_id, sevents.spray_date,sevents.event_id\n" +
                    "from sevents join\n" +
                    "plants on sevents.plant_id = plants.plant_id\n" +
                    "join gardens \n" +
                    "on sevents.garden_id = gardens.id\n" +
                    "join problems\n" +
                    "on problems.problem_id = sevents.problem_id\n" +
                    "join treatments \n" +
                    "on treatments.treatment_id = sevents.treatment_id\n";






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
        tableView.getItems().setAll(sprayEvents);


    }

    private void loadEvents(String selectedDate) throws SQLException {

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
        tableView.getItems().setAll(sprayEvents);


    }

   public void eventSelected(MouseEvent mouseEvent) {
        SprayEvent sevent = tableView.getSelectionModel().getSelectedItem();
        if(sevent==null){
            return;
        }
        else {
            updateSprayEvent.setDisable(false);
            deleteSprayEvent.setDisable(false);
        }
    }

    public void addSprayEvent(ActionEvent event) {

        try {
            sc.changeScenes(event,"CreateSprayEvent.fxml","Добави Напомняне");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updataSprayEvent(ActionEvent event) {

        try {
            sc.changeScenes(event,"CreateSprayEvent.fxml","Редактирай напомняне",tableView.getSelectionModel().getSelectedItem(),new CreateSprayEventController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSprayEvenet(ActionEvent event) {
        ObservableList<SprayEvent> currentItems = FXCollections.observableArrayList();
        currentItems.setAll(tableView.getItems());

           SprayEvent event1 = tableView.getSelectionModel().getSelectedItem();

         Alert alert = new Alert(Alert.AlertType.NONE, "Сигурни ли сте, че искате да изтриете напоняне с дата \"" +  event1.toString()+ "\" ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {

                try {
                    event1.deleteFromDB();
                    currentItems.remove(event1);
                    System.out.println(currentItems.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else{
                alert.close();}

            tableView.getItems().setAll(currentItems);

    }

    public void back(ActionEvent event) {
        try {
            sc.changeScenes(event,"MainWindow.fxml","Начална страница");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void searchBtnClicked(ActionEvent event) {

        try {
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dateChanged(ActionEvent event) {
        try {
            loadEvents(Date.valueOf(datePicker.getValue()).toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
