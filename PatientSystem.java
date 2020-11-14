import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;


public class PatientSystem {

    private static final String DB_URL = MySQLConnectionProperties.getDBUrl();
    private static final String DB_DRV = MySQLConnectionProperties.getDBDriver();
    private static final String DB_USER = MySQLConnectionProperties.getDBUsername();
    private static final String DB_PASSWD = MySQLConnectionProperties.getDBPassword();
    
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    
    public static void main(String[] args) {
        updateTables();
        Login start = new Login();
        start.run();
    }
    
    public static void updateTables(){
        try{
           connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
           statement=connection.createStatement();
           statement.execute("UPDATE appointment_receptionist SET view_loaded = 0 WHERE view_id = 1;");
        }
        catch(Exception ex){
            
        }
        finally{
            try{
                connection.close();
                statement.close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    
}
