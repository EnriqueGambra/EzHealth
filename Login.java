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
//import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*; 
import javafx.event.ActionEvent;

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

// Exception
import java.io.IOException;

/**
 * Login screen and functionality of EzHealth
 *
 * @Derrick Persaud & Enrique Gambra
 * @Sprint 1
 */
public class Login extends Application 
{
    // Setting up the stage
    private static Stage stage;

    // Database connections and credential variables
    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword(); 

    private static String username;
    private static String password; 

    @Override
    public void start(Stage stage) throws Exception
    {
        // Load fxml
        Parent root = FXMLLoader.load(getClass().getResource("fxml/login.fxml"));
        MySQLConnectionProperties.createConnection();
        // Create scene and load it onto the stage
        Scene login = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(login);
        stage.getIcons().add( new Image("images/health.png"));
        stage.show();
    } // end start

    // Load FXML GUI objects
    // Anchor Pane
    @FXML
    private AnchorPane pane;

    // // Labels 
    @FXML
    private Label healthLabel;

    @FXML
    private Label ezLabel;

    // Fields
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // // Images
    @FXML
    private ImageView userImage;

    @FXML
    private ImageView passImage;

    // Text
    @FXML
    private Text newText;

    /**
     * signIn
     * Button method for loginButton
     * Tests user input for signing in
     */    
    @FXML
    public void signIn(ActionEvent event) {
        try
        {        
            // Name
            // Blank input  
            if(usernameField.getText().length() == 0)
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("No Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Please enter a username.");
                warnAlert.showAndWait();
                return;
            }  

            // Password
            // Blank input
            if(passwordField.getText().length() == 0)
            {
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("No Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Please enter your password");
                warnAlert.showAndWait();
                return;
            }                                      

            // Reaches this far then safe to store the new user information to check if they exist
            username = usernameField.getText();
            password = passwordField.getText(); 
            boolean isPastUser = checkUserAndPassword(username, password);

            if(isPastUser){
                //Send the user to menu page that they should have access to... ex
                //doctor goes to doctor view page, receptionist goes to receptionist page... etc based on their roles
                String role = getRole(username, password);

                if(role.equals("p")){
                    // Create and set the patient
                    PatientAppointmentView newPatient = new PatientAppointmentView();

                    // Get the current stage
                    Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

                    // Call the start method
                    newPatient.start(window);
                }
                else if(role.equals("d")){
                    System.out.println("The login was successful as a doctor");
                    // **** Note to Derrick *** you're going to need to pass in the doctor username
                    // as a parameter for creating a new DoctorView object
                    // Idea for future sprint:
                    // DoctorView doctor = new DoctorView();
                    // doctor.run();
                }
                else if(role.equals("r")){
                    // Create and set the receptionist
                    ReceptionistAppointmentView newReceptionist = new ReceptionistAppointmentView();

                    // Get the current stage
                    Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

                    // Call the start method
                    newReceptionist.start(window);
                }
            }
            else{
                Alert warnAlert = new Alert(AlertType.ERROR);
                warnAlert.setTitle("No Entry Error");
                warnAlert.setHeaderText(null); 
                warnAlert.setContentText("Username or password is incorrect");
                warnAlert.showAndWait();
                return;
            }
        }
        catch (Exception error) // very unlikely to occur but safe to catch 
        {
            System.out.println("");
        }
    }// end SignIn

    /**
     * create
     * Button method for createTextFlow
     * Takes user to the create screens
     */
    @FXML
    public void create(ActionEvent event) throws Exception
    {                              
        // Get the current stage
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        // Create a create account instance and call the start method
        CreateAccount newCreate = new CreateAccount();
        newCreate.start(window);
    } // end create

    /**
     * @pre The parameters passed in must be Strings and must be a valid username
    and password combo already in the database
     * @param username
     * @param password 
     * @post Database has checked the user's role
     * @return "p" if the user is a patient, "d" if doctor, "r" if receptionist,
    "" if no role
     **/
    public String getRole(String username, String password){
        //Returns the user's role
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;
        String role = "";

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT * FROM users where username = '" + username 
                + "' and password = '" + password + "';");
            while(resultSet.next()){
                role = resultSet.getString("user_role");
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        finally{
            try {
                resultSet.close();
                //MySQLConnectionProperties.getStatementObject().close();
                //connection.close();
            } 
            catch (SQLException ex) {
            }
        }
        return role;
    } // end getRole

    /**
     * @pre The parameters passed in must be Strings
     * @param username
     * @param password 
     * @post Database has checked whether the user is in the database
     * @return True or False depending on if user is in the database
     **/
    public boolean checkUserAndPassword(String username, String password){
        //Checks to see if the user is within the database by accessing the database
        //Connection connection = null;
        //Statement statement = null;
        ResultSet resultSet = null;
        boolean foundUser = false;

        try{
            //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
            //statement=connection.createStatement();
            resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT * FROM users where username = '" + username 
                + "' and password = '" + password + "';");
            while(resultSet.next()){
                foundUser = true;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        finally{
            try {
                resultSet.close();
                //MySQLConnectionProperties.getStatementObject().close();
                //connection.close();
            } 
            catch (SQLException ex) {
            }
        }
        return foundUser;
    } // end checkUserAndPassword

    /**
     * getUsername
     */
    public static String getUsername()
    {
        return username; 
    }
} // end Login 
