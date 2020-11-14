import java.awt.event.ActionEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Owner
 */
public class CreateAccountTest {
    CreateAccount instance; 
    
    public CreateAccountTest() {
        instance = new CreateAccount();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        MySQLConnectionProperties.createConnection();
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        MySQLConnectionProperties.closeConnection();
    }

    /**
     * Test of checkUsernameAvailable method, of class CreateAccount.
     */
    @Test
    public void testCheckUsernameAvailable() {
        //Test that passes, test that fails...
        String usernameTaken = "egambra";
        String usernameNotTaken = "test999";
        
        System.out.println("Checking if username is available with username already taken on Create Account FAIL: ");
        assertEquals(false, instance.checkUsernameAvailable(usernameTaken));
        
        System.out.println("Checking if username is available with username not taken on Create Account SUCCESS: ");
        assertEquals(true, instance.checkUsernameAvailable(usernameNotTaken));
    }
    
    @Test
    public void testDoPasswordsMatch(){
        String passwordFail = "test";
        String reEnterPasswordFail = "testUser";
        
        String passwordB = "test";
        String reEnterPasswordB = "test";
        
        
        System.out.println("Checking if passwords match in Create Account FAIL: ");
        assertEquals(false, instance.doPasswordsMatch(passwordFail, reEnterPasswordFail));
        
        System.out.println("Checking if passwords match in create account screen pass SUCCESS: ");
        assertEquals(true, instance.doPasswordsMatch(passwordB, reEnterPasswordB));
    }
    
    // @Test
    // public void testIsValidDateFormat(){
        // System.out.println("Checking if birthday field is in valid format mm/dd/yyyy FAIL:");
        // String birthday = "1/22/19922222";
        // boolean expResult = false;
        // boolean result = instance.isValidDateFormat(birthday);
        // assertEquals(expResult, result);
        
        // System.out.println("Checking if birthday field is in valid format mm/dd/yyyy PASS: ");
        // birthday = "10/10/2010";
        // //instance = new CreateAccount();
        // expResult = true;
        // result = instance.isValidDateFormat(birthday);
        // assertEquals(expResult, result);
    // }
    
    /**
     * Test of compliesToPasswordLength method, of class CreateAccount
     */
    @Test
    public void testCompliesToPasswordLength() {
        //Checking if the password length exceeds the 18 character limit
        String passwordEmpty = "";
        String passwordLong = "dwodaojodawjofjofjojaojofjoajojdf";
        String passwordCorrect = "testing123";
        
        System.out.println("Checking if password field complies to password constraints (length of 0) FAIL: ");
        assertEquals(false, instance.compliesToPasswordLength(passwordEmpty));
        
        System.out.println("Checking if password field complies to password constraints (exceeds length of 18) FAIL: ");
        assertEquals(false, instance.compliesToPasswordLength(passwordLong));
        
        System.out.println("Checking if password field complies to password constrains (proper length) SUCCESS: ");
        assertEquals(true, instance.compliesToPasswordLength(passwordCorrect));
    }
 
    /**
     * Test of hasEmptyField method, of class CreateAccount
    */
    @Test
    public void testHasEmptyField(){        
        String filledUsername = "user";
        String filledPassword = "userPass";
        String filledReEnterPass = "userPass";
        String filledFirstName = "Test";
        String filledLastName = "Last";
        LocalDate filledBirthday = LocalDate.of(2020, 10, 8);
        
        String blankField = "";
        
        System.out.println("Leaving one field blank Create Account FAIL: ");
        assertEquals(true, instance.hasEmptyField(filledUsername, filledPassword, filledReEnterPass, blankField, filledLastName, filledBirthday));
        
        
        System.out.println("Filling in every field in Create Account SUCCESS: ");
        assertEquals(false, instance.hasEmptyField(filledUsername, filledPassword, filledReEnterPass, filledFirstName, filledLastName, filledBirthday));
    }
}
