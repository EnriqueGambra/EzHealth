import java.awt.event.ActionEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Derrick Persaud, Enrique Gambra
 */
public class LoginTest {
    
    public LoginTest() {
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
     * Test of run method, of class Login.
     */

    /**
     * Test of getRole method, of class Login.
     * Tests if user has a role
     */
    @Test
    public void testGetRole() {
        //Expecting p as role back
        System.out.println("Get role pass test case");
        String username = "egambra";
        String password = "egambra";
        Login instance = new Login();
        String expResult = "p";
        String result = instance.getRole(username, password);
        assertEquals(expResult, result);
        
        //Expecting empty string for result back
        System.out.println("Get role failing test case");
        username = "dowjodj";
        password = "dwodkwaokd";
        expResult = "";
        result = instance.getRole(username, password);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of checkUserAndPassword method, of class Login.
     * Tests if user is in the database
     */
    @Test
    public void testCheckUserAndPassword() {
        System.out.println("Checking Username and password method test -- already created user");
        String username = "egambra";
        String password = "egambra";
        Login instance = new Login();
        boolean expResult = true;
        boolean result = instance.checkUserAndPassword(username, password);
        assertEquals(expResult, result);
        
        //Create a user then see if it says we passed -- automation
        System.out.println("User not created in database already");
        username = "testingtesting";
        password = "testingtesting";
        instance = new Login();
        expResult = false;
        result = instance.checkUserAndPassword(username, password);
        assertEquals(expResult, result);
    }
}
