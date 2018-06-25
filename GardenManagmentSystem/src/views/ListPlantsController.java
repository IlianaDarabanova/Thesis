package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Plant;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ListPlantsController implements Initializable, StringControllerClass {
    @FXML private ComboBox<String> gardensComboBox;
    @FXML private TableView<Plant> plantTableView;
    @FXML private TableColumn<Plant, String> nameColumn;
    @FXML private TableColumn<Plant, String> descColumn;
    @FXML private TableColumn<Plant, LocalDate> plantedAtColumn;
    @FXML private TableColumn<Plant, LocalDate> lastWateredColumn;
    @FXML private TableColumn<Plant, Integer> periodColumn;
    @FXML private TableColumn<Plant, LocalDate> nextWaterColumn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
        try {
            loadGardens();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gardensComboBox.getSelectionModel().selectFirst();

        nameColumn.setCellValueFactory(new PropertyValueFactory<Plant, String>("plantName"));
        descColumn.setCellValueFactory(new PropertyValueFactory<Plant, String>("description"));
        plantedAtColumn.setCellValueFactory(new PropertyValueFactory<Plant, LocalDate>("plantingDate"));
        lastWateredColumn.setCellValueFactory(new PropertyValueFactory<Plant, LocalDate>("lastWaterDate"));
        periodColumn.setCellValueFactory(new PropertyValueFactory<Plant,Integer>("waterPeriod"));
        nextWaterColumn.setCellValueFactory(new PropertyValueFactory<Plant, LocalDate>("nextWaterDate"));

        try {
            loadPlants(getGardenId(gardensComboBox.getSelectionModel().getSelectedItem()));
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void preloadData(String string)  {
        gardensComboBox.getSelectionModel().select(string);
        try {
            loadPlants(getGardenId(string));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void gardenSelected(ActionEvent event) {
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
        try {
            loadPlants(getGardenId(gardensComboBox.getSelectionModel().getSelectedItem()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void plantSelected(MouseEvent mouseEvent) {
        Plant plant = plantTableView.getSelectionModel().getSelectedItem();
        if(plant==null){
            return;
        }
        else{
            editBtn.setDisable(false);
            deleteBtn.setDisable(false);
        }

    }


    public void addPlant(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        CreatePlantViewController cpvc = new CreatePlantViewController();
        String string = gardensComboBox.getValue();
        sc.changeScenes(event,"CreatePlantView.fxml","Добави растение",string,cpvc);

    }

    public void editPlant(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        Plant selectedPlant = plantTableView.getSelectionModel().getSelectedItem();
        CreatePlantViewController cpvc = new CreatePlantViewController();
        sc.changeScenes(event,"CreatePlantView.fxml","Промени растение",selectedPlant,cpvc);
    }

    public void deletePlant(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.NONE, "Сигурни ли сте, че искате да изтриете растението \"" +  plantTableView.getSelectionModel().getSelectedItem().toString() + "\" ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Plant toDelete = plantTableView.getSelectionModel().getSelectedItem();

            try {
                toDelete.deleteFromDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else{
            alert.close();
        }
        loadPlants(getGardenId(gardensComboBox.getValue()));
    }

    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"MainWindow.fxml","Начална страница");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGardens() throws SQLException {


        ObservableList<String> gardens = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT name FROM gardens");

            //4.  create plant objects from each record
            while (resultSet.next())
            {
                gardens.add(resultSet.getString("name"));
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
        gardensComboBox.getItems().addAll(gardens);

    }

    private void loadPlants(int id) throws SQLException{
        ObservableList<Plant> plants = FXCollections.observableArrayList();
        String query = String.format("select * from plants where garden_id = %s",id);
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

            plantTableView.getItems().setAll(plants);

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

    public int getGardenId(String name) throws SQLException {

        int id = -1;
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String selectedGarden = gardensComboBox.getValue();

        String selectId = String.format("SELECT id FROM gardens WHERE name = '%s'",name);



        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery(selectId);

            //4.  create plant objects from each record


            while (resultSet.next()){
                id = resultSet.getInt("id");
            }

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

        return id;

    }

    /**
     * This method will return the name of a garden by given id
     */
    public String getGardenName(int ID) throws SQLException {

        String name = "";
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String selectId = String.format("SELECT name FROM gardens WHERE id = '%d'",ID);



        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery(selectId);

            //4.  create plant objects from each record


            while (resultSet.next()){
                name = resultSet.getString("name");
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

        return name;


    }

}
