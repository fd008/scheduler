package Models;



public class Appointment {

    private int id;
    private String title;
    private String con;
    private String start;
    private String end;
    private int cusID;
    private String cusName;

    public Appointment(int id, String start, String end, String title, String con, int cusID, String cusName){
        this.id = id;
        this.start = start;
        this.end = end;
        this.title = title;
        this.con = con;
        this.cusID = cusID;
        this.cusName = cusName;
    }

    public Appointment(){

    }

    public Appointment(int id, int cusID, String start, String end, String con){
        this.id = id;
        this.cusID = cusID;
        this.start = start;
        this.end = end;
        this.con = con;

    }

    public Appointment(int id, String start, String end, String title){
        this.id = id;
        this.start = start;
        this.end = end;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getCusID() {
        return cusID;
    }

    public void setCusID(int cusID) {
        this.cusID = cusID;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Customer getCus(){
        return new Customer(cusID, cusName);
    }


}
