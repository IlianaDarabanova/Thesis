package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import models.Garden;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CreateGardenViewController implements Initializable, GardenControllerClass {

    @FXML private Label errorMsg;
    @FXML private Label headerLabel;
    @FXML private DatePicker creatingDatePicker;
    @FXML private TextField descTextField;
    @FXML private TextField nameTextField;

    private Garden garden;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMsg.setText("");
        creatingDatePicker.setValue(LocalDate.now());
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
            if (garden != null) //we need to edit/update an existing volunteer
            {
                refGarden(event);

            }
            else    //we need to create a new plant
            {

            addGarden(event);

            }
}

    public void addGarden(ActionEvent event){

        if(fieldsFilled()){
            Garden currentGarden = new Garden(nameTextField.getText(), descTextField.getText(), creatingDatePicker.getValue());

            //do not show errors if creating Volunteer was successful
            try {
                currentGarden.insertIntoDB();
                garden=currentGarden;
                back(event);

            }
            catch(SQLIntegrityConstraintViolationException unique){
                errorMsg.setText("Градина с име \""+nameTextField.getText()+"\" вече съществува");
            }
            catch (SQLException e) {
               errorMsg.setText(e.getMessage());
            }
        }
    }

    public void refGarden(ActionEvent event){
        if(fieldsFilled()){
            updateGarden();
            try {
                garden.updateIntoDB();
                back(event);

            } catch (SQLException e) {
                errorMsg.setText(e.getMessage());
            }
        }

    }
    private void updateGarden() {
        if(nameTextField.getText().length()<1){
            nameTextField.getStyleClass().add("error");
            System.out.println("added");
        }
        if(descTextField.getText().length()<1){

        }
        garden.setName(this.nameTextField.getText());
        garden.setDescription(this.descTextField.getText());
        garden.setCreatingDate(this.creatingDatePicker.getValue());

    }

    public boolean fieldsFilled(){
        if(nameTextField.getText().length()<1){
            if(descTextField.getText().length()<1){
                descTextField.getStyleClass().add("error");
            }

            nameTextField.getStyleClass().add("error");

        }
        else if(descTextField.getText().length()<1){
            descTextField.getStyleClass().add("error");
        }
        else return true;
        return false;
    }

    public void nameWritten(KeyEvent event) {
        nameTextField.getStyleClass().remove("error");
    }

    public void descWritten(KeyEvent event) {
        descTextField.getStyleClass().remove("error");
    }
}
