
package Models;


public class Customer {

    private int id;
    private String name;
    private int adid;
    private String ad1;
    private String ad2;
    private City city;
    private String country;
    private String phone;
    private String zip;
    private String author;

    public Customer(int cusID, String cusName, String ad1, String ad2, City city, String country, String zip, String phone, int adid){
        this.id = cusID;
        this.name = cusName;
        this.ad1 = ad1;
        this.ad2 = ad2;
        this.city = city;
        this.country = country;
        this.zip = zip;
        this.phone = phone;
        this.adid = adid;
    }

    public Customer(int cusID, String cusName, int adid){
        this.id = cusID;
        this.name = cusName;
        this.adid = adid;
    }

    public Customer(int cusID, String cusName){
        this.id = cusID;
        this.name = cusName;
    }

    public Customer(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdid() {
        return adid;
    }

    public void setAdid(int adid) {
        this.adid = adid;
    }

    public String getAd1() {
        return ad1;
    }

    public void setAd1(String ad1) {
        this.ad1 = ad1;
    }

    public String getAd2() {
        return ad2;
    }

    public void setAd2(String ad2) {
        this.ad2 = ad2;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
