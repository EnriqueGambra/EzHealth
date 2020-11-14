/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 **/

import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Owner
 */
public class PatientAppointmentViewTest {

    public PatientAppointmentViewTest() {
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
     * Test of retrieveName method, of class PatientAppointmentView.
     */
    @Test
    public void testFirstNameDoesNotExistReturnEmptyString() {
        String expResult = "";
        String result = PatientAppointmentView.retrieveName("first_name", "blankUser");
        assertEquals(expResult, result);
    } // end testFirstNameDoesNotExistReturnEmptyString

    /**
     * Test of retrieveName method, of class PatientAppointmentView.
     */
    @Test
    public void testFirstNameDoesExistReturnName() {
        String expResult = "Enrique";
        String result = PatientAppointmentView.retrieveName("first_name", "egambra");
        assertEquals(expResult, result);
    } // end testFirstNameDoesExistReturnName

    /**
     * Test of retrieveName method, of class PatientAppointmentView.
     */
    @Test
    public void testLastNameDoesNotExistReturnEmptyString() {
        String expResult = "";
        String result = PatientAppointmentView.retrieveName("last_name", "blankUser");
        assertEquals(expResult, result);
    } // end testLastNameDoesNotExistReturnEmptyString

    /**
     * Test of retrieveName method, of class PatientAppointmentView.
     */
    @Test
    public void testLastNameDoesExistReturnName() {
        String expResult = "Gambra";
        String result = PatientAppointmentView.retrieveName("last_name", "egambra");
        assertEquals(expResult, result);
    } // end testLastNameDoesExistReturnName

    /**
     * Test of retrieveID method, of class PatientAppointmentView.
     */
    @Test
    public void testInvalidNameReturnNegative999() {
        int expResult = -999;
        int result = PatientAppointmentView.retrieveID("Enrique", "Fake");
        assertEquals(expResult, result);
    } // end testInvalidNameReturnNegative999

    /**
     * Test of retrieveID method, of class PatientAppointmentView.
     */
    @Test
    public void testValidNameReturnsPositiveID() {
        int expResult = 1;
        int result = PatientAppointmentView.retrieveID("Enrique", "Gambra");
        assertEquals(expResult, result);
    } // end testValidNameReturnsPositiveID()

    /**
     * Test of retrieveAppointments method, of class PatientAppointmentView.
     */
    @Test
    public void testInvalidPatientIDReturnsNull() {     
        HashMap<String, List<String>> expResult = null;
        HashMap<String, List<String>> result = PatientAppointmentView.retrieveAppointments(99);
        assertEquals(expResult, result);        
    } // end testInvalidPatientIDReturnsNull

    /**
     * Test of retrieveAppointments method, of class PatientAppointmentView.
     */
    @Test
    public void testValidPatientIDReturnsMap() {     
        boolean expResult = true;
        boolean result = PatientAppointmentView.retrieveAppointments(1).size() > 0;
        assertEquals(expResult, result);      
    } // end testValidPatientIDReturnsMap  
    
    /**
     * Test of retrieveAppointments method, of class PatientAppointmentView.
     */
    @Test
    public void testValidPatientIDWithNoAppointmentsCreatedReturnsNull() {     
        HashMap<String, List<String>> expResult = null;
        HashMap<String, List<String>> result = PatientAppointmentView.retrieveAppointments(32);
        assertEquals(expResult, result);        
    } // end testInvalidPatientIDReturnsNull
} // end PatientAppointmentViewTest
