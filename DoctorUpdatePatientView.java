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

//This class will be for the doctor's view
public class DoctorUpdatePatientView implements Initializable {    
    // Setting up the stage
    private static Stage stage;

    // Doctors username
    private String username;
    // Patient ID
    private int patientID;

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();

    // Database column names               
    public static final String FIRST_NAME_COLUMN = "users.first_name";
    public static final String LAST_NAME_COLUMN = "users.last_name";
    public static final String MEDICAL_HISTORY_COLUMN = "patient_info.medical_history";
    public static final String MEDICATION_COLUMN = "patient_info.medication";
    public static final String INSURANCE_COLUMN = "patient_info.insurance";
    public static final String BIRTHDAY_COLUMN = "users.birthday";

    // Final Queries needed
    public static final String PATIENT_MEDICAL_HISTORY_QUERY = "select patient_info.medical_history from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public static final String PATIENT_LAST_NAME_QUERY = "select users.last_name from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public static final String PATIENT_FIRST_NAME_QUERY = "select users.first_name from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public static final String PATIENT_MEDICATION_QUERY = "select patient_info.medication from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public static final String PATIENT_INSURANCE_INFO_QUERY = "select patient_info.insurance from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public static final String PATIENT_BIRTHDAY_QUERY = "select users.birthday from (patients, patient_info, users) "
        + "where (patient_info.patientID = ) and (patient_info.patientID = patients.patientID) "
        + "and (patients.userID = users.userID);";

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent receptionist = FXMLLoader.load(getClass().getResource("fxml/doctorViewUpdatePatient.fxml"));

        // Create scene and load it onto the stage
        Scene receptionistAppointmentScene = new Scene(receptionist);
        stage.setTitle("Doctor Update Patient View");
        stage.setScene(receptionistAppointmentScene);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

    /**
     * Constructor
     * DoctorAppointmentView
     */
    public DoctorUpdatePatientView()
    {
        username = Login.getUsername();
    } // end DoctorUpdatePatientView

    // Pane
    @FXML
    private AnchorPane pane;

    // Labels
    @FXML
    private Label healthLabel;

    @FXML
    private Label ezLabel;

    @FXML
    private Label patientIDLabel;

    @FXML
    private Label selectLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label IDLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label birthdayLabel;

    @FXML
    private Label medHistoryLabel;

    @FXML
    private Label medicalLabel;

    @FXML
    private Label insuranceLabel;

    // Buttons
    @FXML
    private Button signOutButton;

    @FXML
    private Button goButton;

    @FXML
    private Button submitButton;

    // TextFields / TextAreas
    @FXML
    private TextField IDField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField birthdayField;

    @FXML
    private TextField insuranceField;

    @FXML
    private TextField patientIDField;

    @FXML
    private TextArea medHisTextField;

    @FXML
    private TextArea medField;

    // ComboBox
    @FXML
    private ComboBox doctorComboBox;

    // option
    private String option; 

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
        doctorComboBox.getSelectionModel().select("Update Patient Info");
        option = "Update Patient Info";

        // Set textfields to uneditable 
        IDField.setEditable(false);
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        birthdayField.setEditable(false);

