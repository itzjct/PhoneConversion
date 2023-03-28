package Test.Unit;

import Application.Domain.*;
import Application.Services.ConfigService;
import Persistence.AppDataHandler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;

public class AppDataHandlerTests {
    private String connectionString = "jdbc:sqlite:C:/Users/Julian/Desktop/School/Software Engineering/Project/db/word_conversion.db";
    private AppDataHandler appDataHandler = new AppDataHandler( connectionString );

    private User getUserObject() {
        User user = new User();
        user.setFirstName( "TestFirstName" );
        user.setLastName( "TestLastName" );
        user.setEmail( "test@test.com" );
        user.setPassword( "test".getBytes() );
        user.setPasswordSalt( "test".getBytes() );
        user.setIsAdmin( true );
        return user;
    }

    private void storeUserObject() {
        User user = getUserObject();
        
    }

    private Company getCompanyObject() {
        Company company = new Company();
        company.setName( "TestCompany" );
        return company;
    }

    private int storeCompanyObject() {
        Company company = getCompanyObject();
        return appDataHandler.storeCompany( company );
    }

    private String getPhoneNumber() {
        return "0";
    }

    private String storePhoneNumber() {
        String phoneNumber = getPhoneNumber();
        appDataHandler.storePhoneNumber( phoneNumber, storeCompanyObject() );
        return phoneNumber;
    }

    private List<Word> getWordList() {
        List<Word> list = new LinkedList<Word>();
        list.add( new Word( "test_1", true ) );
        list.add( new Word( "test_2", false ) );
        list.add( new Word( "test_3", false ) );
        return list;
    }

    private List<Integer> storeWordList() {
        List<Word> words = getWordList();
        return appDataHandler.storeWords( words, storePhoneNumber() );
    }

    private void deleteTestUsersFromDB() {
        User user = getUserObject();
        appDataHandler.deleteUser( user );
    }

    private void deleteTestCompaniesFromDB() {
        Company company = getCompanyObject();
        appDataHandler.deleteCompany( company );
    }

    private void deleteTestPhoneFromDB() {
        String phoneNumber = getPhoneNumber();
        appDataHandler.deletePhoneNumber( phoneNumber );
    }

    private void deleteTestWordsFromDB() {
        List<Word> words = getWordList();
        appDataHandler.deleteWords( words );
    }

    @AfterEach
    public void clearDB() {
        deleteTestWordsFromDB();
        deleteTestPhoneFromDB();
        deleteTestUsersFromDB();
        deleteTestCompaniesFromDB();
    }

    @Test
    public void getUser_ValidEmail_ReturnsUser() {
        User expected = getUserObject();
        expected.setCompany( getCompanyObject() );
        expected.getCompany().setId( appDataHandler.storeCompany( expected.getCompany() ) );
        appDataHandler.storeUser( expected );

        User result = appDataHandler.getUser( expected.getEmail() );

        assertTrue( result.getId() > 0 );
        assertEquals( expected.getFirstName(), result.getFirstName() );
        assertEquals( expected.getLastName(), result.getLastName() );
        assertEquals( expected.getEmail(), result.getEmail() );
        assertEquals( expected.getPassword().length, result.getPassword().length );
        assertEquals( expected.getPasswordSalt().length, result.getPasswordSalt().length );
        assertEquals( expected.getIsAdmin(), result.getIsAdmin() );
        assertNotNull( result.getCompany() );
    }

    @Test
    public void getUser_EmailDNE_ReturnsNull() {
        User expected = getUserObject();
        expected.setEmail( "_" );

        User result = appDataHandler.getUser( expected.getEmail() );

        assertNull( result );
    }

    @Test
    public void storeUser_ValidUser_ReturnId() {
        User user = getUserObject();

        int id = appDataHandler.storeUser( user );

        assertTrue( id > 0 );
    }

    @Test
    public void deleteUser_ValidUser_ReturnsId() {
        User user = getUserObject();
        user.setId( appDataHandler.storeUser( user ) );

        int id = appDataHandler.deleteUser( user );

        assertTrue( id > 0 );
    }

