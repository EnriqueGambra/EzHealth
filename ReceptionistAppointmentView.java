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

//This class will be for the receptionist's view
public class ReceptionistAppointmentView implements Initializable {    
    // Setting up the stage
    private static Stage stage;

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent receptionist = FXMLLoader.load(getClass().getResource("fxml/receptionistViewAppointment.fxml"));

        // Create scene and load it onto the stage
        Scene receptionistAppointmentScene = new Scene(receptionist);
        stage.setTitle("Receptionist Create/Edit Appointment");
        stage.setScene(receptionistAppointmentScene);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

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

    // Date Picker
    @FXML
    private DatePicker appointmentDate;

    // Buttons
    @FXML
    private Button signOutButton;

    @FXML
    private Button submitButton;

    // Table
    @FXML
    private TableView<CreateAppointment> appointmentTable;

    @FXML
    private TableColumn<CreateAppointment, String> timeColumn;

    @FXML
    private TableColumn<CreateAppointment, String> patientIDColumn;

    @FXML
    private TableColumn<CreateAppointment, String> patientNameColumn;

    @FXML
    private TableColumn<CreateAppointment, String> doctorColumn;

    private ObservableList<CreateAppointment> data = FXCollections.observableArrayList();

    // ComboBox
    @FXML
    private ComboBox receptionistComboBox;

    // option
    private String option; 

    // timeslot size
    private final int TIME_SLOT_SIZE = 8;

    private static final String DOC_ID_KEY = "docID";
    private static final String PAT_ID_KEY = "patID";
    private static final String TIME_SLOT_KEY = "timeSlot";

    private List<String> timeSlots = new ArrayList<>();

    private static String formattedDate;

