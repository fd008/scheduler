
package scheduler;




import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import Models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {

    Stage aptStage;
    Main main = new Main();

    ObservableList<Appointment> aptList = FXCollections.observableArrayList();
    DateTimeFormatter fd = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    DateTimeFormatter fd1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");



    public void setStage(Stage stage){
        this.aptStage = stage;
    }


    public void handleApt(ActionEvent event) {

        main.showApt();

    }

    public void handleCus(ActionEvent event) {
        main.showCus();

    }

    public void handleReport(ActionEvent event) {
        main.showReport();
    }



    //gets the appointment data for the reminder
    public void getApt(){

        int aptid;
        int cusid;
        String start;
        String end;
        String author;

        try{

            PreparedStatement st = Db.getConn().prepareStatement(
                    "select appointmentId, customerId, start, end, createdBy from appointment where createdBy = @cuser order by start"
            );


            ResultSet rs = st.executeQuery();

            while ( rs.next() ){

                aptid = rs.getInt("appointmentId");
                cusid = rs.getInt("customerId");
                start = String.valueOf(rs.getTimestamp("start").toLocalDateTime());
                end = String.valueOf(rs.getTimestamp("end").toLocalDateTime());
                author = rs.getString("createdBy");

                aptList.add( new Appointment(aptid, cusid, start, end, author) );
            }


        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void alarm(){

        LocalDateTime now = LocalDateTime.now();
        String fnow = now.format(fd);

        LocalDateTime lfnow = LocalDateTime.parse(fnow, fd1 );
        LocalDateTime lfnow15 = lfnow.plusMinutes(15);


        FilteredList<Appointment> list = new FilteredList<>(aptList);

        //lamda expression for simpler event handling
        list.setPredicate(r-> {

            LocalDateTime rd = LocalDateTime.parse( r.getStart() );

            return  rd.isAfter(lfnow.minusMinutes(1)) && rd.isBefore(lfnow15);
        });

        if(list.isEmpty() || list == null){
            System.out.println("No Appointment within 15 minutes");

        }else{
            main.showAlert("Reminder for an Appointment starting at: " + list.get(0).getStart() + " with: " + list.get(0).getCon() );
        }

    }//alarm for the reminder when there is an appointment within 15 minutes



}