    @Test
    public void existsCompany_ValidCompanyName_ReturnsTrue() {
        Company company = getCompanyObject();
        int id = appDataHandler.storeCompany( company );

        Company result = appDataHandler.getCompany( company.getName() );

        assertTrue( result.getId() > 0 );
        assertTrue( id > 0 );
    }

    @Test
    public void existsCompany_InvalidCompanyName_ReturnsFalse() {
        String companyName = "_";

        Company result = appDataHandler.getCompany( companyName );

        assertFalse( result.getId() > 0 );
    }

    @Test
    public void getCompany_ValidCompanyId_ReturnsCompany() {
        Company expected = getCompanyObject();
        int id = appDataHandler.storeCompany( expected );

        Company result = appDataHandler.getCompany( id );

        assertTrue( id > 0 );
        assertTrue( result.getId() > 0 );
        assertEquals( expected.getName(), result.getName() );
    }

    @Test
    public void storeCompany_ValidCompany_ReturnId() {
        Company company = getCompanyObject();

        int id = appDataHandler.storeCompany( company );

        assertTrue( id > 0 );
    }

    @Test
    public void deleteCompany_ValidCompany_ReturnId() {
        Company company = getCompanyObject();
        company.setId( appDataHandler.storeCompany( company ) );

        int id = appDataHandler.deleteCompany( company );

        assertTrue( id > 0 );
    }

    @Test
    public void existsPhoneNumber_ValidPhoneNumber_ReturnsTrue() {
        String phoneNumber = getPhoneNumber();
        boolean isStored = appDataHandler.storePhoneNumber( phoneNumber, 0 );

        boolean result = appDataHandler.existsPhoneNumber( phoneNumber );

        assertTrue( result );
        assertTrue( isStored );
    }

    @Test
    public void existsPhoneNumber_InvalidPhoneNumber_ReturnsFalse() {
        String phoneNumber = "_";

        boolean result = appDataHandler.existsPhoneNumber( phoneNumber );

        assertFalse( result );
    }

    @Test
    public void storePhoneNumber_ValidPhoneNumber_ReturnId() {
        String phoneNumber = getPhoneNumber();

        boolean isInserted = appDataHandler.storePhoneNumber( phoneNumber, 0 );

        assertTrue( isInserted );
    }

    @Test
    public void deletePhoneNumber_ValidPhoneNumber_ReturnTrue() {
        String phoneNumber = getPhoneNumber();
        boolean isInserted = appDataHandler.storePhoneNumber( phoneNumber, 0 );

        boolean isDeleted = appDataHandler.deletePhoneNumber( phoneNumber );

        assertTrue( isDeleted );
        assertTrue( isInserted );
    }

    @Test
    public void storeWords_ValidWordsAndValidPhoneNumber_ReturnIds() {
        String phoneNumber = getPhoneNumber();
        List<Word> words = getWordList();

        List<Integer> ids = appDataHandler.storeWords( words, phoneNumber );

        for ( int id : ids ) {
            assertTrue( id > 0 );
        }
    }

    @Test
    public void storeWords_UpdateValidWordsAndValidPhoneNumber_InfoIsUpdatedInDb() {
        String phoneNumber = getPhoneNumber();
        List<Word> words = getWordList();
        appDataHandler.storeWords( words, phoneNumber );
        for ( Word word : words ) {
            word.setIsApproved( true );
        }

        appDataHandler.storeWords( words, phoneNumber );
        List<Word> updatedWords = appDataHandler.getWords( phoneNumber );

        for ( Word word : updatedWords ) {
            assertTrue( word.getIsApproved() );
        }
    }

    @Test
    public void deleteWords_ValidWords_ReturnIds() {
        List<Word> words = getWordList();
        List<Integer> ids = appDataHandler.storeWords( words, getPhoneNumber() );

        boolean isDeleted = appDataHandler.deleteWords( words );

        assertTrue( isDeleted );
        for ( int id : ids ) {
            assertTrue( id > 0 );
        }
    }
}
