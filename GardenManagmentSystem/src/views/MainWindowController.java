package views;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private SceneChanger sc = new SceneChanger();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
