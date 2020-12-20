// imports 
// Java fx
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
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*; 
import javafx.animation.*;
import javafx.geometry.*;

// Tables
import javafx.collections.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.TableColumn.*;
import javafx.util.converter.*;
import javafx.scene.control.cell.CheckBoxListCell;

// Scroll pane 
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

// Utilities and text parsing
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// SQL
import java.sql.*;

// Exception handling
import java.lang.Exception;

// FXML
import javafx.fxml.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

// Exception
import java.io.IOException;

/**
 * Create account screen and functionality of EzHealth
 *
 * @Derrick Persaud & Enrique Gambra
 * @Sprint 1
 */
public class CreateAccount 
{
    // Setting up the stage
    private static Stage stage; 

    // Database connections and credential variables
    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();

    // Password limit 
    private static final int PW_CHARACTER_LIMIT = 18;

    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent create = FXMLLoader.load(getClass().getResource("fxml/createAccount.fxml"));

        // Create scene and load it onto the stage
        Scene createScene = new Scene(create);
        stage.setTitle("Create");       
        stage.setScene(createScene);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

    // Load FXML GUI objects 
    // Anchor Pane 
    @FXML
    private AnchorPane loginButton;

    // Labels
    @FXML
    private Label healthLabel;

    @FXML    
    private Label ezLabel;

    @FXML
    private Label username;

    @FXML
    private Label firstName;

    @FXML
    private Label lastName;

    @FXML
    private Label birthday;

    @FXML
    private Label pass;

    @FXML
    private Label confirmPass;

    // Fields
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passField;

    @FXML
    private PasswordField confirmPassField;

    // Date Picker 
    @FXML
    private DatePicker datePicker;

    // Buttons 
    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

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
            String usernameInput = "";
            String passInput = ""; 
            String confirmPassInput = "";

