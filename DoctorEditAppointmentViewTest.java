

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;

/**
 * The test class DoctorEditAppointmentViewTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DoctorEditAppointmentViewTest
{
    /**
     * Default constructor for test class DoctorEditAppointmentViewTest
     */
    public DoctorEditAppointmentViewTest()
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
    
    // No appointment data entered should return an empty string for both the reason for appointment field and procedures done field
    @Test
    public void testNoAppointmentsDataReturnsEmptyString(){
        
        DoctorEditAppointmentView editAppointmentObject = new DoctorEditAppointmentView("11/17/2019", "10-11");
        
        HashMap<String, String> appointmentData = editAppointmentObject.retrieveAppointmentInfo();
        
        assertEquals(appointmentData.get("resultReasonForAppointment"), "");
        assertEquals(appointmentData.get("resultProceduresDone"), "");
    }
    
    // appointment data entered should return strings of length greater than 0 -- important note do not change the appointment info for
    // 11/16/2019 at 10-11 otherwise this test will fail
    @Test
    public void testAppointmentsDataReturnsString(){
        
        DoctorEditAppointmentView editAppointmentObject = new DoctorEditAppointmentView("11/16/2019", "10-11");
        
        HashMap<String, String> appointmentData = editAppointmentObject.retrieveAppointmentInfo();
        
        assertEquals(appointmentData.get("resultReasonForAppointment").length() > 0, true);
        assertEquals(appointmentData.get("resultProceduresDone").length() > 0, true);
    }
    
    @Test
    public void testInvalidCharacterLimit999ReturnsFalse(){

        try{
            boolean result = DoctorEditAppointmentView.invalidCharacterLimit(999);
            assertEquals(result, false);
        }
        catch(Exception ex) {
            fail("Threw an invalid exception");
        }
    }
    
    @Test
    public void testInvalidCharacterLimit1000ReturnsFalse(){

        try{
            boolean result = DoctorEditAppointmentView.invalidCharacterLimit(1000);
            assertEquals(result, false);
        }
        catch(Exception ex) {
            fail("Threw an invalid exception");
        }
    }
    
    @Test
    public void testInvalidCharacterLimit1001ThrowsException(){

        try{
            boolean result = DoctorEditAppointmentView.invalidCharacterLimit(1001);
            fail("Did not throw an exception that was expected");
        }
        catch(Exception ex) {
            assertTrue(true);
        }
    }
    
}
