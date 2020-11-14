

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class DoctorViewTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DoctorViewTest
{
    /**
     * Default constructor for test class DoctorViewTest
     */
    public DoctorViewTest()
    {
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
    
    // Doctor username is not in database, returns -99
    @Test
    public void testInvalidDoctorUsernameReturnsNegative99(){
        int expResult = -99;
        int result = DoctorView.getDoctorID("blahblah");
        assertEquals(expResult, result);
    }
    
    // Doctor username is in database, returns a valid ID (1 <= x <= infinity)
    @Test
    public void testValidDoctorUsernameReturnsValidID(){
        int expResult = 1;
        int result = DoctorView.getDoctorID("DrJenn");
        assertEquals(expResult, result);
    }
    
    // Patient ID is not in database, returns false
    @Test
    public void testInvalidPatientIDReturnsFalse(){
        boolean expResult = false;
        boolean result = DoctorView.findPatient("-99");
        assertEquals(expResult, result);
    }
    
    // Patient ID is in database, returns true
    @Test
    public void testValidPatientIDReturnsTrue(){
        boolean expResult = true;
        boolean result = DoctorView.findPatient("1");
        assertEquals(expResult, result);
    }
}
