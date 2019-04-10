
package scheduler;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoginController {

    private Main main = new Main();
    private Stage loginStage;
    private User newuser;

    Logger logger = Logger.getLogger("UserLog");
    FileHandler fh1;
    Locale es = new Locale("es", "ES");
    Locale us = new Locale( System.getProperty("user.language"), System.getProperty("user.country")  );
    //change the second parameter l to s to check if spanish language works
    ResourceBundle rb = ResourceBundle.getBundle("msg", us );



    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label msg;

    @FXML
    private Label txt;

    @FXML
    private Label login;

    @FXML
    private Label userLabel;

    @FXML
    private Label passLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;




    public void handleSignin(ActionEvent event) throws IOException {



        if(username == null || password == null || username.equals("") || password.equals("")){

            main.showAlert("Please fill up all feilds!");
        }else{

            if( Db.validateLogin( username.getText(), password.getText() ) == true){

                //((Node)(event.getSource())).getScene().getWindow().hide();
                newuser = new User(username.getText() );
                loginStage.hide();
                main.mainScene(newuser);
                toLog(username.getText() + " successfully logged in!");

            }
            else{
                msg.setText(rb.getString("login"));
                toLog(username.getText() + " unsuccessful attempt to log in!");
            }

        }

    }


    public void handleCancel(ActionEvent event) {

        if( main.showConfirm("Confirm", "Are you sure you want to exit?", "Press Ok to exit") == true ){
            Db.close();
            System.exit(0);
        }

    }

    public void setLoginStage(Stage stage){
        String st = rb.getString("cn") + " : "+ System.getProperty("user.country") + " | " + rb.getString("lan") + " : " + System.getProperty("user.language");
        this.loginStage = stage;

        login.setText(rb.getString("signIn"));
        userLabel.setText(rb.getString("username"));
        passLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("signIn"));
        cancelButton.setText(rb.getString("cancel"));
        txt.setText(st);

    }


    public User getUser(){
        return newuser;
    }

    //tracks user activities and appends new records
    public void toLog(String st){

        try{

            fh1 = new FileHandler("UserLog.txt", true);
            logger.addHandler(fh1);

            SimpleFormatter sf = new SimpleFormatter();
            fh1.setFormatter(sf);

            logger.info(st);

        }catch (SecurityException s){
            s.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }





}
