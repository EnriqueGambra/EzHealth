
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class DoctorAppointmentViewTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DoctorAppointmentViewTest2
{
    /**
     * Default constructor for test class DoctorAppointmentViewTest
     */
    public DoctorAppointmentViewTest2()
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

    // No appointments for a particular date returns a size of 0
    @Test
    public void testNoAppointmentsForDateFirstNameReturnsSize0(){
        int expResult = 0;
        int result = DoctorAppointmentView2.getPatientsFirstName("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }

    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateFirstNameReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView2.getPatientsFirstName("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }

    // No appointments for a particular date returns a size of 0
    @Test
    public void testNoAppointmentsForDateLastNameReturnsSize0(){
        int expResult = 0;
        int result = DoctorAppointmentView2.getPatientsLastName("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }

    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateLastNameReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView2.getPatientsLastName("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }

    // No appointments for a particular date returns a size of 0
    @Test
    public void testNoAppointmentsForDateTimesScheduledReturnsSize0(){
        int expResult = 0;
        int result = DoctorAppointmentView2.getTimesScheduled("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }

    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateTimesScheduledReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView2.getTimesScheduled("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }
    
    // Doctor username is not in database, returns -99
    @Test
    public void testInvalidDoctorUsernameReturnsNegative99(){
        int expResult = -99;
        int result = DoctorAppointmentView2.getDoctorID("blahblah");
        assertEquals(expResult, result);
    }
    
    // Doctor username is in database, returns a valid ID (1 <= x <= infinity)
    @Test
    public void testValidDoctorUsernameReturnsValidID(){
        int expResult = 1;
        int result = DoctorAppointmentView2.getDoctorID("DrJenn");
        assertEquals(expResult, result);
    }
}