            // birthday
            if(hasEmptyField(usernameField.getText(), passField.getText(), confirmPassField.getText(),
                firstNameField.getText(), lastNameField.getText(), datePicker.getValue()))
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("No Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Please fill all fields.");
                warnAlert.showAndWait();
                return;
            }

            // Invalid username (already taken)
            if(!(checkUsernameAvailable(usernameField.getText())))
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Invalid Username");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Username is already taken.");
                warnAlert.showAndWait();
                return;
            }

            // Password too large 
            if(!(compliesToPasswordLength(passField.getText())))
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Password Length Exceeds Limit");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Password should not exceed 18 characters");
                warnAlert.showAndWait();
                return;
            }

            // Passwords don't match 
            if(!(doPasswordsMatch(passField.getText(),(confirmPassField.getText()))))
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Re-enter Password");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Passwords do not match!");
                warnAlert.showAndWait();
                return;
            }

            // Reaches here, everything should be safe 
            firstNameInput = firstNameField.getText();
            lastNameInput = lastNameField.getText();
            birthdayInput = datePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            usernameInput = usernameField.getText();
            passInput = passField.getText(); 
            confirmPassInput = confirmPassField.getText();

            // Create the account
            boolean accountCreated = insertIntoDB(usernameInput, passInput, firstNameInput, lastNameInput, birthdayInput);
            if(accountCreated){
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Success");
                confirmAlert.setHeaderText(null); 
                confirmAlert.setContentText("Account created successfully!");
                confirmAlert.showAndWait();
                return;
            }
            else{
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("Failed");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("An error has occured while creating the account. Please try again!");
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
     * @pre The parameter passed in must be a string, must not be a username already used 
     * @param username (String) new user's attempted username
     * @return True or false if the database already contains the username value
     * @post username is determine to be available (meaning no other entries in the DB with that username) or unavailable
     */
    public boolean checkUsernameAvailable(String username){
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;
        boolean usernameAvailable = true;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("select username from users where "
                + "username = '" + username +"';");
            while(resultSet.next()){
                usernameAvailable = false;
                System.out.println("Here");
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return false;
        }
        finally{
            try {
                resultSet.close();
                //statement.close();
                //connection.close();
            } 
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
                return false;
            }
        }

        return usernameAvailable;            
    } // end checkUsernameAvailable

    /**
     * @pre String values passed in, values passed must match
     * @param password (String) - password new user is attempting to use
     * @param reEnterPass (String) - password new user is attempting to use
     * @post Password fields determined to be valid/invalid based off of containing same exact characters
     * @return True if password and reEnterPass match, false if they don't
     */
    public boolean doPasswordsMatch(String password, String reEnterPass){
        return password.equals(reEnterPass);
    } // end doPasswordsMatch

    /**
     * @pre String values passed in, must not contain an empty field for any of the parameters passed in
     * @param username (String) - new user's username
     * @param password (String) - user's password
     * @param reEnterPass (String) - user's password (re-entered)
     * @param firstName (String) - new user's first name
     * @param lastName (String) - new user's last name
     * @param birthday (LocalDate) - new user's birthday
     * @post Correctly determines if there is an empty field in the create account screen
     * @return True if there is an empty field (empty string) false if all fields have been filled
     */
    public boolean hasEmptyField(String username, String password, String reEnterPass, String firstName, String lastName, LocalDate birthday){
        if(username.equals("") || password.equals("") || reEnterPass.equals("") || firstName.equals("") || lastName.equals("") || birthday == null){
            return true;
        }
        return false;
    } // end hasEmptyField

    /**
     * @pre String password passed in, 0 < password.length() <= 18
     * @param password (String) - password new user is attempting to use
     * @post Password fields determined to be valid/invalid based off of complying to password length constraints
     * @return True if password has valid length, false if password fails to meet length requirements
     */
    public boolean compliesToPasswordLength(String password){
        if(password.length() > PW_CHARACTER_LIMIT || password.length() == 0){
            return false;
        }
        return true;
    } // end compliesToPassword

    /**
     * @pre parameters passed in must apply to error handling checks prior to calling this method
     * @param username (String) - new user's username
     * @param password (String) - new user's password
     * @param firstName (String) - new user's first name
     * @param lastName (String) - new user's last name
     * @param birthday (String) - new user's birthday
     * @post User inserted into database or encountered error
     * @return returns true if user inserted into DB successfully, false if error occured
     **/
    public boolean insertIntoDB(String username, String password, String firstName, String lastName, String birthday){
        //Connection connection = null;
        //Statement statement = null;
        int patientId = nextRowPatient() + 1;
        int userId = nextRowUsers() + 1;
        boolean accountCreated = false;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();

            //Insert values into database for user table
            String insertQuery = "insert into users values (" + userId +
                ", '" + username + "', '" + password + "', 'p', '" + firstName
                + "', '" + lastName + "', '" + birthday + "');" ;
            MySQLConnectionProperties.getStatementObject().execute(insertQuery);

            insertQuery = "insert into patients values (" + patientId + ", " + userId + ");";
            MySQLConnectionProperties.getStatementObject().execute(insertQuery);

            insertQuery = "insert into patient_info (patientID) values (" + patientId + ");";
            MySQLConnectionProperties.getStatementObject().execute(insertQuery);
            accountCreated = true;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            accountCreated = false;
        }
        finally{
            try {
               // if(statement != null){
               //     statement.close();  
               // }

               // if(connection != null){
               //     connection.close();  
                //}
            } 
            catch (Exception ex) {
                System.out.println(ex.getMessage());
                return accountCreated;
            }
        }
        return accountCreated;
    } // end insertIntoDB

    public int nextRowUsers(){
        //Gets the row number for the next available row
        //Connection connection = null;
       // Statement statement = null;
        ResultSet resultSet = null;
        int numRows = -1;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT userID FROM users order by userID desc limit 1;");
            while(resultSet.next()){
                numRows= resultSet.getInt(1);
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        finally{
            try {
                resultSet.close();
                //statement.close();
                //connection.close();
            } 
            catch (SQLException ex) {
            }
        }

        return numRows;
    } // end nextRowUsers

    public int nextRowPatient(){
        //Gets the row number for the next available row
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;
        int numRows = -1;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT patientID FROM patients order by patientID desc limit 1;");
            while(resultSet.next()){
                numRows= resultSet.getInt(1);
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        finally{
            try {
                resultSet.close();
                //statement.close();
                //connection.close();
            } 
            catch (SQLException ex) {
            }
        }
        return numRows;
    } // end nextRowPatient
} // end createAccount
