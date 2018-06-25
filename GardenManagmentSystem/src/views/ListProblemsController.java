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
import models.Problem;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ListProblemsController implements Initializable {


    @FXML private TableView<Problem> tableView;
    @FXML private TableColumn<Problem,String> nameColumn;
    @FXML private TableColumn<Problem,String> typeColumn;
    @FXML private TableColumn<Problem,String> descriptionColumn;
    @FXML private Button changeGardenBtn;
    @FXML private Button deleteProblemBtn;
    SceneChanger sceneChanger = new SceneChanger();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.changeGardenBtn.setDisable(true);
        this.deleteProblemBtn.setDisable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Problem, String>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Problem, String>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<Problem, String>("problemType"));

        try {
            loadProblems();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void problemSelected(MouseEvent mouseEvent) {
        Problem newProblem = tableView.getSelectionModel().getSelectedItem();
        if(newProblem==null){
            return;
        }
        else{
            this.changeGardenBtn.setDisable(false);
            this.deleteProblemBtn.setDisable(false);
        }

    }

    public void addProblem(ActionEvent event) {
        try {
            sceneChanger.changeScenes(event,"CreateProblem.fxml","Добави вредител");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeProblem(ActionEvent event) {
        Problem selected = getSelectedProblem();
        CreateProblemController cpc = new CreateProblemController();
        try {
            sceneChanger.changeScenes(event,"CreateProblem.fxml","Промени вредител",selected,cpc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Problem getSelectedProblem() {
       Problem problem= tableView.getSelectionModel().getSelectedItem();
       return  problem;
    }

    public void deleteProblem(ActionEvent event) {

        Problem selected = getSelectedProblem();

        Alert alert = new Alert(Alert.AlertType.NONE, "Сигурни ли сте, че искате да изтриете вредител \"" +  getSelectedProblem().toString() + "\" ?", ButtonType.YES, ButtonType.NO);
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
            loadProblems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void back(ActionEvent event) {
        try {
            sceneChanger.changeScenes(event,"MainWindow.fxml","Начална страница");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProblems() throws SQLException {
        ObservableList<Problem> problems = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            //1. connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardens_dataBase", "ADMIN", "ADMIN");
            //2.  create a statement object
            statement = conn.createStatement();

            //3.  create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM problems");

            //4.  create plant objects from each record
            while (resultSet.next())
            {
                Problem newProblem = new Problem(resultSet.getString("problem_name"),resultSet.getString("type"),resultSet.getString("problem_desc"));
                newProblem.setProblemId(resultSet.getInt("problem_id"));

                problems.add(newProblem);
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
        tableView.getItems().setAll(problems);
    }
}
