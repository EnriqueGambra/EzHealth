import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//This class is the patient information view... a choice extension for the doctor...
public class PatientInformation {
    
    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();
    
    private static JFrame frame;
    private static JLabel lblFname, lblLname, lblMedHist, lblMedicine, lblId, lblInsurance, lblBirthday;
    private static JTextField txtFname, txtLname, txtMedHist, txtMedicine, txtId, txtInsurance, txtBirthday;
    private static JButton btnSubmit, btnBack;
    
    private static int patientID;
    
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
    
    // Database column names               
    public static final String FIRST_NAME_COLUMN = "users.first_name";
    public static final String LAST_NAME_COLUMN = "users.last_name";
    public static final String MEDICAL_HISTORY_COLUMN = "patient_info.medical_history";
    public static final String MEDICATION_COLUMN = "patient_info.medication";
    public static final String INSURANCE_COLUMN = "patient_info.insurance";
    public static final String BIRTHDAY_COLUMN = "users.birthday";
    
    public PatientInformation(int patientID){
        patientID = patientID;
    }
    
    public void run(){
        createJFrame();
        ArrayList<JLabel> labels = createJLabels();
        ArrayList<JTextField> textFields = createJTextFields();
        placeLabelAndText(labels, textFields);
        createButtons();
        getPatientInfo();
        
        btnBack.addActionListener((ActionEvent e) -> {
            buttonBack(e);
        });
        
        btnSubmit.addActionListener((ActionEvent e) -> {
            buttonSubmit(e);
        });
    }
    
    /**
     * @pre
     * @param id - patientID
     * @param query
     * @param dbColumnName - name of the column located in the database
     * @return null (indicating could not find result or error) or a String
     * @post
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
    
    /**
     * Displays patient info onto the GUI
     */
    public static void getPatientInfo(){

        int id = getPatientID();
        txtId.setText(Integer.toString(id));
           
        //Gets and sets the patient's first name
        txtFname.setText(retrieveResultsFromDatabase(id, PATIENT_FIRST_NAME_QUERY, FIRST_NAME_COLUMN));
           
        //Gets and sets the patient's last name
        txtLname.setText(retrieveResultsFromDatabase(id, PATIENT_LAST_NAME_QUERY, LAST_NAME_COLUMN));
           
        //Gets and sets the patient's medical information
        txtMedHist.setText(retrieveResultsFromDatabase(id, PATIENT_MEDICAL_HISTORY_QUERY, MEDICAL_HISTORY_COLUMN));
           
        //Gets and sets the patient's medication history
        txtMedicine.setText(retrieveResultsFromDatabase(id, PATIENT_MEDICATION_QUERY, MEDICATION_COLUMN));
              
        //Gets and sets the patient's insurance information
        txtInsurance.setText(retrieveResultsFromDatabase(id, PATIENT_INSURANCE_INFO_QUERY, INSURANCE_COLUMN));
           
        //Gets and sets the patient's birthday
        txtBirthday.setText(retrieveResultsFromDatabase(id, PATIENT_BIRTHDAY_QUERY, BIRTHDAY_COLUMN));
    }
    
    public static void buttonSubmit(ActionEvent e){
        String medHist = txtMedHist.getText();
        String medicine = txtMedicine.getText();
        String insurance = txtInsurance.getText();
        insertIntoDB(medHist, medicine, insurance);
        frame.dispose();
    }
    
    public static int getPatientID(){
        return patientID;
    }
    
    public static void insertIntoDB(String medHist, String medicine, String insurance){
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
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public static void buttonBack(ActionEvent e){
        frame.dispose();
    }
    
    public void placeLabelAndText(ArrayList<JLabel> labels, ArrayList<JTextField> textFields){
        for(int i = 0; i < labels.size(); i++){
            labels.get(i).setPreferredSize(new Dimension(100, 30));
            frame.add(labels.get(i));
            textFields.get(i).setPreferredSize(new Dimension(300, 40));
            frame.add(textFields.get(i));
        }
    }
    
    public void createJFrame(){
        //Creates the JFrame
        frame = new JFrame("Patient Record System");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setSize(500, 500);
        frame.setResizable(true);
        frame.setVisible(true);
    }
    
    public void createButtons(){
        //Creates the button
        btnSubmit = new JButton("Submit");
        btnBack = new JButton("Back");
        btnSubmit.setPreferredSize(new Dimension(100, 40));
        btnBack.setPreferredSize(new Dimension(100, 40));
        frame.add(btnSubmit);
        frame.add(btnBack);
    }
    
    public ArrayList<JLabel> createJLabels(){
        //Creates all labels and returns them in an arraylist
        lblId = new JLabel("ID #:");
        lblFname = new JLabel("First Name:");
        lblLname = new JLabel("Last Name:");
        lblMedicine = new JLabel("Medications:");
        lblMedHist = new JLabel("Medical History:");
        lblInsurance = new JLabel("Insurance:");
        lblBirthday = new JLabel("Birthday:");
        
        ArrayList<JLabel> labels = new ArrayList<>();
        labels.add(lblId);
        labels.add(lblFname);
        labels.add(lblLname);
        labels.add(lblBirthday);
        labels.add(lblMedHist);
        labels.add(lblMedicine);
        labels.add(lblInsurance);
        
        return labels;
    }   
    
    public ArrayList<JTextField> createJTextFields(){
        //Creates all textfields and returns them in an arraylist
        txtFname = new JTextField();
        txtLname = new JTextField();
        txtMedHist = new JTextField();
        txtMedicine = new JTextField(); 
        txtId = new JTextField();
        txtInsurance = new JTextField();
        txtBirthday = new JTextField();
        
        //Makes the textfields uneditable
        txtFname.setEditable(false);
        txtLname.setEditable(false);
        txtId.setEditable(false);
        txtBirthday.setEditable(false);
        
        ArrayList<JTextField> textFields = new ArrayList<>();
        textFields.add(txtId);
        textFields.add(txtFname);
        textFields.add(txtLname);
        textFields.add(txtBirthday);
        textFields.add(txtMedHist);
        textFields.add(txtMedicine);
        textFields.add(txtInsurance);

        return textFields;
    } 
}

