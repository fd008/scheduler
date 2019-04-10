
package scheduler;




import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Models.*;

import java.io.IOException;
import java.util.Optional;


public class Main extends Application {

    Stage primaryStage;
    Stage stage;
    User cuser;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        showLogin();
    }

    public void showLogin(){


        try{

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Login.fxml"));
            AnchorPane pane = (AnchorPane) fxmlLoader.load();

            LoginController controller = fxmlLoader.getController();
            controller.setLoginStage(primaryStage);


            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Schedule Management");
            primaryStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void showCus(){

        try{

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Customer.fxml"));
            AnchorPane pane = (AnchorPane) fxmlLoader.load();


            Stage cusStage = new Stage();
            Scene scene = new Scene(pane);

            CustomerController controller = fxmlLoader.getController();

            cusStage.setTitle("Customer");
            cusStage.initModality(Modality.APPLICATION_MODAL);
            cusStage.initOwner(this.stage);
            cusStage.setScene(scene);

            controller.setup();
            controller.setTableView();

            cusStage.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
        }

    }



    public void mainScene(User user) {

        this.cuser = user;


        try{

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Main.fxml"));
            AnchorPane mainpane = (AnchorPane) fxmlLoader.load();


            stage = new Stage();
            stage.setTitle("Schedule Management");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(primaryStage);

            Scene scene = new Scene(mainpane);
            stage.setScene(scene);

            MainController controller = fxmlLoader.getController();
            controller.setStage(stage);

            //gets the appointment data
            controller.getApt();

            //alarm or reminder when there is a appointment within 15 minutes
            controller.alarm();

            stage.showAndWait();




        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void showApt(){


        FXMLLoader fxmlLoader = null;
        Parent root = null;

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("Appt.fxml"));
            root = fxmlLoader.load();
        }catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        ApptController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setUp();


        stage.setTitle("Appointment Management");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(this.stage);
        stage.setScene(scene);


        stage.showAndWait();


    }

    public void showNewApt(){

        FXMLLoader fxmlLoader = null;
        Parent root = null;

        try{

            fxmlLoader = new FXMLLoader(getClass().getResource("EditAppt.fxml"));
            root = fxmlLoader.load();

        }catch (IOException e){
            e.printStackTrace();
        }

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        EditApptController controller = fxmlLoader.getController();
        controller.setStage(stage, cuser);
        controller.setUp();

        stage.setTitle("Add Appointment");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(this.stage);
        stage.setScene(scene);

        stage.showAndWait();

    }

    public void showReport(){

        try{

            FXMLLoader fxmlLoader = null;
            Parent root = null;

            try{

                fxmlLoader = new FXMLLoader(getClass().getResource("Report.fxml"));
                root = fxmlLoader.load();

            }catch (IOException e){
                e.printStackTrace();
            }

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            ReportController controller = fxmlLoader.getController();
            controller.setStage(stage);


            stage.setTitle("Report");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.stage);
            stage.setScene(scene);

            stage.showAndWait();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showEditApt(Appointment apt){

        if(apt == null){
            showAlert("Please select a row to continue!");
        }else{


            FXMLLoader fxmlLoader = null;
            Parent root = null;

            try{

                fxmlLoader = new FXMLLoader(getClass().getResource("EditAppt.fxml"));
                root = fxmlLoader.load();

            }catch (IOException e){
                e.printStackTrace();
            }

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            EditApptController controller = fxmlLoader.getController();
            controller.setStage(stage, cuser);
            controller.setUp();
            controller.setUpEdit(apt);

            stage.setTitle("Edit Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.stage);
            stage.setScene(scene);

            stage.showAndWait();


        }
    }


    public static void main(String[] args) throws ClassNotFoundException {

        Db.init();
        launch(args);
        Db.close();
    }

    public boolean showConfirm(String title, String text, String body){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(body);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            return true;
        }else{
            alert.close();
            return false;
        }

    }

    public void showAlert(String st){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(st);
        alert.showAndWait();

    }


    public User getCuser(){
        return cuser;
    }





}
