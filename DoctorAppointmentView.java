// imports 
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
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*; 
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView.TableViewSelectionModel;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;

// SQL
import java.sql.*;

// Tables
import javafx.collections.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.TableColumn.*;
import javafx.util.converter.*;
import javafx.scene.control.cell.CheckBoxListCell;

// FXML
import javafx.fxml.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.ResourceBundle; 
import java.net.*;
import javafx.util.Callback;

// Exception
import java.io.IOException;

//This class will be for the doctor's view
public class DoctorAppointmentView implements Initializable {    
    // Setting up the stage
    private static Stage stage;

    // Doctors username
    private String username;
    public static int doctorID;

    // Date
    private static String formattedDate;
    private LocalDate newDate;  

    // Currently selected appointment timeslot and patient name
    private static String selectedTimeslot, selectedPatient;

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent receptionist = FXMLLoader.load(getClass().getResource("fxml/doctorViewAppointment.fxml"));

        // Create scene and load it onto the stage
        Scene receptionistAppointmentScene = new Scene(receptionist);
        stage.setTitle("Doctor Appointment View");
        stage.setScene(receptionistAppointmentScene);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

    /**
     * Constructor
     * DoctorAppointmentView
     */
    public DoctorAppointmentView()
    {
        username = Login.getUsername();
        doctorID = getDoctorID(username);
        System.out.println("DOCTOR ID IS: " + doctorID);
    } // end DoctorAppointView

    // Pane
    @FXML
    private AnchorPane pane;

    // Labels
    @FXML
    private Label healthLabel;

    @FXML
    private Label ezLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label selectLabel;

    @FXML
    private Label welcomeLabel;

    // Date Picker
    @FXML
    private DatePicker appointmentDate;

    // Buttons
    @FXML
    private Button signOutButton;

    @FXML
    private Button editButton;

    // Table
    @FXML
    private TableView<DoctorAppointment> appointmentTable;

    @FXML
    private TableColumn<DoctorAppointment, String> timeColumn;

    @FXML
    private TableColumn<DoctorAppointment, String> patientNameColumn;

    private ObservableList<DoctorAppointment> data = FXCollections.observableArrayList();

    // ComboBox
    @FXML
    private ComboBox doctorComboBox;

    // option
    private String option; 

    // timeslot size
    private final int TIME_SLOT_SIZE = 8;

    private static final String DOC_ID_KEY = "docID";
    private static final String PAT_ID_KEY = "patID";
    private static final String TIME_SLOT_KEY = "timeSlot";

    private List<String> timeSlots = new ArrayList<>();

    /**
     * Initializes the controller class for SceneBuilder
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // load name into the label
        welcomeLabel.setText("Welcome Dr. " + username);

        // Initialize combox box
        doctorComboBox.getItems().removeAll(doctorComboBox.getItems());
        doctorComboBox.getItems().addAll("View All Appointments", "Update Patient Info");
        doctorComboBox.getSelectionModel().select("View All Appointments");
        option = "View All Appointments";
        //MySQLConnectionProperties.createConnection();

        // set button up 
        editButton.setDisable(true);

        // set table up 
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<DoctorAppointment, String>("patientName"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<DoctorAppointment, String>("timeSlot"));

        // Add event listener to combo box
        doctorComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
                    // set option to new value 
                    option = newValue;
                }    
            });

        // Add event listener to datepicker
        appointmentDate.valueProperty().addListener((ov, oldValue, newValue) -> {
                // Gets the date selected as a localdate variable 
                newDate = newValue; 
                data.clear();

                String[] dateArray = newDate.toString().split("-");

                String year = dateArray[0];
                String month = dateArray[1];
                String day = dateArray[2];

                formattedDate = month + "/" + day + "/" + year;

                System.out.println("LISTENER LISTENING, the date is: " + formattedDate);

                loadData(formattedDate);

                // disable edit until user selects a new date
                editButton.setDisable(true);

                //ArrayList appointmentDataList = retrieveAppointmentInfo(formattedDate);
                //Start initialization to display our appointment info
                //initDisplayAppointmentInfo(appointmentDataList);
                System.out.println("Data list size is: " + data.size());

                if(data.size() != 0){
                    appointmentTable.setItems(data); 
                } 
                else {
                    appointmentTable.setItems(null);
                    System.out.println("Appointment list is size 0");
                }
            });

        // Row selection
        appointmentTable.getSelectionModel().setCellSelectionEnabled(false);
        // Listener view for table 
        appointmentTable.getFocusModel().focusedCellProperty().addListener(new ChangeListener<TablePosition>() {
                @Override
                public void changed(ObservableValue<? extends TablePosition> arg0, TablePosition arg1, TablePosition arg2) {
                    // edit button accessible
                    editButton.setDisable(false);
                    
                    // get the appointment
                    DoctorAppointment da = appointmentTable.getItems().get(arg2.getRow());

                    // set the patient name and time slot of the currently selected appointment
                    selectedPatient = (String) patientNameColumn.getCellObservableValue(da).getValue();
                    selectedTimeslot = (String) timeColumn.getCellObservableValue(da).getValue();
                }
            });
    } // end initialize

    /**
     * editAction
     * Takes the doctor to the edit appointment screen of the currently selected appointment
     */
    @FXML
    private void editAction(ActionEvent event) throws Exception
    {
        // Get the current stage
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        // Create an edit appointment view
        DoctorEditAppointmentView editAppointment = new DoctorEditAppointmentView();
        editAppointment.start(window);
    } // end editAction

