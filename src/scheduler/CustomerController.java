
package scheduler;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Models.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class CustomerController {

    @FXML
    private TableView<Customer> cusTV;

    @FXML
    private TableColumn<Customer, String> cusName;

    @FXML
    private TableColumn<Customer, Integer> cusID;

    @FXML
    private TextField idValue;

    @FXML
    private TextField nameValue;

    @FXML
    private TextField ad1Value;

    @FXML
    private TextField ad2Value;


    @FXML
    private ComboBox cityValue;

    @FXML
    private TextField cnValue;

    @FXML
    private TextField pcValue;

    @FXML
    private TextField phoneValue;

    @FXML
    private ButtonBar editBar;

    @FXML
    private ButtonBar saveBar;

    private boolean ifEdit = false;

    Main main = new Main();


    public void setup(){

        ObservableList<String> cty = FXCollections.observableArrayList("New York", "London", "Phoenix");
        ObservableList<String> cn = FXCollections.observableArrayList("USA", "England");

        cusName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("id"));

        //disable the button to prevent accidental edit
        disable();
        cityValue.setItems(cty);
        idValue.setText("Auto Generated");

        cusTV.getItems().setAll(cusData());

    }

    public void setTableView(){
        cusTV.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> {
            if(newValue!=null){
                fillup(newValue);
            }
        } );
    }

    public Integer getCity(String name){

        Integer id = null;

        try{
            PreparedStatement st = Db.getConn().prepareStatement("select cityId, city from city where city = ?");
            st.setString(1, name);

            ResultSet rs = st.executeQuery();

            while ( rs.next() ){
                id = rs.getInt("cityId");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return id;

    }

    public Integer getAdId(String ad){
        Integer id = null;

        try{
            PreparedStatement st = Db.getConn().prepareStatement("select addressId from address where address = ?");
            st.setString(1, ad);

            ResultSet rs = st.executeQuery();

            while ( rs.next() ){
                id = rs.getInt("addressId");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return id;
    }

    public void fillup(Customer cus){

        if(cus!=null){
            idValue.setText(String.valueOf(cus.getId()));
            nameValue.setText(cus.getName());
            ad1Value.setText(cus.getAd1());
            ad2Value.setText(cus.getAd2());
            cityValue.setValue( cus.getCity().getCity() );
            cnValue.setText(cus.getCountry());
            pcValue.setText(cus.getZip());
            phoneValue.setText(cus.getPhone());
        }else{
            main.showAlert("No customer selected!");
        }
    }


    public void addCustomer(ActionEvent event) {



        cusTV.setDisable(true);
        editBar.setDisable(true);
        saveBar.setDisable(false);
        clear();
        idValue.setText("Auto Generated");
        enable();
    }

    public void editCustomer(ActionEvent event) {

        Customer cus1 = cusTV.getSelectionModel().getSelectedItem();


        if(cus1 != null){

            ifEdit = true;
            cusTV.setDisable(true);
            editBar.setDisable(true);
            saveBar.setDisable(false);
            enable();

        }else{
            System.out.println("Please select a customer to edit");
        }

    }

    public void deleteCustomer(ActionEvent event) {

        Customer cus = cusTV.getSelectionModel().getSelectedItem();

        if(main.showConfirm("Confirm", "Data can't be recovered once deleted. Are you sure?", "Press ok to delete") == true ){
            try{

                PreparedStatement st = Db.getConn().prepareStatement("" +
                        "delete customer.*, address.* from customer, address where customerId = ? and customer.addressId = address.addressId;");
                st.setInt(1, Integer.parseInt( idValue.getText() ) );

                System.out.println("Delete id: " +  idValue.getText() );

                int res = st.executeUpdate();

                if(res > 0){
                    cusTV.getItems().setAll(cusData());
                }else{
                    System.out.println("Delete didn't work!" + res);
                }


            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }

    public void saveCustomer(ActionEvent event) {

            String cusname = nameValue.getText();
            String ad1 = ad1Value.getText();
            String ad2 = ad2Value.getText();
            String city = String.valueOf(cityValue.getValue());
            String pc = pcValue.getText();
            String phone = phoneValue.getText();



            if(ifEmpty() == true){

                main.showAlert("All fields must be filled!");

                //new type of exception control
                throw new IllegalArgumentException("All fields must be filled!");


            }else{


                if(ifEdit == true){

                    cusTV.setDisable(false);
                    saveBar.setDisable(true);
                    editBar.setDisable(false);

                    saveEdit();
                    cusTV.getItems().setAll(cusData());
                    cusTV.getSelectionModel().selectFirst();
                    disable();

                }else{

                    cusTV.setDisable(false);
                    saveBar.setDisable(true);
                    editBar.setDisable(false);

                    saveNew(cusname, ad1, ad2, city, pc, phone);
                    cusTV.setDisable(false);
                    cusTV.getItems().setAll(cusData());
                    cusTV.getSelectionModel().selectFirst();
                    disable();
                }

            }





    }


    //saves a new customer to the database
    public void saveNew(String cusname, String ad1, String ad2, String city, String pc, String phone){


        saveAddress(cusname, ad1, ad2, city, pc, phone );
        saveCustomer(cusname, ad1);


    }

    public void saveAddress(String cusname, String ad1, String ad2, String city, String pc, String phone){

        try {

            //save address record
            PreparedStatement st2 = Db.getConn().prepareStatement("" +
                    "insert into address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy ) values (?, ?, ?, ?, ?, current_timestamp, @cuser, @cuser )");
            st2.setString(1, ad1);
            st2.setString(2, ad2);
            st2.setInt(3,  getCity(city) );
            st2.setString(4, pc );
            st2.setString(5, phone );

            int res2 = st2.executeUpdate();

            if(res2 == 1){
                System.out.println("Address successfully inserted!");

            }else{
                System.out.println("Sql insert didn't work!");
            }



        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveCustomer(String cusname, String ad){

        try{

            //save customer record
            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy ) values (?, ?, 1, current_timestamp, @cuser, @cuser)");
            st.setString(1, cusname );
            st.setInt(2, getAdId( ad ) );

            int res = st.executeUpdate();

            if(res == 1){
                System.out.println("Customer Successfully Inserted!");

            }else{
                System.out.println("SQL Insert didn't work!");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    //updates existing customer into the database
    public void saveEdit(){

        Customer cus = cusTV.getSelectionModel().getSelectedItem();

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "UPDATE address " +
                    "SET address = ?, address2 = ?, cityId = ?, postalCode = ?, " +
                    "phone = ?, lastUpdateBy = @cuser " +
                    "WHERE addressId = ?");
            st.setString(1, ad1Value.getText() );
            st.setString(2, ad2Value.getText() );
            st.setInt(3, getCity(String.valueOf(cityValue.getValue())) );
            st.setString(4, pcValue.getText() );
            st.setString(5, phoneValue.getText() );
            st.setInt(6, cus.getAdid() );

            int res = st.executeUpdate();

            if(res == 1){
                System.out.println("Address edit updated!");
            }else{
                System.out.println("Address edit didn't work!");
            }


            PreparedStatement st2 = Db.getConn().prepareStatement("" +
                    "update customer set customerName = ? where customerId = ? ");
            st2.setString(1, nameValue.getText() );
            st2.setInt(2, Integer.parseInt(idValue.getText()) );

            int res2 = st2.executeUpdate();

            if(res2 == 1){
                System.out.println("Customer edit updated!");
                ifEdit = false;
                cusTV.setDisable(false);
            }else{
                System.out.println("Customer edit didn't work!");
            }


        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Problem saving edited records");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Boolean ifEmpty(){

        Customer cus = cusTV.getSelectionModel().getSelectedItem();

        if( cusName.getText().trim().isEmpty() || ad1Value.getText().trim().isEmpty() || ad2Value.getText().trim().isEmpty() || cityValue.getSelectionModel().isEmpty() || cityValue.getValue() == null || cnValue.getText().trim().isEmpty() || pcValue.getText().trim().isEmpty() || phoneValue.getText().trim().isEmpty() ){
            return true;
        }else{
            return false;
        }

    }

    //when user selects a city
    public void citySelect(){


        if(cityValue.getValue() == "London"){
            cnValue.setText("England");
        }else{
            cnValue.setText("USA");
        }
    }


    public void cancelCustomer(ActionEvent event) {

        if(ifEdit){
            ifEdit = false;
        }

        saveBar.setDisable(true);
        editBar.setDisable(false);
        cusTV.setDisable(false);
        disable();
        clear();

    }

    public void disable(){

        nameValue.setEditable(false);
        ad1Value.setEditable(false);
        ad2Value.setEditable(false);
        pcValue.setEditable(false);
        phoneValue.setEditable(false);

    }

    public void enable(){

        nameValue.setEditable(true);
        ad1Value.setEditable(true);
        ad2Value.setEditable(true);
        pcValue.setEditable(true);
        phoneValue.setEditable(true);
    }

    public void clear(){
        idValue.clear();
        nameValue.clear();
        ad1Value.clear();
        ad2Value.clear();
        pcValue.clear();
        phoneValue.clear();
    }

    public List<Customer> cusData(){

        int id;
        int adid;
        String name;
        String ad1;
        String ad2;
        City cCity;
        String cn;
        String zip;
        String phone;

        ObservableList<Customer> cList = FXCollections.observableArrayList();

        try{

            PreparedStatement st = Db.getConn().prepareStatement("" +
                    "select customer.customerId, customer.customerName, customer.addressId, address.address, address.address2, address.postalcode, address.phone, city.cityId, city.city, country.country from customer, address, city, country where customer.addressId = address.addressId and address.cityId = city.cityId and city.countryId = country.countryId order by customer.customerName");

            ResultSet rs = st.executeQuery();

            while (rs.next()){

                id = rs.getInt("customer.customerId");
                name = rs.getString("customer.customerName");
                ad1 = rs.getString("address.address");
                ad2 = rs.getString("address.address2");
                zip = rs.getString("address.postalcode");
                cCity = new City( rs.getInt("city.cityId"), rs.getString("city.city") );
                cn = rs.getString("country.country");
                phone = rs.getString("address.phone");
                adid = rs.getInt("customer.addressId");

                cList.add( new Customer(id, name, ad1, ad2, cCity, cn, zip, phone, adid) );

            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return cList;
    }
}