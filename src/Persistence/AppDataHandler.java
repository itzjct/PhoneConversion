package Persistence;

import Application.Domain.*;
import Application.Services.ConfigService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class AppDataHandler {
    private String connectionString;

    public AppDataHandler() {
        ConfigService configService = new ConfigService();
        Properties properties = configService.getProperties();
        String base = properties.getProperty( ConfigService.DB_BASE );
        String path = properties.getProperty( ConfigService.DB_PATH );
        connectionString = base + path;
    }

    /*
     * This constructor initializes object with a given connection string
     * USED ONLY FOR TESTS
     */
    public AppDataHandler( String connectionString ) {
        this.connectionString = connectionString;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection( connectionString );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }
        return conn;
    }

    /*
     * This method retrieves user from db with given email
     */
    public User getUser( String email ) {
        String query = "SELECT user_id, first_name, last_name, email, password, password_salt, is_admin, company_id " +
                "FROM users WHERE email = ?;";

        User user = new User();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query );) {
            stmt.setString( 1, email );
            ResultSet result = stmt.executeQuery();
            int count = 0;
            while ( result.next() ) {
                count++;
                user.setId( result.getInt( "user_id" ) );
                user.setFirstName( result.getString( "first_name" ) );
                user.setLastName( result.getString( "last_name" ) );
                user.setEmail( result.getString( "email" ) );
                user.setPassword( result.getBytes( "password" ) );
                user.setPasswordSalt( result.getBytes( "password_salt" ) );
                user.setIsAdmin( result.getBoolean( "is_admin" ) );
                user.getCompany().setId( result.getInt( "company_id" ) );
            }
            result.close();

            // No user found
            if ( count == 0 ) {
                return null;
            }
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return null;
        }

        // Retrieve company object for given user
        user.setCompany( getCompany( user.getCompany().getId() ) );

        return user;
    }

    /*
     * This method stores given user into db
     */
    public int storeUser( User user ) {
        String query = "INSERT INTO users " +
                "( first_name, last_name, email, password, password_salt, is_admin, company_id ) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ? );";
        int id = 0;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            int index = 1;
            stmt.setString( index++, user.getFirstName() );
            stmt.setString( index++, user.getLastName() );
            stmt.setString( index++, user.getEmail() );
            stmt.setBytes( index++, user.getPassword() );
            stmt.setBytes( index++, user.getPasswordSalt() );
            stmt.setBoolean( index++, user.getIsAdmin() );
            stmt.setInt( index++, user.getCompany().getId() );

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            while ( keys.next() ) {
                id = keys.getInt( 1 );
            }
            stmt.close();
            return id;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
    }

    /*
     * This methods deletes given user from db
     */
    public int deleteUser( User user ) {
        String query = "DELETE FROM users WHERE email = ?;";
        int id = 0;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, user.getEmail() );

            stmt.executeUpdate();
            return user.getId();
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
    }

    /*
     * This method retrieves company from db with given name
     */
    public Company getCompany( String name ) {
        String query = "SELECT company_id, company_name FROM companies WHERE company_name = ?;";
        Company company = new Company();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, name );
            ResultSet result = stmt.executeQuery();
            int count = 0;
            while ( result.next() ) {
                count++;
                company.setId( result.getInt( "company_id" ) );
                company.setName( result.getString( "company_name" ) );
            }
            result.close();

            // No company found
            if ( count == 0 ) {
                return null;
            }
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return null;
        }

        // Retrieve phone numbers
        company.setPhoneNumbers( getPhoneNumbers( company.getId() ) );

        return company;
    }

    /*
     * This method retrieves company from db with given id
     */
    public Company getCompany( int companyId ) {
        String query = "SELECT company_id, company_name FROM companies WHERE company_id = ?;";
        Company company = new Company();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, companyId );
            ResultSet result = stmt.executeQuery();
            int count = 0;
            while ( result.next() ) {
                count++;
                company.setId( result.getInt( "company_id" ) );
                company.setName( result.getString( "company_name" ) );
            }
            result.close();

            // No company found
            if ( count == 0 ) {
                return null;
            }
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return null;
        }

        // Retrieve phone numbers
        company.setPhoneNumbers( getPhoneNumbers( companyId ) );

        return company;
    }

    /*
     * This method stores given company into db
     */
    public int storeCompany( Company company ) {
        String query = "INSERT INTO companies ( company_name ) VALUES ( ? );";
        int id = 0;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, company.getName() );

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            while ( keys.next() ) {
                id = keys.getInt( 1 );
            }
            keys.close();
            return id;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
    }

    /*
     * This method deletes given company from db
     */
    public int deleteCompany( Company company ) {
        String query = "DELETE FROM companies WHERE company_name = ?;";
        int id = 0;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, company.getName() );

            stmt.executeUpdate();
            return company.getId();
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
    }

    /*
     * This method checks if given phone number exists in db
     */
    public boolean existsPhoneNumber( String phoneNumber ) {
        String query = "SELECT phone_id FROM phone_numbers WHERE phone_number = ?;";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, phoneNumber );

            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                return true;
            }
            return false;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return false;
        }
    }

    public List<PhoneNumber> getPhoneNumbers( int companyId ) {
        String query = "SELECT phone_id, phone_number FROM phone_numbers WHERE company_id = ?;";
        List<PhoneNumber> phoneNumbers = new LinkedList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, companyId );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId( result.getInt( "phone_id" ) );
                phoneNumber.setPhoneNumber( result.getString( "phone_number" ) );
                phoneNumbers.add( phoneNumber );
            }
            result.close();

            // Retrieve words for each phone number
            for ( PhoneNumber phoneNumber : phoneNumbers ) {
                phoneNumber.setWords( getWords( phoneNumber.getId() ) );
            }
            return phoneNumbers;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return phoneNumbers;
        }
    }

    /*
     * This method retrieves all phone numbers that do not belong
     * to the specified company
     */
    public Set<String> getNonUsablePhoneNumbers( int companyId ) {
        String query = "SELECT phone_number FROM phone_numbers WHERE company_id IS NOT ?;";
        Set<String> phoneNumbers = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, companyId );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                phoneNumbers.add( result.getString( "phone_number" ) );
            }
            result.close();
            return phoneNumbers;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return phoneNumbers;
        }
    }

    /*
     * This method stores the phone number to given company into db
     */
    public int storePhoneNumber( PhoneNumber phoneNumber, int companyId ) {
        String insertQuery = "INSERT INTO phone_numbers ( phone_number, company_id ) VALUES ( ?, ? );";
        String updateQuery = "UPDATE phone_numbers SET phone_number = ?, company_id = ? WHERE phone_id = ?;";
        boolean isUpdate = phoneNumber.getId() > 0;
        int id = phoneNumber.getId();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( isUpdate ? updateQuery : insertQuery )) {
            int index = 1;
            stmt.setString( index++, phoneNumber.getPhoneNumber() );
            stmt.setInt( index++, companyId );
            if ( isUpdate ) {
                stmt.setInt( index++, phoneNumber.getId() );
            }
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            while ( keys.next() ) {
                id = keys.getInt( 1 );
            }
            stmt.close();
            return id;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
    }

    /*
     * This method deletes given phone number from db
     */
    public boolean deletePhoneNumber( String phoneNumber ) {
        String query = "DELETE FROM phone_numbers WHERE phone_number = ?;";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, phoneNumber );

            stmt.executeUpdate();
            return true;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return false;
        }
    }

    /*
     * This method retrieves a map of words for a given phone number
     */
    public Set<Word> getWords( int phoneId ) {
        String query = "SELECT word_id, word, start_index, end_index FROM words WHERE phone_id = ?;";
        Set<Word> words = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, phoneId );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                Word word = new Word();
                word.setId( result.getInt( "word_id" ) );
                word.setWord( result.getString( "word" ) );
                word.setStartIndex( result.getInt( "start_index" ) );
                word.setEndIndex( result.getInt( "end_index" ) );
                words.add( word );
            }
            result.close();
            return words;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return words;
        }
    }

    /*
     * This method stores words to given phone number into db
     */
    public List<Integer> storeWords( Map<Integer, List<Word>> words, int phoneId ) {
        String insertQuery = "INSERT INTO words ( word, start_index, end_index, phone_id ) VALUES ( ?, ?, ?, ? );";
        String updateQuery = "UPDATE words SET word = ?, start_index = ?, end_index = ?, phone_id = ? WHERE word_id = ?;";
        List<Integer> ids = new LinkedList<Integer>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = null;
            ResultSet keys = null;
            for ( int key : words.keySet() ) {
                for ( Word word : words.get( key ) ) {
                    boolean isUpdate = word.getId() > 0;
                    stmt = conn.prepareStatement( isUpdate ? updateQuery : insertQuery );
                    int index = 1;
                    stmt.setString( index++, word.getWord() );
                    stmt.setInt( index++, word.getStartIndex() );
                    stmt.setInt( index++, word.getEndIndex() );
                    stmt.setInt( index++, phoneId );
                    if ( isUpdate ) {
                        stmt.setInt( index++, word.getId() );
                    }

                    stmt.executeUpdate();
                    keys = stmt.getGeneratedKeys();
                    while ( keys.next() ) {
                        int id = keys.getInt( 1 );
                        word.setId( id );
                        ids.add( id );
                    }
                    stmt.clearParameters();
                }
            }
            return ids;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return ids;
        }
    }

    /*
     * This method deletes the given words from db
     */
    public boolean deleteWords( List<Word> words ) {
        String query = "DELETE FROM words WHERE word = ?;";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            for ( Word word : words ) {
                stmt.setString( 1, word.getWord() );
                stmt.executeUpdate();
                stmt.clearParameters();
            }
            if ( words == null || words.size() == 0 ) {
                return false;
            }
            return true;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return false;
        }
    }
}