    // Date
    private LocalDate newDate;     
    /**
     * Initializes the controller class for SceneBuilder
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize combox box

        receptionistComboBox.getItems().removeAll(receptionistComboBox.getItems());
        receptionistComboBox.getItems().addAll("Create/Edit Appointment", "Patient Lookup");
        receptionistComboBox.getSelectionModel().select("Create/Edit Appointment");
        option = "Create/Edit Appointment";
        //MySQLConnectionProperties.createConnection();

        // Add event listener to combo box
        receptionistComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
                    // set option to new value 
                    option = newValue;
                }    
            });

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    if(option.equals("Create/Edit Appointment")){
                        //Create/edit appointments page
                        System.out.println("Submitting event");
                        updateTimeslots();
                        submitDataToDatabase();
                    }
                }
            });

        // Add event listener to datepicker
        appointmentDate.valueProperty().addListener((ov, oldValue, newValue) -> {
                // Gets the date selected as a localdate variable 
                newDate = newValue; 

                String[] dateArray = newDate.toString().split("-");

                String year = dateArray[0];
                String month = dateArray[1];
                String day = dateArray[2];

                formattedDate = month + "/" + day + "/" + year;
                ArrayList appointmentDataList = retrieveAppointmentInfo(formattedDate);

                //Start initialization to display our appointment info
                initDisplayAppointmentInfo(appointmentDataList);
            });

        String timeSlotsFinal[] = new String[] {"9-10", "10-11", "11-12", "12-1", "1-2", "2-3", "3-4", "4-5"};
        timeSlots = Arrays.asList(timeSlotsFinal);
        // set table up 
        timeColumn.setCellValueFactory(new PropertyValueFactory<CreateAppointment, String>("timeSlot"));

        patientIDColumn.setCellValueFactory(new PropertyValueFactory<CreateAppointment, String>("patientID"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<CreateAppointment, String>("patientName"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<CreateAppointment, String>("doctor"));

        patientIDColumn.setCellFactory(TextFieldTableCell.<CreateAppointment, String>forTableColumn(new DefaultStringConverter()));
        patientIDColumn.setOnEditCommit(t -> t.getRowValue().setPatientID(t.getNewValue()));

        patientNameColumn.setCellFactory(TextFieldTableCell.<CreateAppointment, String>forTableColumn(new DefaultStringConverter()));
        patientNameColumn.setOnEditCommit(t -> t.getRowValue().setPatientName(t.getNewValue()));

        doctorColumn.setCellFactory(TextFieldTableCell.<CreateAppointment, String>forTableColumn(new DefaultStringConverter()));
        doctorColumn.setOnEditCommit(t -> t.getRowValue().setDoctorName(t.getNewValue()));

        // appointmentTableView.setItems(data);
        // for(Appointment a: data)
        // {
        // System.out.println(a.toString());
        // }
    } // end initialize

    /**
     * comboChange
     * Directs user to the appropriate page of the combo box
     */
    @FXML
    private void comboChange(ActionEvent event) throws Exception
    {
        if(option.equals("Patient Lookup"))
        {
            // Get the current stage
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            // Create a lookup view and start it
            ReceptionistLookupView lookup = new ReceptionistLookupView();
            lookup.start(window);
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

    @FXML
    private void submit(ActionEvent event){
        System.out.println("Attempting to submit event event");
        try{
            if(option.equals("Create/Edit Appointment")){
                //Create/edit appointments page
                System.out.println("Submitting event");
                updateTimeslots();
                submitDataToDatabase();
                // newAppointment.run();
            }
        }
        catch(Exception ex){
            System.out.println("Error");
            System.out.println(ex.getMessage());
        }
    }

    public void submitDataToDatabase(){
        try{
            // int for incomplete appointments 
            int incomplete = 0;
            String timeSlot = null;

            //loop through the various fields to update the values to the DB
            for(int i = 0; i < TIME_SLOT_SIZE; i++){               
                timeSlot = String.valueOf(appointmentTable.getColumns().get(0).getCellObservableValue(i).getValue());
                String patientID = String.valueOf(appointmentTable.getColumns().get(1).getCellObservableValue(i).getValue());
                String doctorName = String.valueOf(appointmentTable.getColumns().get(3).getCellObservableValue(i).getValue());

                // if at least one field has information therefore an appointment was attempted to be made
                // in that case scenario we check if all fields have input
                if(((!patientID.isEmpty() || !doctorName.isEmpty())) && ((patientID.isEmpty() || doctorName.isEmpty())))
                {
                    // there was an incomplete appointment 
                    incomplete++;
                }
                else // appointment was empty or valid
                {
                    //patientID of 5 is the generic 'null' patient. In the database
                //I designed it so whenever we look up a new date (let's say for
                //instance we never looked up the date 12/21/2012) we'll create
                //a bunch of empty record appointment timeslots within the database
                //so we know we have looked at it... setting these slots to patient
                //ID of 5 (sort of a dummy way of saying there is a patient there but
                //in reality there isn't...) 
                if(patientID.equals("")){
                    patientID = String.valueOf(MySQLConnectionProperties.getNullUserID());
                    doctorName = "";
                }

                String updateValues = "UPDATE appointment SET doctorID = (SELECT doctors.doctorID from doctors, users where users.userID = doctors.userID " +
                    "AND users.last_name = '" + doctorName + "'), patientID = " + Integer.parseInt(patientID) + 
                    " where date_scheduled = '" + formattedDate + "' AND time_scheduled = '" + timeSlot + "';";

                MySQLConnectionProperties.getStatementObject().execute(updateValues);
                }
            }
            
            // Print the appropriate alert, notify of errors or if it was successful
            if(incomplete > 0)
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Missing Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText(incomplete + " appointments failed to update.\nEnsure to fill both Patient ID and Doctor name filed.");
                warnAlert.showAndWait();
                return;
            }
            else
            {
                // Success message
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Success");
                confirmAlert.setHeaderText(null); 
                confirmAlert.setContentText("All appointments saved successfully!");
                confirmAlert.showAndWait();
                return;
            }
        }

        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        finally{
            try{
                //connection.close();
                //MySQLConnectionProperties.getStatementObject().close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Checks to see if ALL time slots have been created for a specific date.
     * If some time slots haven't been created, inserts these time slots into
     * the database to allow the successful creation of appointments for users
     * in the future.
     */
    public void updateTimeslots(){

        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;

        //List that will contain time slots already created for a particular
        //appointment date in the database
        ArrayList<String> timeslotsAlreadyCreated = new ArrayList<>();

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT time_scheduled FROM appointment where date_scheduled = '" + formattedDate + "';");

            //See if there is a time_scheduled that corresponds already to an appointment there and
            //add it to the timeslotsAlreadyCreated list
            while(resultSet.next()){
                String time = resultSet.getString("time_scheduled");
                timeslotsAlreadyCreated.add(time);
            }

            resultSet.close();

            // loop through the timeSlots list and compare to see what time slots
            // are missing between the timeslotsAlreadyCreated list. If time slots
            // are missing, we need to create an "empty" record for the appointment
            // to ensure if we do add a user to that particular time slot in the
            // future it's already created

            for(int i = 0; i < TIME_SLOT_SIZE; i++){
                String timeSlot = String.valueOf(appointmentTable.getColumns().get(0).getCellObservableValue(i).getValue());
                if(timeslotsAlreadyCreated.indexOf(timeSlot) == -1){
                    String insertNullValues = "INSERT INTO appointment (date_scheduled, time_scheduled) " +
                        "values ('" + formattedDate + "', '" + timeSlot + "');";

                    MySQLConnectionProperties.getStatementObject().execute(insertNullValues);
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        finally{
            try{
                //connection.close();
                //MySQLConnectionProperties.getStatementObject().close();
                resultSet.close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }

    }

    /**
     * @pre date must be properly formatted
     * Retrieves appointment info from the database and places all data into
     * a map.
     * @param formattedDate - date of the list of appointments we want to retrieve
     * @return an ArrayList containing all appointment data for a specific date.
     */
    public ArrayList<HashMap<String, String>> retrieveAppointmentInfo(String formattedDate){
        //Checks the database to see if the date entered is within the DB
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;

        //List to hold all appointment data for a given day
        ArrayList<HashMap<String, String>> appointmentDataList = new ArrayList<>();

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();

            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT * FROM appointment where date_scheduled = '" + formattedDate + "';");

            while(resultSet.next()){
                //holds individual appointment data objects
                HashMap<String, String> appointmentData = new HashMap<>();

                //Put appropriate values into the hashmap
                appointmentData.put(DOC_ID_KEY, String.valueOf(resultSet.getInt("doctorID")));
                appointmentData.put(PAT_ID_KEY, String.valueOf(resultSet.getInt("patientID")));
                appointmentData.put(TIME_SLOT_KEY, resultSet.getString("time_scheduled"));

                //Add hashmap to the arraylist holding all appointment values
                appointmentDataList.add(appointmentData);
            }

            resultSet.close();    
            //MySQLConnectionProperties.getStatementObject().close();

            return appointmentDataList;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
        finally{
            try {
                resultSet.close();
                //MySQLConnectionProperties.getStatementObject().close();
                //connection.close();
            } 
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }
    }

    /**
     * @param appointmentDataList - ArrayList containing a HashMap<String, String> that contains information about each separate appointment
     * @post
     * Loops through the appointmentDataList passed in to call various methods
     * and begin displaying the values on various GUI components
     */
    public void initDisplayAppointmentInfo(ArrayList<HashMap<String, String>> appointmentDataList){
        ArrayList<CreateAppointment> appointmentList = new ArrayList<>();
        try{

            if(appointmentDataList.size() > 0){
                for(HashMap<String,String> appointmentData: appointmentDataList){
                    String patientFieldID = null;

                    //Get the time slot index (will be used as the index for each 
                    int index = timeSlots.indexOf(appointmentData.get(TIME_SLOT_KEY));
                    int patientID = Integer.parseInt(appointmentData.get(PAT_ID_KEY));
                    int doctorID = Integer.parseInt(appointmentData.get(DOC_ID_KEY));

                    String doctorName = retrieveDoctorName(doctorID);
                    String patientFirstName = retrievePatientFirstName(patientID);
                    String patientLastName = retrievePatientLastName(patientID);

                    String timeSlotAppointment = appointmentData.get(TIME_SLOT_KEY);

                    //If the patientID equals the nullUserID that means we don't want
                    //to display a patient ID of 5, but rather make it an empty String
                    if(patientID == MySQLConnectionProperties.getNullUserID()){
                        //textFieldPatientID.get(index).setText("");
                        patientFieldID = "";
                    }
                    else{
                        //textFieldPatientID.get(index).setText(Integer.toString(patientID));
                        patientFieldID = Integer.toString(patientID);
                    }

                    CreateAppointment newAppointment = new CreateAppointment(timeSlotAppointment, patientFieldID, patientFirstName + " " + patientLastName, 
                            doctorName); 

                    //appointmentTable.setItems(newAppointment);
                    appointmentList.add(newAppointment);
                }
            }
            else {
                for(int i = 0; i < TIME_SLOT_SIZE; i++){
                    CreateAppointment newAppointment = new CreateAppointment(timeSlots.get(i), "", "", "");
                    appointmentList.add(newAppointment);
                }

            }

            ObservableList<CreateAppointment> data = FXCollections.observableArrayList(appointmentList);
            appointmentTable.setItems(data);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Displays the doctor's last name on the appropriate GUI component
     * @param index - index of GUI component in list
     * @param doctorID
     */
    //    public void displayDoctorName(int index, int doctorID){
    //        String doctorName = retrieveDoctorName(doctorID);
    //        textFieldDoctors.get(index).setText(doctorName);
    //    }

    /**
     * @pre doctorID within database
     * Retrieves the doctor's ID from the database and returns the doctor's last name
     * @param doctorID 
     * @return doctor's last name or null (indicating we could not find the name or error).
     */
    public String retrieveDoctorName(int doctorID){
        String doctorNameQuery = "SELECT users.last_name from users, doctors where "
            + "doctorID = " + doctorID + " and doctors.userID = users.userID";

        try{

            //Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //Statement statement = connection.createStatement();

            String doctorLastName = null;

            ResultSet resultDoctorName = MySQLConnectionProperties.getStatementObject().executeQuery(doctorNameQuery);

            while(resultDoctorName.next()){
                doctorLastName = resultDoctorName.getString("users.last_name");
            }

            resultDoctorName.close();
            //connection.close();
            //MySQLConnectionProperties.getStatementObject().close();

            return doctorLastName;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Displays the patient's first name on the appropriate GUI component.
     * @param index
     * @param patientID 
     */
    //    public void displayPatientFirstName(int index, int patientID){
    //        String patientFirstName = retrievePatientFirstName(patientID);
    //        textFieldPatients.get(index).setText(patientFirstName);
    //    }

    /**
     * @pre patientID within database
     * Retrieves the patientID from the database and returns the patient's first name
     * @param patientID 
     * @return patient's first name or null (indicating we could not find the name or error).
     */
    public String retrievePatientFirstName(int patientID){
        String patientFirstNameQuery = "SELECT users.first_name "
            + "from users, patients where patients.patientID = " + patientID + " and "
            + "patients.userID = users.userID";

        try{

            //Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //Statement statement = connection.createStatement();

            String patientFirstName = null;
            ResultSet resultPatientFirstName = MySQLConnectionProperties.getStatementObject().executeQuery(patientFirstNameQuery);

            while(resultPatientFirstName.next()){
                patientFirstName = resultPatientFirstName.getString("users.first_name");
            }

            resultPatientFirstName.close();
            //connection.close();
            //MySQLConnectionProperties.getStatementObject().close();

            return patientFirstName;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Displays the patient's last name on the appropriate GUI component.
     * @param index
     * @param patientID 
     */
    //    public void displayPatientLastName(int index, int patientID){
    //        String patientLastName = retrievePatientLastName(patientID);
    //        String priorText = textFieldPatients.get(index).getText();
    //        textFieldPatients.get(index).setText(priorText + " " + patientLastName);
    //    }

    /**
     * @pre patientID within database
     * Retrieves the patientID from the database and returns the patient's last name
     * @param patientID
     * @return patient's last name or null (indicating we could not find the name or error).
     */
    public String retrievePatientLastName(int patientID){
        String patientLastNameQuery = "SELECT users.last_name "
            + "from users, patients where patients.patientID = " + patientID + " and "
            + "patients.userID = users.userID";

        try{

            // Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            // Statement statement = connection.createStatement();

            String patientLastName = null;
            // ResultSet resultPatientLastName = MySQLConnectionProperties.getStatementObject().executeQuery(patientLastNameQuery);
            ResultSet resultPatientLastName = MySQLConnectionProperties.getStatementObject().executeQuery(patientLastNameQuery);

            while(resultPatientLastName.next()){
                patientLastName = resultPatientLastName.getString("users.last_name");
            }

            resultPatientLastName.close();
            //connection.close();
            //MySQLConnectionProperties.getStatementObject().close();

            return patientLastName;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Used to hold information for the edit/create appointment table
     **/
    public class CreateAppointment
    {
        private String timeSlot, patientName, doctor, patientID; 
        /**
         *  CreateAppointment - holds an appointment of a specific date 
         *  
         *  String newTimeSlot - new timeslot of the appointment
         *  int newPatientID - ID of the patient of the appointment
         *  String newPatientName - name of the patient of the appointment
         *  String newDoctor - doctor of the appointment
         */
        public CreateAppointment(String newTimeSlot, String newPatientID, String newPatientName, String newDoctor)
        {
            timeSlot = newTimeSlot;
            patientID = newPatientID; 
            patientName = newPatientName;
            doctor = newDoctor; 
        } // end CreateAppointment 

        /**
         * getTimeSlot
         * returns the time slot
         */
        public String getTimeSlot()
        {
            return timeSlot; 
        } // end getTimeSlot

        /**
         * getPatientID
         * returns the Patient ID
         */
        public String getPatientID()
        {
            return patientID; 
        } // end getTimeSlot

        /**
         * getPatientName
         * returns the Patient Name
         */
        public String getPatientName()
        {
            return patientName; 
        } // end getPatientName

        /**
         * getDoctor
         * returns the doctor
         */
        public String getDoctor()
        {
            return doctor; 
        } // end getDoctor

        public void setPatientID(String newPatientID){
            this.patientID = newPatientID;
        }

        public void setPatientName(String name){
            this.patientName = name;
        }

        public void setDoctorName(String name){
            this.doctor = name;
        }
    } // end CreateAppointment
} // end ReceptionistView
