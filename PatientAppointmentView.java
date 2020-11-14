//// imports 
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
//import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*; 
import javafx.event.ActionEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// SQL
import java.sql.*;

// Tables
import javafx.collections.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.TableColumn.*;
import javafx.util.converter.*;
import javafx.scene.control.cell.CheckBoxListCell;

import java.util.ArrayList;

// FXML
import javafx.fxml.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.ResourceBundle; 
import java.net.*;

// Exception
import java.io.IOException;

/**
 * PatientAppointmentView screen and functionality of EzHealth
 *
 * @Derrick Persaud & Enrique Gambra
 * @Sprint 2
 */
public class PatientAppointmentView implements Initializable
{
    // Setting up the stage
    private static Stage stage;

    // Database connections and credential variables
    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword(); 

    private static String fullName;
    private String username;
    private int patientID;

    private static final String sqlFirstName = "first_name";
    private static final String sqlLastName = "last_name";

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent patient = FXMLLoader.load(getClass().getResource("fxml/patientAppointmentView.fxml"));

        // Create scene and load it onto the stage
        Scene patientScene = new Scene(patient);
        stage.setTitle("Patient Appointment View");
        stage.setScene(patientScene);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

    /**
     * Constructor
     * PatientAppointmentView
     */
    public PatientAppointmentView()
    {
        username = Login.getUsername();
    } // end PatientAppointView

    // Pane 
    @FXML
    private AnchorPane pane;

    // Labels 
    @FXML
    private Label ezLabel;
    
    @FXML
    private Label healthLabel;

    @FXML
    private Label appointmentLabel;

    // Buttons 
    @FXML
    private Button signOutButton;

    // Table
    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, String> dateColumn;

    @FXML
    private TableColumn<Appointment,String> timeColumn;

    private ObservableList<Appointment> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller class for SceneBuilder
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // load all data including patient name and appointments
        loadData(); 

        // load name into the label
        appointmentLabel.setText("Upcoming Appointments for " + fullName);

