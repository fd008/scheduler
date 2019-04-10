package Models;



public class Address {

    private Integer id;
    private String ad1;
    private String ad2;
    private int cityid;
    private int zip;
    private String phone;

    public Address(){

    }

    public Address(Integer id, String ad1, String ad2, int cityid, int zip, String phone) {
        this.id = id;
        this.ad1 = ad1;
        this.ad2 = ad2;
        this.cityid = cityid;
        this.zip = zip;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
