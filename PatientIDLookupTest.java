/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
public class PatientIDLookupTest {
    
    public PatientIDLookupTest() {
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
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testInvalidPatientInfoReturnsNegative1() {
        int expResult = -1;
        int result = ReceptionistLookupView.retrievePatientID("Thomas", "Jefferson", "03/10/19988");
        assertEquals(expResult, result);
    } //end testInvalidPatientInfoReturnsNegative1
    
    /**
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testInvalidBirthdayFormatReturnsNegative1() {
        int expResult = -1;
        int result = ReceptionistLookupView.retrievePatientID("Enrique", "Gambra", "03/10/19988");
        assertEquals(expResult, result);
    } //end testInvalidBirthdayFormatReturnsNegative1
    
    /**
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testMissingBirthdayReturnsNegative1() {
        int expResult = -1;
        int result = ReceptionistLookupView.retrievePatientID("Enrique", "Gambra", "");
        assertEquals(expResult, result);
    } //end testMissingBirthdayReturnsNegative1
    
    /**
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testMissingLastNameReturnsNegative1() {
        int expResult = -1;
        int result = ReceptionistLookupView.retrievePatientID("Enrique", "", "03/10/1997");
        assertEquals(expResult, result);
    } //end testMissingLastNameReturnsNegative1
    
    /**
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testFirstNameReturnsNegative1() {
        int expResult = -1;
        int result = ReceptionistLookupView.retrievePatientID("", "Gambra", "03/10/1997");
        assertEquals(expResult, result);
    } //end testFirstNameReturnsNegative1

    /**
     * Test of retrievePatientID method, of class PatientIDLookup.
     */
    @Test
    public void testValidPatientInfoReturnsValidPatientID() {
        int expResult = 1;
        int result = ReceptionistLookupView.retrievePatientID("Enrique", "Gambra", "03/10/1997");
        assertEquals(expResult, result);
    } //end testValidPatientInfoReturnsValidPatientID

}
