package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Garden;
import models.Plant;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateGardenViewController implements Initializable, GardenControllerClass {

    @FXML Label headerLabel;
    @FXML private DatePicker creatingDatePicker;
    @FXML private TextField descTextField;
    @FXML private TextField nameTextField;

    private Garden garden;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void preloadData(Garden garden) {
        this.garden = garden;
        this.headerLabel.setText("Промени градина");
        nameTextField.setText(garden.getName());
        descTextField.setText(garden.getDescription());
        creatingDatePicker.setValue(garden.getCreatingDate());


    }

    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"ListGardens.fxml","Градини");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveGarden(ActionEvent event) {
        try
        {
            if (garden != null) //we need to edit/update an existing volunteer
            {
                updateGarden();
                try {
                    garden.updateIntoDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else    //we need to create a new plant
            {

                garden = new Garden(nameTextField.getText(),descTextField.getText(),creatingDatePicker.getValue());

                //do not show errors if creating Volunteer was successful
                garden.insertIntoDB();

            }
    }

        catch (Exception e)
    {
        System.err.println(e.getMessage());
    }

        back(event);



}

    private void updateGarden() {
        garden.setName(this.nameTextField.getText());
        garden.setDescription(this.descTextField.getText());
        garden.setCreatingDate(this.creatingDatePicker.getValue());

    }
    }
