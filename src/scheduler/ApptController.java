
package scheduler;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Models.*;


public class ApptController {

    private Stage aptStage;

    Main main = new Main();

    User user;

    String username;

    DateTimeFormatter fd = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    DateTimeFormatter fd1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private ChoiceBox aptChoice;

    @FXML
    private AnchorPane anc;

    @FXML
    private TableView<Appointment> aptTV;

    @FXML
    private TableColumn aptID;

    @FXML
    private TableColumn aptStart;

    @FXML
    private TableColumn aptEnd;

    @FXML
    private TableColumn aptTitle;

    @FXML
    private TableColumn aptCon;

    @FXML
    private TableColumn<Appointment, Customer> aptCusID;

    @FXML
    private TableColumn<Appointment, Customer> aptCusName;


    ObservableList<Appointment> aptList = FXCollections.observableArrayList();


    ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList(
            "Month", "Week"));



    //populates the data to the Appointment TableView
    public void setUp(){

        try{

            PreparedStatement st = Db.getConn().prepareStatement("select @cuser");
            ResultSet rs = st.executeQuery();

            while (rs.next()){
                username = rs.getString("@cuser");
                System.out.println("Appointment current user : " + username);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


        aptID.setCellValueFactory(new PropertyValueFactory<>("id"));
        aptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        aptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        aptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        aptCon.setCellValueFactory(new PropertyValueFactory<>("con"));
        aptCusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        aptCusName.setCellValueFactory(new PropertyValueFactory<>("cusName"));

        aptData();
        aptTV.getItems().setAll(aptList);
        alarm();
    }

    public void aptData(){

        try{


            PreparedStatement st = Db.getConn().prepareStatement(
                    "select appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, appointment.start as start, appointment.end as end, appointment.createdBy, customer.customerId, customer.customerName, customer.addressId from appointment, customer where appointment.customerId = customer.customerId order by start"
            );

            ResultSet rs = st.executeQuery();

            while (rs.next()){

                int id = rs.getInt("appointment.appointmentId");
                int cusID = rs.getInt("appointment.customerId");
                String title = rs.getString("appointment.title");
                String des = rs.getString("appointment.description");
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                String creator = rs.getString("appointment.createdBy");
                String cusName = rs.getString("customer.customerName");
                String loc  = rs.getString("customer.addressId");



                Customer nc = new Customer(cusID, cusName, Integer.parseInt(loc));

                aptList.add(new Appointment(id, utcToLocal(start + "Z"), utcToLocal(end + "Z"), title, creator, cusID, cusName ));


                System.out.format("%s, %s, %s, %s, %s, %s, %s\n", id, utcToLocal(start +"Z"), utcToLocal(end+"Z"), title, creator, cusName, cusID );

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setStage(Stage stage){
        this.aptStage = stage;
        anc.getChildren().add(cb);


        //lamda expression to simplify event handling
        cb.setOnAction( e -> {
            LocalDate lt = LocalDate.now();
            LocalDate ld = null;

            if(cb.getValue() == "Week"){
                ld = lt.plusWeeks(1);
                byMonthWeek(lt, ld);
            }else{
                ld = lt.plusMonths(1);
                byMonthWeek(lt, ld);
            }

        });


    };



    public void addAppt(ActionEvent event) {


        main.showNewApt();

        aptList.clear();
        aptTV.getItems().clear();

        aptData();
        aptTV.getItems().setAll(aptList);
        alarm();

    }

    public void editAppt(ActionEvent event) {

        Appointment apt = aptTV.getSelectionModel().getSelectedItem();

        main.showEditApt(apt);

        aptList.clear();
        aptTV.getItems().clear();


        aptData();
        aptTV.getItems().setAll(aptList);
        alarm();

    }

    public void deleteAppt(ActionEvent event) {

        Appointment apt = aptTV.getSelectionModel().getSelectedItem();

        if(apt !=null){

            if( main.showConfirm("Confirm", "Deleting is irreversible! Are you sure?", "Press Ok to delete!") == true ){

                try{

                    PreparedStatement st = Db.getConn().prepareStatement("delete from appointment where appointmentId = ?");
                    st.setInt(1, apt.getId() );
                    st.executeUpdate();

                    setUp();

                }catch (SQLException e){
                    e.printStackTrace();
                }

            }

        }else{
            main.showAlert("Please select an appointment to delete!");
        }
    }

    public String utcToLocal(String date){

        ZonedDateTime zdt = ZonedDateTime.parse(date);
        ZonedDateTime zd = zdt.withZoneSameInstant(ZoneId.systemDefault());
        return zd.toLocalDateTime().toString();

    }

    public String convertToUTC(String date){

        LocalDateTime ldt = LocalDateTime.parse(date);
        ZonedDateTime zdt = ZonedDateTime.of( ldt , ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        return zdt.toLocalDateTime().toString();

    }


    public void byMonthWeek(LocalDate lt, LocalDate ld){


        FilteredList<Appointment> filteredList = new FilteredList<>(aptList);

        //lamda expression to simplify event listener
        filteredList.setPredicate(r -> {
            LocalDate rd = LocalDateTime.parse(r.getStart()).toLocalDate();
            return rd.isAfter(lt.minusDays(1)) && rd.isBefore(ld);
        });
        aptTV.setItems(filteredList);
    }

    public void alarm(){

        LocalDateTime now = LocalDateTime.now();
        String fnow = now.format(fd);

        LocalDateTime lfnow = LocalDateTime.parse(fnow, fd1 );
        LocalDateTime lfnow15 = lfnow.plusMinutes(15);


        FilteredList<Appointment> list = new FilteredList<>(aptList);


        //lamda expression to simplify event listener
        list.setPredicate(r-> {

            LocalDateTime rd = LocalDateTime.parse( r.getStart() );
            //LocalDateTime nd = LocalDateTime.parse(rd.format(fd));

            return  rd.isAfter(lfnow.minusMinutes(1)) && rd.isBefore(lfnow15);
        });

        if(list.isEmpty() || list == null){
            System.out.println("No reminders within 15 minutes");

        }else if( list.get(0).getCon().equals(username) ){
            main.showAlert("Reminder for an Appointment starting at: " + list.get(0).getStart() + " with " + list.get(0).getCusName() );
        }else{
            System.out.println("No user found! " + username + list.get(0).getCon() );

        }

    }//alarm for the reminder when there is an appointment within 15 minutes


}


