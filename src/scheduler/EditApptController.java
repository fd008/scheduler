/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import Models.*;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;


public class EditApptController {

    private Stage stage;
    private Main main = new Main();
    private User cuser;

    @FXML
    private Label aptLabel;

    @FXML
    private TableView<Customer> cusTV;

    @FXML
    private TableColumn<Customer, String> cusCol;

    @FXML
    private TableColumn<Customer, Integer> idCol;

    @FXML
    private TextField title;

    @FXML
    private TextField aptID;


    @FXML
    private DatePicker da;

    @FXML
    private ComboBox start;


    @FXML
    private ComboBox end;




    ObservableList<Integer> sh;
    DateTimeFormatter tf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    DateTimeFormatter df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    ZoneId zon = ZoneId.systemDefault();

    // disable weekends
    private Callback<DatePicker, DateCell> gDCF() {

        final Callback<DatePicker, DateCell> dcF = new Callback<DatePicker, DateCell>() {

            @Override
            public DateCell call(final DatePicker datePicker) {
                //
                return new DateCell() {

                    //
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        //
                        super.updateItem(item, empty);

                        // Disable Sunday, Saturday
                        if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            setDisable(true);
                            setStyle("-fx-background-color: #f2e8ea;");
                        }
                    }
                };
            }
        };
        return dcF;
    }


    public void initialize(URL url, ResourceBundle rb) {

        sh = FXCollections.observableArrayList();
        for(int i=9; i<=17; i++){
            sh.add(i);
        }
        start.setItems(sh);

        setUp();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cusCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        Callback<DatePicker, DateCell> dCF = this.gDCF();
        da.setDayCellFactory(dCF);

    }


    public void setStage(Stage stage, User user){
        this.stage = stage;
        this.cuser = user;
        initialize(null, null);
    }


    public void setUp(){


        ObservableList<Customer> cl = FXCollections.observableArrayList();

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select customerId, customer.customerName, customer.addressId from customer, address, city, country where customer.addressId = address.addressId and address.cityId = city.cityId and city.countryId = country.countryId");

            ResultSet rs = st.executeQuery();

            while (rs.next()){
                int id = rs.getInt("customer.customerId");
                String n = rs.getString("customer.customerName");
                String l = rs.getString("customer.addressId");
                cl.add( new Customer(id, n, Integer.parseInt(l)) );
            }
            aptID.setText("Auto Generated");
            cusTV.setItems(cl);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void saveApt(){

        String star = "";
        String et = "";
        String id = aptID.getText();

        if(title.getText().trim().isEmpty() || title.getText() == null || da.getValue() == null || start.getValue() == null || end.getValue() == null || start.getSelectionModel().getSelectedItem() == null || start.getSelectionModel().getSelectedItem() == null){
            main.showAlert("All fields must be filled!");
        }else{

            if( start.getValue().toString().equals("9") ){
                star = da.getValue() + "T09:00";
            }else{
                star = da.getValue() + "T" + start.getValue()+ ":00";
            }

            if(end.getValue().toString().equals("9")){
                et = da.getValue() + "T09:00";
            }else{
                et = da.getValue() + "T" + end.getValue()+ ":00";
            }


            System.out.println(star + " - " + et);
            System.out.println( convertToUTC(star) + " - " + convertToUTC(et));

        }







        if(cusTV.getSelectionModel().getSelectedItem() == null){

            main.showAlert("Please select a customer to continue!");

        }else{

            if(aptLabel.getText().equals("Edit Appointment")){

                //checks for overlapping

                if( overlapApt(star, et, id) == true){
                    System.out.println("Schedule can't overlap!");
                }else{
                    saveUpdate(convertToUTC(star), convertToUTC(et));
                    System.out.println(star + " - " + et);
                }



            }else{

                //checks for overlapping
                if( overlapApt(star, et, id) == true){
                    System.out.println("Schedule can't overlap!");
                }else{
                    saveNew(convertToUTC(star), convertToUTC(et));
                }

            }


        }



    }

    public void cancelApt(){

        if( main.showConfirm("confirm!", "Are you sure you want to cancel?", "Press OK to cancel.") == true){
            stage.hide();
        }

    }

    public void setUpEdit(Appointment apt){

        //LocalDateTime startldt = LocalDateTime.parse(apt.getStart(), df);

        LocalTime stime = LocalDateTime.parse(apt.getStart()).toLocalTime();
        LocalTime etime = LocalDateTime.parse(apt.getEnd()).toLocalTime();

        String sStart = stime.toString().split(":")[0].replaceFirst("^0+(?!$)", "");
        String sEnd = etime.toString().split(":")[0].replaceFirst("^0+(?!$)", "");



        aptLabel.setText("Edit Appointment");
        aptID.setText( String.valueOf(apt.getId()) );
        title.setText(apt.getTitle());
        da.setValue(LocalDateTime.parse(apt.getStart()).toLocalDate());
        start.getSelectionModel().select( sStart );
        end.getSelectionModel().select( sEnd );
        cusTV.getSelectionModel().select(apt.getCus());



    }

    public void saveNew(String star, String et){


        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "insert into appointment ( title, description, location, contact, url, start, end, createDate, createdBy, lastUpdateBy, customerId) values (?, ?, ?, ?, ?, ?, ?, current_timestamp, @cuser, @cuser, ?)");

            st.setString(1, title.getText());
            st.setString(2, title.getText());
            st.setString(3, String.valueOf(cusTV.getSelectionModel().getSelectedItem().getAdid()) );
            st.setString(4, "5555555555");
            st.setString(5, "website.com");
            st.setString(6, star );
            st.setString(7, et );
            st.setInt(8, cusTV.getSelectionModel().getSelectedItem().getId() );

            int res = st.executeUpdate();

            if(res == 1){
                System.out.println("Successfully Inserted!");
                stage.hide();
            }else{
                System.out.println("SQL Insert didn't work!");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    public void saveUpdate(String star, String et){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "update appointment set title = ?, description = ?, location = ?, start = ?, end = ?, lastUpdateBy = @cuser, customerId = ? where appointmentId = ?");
            st.setString(1, title.getText());
            st.setString(2, title.getText());
            st.setString(3, String.valueOf(cusTV.getSelectionModel().getSelectedItem().getAdid()) );
            st.setString(4, star );
            st.setString(5, et );
            st.setString(6, String.valueOf(cusTV.getSelectionModel().getSelectedItem().getId()));
            st.setInt(7, Integer.parseInt(aptID.getText()) );


            int res = st.executeUpdate();

            if(res == 1){
                System.out.println("Successfully Updated!");
                stage.hide();
            }else{
                System.out.println("SQL update didn't work!");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public void handleDate(ActionEvent event) {

        LocalDate ld = da.getValue();
        String dw = ld.getDayOfWeek().toString();


        if(dw.equals("SUNDAY") || dw.equals("SATURDAY") ){

            main.showAlert("Weekends can't be selected! Please select from MON - FRI.");
            da.setValue(ld.plusDays(2));


        }else{

            System.out.println(" Day " + ld.getDayOfWeek());
        }

    }

    public boolean overlapApt(String start, String end, String id){


        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select * from appointment where ( ? < end and ? > start) and createdBy = @cuser and appointmentID != ?");

            st.setString(1, convertToUTC(start) );
            st.setString(2, convertToUTC(end) );
            st.setString(3, id);

            ResultSet rs = st.executeQuery();

            while (rs.next()){
                main.showAlert("Schedule can't overlap! Please, select a different time!");
                return true;
            }

        }catch (SQLException s){
            s.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return  false;

    }

    public String convertToUTC(String date){

        LocalDateTime ldt = LocalDateTime.parse(date);
        ZonedDateTime zdt = ZonedDateTime.of( ldt , ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        return zdt.toLocalDateTime().toString();

    }

    public void handleStart(ActionEvent event) {

        ObservableList<Integer> eh = FXCollections.observableArrayList();


        for(int i= sh.indexOf( start.getValue() )+1; i<sh.size(); i++ ){
            eh.add(sh.get(i));
        }
        end.setItems(eh);

        System.out.println(start.getValue());
    }

    public void handleEnd(ActionEvent event) {

        System.out.println(end.getSelectionModel().getSelectedItem());

    }












}