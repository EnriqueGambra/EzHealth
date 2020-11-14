

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class ReceptionistAppointmentViewTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class ReceptionistAppointmentViewTest
{
    /**
     * Default constructor for test class ReceptionistAppointmentViewTest
     */
    public ReceptionistAppointmentViewTest()
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
    
    /**
     * Test of retrieveAppointmentInfo method, of class Appointment.
     * Retrieves the appointment info from the database and returns a hashmap
     * with all appointment info. If error will return an exception or false
     * signifying the date hasn't been created in the database
     */
    @Test
    public void testCreatedDateReturnsTrue() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        boolean result = instance.retrieveAppointmentInfo("11/15/2019").size() > 0;
        assertEquals(true, result);
        
    }
    
    /**
     * Test of retrieveAppointmentInfo method, of class Appointment.
     * Retrieves the appointment info from the database and will return a hashmap
     * of less than size 0 because this particular date hasn't yet been created
     * in the database
     * NOTE: if we actually run through the logic for the date 10/10/1900
     * and create appointments for this date this test will fail...
     */
    @Test
    public void testNotYetCreatedDateReturnsFalse() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        boolean result = instance.retrieveAppointmentInfo("10/10/1900").size() > 0;
        assertEquals(false, result);
        
    }
    
    /**
     * Test of retrieveAppointmentInfo method, of class Appointment.
     */
    @Test
    public void testImproperFormattedDateReturnsFalse() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        boolean result = instance.retrieveAppointmentInfo("12/21/20200").size() > 0;
        assertEquals(false, result);
        
    }
    
    /**
     * Test of retrieveAppointmentInfo method, of class Appointment.
     */
    @Test
    public void testProperFormattedDateReturnsTrue() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        boolean result = instance.retrieveAppointmentInfo("12/21/2020").size() > 0;
        assertEquals(false, result);
        
    }

    /**
     * Test of retrieveDoctorName method, of class Appointment.
     */
    @Test
    public void testValidDoctorIDReturnsName() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrieveDoctorName(1);
        assertEquals("Maldonado", result);
        
    }
    
    /**
     * Test of retrieveDoctorName method, of class Appointment.
     */
    @Test
    public void testInvalidDoctorIDReturnsNull() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrieveDoctorName(3);
        assertEquals(null, result);
        
    }

    /**
     * Test of retrievePatientFirstName method, of class Appointment.
     */
    @Test
    public void testValidPatientIDReturnsFirstName() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrievePatientFirstName(1);
        assertEquals("Enrique", result);

    }
    
    /**
     * Test of retrievePatientFirstName method, of class Appointment.
     */
    @Test
    public void testInvalidPatientIDFirstNameReturnsNull() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrievePatientFirstName(150);
        assertEquals(null, result);

    }

    /**
     * Test of retrievePatientLastName method, of class Appointment.
     */
    @Test
    public void testValidPatientIDReturnsLastName() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrievePatientLastName(1);
        assertEquals("Gambra", result);
    }

    /**
     * Test of retrievePatientLastName method, of class Appointment.
     */
    @Test
    public void testInvalidPatientIDLastNameReturnsNull() {
        ReceptionistAppointmentView instance = new ReceptionistAppointmentView();
        String result = instance.retrievePatientLastName(150);
        assertEquals(null, result);
    }
}
