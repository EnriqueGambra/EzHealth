/**
 * Derrick Persaud, Enrique Gambra
 **/
 import java.sql.*;
 
public class MySQLConnectionProperties {
   
   private static final String DB_URL = "jdbc:mysql://sql9.freemysqlhosting.net:3306/sql9367549?zeroDateTimeBehavior=convertToNull";
   private static final String DB_DRV = "com.mysql.jdbc.Driver";
   private static final String DB_USER = "sql9367549";
   private static final String DB_PASSWD = "SWGzWGWjnt";
   private static final int nullUserID = 5;
   private static Statement statement = null;
   private static Connection connection = null;
   
   public static void createConnection() {
       try{
           connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
           statement = connection.createStatement();
       }
       catch (Exception ex){
           System.out.println(ex.getMessage());
        }
       
    }
    
    public static void closeConnection() {
        
        try{
            if(statement != null){
                statement.close();
            }
        
            if(connection != null){
                connection.close();
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
   
   public static String getDBUrl(){
       return DB_URL;
   }
   
   public static String getDBDriver(){
       return DB_DRV;
   }
   
   public static String getDBUsername(){
       return DB_USER;
   }
   
   public static String getDBPassword(){
       return DB_PASSWD;
   }
   
   public static int getNullUserID(){
       return nullUserID;
   }
   
   public static Statement getStatementObject(){
       return statement;
    }
   
}
