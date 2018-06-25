package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import models.Garden;
import models.Problem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateProblemController implements Initializable, ProblemControllerClass {
    @FXML private Label headerLabel;
    @FXML private TextField nameTextField;
    @FXML private RadioButton bugSelect;
    @FXML private RadioButton disaese;
    @FXML private RadioButton animal;
    @FXML private TextField descTextField;
    private ToggleGroup problemType;

    private Problem problem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.problemType = new ToggleGroup();
        bugSelect.setToggleGroup(problemType);
        disaese.setToggleGroup(problemType);
        animal.setToggleGroup(problemType);



    }

    public void back(ActionEvent event) {
        SceneChanger sc = new SceneChanger();
        try {
            sc.changeScenes(event,"ListProblems.fxml","Списък Вредители");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProblem(ActionEvent event) {
        try
        {
            if (problem != null) //edit
            {
                updateProblem();
                problem.updateIntoDB();
                try {
                    problem.updateIntoDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else    //we need to create a new plant
            {
                String selectedType = getSelectedType();
                problem = new Problem(nameTextField.getText(),selectedType,descTextField.getText());

                problem.insertIntoDB();

            }
        }

        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        back(event);
    }

    private void updateProblem() {
        problem.setName(this.nameTextField.getText());
        problem.setDescription(this.descTextField.getText());
        problem.setProblemType(getSelectedType());
    }

    private String getSelectedType() {
        String type = "";
        if(this.problemType.getSelectedToggle().equals(bugSelect)){
            type= "bug";

        }
        else if(this.problemType.getSelectedToggle().equals(animal)){
            type= "animal";
        }
        else if(this.problemType.getSelectedToggle().equals(disaese)){
            type= "disease";
        }
        return type;
    }


    @Override
    public void preloadData(Problem problem) {
        this.problem = problem;
        headerLabel.setText("Промени вредител");
        this.nameTextField.setText(problem.getName());
        this.descTextField.setText(problem.getDescription());
        String type = problem.getProblemType();
        switch (type){
            case "bug": problemType.selectToggle(bugSelect);
            break;
            case "animal": problemType.selectToggle(animal);
            break;
            case "disease": problemType.selectToggle(disaese);
            break;
            default:throw new IllegalArgumentException("No such problem type!");
        }

    }
}
