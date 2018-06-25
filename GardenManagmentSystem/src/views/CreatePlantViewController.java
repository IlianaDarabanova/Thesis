package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Plant;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CreatePlantViewController implements Initializable,StringControllerClass, ControllerClass {
    @FXML private Label errorMessageLabel;
    @FXML private Label headerLabel;
    @FXML private TextField nameTextField;
    @FXML private TextField descTextField;
    @FXML private DatePicker creatingDatePicker;
    @FXML private DatePicker lastWaterDatePicker;
    @FXML private ComboBox<String> gardenComboBox;
    @FXML private Spinner<Integer> waterPeriodSpinner;
    @FXML private ImageView plantImageView;

    private File imageFile;
    private boolean imageFileChanged;
    private Plant plant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //set error message
        errorMessageLabel.setText("");

        imageFileChanged = false;

        try{
            imageFile = new File("./src/images/default.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            plantImageView.setImage(image);

        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        try {
            loadGardens();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,50,1,1);
        waterPeriodSpinner.setValueFactory(svf);
        waterPeriodSpinner.setEditable(true);

    }

    @Override
    public void preloadData(String string) {
        gardenComboBox.getSelectionModel().select(string);

    }

    @Override
    public void preloadData(Plant plant) {
        this.plant = plant;
        this.headerLabel.setText("Промени растение");
        try {
            this.gardenComboBox.getSelectionModel().select(getGardenName(plant.getGardenId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.nameTextField.setText(plant.getPlantName());
        this.descTextField.setText(plant.getDescription());
        this.creatingDatePicker.setValue(plant.getPlantingDate());
        this.lastWaterDatePicker.setValue(plant.getLastWaterDate());
        this.waterPeriodSpinner.getValueFactory().setValue(plant.getWaterPeriod());
        //load the image
        try{
            String imgLocation = ".\\src\\images\\" + plant.getImageFile().getName();
            imageFile = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            plantImageView.setImage(img);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

    }

    public void changeImage(ActionEvent event) {
        //get the Stage to open a new window (or Stage in JavaFX)
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        //Instantiate a FileChooser object
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        //filter for .jpg and .png
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);

        //Set to the user's picture directory or user directory if not available
        String userDirectoryString = System.getProperty("user.home")+"\\Pictures";
        File userDirectory = new File(userDirectoryString);

        //if you cannot navigate to the pictures directory, go to the user home
        if (!userDirectory.canRead())
            userDirectory = new File(System.getProperty("user.home"));

        fileChooser.setInitialDirectory(userDirectory);

        //open the file dialog window
        File tmpImageFile = fileChooser.showOpenDialog(stage);

        if (tmpImageFile != null)
        {
            imageFile = tmpImageFile;

            //update the ImageView with the new image
            if (imageFile.isFile())
            {
                try
                {
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    plantImageView.setImage(img);
                    imageFileChanged = true;
                }
                catch (IOException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public void savePlant(ActionEvent event) {
        try
        {
            if (plant != null) //we need to edit/update an existing volunteer
            {
                updatePlant();
                plant.updateIntoDB();
            }
            else    //we need to create a new plant
            {
                if (imageFileChanged) //create a plant with a custom image
                {
                    plant = new Plant(nameTextField.getText(),descTextField.getText(),
                            creatingDatePicker.getValue(),lastWaterDatePicker.getValue(),waterPeriodSpinner.getValue(),getGardenId(gardenComboBox.getSelectionModel().getSelectedItem()), imageFile);
                }
                else  //create a Volunteer with a default image
                {
                    plant = new Plant(nameTextField.getText(),descTextField.getText(),
                            creatingDatePicker.getValue(),lastWaterDatePicker.getValue(),waterPeriodSpinner.getValue(),getGardenId(gardenComboBox.getSelectionModel().getSelectedItem()), imageFile);

                }
                  //do not show errors if creating Volunteer was successful
                plant.insertIntoDB();

            }

//            SceneChanger sc = new SceneChanger();
//            sc.changeScenes(event, "VolunteerTableView.fxml", "All Volunteers");
        }
        catch (Exception e)
        {
            errorMessageLabel.setText(e.getMessage());
        }
        try {
            back(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void back(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        ListPlantsController lpc = new ListPlantsController();
        String gardenName = gardenComboBox.getValue();
        sc.changeScenes(event,"ListPlants.fxml","Растения",gardenName,lpc);
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
            gardenComboBox.getItems().addAll(gardens);

    }

    public void updatePlant() throws IOException, SQLException {
        plant.setPlantName(nameTextField.getText());
        plant.setDescription(descTextField.getText());
        plant.setPlantingDate(creatingDatePicker.getValue());
        plant.setGardenId(getGardenId(gardenComboBox.getValue().toString()));
        plant.setLastWaterDate(lastWaterDatePicker.getValue());
        plant.setWaterPeriod(waterPeriodSpinner.getValue());
        plant.setNextWaterDate();
        plant.setImageFile(imageFile);
        if (imageFileChanged)
            plant.copyImageFile();

    }

    public int getGardenId(String name) throws SQLException {

        int id = -1;
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

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
