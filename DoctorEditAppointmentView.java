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
public class DoctorEditAppointmentView implements Initializable {    
    // Setting up the stage
    private static Stage stage;

    // Variables needed
    private String doctorUsername, patientName, date, time, appointmentID;

    // Patient ID
    private int patientID;

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();
    
    private static final int FIELD_CHARACTER_LIMIT = 1000;

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent receptionist = FXMLLoader.load(getClass().getResource("fxml/doctorEditAppointment.fxml"));

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
    public DoctorEditAppointmentView()
    {
        this(DoctorAppointmentView.getFormattedDate(), DoctorAppointmentView.getSelectedTimeslot());

    } // end DoctorEditAppointmentView
    
    public DoctorEditAppointmentView(String date, String time) {
        this.date = date;
        this.time = time;
    }

    // Pane
    @FXML
    private AnchorPane pane;

    // Labels
    @FXML
    private Label healthLabel;

    @FXML
    private Label ezLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Label medHistoryLabel;

    @FXML
    private Label reasonLabel;

    // Buttons
    @FXML
    private Button signOutButton;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    // TextFields / TextAreas
    @FXML
    private TextArea appointmentReasonField;

    @FXML
    private TextArea procedureField;

    /**
     * Initializes the controller class for SceneBuilder
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // set values
        // doctorUsername = Login.getUsername();
        this.patientName = DoctorAppointmentView.getSelectedPatientName();
        
        // load patient name, date and time of appointment being edited
        nameLabel.setText("Editing Appointment for " + this.patientName);
        dateTimeLabel.setText("Date: " + date + "\t\tTime: " + time);
        
        displayAppointmentInfo();
    } // end initialize

    /**
     * goAction
     * Takes the Patient ID from the textfield
     */
    @FXML
    void goAction(ActionEvent event) {
  
    } // end goAction

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
     * backAction
     * Returns the user to the view appointment screen
     */
    @FXML
    private void backAction(ActionEvent event) throws Exception
    {
        // Get the current stage
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        // Create a doctor appointment view and start it
        DoctorAppointmentView appointments = new DoctorAppointmentView();
        appointments.start(window);
    } // end backAction
    

    /**
     * Displays appointment info on the GUI
     */
    public void displayAppointmentInfo(){
        
        HashMap<String, String> appointmentData = retrieveAppointmentInfo();
        
        appointmentReasonField.setText(appointmentData.get("resultReasonForAppointment"));
        procedureField.setText(appointmentData.get("resultProceduresDone"));
        
        this.appointmentID = appointmentData.get("resultAppointmentID");
    }

    /**
     * @pre - must have a valid appointment date and time
     * @param id - patientID
     * @param query
     * @param dbColumnName - name of the column located in the database
     * @return null (indicating could not find result or error) or a String
     * @post retrieves the appropriate appointment info
     */
    public HashMap<String, String> retrieveAppointmentInfo(){        
        ResultSet resultSet;
        HashMap<String, String> resultData = new HashMap<>();

        try{
            // Replace parameters in the query to have the patientID in place
            String query = "SELECT reason_for_appointment, procedures_done, appointmentID FROM appointment WHERE "
                + "date_scheduled LIKE'" + this.date + "' AND time_scheduled LIKE '" + this.time + "';";

            resultSet = MySQLConnectionProperties.getStatementObject().executeQuery(query);

            while(resultSet.next()){
                
                String resultReasonForAppointment = resultSet.getString("reason_for_appointment");
                
                if(resultReasonForAppointment == null) {
                    resultReasonForAppointment = "";
                }
                
                String resultProceduresDone = resultSet.getString("procedures_done");
                
                if(resultProceduresDone == null) {
                    resultProceduresDone = "";
                }
                
                String resultAppointmentID = Integer.toString(resultSet.getInt("appointmentID"));
                
                resultData.put("resultReasonForAppointment", resultReasonForAppointment);
                resultData.put("resultProceduresDone", resultProceduresDone);
                resultData.put("resultAppointmentID", resultAppointmentID);
            }

            resultSet.close();
            return resultData;
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
            String appointmentReason = appointmentReasonField.getText();
            String procedure = procedureField.getText();
            
            if(!invalidCharacterLimit(appointmentReason.length()) && !invalidCharacterLimit(procedure.length())) {
                insertIntoDB(appointmentReason, procedure);
            }  
            
            // Get the current stage
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            // Create a doctor appointment view and start it
            DoctorAppointmentView appointments = new DoctorAppointmentView();
            appointments.start(window);
        }
        catch(Exception e){
            Alert warnAlert = new Alert(AlertType.ERROR);
            if(e.getMessage().equals("Invalid character limit")) {
                warnAlert.setContentText("Invalid character limit. You are only permitted 1000 characters in either field.");
            }
            else {
                warnAlert.setContentText("An error occured, please try again");
            }
            warnAlert.setTitle("Update failed");
            warnAlert.setHeaderText(null); 
            
            warnAlert.showAndWait();
            return;
        }
    } // end submit

    /**
     * @pre
     * @param fieldValueLength - the length of a string from a particular field
     * @return false -- if it doesn't violate the FIELD_CHARACTER_LIMIT or throws an exception if it does violate
     * @post proceeds to allow queries to be inserted into the database depending on if there was a violation or not
     */
    public static boolean invalidCharacterLimit(int fieldValueLength) throws Exception {
    
        if(fieldValueLength > FIELD_CHARACTER_LIMIT) {
            throw new Exception("Invalid character limit");
        }
        
        return false; 
    }
    
    public int getPatientID(){
        return patientID;
    } // end getPatientID

    /**
     * @pre Appointment info successfully retrieved
     * @param appReason - the appointment reason field text
     * @param procedure - the procedure field text
     * @post data successfully inserted into database
     */
    public void insertIntoDB(String appReason, String procedure){
        try{
            int patientID = getPatientID();
            //Insert values into database from patient information
            // FIX THIS QUERY TO WHATEVER YOU'RE DOING
            String insertQuery = "update appointment set reason_for_appointment = '" + appReason +
             "', procedures_done = '" + procedure + "' where appointmentID = " + Integer.parseInt(this.appointmentID) + ";";

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