    /**
     * @pre must have a properly formatted date and doctorID
     * @param date
     * @param doctorID
     * @return - an ArrayList containing strings of patients first names that have scheduled appointments, or null if exception occurs
     * @post retrieves the patient's first name
     */
    public static ArrayList<String> getPatientsFirstName(String date, int doctorID){

        ArrayList<String> patientFNArray = new ArrayList<>();
        ResultSet patientFNameResults = null;
        try{
            String patientFNQuery = "select users.first_name from users, appointment, patients where "
                + "users.userID = patients.userID AND patients.patientID = appointment.patientID " +
                "AND appointment.date_scheduled = '" + date + "' AND appointment.doctorID = " + doctorID + ";";

            patientFNameResults = MySQLConnectionProperties.getStatementObject().executeQuery(patientFNQuery);

            while(patientFNameResults.next()){
                patientFNArray.add(patientFNameResults.getString("users.first_name"));
            }

            patientFNameResults.close();
            return patientFNArray;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * @pre must have a properly formatted date and doctorID
     * @param date
     * @param doctorID
     * @return - an ArrayList containing strings of patients last names that have scheduled appointments, or null if exception occurs
     * @post retrieves the patient's last name
     */
    public static ArrayList<String> getPatientsLastName(String date, int doctorID){

        ArrayList<String> patientLNArray = new ArrayList<>();

        ResultSet patientLNameResults = null;

        String patientLNQuery = "select users.last_name from users, appointment, patients where "
            + "users.userID = patients.userID AND patients.patientID = appointment.patientID " +
            "AND appointment.date_scheduled = '" + date + "' AND appointment.doctorID = " + doctorID + ";";

        try{

            patientLNameResults = MySQLConnectionProperties.getStatementObject().executeQuery(patientLNQuery);

            while(patientLNameResults.next()){
                patientLNArray.add(patientLNameResults.getString("users.last_name"));
            }

            patientLNameResults.close();
            return patientLNArray;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    } 

    /**
     * @pre must have a properly formatted date and doctorID
     * @param date
     * @param doctorID
     * @return - an ArrayList containing strings of times scheduled for appointments, or null if exception occurs
     * @post retrieves a list of times scheduled for a particular appointment date
     */
    public static ArrayList<String> getTimesScheduled(String date, int doctorID){

        ArrayList<String> timeSchArray = new ArrayList<>();

        String timeScheduleQuery = "select DISTINCT(appointment.time_scheduled) from appointment, patients " +
            "where appointment.patientID != 5 AND appointment.date_scheduled = '" + date + "' AND appointment.doctorID = " + doctorID + ";";

        try{
            ResultSet timeScheduledResults = MySQLConnectionProperties.getStatementObject().executeQuery(timeScheduleQuery);

            while(timeScheduledResults.next()){
                timeSchArray.add(timeScheduledResults.getString("appointment.time_scheduled"));
            }

            timeScheduledResults.close();
            return timeSchArray;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

    }

    /**
     * Load data from the database and populate the GUI
     */
    public void loadData(String formattedDate){

        ArrayList<String> patientFNArray = new ArrayList<>();
        ArrayList<String> patientLNArray = new ArrayList<>();
        ArrayList<String> timeSchArray = new ArrayList<>();

        try{
            patientFNArray = getPatientsFirstName(formattedDate, doctorID);

            System.out.println("patientFNArray size is: " + patientFNArray.size()); 
            if(patientFNArray != null && !patientFNArray.isEmpty()){

                patientLNArray = getPatientsLastName(formattedDate, doctorID);

                timeSchArray = getTimesScheduled(formattedDate, doctorID);

                for(int i = 0; i < timeSchArray.size(); i++){
                    String patientFullName = patientFNArray.get(i) + " " + patientLNArray.get(i);
                    System.out.println("Patient full name is: " + patientFullName);
                    System.out.println("time scheduled is: " + timeSchArray.get(i));
                    DoctorAppointment appointment = new DoctorAppointment(timeSchArray.get(i), patientFullName);
                    data.add(appointment);
                }
            }
            else{
                // frame.setVisible(false);
                // frame.dispose();
                // JOptionPane.showMessageDialog(null, "No appointments have been created for " + date + " yet!");
            }   
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * @pre must be logged into the system with an appropriate username
     * @param username
     * @return -99 = invalid doctor ID or (1 <= x <= infinity) signifying valid doctor ID
     * @post retrieves the doctor ID
     */
    public static int getDoctorID(String username){
        try{
            ResultSet retrieveDoctorID = MySQLConnectionProperties.getStatementObject().executeQuery("SELECT doctors.doctorID FROM doctors, users WHERE users.username = '" + username 
                    + "' AND users.userID = doctors.userID");
            while(retrieveDoctorID.next()){
                int doctorID = retrieveDoctorID.getInt("doctorID");
                return doctorID;
            }
            return -99;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return -99;
        }
    }

    /**
     * comboChange
     * Directs user to the appropriate page of the combo box
     */
    @FXML
    private void comboChange(ActionEvent event) throws Exception
    {
        if(option.equals("Update Patient Info"))
        {
            // Get the current stage
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            // Create a update patient view and start it 
            DoctorUpdatePatientView update = new DoctorUpdatePatientView();
            update.start(window);
        }
    } // end comboChange

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

    // get methods to get data in the edit appointment view 
    public static String getFormattedDate()
    {
        return formattedDate;
    } // end getFormattedDate

    public static String getSelectedPatientName()
    {
        return selectedPatient;
    } // end getSelectedPatientName

    public static String getSelectedTimeslot()
    {
        return selectedTimeslot;
    } // end getSelectedTimeslot

    /**
     * Used to hold information in appointment table
     **/
    public class DoctorAppointment
    {
        private String timeSlot, patientName;

        /**
         *  CreateAppointment - holds an appointment of a specific date 
         *  
         *  String newTimeSlot - new timeslot of the appointment
         *  String newPatientName - name of the patient of the appointment
         */
        public DoctorAppointment(String newTimeSlot, String newPatientName)
        {
            timeSlot = newTimeSlot;
            patientName = newPatientName;
        } // end DoctorAppointment 

        /**
         * getTimeSlot
         * returns the time slot
         */
        public String getTimeSlot()
        {
            return timeSlot; 
        } // end getTimeSlot

        /**
         * getPatientName
         * returns the Patient Name
         */
        public String getPatientName()
        {
            return patientName; 
        } // end getPatientName

        public void setTimeSlot(String timeslot){
            this.timeSlot = timeslot;
        } // end setTimeSlot

        public void setPatientName(String name){
            this.patientName = name;
        } // end setPatientName         
    } // end CreateAppointment
} // end DoctorAppointmentView
