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
     * This method checks if a given company exists in db
     */
    public int existsCompany( String name ) {
        String query = "SELECT company_id FROM companies WHERE company_name = ?;";
        int id = 0;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setString( 1, name );

            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                id = result.getInt( "company_id" );
            }
            result.close();
            return id;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return id;
        }
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

        // Retrieve phone numbers to words map
        company.setPhoneNumbersToWords( getPhoneNumbers( companyId ) );

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
        String query = "SELECT phone_number FROM phone_numbers WHERE phone_number = ?;";
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

    /*
     * This method retrieves the map from phone numbers to words for a given company
     * id
     */
    public Map<String, List<Word>> getPhoneNumbers( int companyId ) {
        String query = "SELECT phone_numbers.phone_number, words.word_id, words.word, words.is_approved " +
                "FROM phone_numbers " +
                "INNER JOIN words ON phone_numbers.phone_number = words.phone_number " +
                "WHERE company_id = ?;";
        TreeMap<String, List<Word>> phoneToWords = new TreeMap<String, List<Word>>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, companyId );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                Word word = new Word();
                word.setId( result.getInt( "word_id" ) );
                word.setWord( result.getString( "word" ) );
                word.setIsApproved( result.getBoolean( "is_approved" ) );

                String phoneNumber = result.getString( "phone_number" );
                if ( phoneToWords.containsKey( phoneNumber ) ) {
                    phoneToWords.get( phoneNumber ).add( word );
                }
                else {
                    List<Word> words = new LinkedList<Word>();
                    words.add( word );
                    phoneToWords.put( phoneNumber, words );
                }
            }
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return phoneToWords;
        }
        return phoneToWords;
    }

    /*
     * This method stores the phone number to given company into db
     */
    public boolean storePhoneNumber( String phoneNumber, int companyId ) {
        String query = "INSERT INTO phone_numbers ( phone_number, company_id ) VALUES ( ?, ? );";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            int index = 1;
            stmt.setString( index++, phoneNumber );
            stmt.setInt( index++, companyId );

            stmt.executeUpdate();
            return true;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return false;
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
     * Not implemented
     * May not be needed
     */
    public List<Word> getWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    /*
     * This method stores words to given phone number into db
     */
    public List<Integer> storeWords( List<Word> words, String phoneNumber ) {
        String insertQuery = "INSERT INTO words ( word, is_approved, phone_number ) VALUES ( ?, ?, ? );";
        String updateQuery = "UPDATE words SET word = ?, is_approved = ?, phone_number = ? WHERE word_id = ?;";
        List<Integer> ids = new LinkedList<Integer>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = null;
            ResultSet keys = null;
            for ( Word word : words ) {
                stmt = conn.prepareStatement( word.getId() > 0 ? updateQuery : insertQuery );
                int index = 1;
                stmt.setString( index++, word.getWord() );
                stmt.setBoolean( index++, word.getIsApproved() );
                stmt.setString( index++, phoneNumber );
                stmt.setInt( index++, word.getId() );

                stmt.executeUpdate();
                keys = stmt.getGeneratedKeys();
                while ( keys.next() ) {
                    int id = keys.getInt( 1 );
                    word.setId( id );
                    ids.add( id );
                }
                stmt.clearParameters();
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