        // set table up 
        dateColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("time"));
        
        System.out.println("Data list size is: " + data.size());
        
        if(data.size() != 0){
           appointmentTableView.setItems(data); 
        } else {
            appointmentTableView.setItems(null);
            System.out.println("Appointment list is size 0");
        }
        
        
        for(Appointment a: data)
        {
            System.out.println(a.toString());
        }
    } // end initialize

    /**
     * @pre
     * @param sqlName - attribute column to retrieve from the database (either first_name or last_name)
     * @param username - patient ID's username
     * @return "" if database could not find the user's name, or a string of the actual user's name
     * @post 
     */
    public static String retrieveName(String sqlName, String username){

        //Connection connection = null;
        //Statement statement = null;
        String name = "";

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();

            String patientNameQuery = "select " + sqlName + " from users where username = '" + username + "';";

            ResultSet patientFNResults = MySQLConnectionProperties.getStatementObject().executeQuery(patientNameQuery);

            while(patientFNResults.next()){
                name = patientFNResults.getString(sqlName);
            }

            patientFNResults.close();

            return name;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return name;
        }
        finally{
            try{
                //connection.close();
                //MySQLConnectionProperties.getStatementObject().close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                return name;
            }
        }        
    } // end retrieveName

    /**
     * @pre must have successfully retrieved a patient's first name and last name from the database
     * @param firstName - patient's first name
     * @param lastName - patient's last name
     * @return -999 if patientID could not be found or patient ID < 0 if found
     * @post 
     */
    public static int retrieveID(String firstName, String lastName){
        //Connection connection = null;
        //Statement statement = null;
        int patID = -999;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();

            String patientIDQuery = "SELECT patients.patientID from patients, users "
                + "where users.userID = patients.userID AND users.first_name = '" + firstName
                + "' AND users.last_name = '" + lastName + "';";

            ResultSet patientIDResults = MySQLConnectionProperties.getStatementObject().executeQuery(patientIDQuery);

            while(patientIDResults.next()){
                patID = patientIDResults.getInt("patients.patientID");
            }

            patientIDResults.close();

            return patID;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return patID;
        }
        finally{
            try{
                //connection.close();
                //MySQLConnectionProperties.getStatementObject().close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                return patID;
            }
        }
    } // end retrieveID

    public void loadData(){
        //Connection connection = null;
        //Statement statement = null;

        // Getting and setting the patients name 
        String firstName = retrieveName(sqlFirstName, username);
        String lastName = retrieveName(sqlLastName, username);

        if(firstName.equals("") || lastName.equals("")){
            //logic here to display an error
        }

        patientID = retrieveID(firstName, lastName);

        fullName = firstName + " " + lastName;

        // Getting and setting appointments
        HashMap appointmentMap = retrieveAppointments(patientID);
        if(appointmentMap != null){
            insertAppointments(appointmentMap);
        }
        else {
            System.out.println("In loadData() apppointmentMap is null");
        }
    } // end loadData

    /**
     * @pre
     * @param appointmentMap - HashMap containing a list of appointments to display
     * @post GUI displays the appropriate appointments
     */
    public void insertAppointments(HashMap<String, List<String>> appointmentMap){
        for(String date: appointmentMap.keySet()){
            List<String> timeSlots = appointmentMap.get(date);

            for(String time: timeSlots){
                // Create appointment with the date and time 
                Appointment newAppointment = new Appointment(date, time);

                // Add appointment to the observable list 
                data.add(newAppointment);
            }
        }
    } // end insertAppointments 

    /**
     * @pre - patient must have appointments saved in database in order to successfully fill hashmap with appropriate data
     * @param patID - patient's ID
     * @return HashMap<String, List<String>> that contains a mapping of the appointments, if empty hashmap returns null
     * @post
     */
    public static HashMap<String, List<String>> retrieveAppointments(int patID){
        //Connection connection = null;
        //Statement statement = null;

        HashMap<String, List<String>> appointmentMap = new HashMap<>();

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();

            String appointmentQuery = "select * from appointment " +
                "where patientID = " + patID + ";";

            ResultSet appointmentResults = MySQLConnectionProperties.getStatementObject().executeQuery(appointmentQuery);

            while(appointmentResults.next()){
                String time = appointmentResults.getString("time_scheduled");
                String date = appointmentResults.getString("date_scheduled");

                if(appointmentMap.containsKey(date)){
                    appointmentMap.get(date).add(time);
                } else {
                    // appointmentMap.put(date, Arrays.asList(time));
                   ArrayList<String> newList = new ArrayList<>();
                   newList.add(time);
                   appointmentMap.put(date, newList);
                }
            }

            appointmentResults.close();

            if(appointmentMap.isEmpty()){
                return null;
            }

            return appointmentMap;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
        finally{
            try{
                //connection.close();
                //MySQLConnectionProperties.getStatementObject().close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                return null;
            }
        }
    } // end HashMap
    
    /**
     * signIn
     * Returns the user to the sign-in screen
     */
    @FXML
    private void signIn(ActionEvent event) throws Exception
    {
        // Get the current stage
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        // Create a create account instance and call the start method
        Login newLogin = new Login();
        newLogin.start(window);
    } // end signIn

    /**
     * Used to hold date and time for an appointment
     */
    public class Appointment
    {
        private String date, time; 

        /**
         *  Appointment
         *  String date - new date of the appointment
         *  String time - new time of the appointment 
         */
        public Appointment(String newDate, String newTime)
        {
            date = newDate;
            time = newTime; 
        } // end Appointment 

        /**
         * getTime
         * returns the time
         */
        public String getTime()
        {
            return time; 
        } // end getTime

        /**
         * getDate
         * returns the date 
         */
        public String getDate()
        {
            return date;
        } // end getData
    } // end Appointment
} // end PatientAppointmentView 
