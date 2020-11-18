
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class PatientInformationTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DoctorUpdatePatientViewTest
{
    /**
     * Default constructor for test class PatientInformationTest
     */
    public DoctorUpdatePatientViewTest()
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

    // Valid ID for patientID 1 returns Enrique
    @Test
    public void testValidIDReturnsPatientFirstName(){
        String expResult = "Enrique";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_FIRST_NAME_QUERY,
                DoctorUpdatePatientView.FIRST_NAME_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 1 returns Gambra
    @Test
    public void testValidIDReturnsPatientLastName(){
        String expResult = "Gambra";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_LAST_NAME_QUERY,
                DoctorUpdatePatientView.LAST_NAME_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 1 returns 03/10/1997
    @Test
    public void testValidIDReturnsPatientBirthday(){
        String expResult = "03/10/1997";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_BIRTHDAY_QUERY,
                DoctorUpdatePatientView.BIRTHDAY_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 32 returns null
    @Test
    public void testValidIDWithBlankMedicalHistoryReturnsNull(){
        String expResult = null;
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(32, DoctorUpdatePatientView.PATIENT_MEDICAL_HISTORY_QUERY,
                DoctorUpdatePatientView.MEDICAL_HISTORY_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 1 returns Heart Murmur
    @Test
    public void testValidIDWithFilledInMedicalHistoryReturnsHearMurmur(){
        String expResult = "Heart Murmur";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_MEDICAL_HISTORY_QUERY,
                DoctorUpdatePatientView.MEDICAL_HISTORY_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 32 returns null
    @Test
    public void testValidIDWithBlankMedicationReturnsNull(){
        String expResult = null;
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(32, DoctorUpdatePatientView.PATIENT_MEDICATION_QUERY,
                DoctorUpdatePatientView.MEDICATION_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 1 returns Amoixcillin
    @Test
    public void testValidIDWithFilledInMedicationReturns(){
        String expResult = "Amoixcillin";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_MEDICATION_QUERY,
                DoctorUpdatePatientView.MEDICATION_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 32 returns null
    @Test
    public void testValidIDWithBlankInsuranceReturnsNull(){
        String expResult = null;
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(32, DoctorUpdatePatientView.PATIENT_INSURANCE_INFO_QUERY,
                DoctorUpdatePatientView.INSURANCE_COLUMN);
        assertEquals(expResult, result);
    }

    // Valid ID for patientID 1 returns VSP
    @Test
    public void testValidIDWithFilledInInsuranceReturnsVSP(){
        String expResult = "VSP";
        String result = DoctorUpdatePatientView.retrieveResultsFromDatabase(1, DoctorUpdatePatientView.PATIENT_INSURANCE_INFO_QUERY,
                DoctorUpdatePatientView.INSURANCE_COLUMN);
        assertEquals(expResult, result);
    }
}
