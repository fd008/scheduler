/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Models.Appointment;
import Models.AptReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportController {

    Stage reportStage;
    String username;

    @FXML
    private TabPane mainTab;

    //apt
    @FXML
    private Tab aptTab;

    @FXML
    private TableView<AptReport> aptTV;

    @FXML
    private TableColumn<AptReport, String> monCol;

    @FXML
    private TableColumn<AptReport, String> typeCol;

    @FXML
    private TableColumn<AptReport, String> ntypeCol;


    //schedule
    @FXML
    private Tab schTab;

    @FXML
    private TableView<Appointment> schTV;

    @FXML
    private TableColumn<Appointment, Integer> idCol;

    @FXML
    private TableColumn<Appointment, String> startCol;

    @FXML
    private TableColumn<Appointment, String> endCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;


    //popular types
    @FXML
    private Tab paptTab;

    @FXML
    private TableView<AptReport> paptTV;

    @FXML
    private TableColumn<AptReport, String> paptType;

    @FXML
    private TableColumn<AptReport, Integer> paptnTypes;

    private ObservableList<AptReport> aptList = FXCollections.observableArrayList();
    private ObservableList<Appointment> schList = FXCollections.observableArrayList();
    private ObservableList<AptReport> popList = FXCollections.observableArrayList();


    public void setStage(Stage stage){
        this.reportStage = stage;
        setup();
    }

    public void setup(){

        getUsername();
        getApt();
        getSch();
        getPop();

        //apt
        monCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        ntypeCol.setCellValueFactory(new PropertyValueFactory<>("number"));

        //sch
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        //pop
        paptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        paptnTypes.setCellValueFactory(new PropertyValueFactory<>("number"));


    }

    public void getUsername(){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("select @cuser");
            ResultSet rs = st.executeQuery();

            while (rs.next()){
                username = rs.getString("@cuser");
                System.out.println("Report current user : " + username);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public void getApt(){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select monthname(start) as month, title as type, count(*) as number from appointment group by monthname(start), title");

            ResultSet rs = st.executeQuery();

            while (rs.next()){
                String month = rs.getString("month");
                String type = rs.getString("type");
                String number = rs.getString("number");

                aptList.add(new AptReport(month, type, number));

            }

        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("someting is wrong!");
        }

        aptTV.getItems().setAll(aptList);


    }

    public void getSch(){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select appointmentId, start, end, title from appointment where createdBy = @cuser order by start");
            ResultSet rs = st.executeQuery();

            while (rs.next()){

                int id = rs.getInt("appointmentId");
                String start = rs.getTimestamp("start").toLocalDateTime().toString();
                String end = rs.getTimestamp("end").toLocalDateTime().toString();
                String title = rs.getString("title");

                schList.add( new Appointment(id, start, end, title) );
            }

        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        schTV.getItems().setAll(schList);

    }

    public void getPop(){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select title, count(*) as number from appointment group by title order by number desc");
            ResultSet rs = st.executeQuery();

            while (rs.next()){
                String title = rs.getString("title");
                int number = rs.getInt("number");
                popList.add( new AptReport(null, title, String.valueOf(number)) );
            }



        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        paptTV.getItems().setAll(popList);

    }




}
