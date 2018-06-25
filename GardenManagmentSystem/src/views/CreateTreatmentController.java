package views;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Garden;
import models.Treatment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateTreatmentController implements Initializable, TreatControllerClass {
    public Label headerLabel;
    public TextField nameTextField;
    public TextField descTextField;

    private Treatment treatment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void preloadData(Treatment treatment) {
        this.treatment = treatment;
        this.headerLabel.setText("Промени препарат");
        this.descTextField.setText(treatment.getDescription());
        this.nameTextField.setText(treatment.getTreatmentName());

    }

    public void saveTreat(ActionEvent event) {
        try
        {
            if (treatment != null) //we need to edit/update an existing volunteer
            {
                updateTreatment();
                try {
                    treatment.updateIntoDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else    //we need to create a new plant
            {

                treatment = new Treatment(nameTextField.getText(),descTextField.getText());

                //do not show errors if creating Volunteer was successful
                treatment.insertIntoDB();

            }
        }

        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        back(event);


    }

    private void updateTreatment() {
        this.treatment.setTreatmentName(this.nameTextField.getText());
        this.treatment.setDescription(this.descTextField.getText());
    }

    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"ListTreatments.fxml","Методи за обработка на растения");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