        // Add event listener to combo box
        doctorComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
                    // set option to new value 
                    option = newValue;
                }    
            });
    } // end initialize

    /**
     * goAction
     * Takes the Patient ID from the textfield
     */
    @FXML
    void goAction(ActionEvent event) {
        String patientIDString = patientIDField.getText();
        try{
            // attempt to parse patientID to an int 
            patientID = Integer.parseInt(patientIDString);

            // get the patient info and display it onto the GUI
            getPatientInfo(); 
        }
        catch(Exception e)
        {
            Alert warnAlert = new Alert(AlertType.ERROR);
            warnAlert.setTitle("Incorrect Data Type");
            warnAlert.setHeaderText(null); 
            warnAlert.setContentText("Please enter the Patient ID as an integer");
            warnAlert.showAndWait();
            return;
        }
    } // end goAction

    /**
     * comboChange
     * Directs user to the appropriate page of the combo box
     */
    @FXML
    private void comboChange(ActionEvent event) throws Exception
    {
        if(option.equals("View All Appointments"))
        {
            // Get the current stage
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            // Create a doctor appointment view and start it
            DoctorAppointmentView appointments = new DoctorAppointmentView();
            appointments.start(window);
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

    /**
     * Displays patient info onto the GUI
     */
    public void getPatientInfo(){
        int id = getPatientID();

        // Set the fields 
        IDField.setText(Integer.toString(id));

        //Gets and sets the patient's first name
        firstNameField.setText(retrieveResultsFromDatabase(id, PATIENT_FIRST_NAME_QUERY, FIRST_NAME_COLUMN));

        //Gets and sets the patient's last name
        lastNameField.setText(retrieveResultsFromDatabase(id, PATIENT_LAST_NAME_QUERY, LAST_NAME_COLUMN));

        //Gets and sets the patient's medical information
        medHisTextField.setText(retrieveResultsFromDatabase(id, PATIENT_MEDICAL_HISTORY_QUERY, MEDICAL_HISTORY_COLUMN));

        //Gets and sets the patient's medication history
        medField.setText(retrieveResultsFromDatabase(id, PATIENT_MEDICATION_QUERY, MEDICATION_COLUMN));

        //Gets and sets the patient's insurance information
        insuranceField.setText(retrieveResultsFromDatabase(id, PATIENT_INSURANCE_INFO_QUERY, INSURANCE_COLUMN));

        //Gets and sets the patient's birthday
        birthdayField.setText(retrieveResultsFromDatabase(id, PATIENT_BIRTHDAY_QUERY, BIRTHDAY_COLUMN));
    } // end getPatientInfo

    /**
     * @pre must have a valid appointment and valid queries
     * @param id - patientID
     * @param query
     * @param dbColumnName - name of the column located in the database
     * @return null (indicating could not find result or error) or a String
     * @post retrieves results from the database to be populated in the resulting textfield
     */
    public static String retrieveResultsFromDatabase(int patientID, String query, String dbColumnName){        
        ResultSet resultSet;
        String result = null;

        try{
            // Replace parameters in the query to have the patientID in place
            query = query.replace("where (patient_info.patientID = ", "where (patient_info.patientID = " + patientID);

            resultSet = MySQLConnectionProperties.getStatementObject().executeQuery(query);

            while(resultSet.next()){
                result = resultSet.getString(dbColumnName);
            }

            resultSet.close();
            return result;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    } 

    @FXML
    private void submit(ActionEvent event){
        System.out.println("Attempting to submit event event");
        try{
            String medHist = medHisTextField.getText();
            String medicine = medField.getText();
            String insurance = insuranceField.getText();
            insertIntoDB(medHist, medicine, insurance);
        }
        catch(Exception e){
            Alert warnAlert = new Alert(AlertType.ERROR);
            warnAlert.setTitle("Update failed");
            warnAlert.setHeaderText(null); 
            warnAlert.setContentText("An error occured, please try again");
            warnAlert.showAndWait();
            return;
        }
    } // end submit

    public int getPatientID(){
        return patientID;
    } // end getPatientID

    public void insertIntoDB(String medHist, String medicine, String insurance){
        //Connection connection = null;
        //Statement statement = null;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            int patientID = getPatientID();
            //Insert values into database from patient information
            String insertQuery = "update patient_info set medical_history = '" + medHist +
                "', medication = '" + medicine + "', insurance = '" + insurance + "' where patientID = " + patientID + ";";

            MySQLConnectionProperties.getStatementObject().execute(insertQuery);

            // If we reach here success message
            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("Success");
            confirmAlert.setHeaderText(null); 
            confirmAlert.setContentText("Patient information updated");
            confirmAlert.showAndWait();
            return;
        }
        catch(SQLException ex)
        {
            Alert warnAlert = new Alert(AlertType.ERROR);
            warnAlert.setTitle("Update failed");
            warnAlert.setHeaderText(null); 
            warnAlert.setContentText("An error occured, please try again");
            warnAlert.showAndWait();
            return;
        }

    } // end insertIntoDB
} // end DoctorUpdatePatientView