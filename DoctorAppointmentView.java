import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class DoctorAppointmentView {
   
   private static JFrame frame;
   private static JLabel header;
   private static JTextArea txtTime, txtPatients;
   private static JButton btnOk;
   
   private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
   private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
   private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
   private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();
   
   private static String date;
   private static int doctorID;
   
   public DoctorAppointmentView(String date, int doctorID){       
       date = date;
       doctorID = doctorID;
   }
   
   public void run(){
       createJFrame();
       createJLabel();
       createJTextField();
       createJButton();
       loadData();
       
       btnOk.addActionListener((ActionEvent e) -> {
            frame.dispose();
        });
       
       frame.setVisible(true);
   }
   
   public void createJFrame(){
        //Creates the JFrame
        frame = new JFrame("Doctor's appointments");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 260);
        frame.setResizable(false);
    }
   
   public void createJLabel(){
       //Creates the JLabel
       header = new JLabel("Appointments for " + date);
       header.setPreferredSize(new Dimension(201, 40));
       frame.getContentPane().add(header);
   }
   
   public void createJTextField(){
       txtTime = new JTextArea();
       txtPatients = new JTextArea();
       txtTime.setEditable(false);
       txtPatients.setEditable(false);
       txtTime.setPreferredSize(new Dimension(100, 120));
       txtPatients.setPreferredSize(new Dimension(140, 120));
       frame.getContentPane().add(txtTime);
       frame.getContentPane().add(txtPatients);
   }
   
   public void createJButton(){
       btnOk = new JButton("Ok");
       btnOk.setPreferredSize(new Dimension(100, 40));
       frame.getContentPane().add(btnOk);
   }
   
   /**
    * @pre
    * @param date
    * @param doctorID
    * @return - an ArrayList containing strings of patients first names that have scheduled appointments, or null if exception occurs
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
    * @pre
    * @param date
    * @param doctorID
    * @return - an ArrayList containing strings of patients last names that have scheduled appointments, or null if exception occurs
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
    * @pre
    * @param date
    * @param doctorID
    * @return - an ArrayList containing strings of times scheduled for appointments, or null if exception occurs
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
   public void loadData(){
       
       ArrayList<String> patientFNArray = new ArrayList<>();
       ArrayList<String> patientLNArray = new ArrayList<>();
       ArrayList<String> timeSchArray = new ArrayList<>();
       
       try{
           patientFNArray = getPatientsFirstName(date, doctorID);

           if(patientFNArray != null && !patientFNArray.isEmpty()){
               
                patientLNArray = getPatientsLastName(date, doctorID);
                
                timeSchArray = getTimesScheduled(date, doctorID);
                
                for(int i = 0; i < timeSchArray.size(); i++){
                    txtTime.setText(txtTime.getText() + "\n" + timeSchArray.get(i));
                    txtPatients.setText(txtPatients.getText() + "\n" + patientFNArray.get(i) + 
                            " " + patientLNArray.get(i));
                }
           }
           else{
               frame.setVisible(false);
               frame.dispose();
               JOptionPane.showMessageDialog(null, "No appointments have been created for " + date + " yet!");
           }   
       }
       catch(Exception e){
           System.out.println(e.getMessage());
       }
   }
}
