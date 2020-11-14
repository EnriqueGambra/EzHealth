

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
public class DoctorAppointmentViewTest
{
    /**
     * Default constructor for test class DoctorAppointmentViewTest
     */
    public DoctorAppointmentViewTest()
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
        int result = DoctorAppointmentView.getPatientsFirstName("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }
    
    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateFirstNameReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView.getPatientsFirstName("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }
    
    // No appointments for a particular date returns a size of 0
    @Test
    public void testNoAppointmentsForDateLastNameReturnsSize0(){
        int expResult = 0;
        int result = DoctorAppointmentView.getPatientsLastName("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }
    
    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateLastNameReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView.getPatientsLastName("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }
    
    // No appointments for a particular date returns a size of 0
    @Test
    public void testNoAppointmentsForDateTimesScheduledReturnsSize0(){
        int expResult = 0;
        int result = DoctorAppointmentView.getTimesScheduled("01/01/2000", 1).size();
        assertEquals(expResult, result);
    }
    
    // Appointments for a particular date returns a size 1 <= x
    @Test
    public void testAppointmentsForDateTimesScheduledReturnsSizeGreaterThan0(){
        boolean expResult = true;
        boolean result = DoctorAppointmentView.getTimesScheduled("11/16/2019", 1).size() > 0;
        assertEquals(expResult, result);
    }
}
