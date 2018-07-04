package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Garden;
import models.Plant;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ListGardensController implements Initializable {
    @FXML private TableView<Garden> tableView;
    @FXML private TableColumn<Garden,String> nameColumn;
    @FXML private TableColumn<Garden,String> descriptionColumn;
    @FXML private TableColumn<Garden,LocalDate> creatingDateColumn;
    @FXML private Button deleteGardenBtn;
    @FXML private Button changeGardenBtn;

    Garden garden;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeGardenBtn.setDisable(true);
        deleteGardenBtn.setDisable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Garden, String>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Garden, String>("description"));
        creatingDateColumn.setCellValueFactory(new PropertyValueFactory<Garden, LocalDate>("creatingDate"));

        try {
            loadGardens();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void gardenSelected(MouseEvent mouseEvent) {
        Garden newGarden = tableView.getSelectionModel().getSelectedItem();
        if(newGarden==null){
            return;
        }
        else{
            changeGardenBtn.setDisable(false);
            deleteGardenBtn.setDisable(false);
        }

    }

    public void deleteGarden(ActionEvent event) {
        Garden garden = tableView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE, "Сигурни ли сте, че искате да изтриете градина \"" +  tableView.getSelectionModel().getSelectedItem().toString() + "\" ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            try {
                garden.deleteFromDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
            alert.close();

        try {
            loadGardens();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteGardenBtn.setDisable(true);
        changeGardenBtn.setDisable(true);
    }

    public void changeGarden(ActionEvent event) {
       Garden garden = tableView.getSelectionModel().getSelectedItem();
       CreateGardenViewController cgvc = new CreateGardenViewController();
       SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"CreateGardenView.fxml","Промени градина",garden,cgvc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGarden(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"CreateGardenView.fxml","Добави градина");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event,"MainWindow.fxml","Начална страница");
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
                Garden newGarden = new Garden(resultSet.getString("name"),
                        resultSet.getString("description"),
                                resultSet.getDate("creating_date").toLocalDate());
                newGarden.setId(resultSet.getInt("id"));

                gardens.add(newGarden);
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
        tableView.getItems().setAll(gardens);

    }


}
