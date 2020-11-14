import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

//Class that will create the Doctor view menu...
public class DoctorView {
    
   private static JFrame frame;
   private static Choice menuOption;
   private static JButton buttonMain;
   private static JLabel label;
    
   private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
   private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
   private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
   private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();
   
   private static String username;
    
    public DoctorView(String username){
        username = username;
    }
    
    public void run(){
        createJFrame();
        createLabel();
        createChoice();
        createButton();
        frame.setVisible(true);
        
        //Adds actionListener to the main button
        buttonMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonEvent(e);
            }
        });
    }
    
    public void createJFrame(){
        //Creates the JFrame
        frame = new JFrame("Patient Record System");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
    }
    
    public void createChoice(){
        //Creates the choice
        menuOption = new Choice();
        menuOption.add("View appointments");
        menuOption.add("Update an existing patient's information");
        menuOption.setSize(label.getX(), label.getY() - 20);
        frame.getContentPane().add(menuOption);
    }
    
    public void createButton(){
        //Creates the button
        buttonMain = new JButton("Submit");
        buttonMain.setPreferredSize(new Dimension(100, 40));
        frame.getContentPane().add(buttonMain);
    }
    
    public void createLabel(){
        //Creates the label
        label = new JLabel("Selet a menu option");
        frame.getContentPane().add(label);
    }
    
    public static void buttonEvent(ActionEvent e){
        String optionSelected = menuOption.getSelectedItem();
        
        if(optionSelected.equals(menuOption.getItem(0))){ 
            //Option to view appointmnets.... TODODODODODDO
            //int doctorID = Integer.parseInt(JOptionPane.showInputDialog("Enter your doctor ID:"));
            int doctorID = getDoctorID(username);
            // We should never hit the point where its a bad doctorID since we are logged in already,
            // but in case we do we can put an error dialog here saying an error has occured...
            String date = JOptionPane.showInputDialog("Enter in the date for the appointments you'd like to view:");
            
            DoctorAppointmentView view = new DoctorAppointmentView(date, doctorID);
            view.run();
        }
        else if(optionSelected.equals(menuOption.getItem(1))){ //Update a patient's information
            String patientId = JOptionPane.showInputDialog("Enter in the Patient's ID: ");
            
            boolean patientFound = findPatient(patientId); //Looks to see if the patient's id is within the database
            
            if(patientFound){ //Patient found
                PatientInformation newPatient = new PatientInformation(Integer.parseInt(patientId));
                newPatient.run();
            }
            else{  //Patient not found
                JOptionPane.showMessageDialog(null, "Patient not found!");
            }
        }
    }
    
    /**
     * @pre
     * @param username
     * @return -99 = invalid doctor ID or (1 <= x <= infinity) signifying valid doctor ID
     * @post
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
     * @pre
     * @param patientID
     * @return true/false depending on if database could find the patientID in query
     * @post
     */
    public static boolean findPatient(String patientId){
            //Connection connection = null;
            //Statement statement = null;
            ResultSet resultSet = null;
            boolean foundPatient = false;
            
            try{
               //connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
               //statement=connection.createStatement();
               resultSet=MySQLConnectionProperties.getStatementObject().executeQuery("SELECT patientID FROM patients where patientID = " + Integer.parseInt(patientId) + ";");
               while(resultSet.next()){
                  int receivedPatientID = resultSet.getInt("patientID");
                  if(receivedPatientID == Integer.parseInt(patientId)){
                      foundPatient = true; //Patient found
                      break;
                  }
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
            
            
            if(foundPatient){
                return true;
            }
            
            return false;
    }
}