package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import models.Plant;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML TableView<Plant> tableView;
    @FXML private TableColumn<Plant,String> plantColumn;
    @FXML private TableColumn<Plant,String> gardenColumn;
    @FXML private TableColumn<Plant,LocalDate> waterAtColumn;
    @FXML private TableColumn<Plant,LocalDate> nextWaterColumn;
    @FXML private Button waterBtn;
    @FXML private File imageFile;
    @FXML private ImageView imageView;
    private Plant plant;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        plantColumn.setCellValueFactory(new PropertyValueFactory<Plant, String>("plantName"));
        gardenColumn.setCellValueFactory(new PropertyValueFactory<Plant, String>("description"));
        waterAtColumn.setCellValueFactory(new PropertyValueFactory<Plant, LocalDate>("lastWaterDate"));
        nextWaterColumn.setCellValueFactory(new PropertyValueFactory<Plant, LocalDate>("nextWaterDate"));

        waterBtn.setDisable(true);

        try {
            loadTableViewDate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public void plantSelected(MouseEvent mouseEvent) {
        Plant myPlant = tableView.getSelectionModel().getSelectedItem();
        if(myPlant==null){
            return;
        }
        waterBtn.setDisable(false);
    }



    public void waterPlant(ActionEvent event) {
        Plant myPlant = tableView.getSelectionModel().getSelectedItem();

        int id = myPlant.getPlantId();
        int period = myPlant.getWaterPeriod();
        LocalDate nextPlantingDate = LocalDate.now().plusDays(period);

        try {
            plant.updateLastWaterDateInDB(id,nextPlantingDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            loadTableViewDate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadTableViewDate() throws SQLException {
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
                plant = new Plant(resultSet.getString("plant_name"),
                        resultSet.getString("name"),

                        resultSet.getDate("last_water_date").toLocalDate(),
                        resultSet.getDate("next_water_date").toLocalDate()
                        );
                plant.setPlantId(resultSet.getInt("plant_id"));
                plant.setWaterPeriod(resultSet.getInt("water_period"));


                plants.add(plant);
            }

            tableView.getItems().setAll(plants);

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
    }


    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"MainWindow.fxml","Начална страница");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
