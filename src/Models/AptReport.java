package Models;



public class AptReport {

    private String month;
    private String type;
    private String number;

    public AptReport(String month, String type, String number) {
        this.month = month;
        this.type = type;
        this.number = number;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
}
