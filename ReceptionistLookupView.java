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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

// Exception
import java.io.IOException;

//This class will be for the receptionist's view
public class ReceptionistLookupView implements Initializable {    
    // Setting up the stage
    private static Stage stage;

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();

    private static final int NO_ID_FOUND = -1;

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent receptionist = FXMLLoader.load(getClass().getResource("fxml/receptionistViewLookUp.fxml"));

        // Create scene and load it onto the stage
        Scene receptionistAppointmentScene = new Scene(receptionist);
        stage.setTitle("Receptionist Patient Lookup");
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
    private Label selectLabel;

    @FXML
    private Label firstName;

    @FXML
    private Label lastName;

    @FXML
    private Label birthday;

    // Fields 
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    // Date Picker
    @FXML
    private DatePicker datePicker;

    // Buttons
    @FXML
    private Button signOutButton;

    @FXML
    private Button searchButton;

    // ComboBox
    @FXML
    private ComboBox receptionistComboBox;

    // option
    private String option; 
    /**
     * Initializes the controller class for SceneBuilder
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize combox box
        receptionistComboBox.getItems().removeAll(receptionistComboBox.getItems());
        receptionistComboBox.getItems().addAll("Create/Edit Appointment", "Patient Lookup");
        receptionistComboBox.getSelectionModel().select("Patient Lookup");

        // Add event listener
        receptionistComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
                    // set option to new value 
                    option = newValue;
                }    
            });
    } // end initialize

    /**
     * comboChange
     * Directs user to the appropriate page of the combo box
     */
    @FXML
    private void comboChange(ActionEvent event) throws Exception
    {
        if(option.equals("Create/Edit Appointment"))
        {
            // Get the current stage
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            // Create appointment create/edit view and start it
            ReceptionistAppointmentView createAppointment = new ReceptionistAppointmentView();
            createAppointment.start(window);
        }
    } // end comboChange

    /**
     * submit
     * Checks user input and creates a new user in the database
     * If information is missing or invalid an appropriate error will be displayed
     */
    @FXML
    private void submit(ActionEvent event)
    {
        try
        {
            String firstNameInput = "";
            String lastNameInput = "";
            String birthdayInput = "";

            if(hasEmptyField(firstNameField.getText(), lastNameField.getText(), datePicker.getValue()))
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("No Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Please fill all fields.");
                warnAlert.showAndWait();
                return;
            }

            // Reaches here, everything should be safe 
            firstNameInput = firstNameField.getText();
            lastNameInput = lastNameField.getText();
            birthdayInput = datePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            // Search for the patient ID
            try{
                //Retrieve the patient ID from the database
                int patientID = retrievePatientID(firstNameInput, lastNameInput, birthdayInput);

                //Display appropriate dialog message
                if(patientID == NO_ID_FOUND){
                    Alert warnAlert = new Alert(AlertType.ERROR);
                    warnAlert.setTitle("Error: Patient ID not found");
                    warnAlert.setHeaderText(null); 
                    warnAlert.setContentText("Patient is not within the database. Check if name spelling and birthday is correct!");
                    warnAlert.showAndWait();
                    return;
                }
                else{
                    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Success: Patient ID found");
                    confirmAlert.setHeaderText(null); 
                    confirmAlert.setContentText(firstNameInput + " " + lastNameInput + "'s patientID is " + patientID);
                    confirmAlert.showAndWait();
                    return;
                }
            }
            catch(Exception ex){
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("An error has occured.");
                warnAlert.showAndWait();
                return;
            }
        }

        catch (Exception error) // very unlikely to occur but safe to catch 
        {
            System.out.println("An error has occured");
        }
    } // end submit 

    /**
     * @pre must have valid patient ID in database set up with proper name and birthday passed in as parameters
     * @param firstName - patient's first name
     * @param lastName - patient's last name
     * @param birthday - patient's birthday
     * @return -1 if DB can't find patient, 1 <= x if patient Id is found
     * @post retrieves and returns the valid patientID
     */
    public static int retrievePatientID(String firstName, String lastName, String birthday){
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;

        //Set patient ID to -1 encase of failure to retrieve patient ID
        int patientID = -1;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            //Search for patient within the database
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT patients.patientID FROM patients, users where users.first_name = '" + firstName +
                "' AND users.last_name = '" + lastName + "' AND users.birthday = '" + birthday + "' AND users.userID = patients.userID");

            //loop through result set
            while(resultSet.next()){
                patientID = resultSet.getInt("patients.patientID");
            }

            resultSet.close();

            return patientID;
        }
        catch(Exception ex){
            Alert warnAlert = new Alert(AlertType.ERROR);
            warnAlert.setTitle("Error");
            warnAlert.setHeaderText(null); 
            warnAlert.setContentText("An error has occured.");
            warnAlert.showAndWait();
            return patientID;
        }
    } // end retrievePatientID

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
     * @pre String values passed in, must not contain an empty field for any of the parameters passed in
     * @param firstName (String) - new user's first name
     * @param lastName (String) - new user's last name
     * @param birthday (LocalDate) - new user's birthday
     * @return True if there is an empty field (empty string) false if all fields have been filled
     * @post validates whether there is an empty field
     */
    public boolean hasEmptyField(String firstName, String lastName, LocalDate birthday){
        if(firstName.equals("") || lastName.equals("") || birthday == null){
            return true;
        }
        return false;
    } // end hasEmptyField
} // end ReceptionistView
