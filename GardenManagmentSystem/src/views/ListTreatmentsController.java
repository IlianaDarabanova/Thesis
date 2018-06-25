package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Garden;
import models.Problem;
import models.Treatment;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ListTreatmentsController implements Initializable {
    public TableView<Treatment> tableView;
    public TableColumn<Treatment, String> nameColumn;
    public TableColumn<Treatment, String> descColumn;
    public Button updateTreatment;
    public Button deleteTreatment;

    SceneChanger sc = new SceneChanger();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTreatment.setDisable(true);
        deleteTreatment.setDisable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Treatment, String>("treatmentName"));
        descColumn.setCellValueFactory(new PropertyValueFactory<Treatment, String>("description"));

        try {
            loadTreatments();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void back(ActionEvent event) {

        try {
            sc.changeScenes(event,"MainWindow.fxml","Начална страница");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTreatment(ActionEvent event) {
        try {
            sc.changeScenes(event,"CreateTreatment.fxml","Добави препарат");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeTreatment(ActionEvent event) {
        try {
            sc.changeScenes(event,"CreateTreatment.fxml","Промени препарат",tableView.getSelectionModel().getSelectedItem(),new CreateTreatmentController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTreatment(ActionEvent event) {
        Treatment selected = tableView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE, "Сигурни ли сте, че искате да изтриете препарат \"" +  selected.toString() + "\" ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            try {
                selected.deleteFromDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
            alert.close();

        try {
            loadTreatments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void treatSelected(MouseEvent mouseEvent) {
        Treatment treatment = tableView.getSelectionModel().getSelectedItem();
        if (treatment == null) {
            return;
        } else {
            updateTreatment.setDisable(false);
            deleteTreatment.setDisable(false);
        }
    }

    private void loadTreatments() throws SQLException {
        ObservableList<Treatment> treats = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM treatments");

            //4.  create plant objects from each record
            while (resultSet.next()) {
                Treatment myTreatment = new Treatment(resultSet.getString("treatment_name"), resultSet.getString("treatment_desc"));
                myTreatment.setTreatmentId(resultSet.getInt("treatment_id"));

                treats.add(myTreatment);
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
        tableView.getItems().setAll(treats);
    }


}




