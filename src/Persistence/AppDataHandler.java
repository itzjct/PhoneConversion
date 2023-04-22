package Persistence;

import Application.Domain.*;
import Application.Services.ConfigService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * AppDataHandler is the class responsible for interacting with
 * the selected database.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class AppDataHandler {

    // String used to connect to database
    private String connectionString;

    /**
     * This constructor handles initialization of needed
     * application-related logic.
     * 
     * @param configService A ConfigService object.
     */
    public AppDataHandler( ConfigService configService ) {
        Properties properties = configService.getProperties();
        String base = properties.getProperty( ConfigService.DB_BASE );
        String path = properties.getProperty( ConfigService.DB_PATH );
        connectionString = base + path;
    }

    /**
     * This constructor handles initialization of needed
     * application-related logic.
     * ** FOR TESTS ONLY **
     * 
     * @param connectionString A String used to connected to database.
     */
    public AppDataHandler( String connectionString ) {
        this.connectionString = connectionString;
    }

    /**
     * This method is used to retrieve a new database
     * connection object.
     * 
     * @return A Connection object.
     */
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

    /**
     * This method retrieves user from database with given email.
     * 
     * @param email A String representing an email address.
     * @return A User object if exists in database.
     *         Null otherwise.
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

    /**
     * This method stores given user into database.
     * 
     * @param user A User object to be stored.
     * @return An int representing the id of the stored User.
     *         If it's zero then store operation failed.
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

    /**
     * This methods deletes given user from database.
     * 
     * @param user A User object to be deleted.
     * @return An int representing id of the deleted user.
     *         If it's zero then store operation failed.
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

    /**
     * This method retrieves company from database
     * with given name.
     * 
     * @param name A String representing a company name.
     * @return A Company object if exists in database.
     *         Null otherwise.
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
        company.setPhoneNumbers( getPhoneNumbers( company ) );

        return company;
    }

    /**
     * This method retrieves company from database
     * with given id.
     * 
     * @param companyId An int representing a company's database id.
     * @return A Company object if exists in database.
     *         Null otherwise.
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
        company.setPhoneNumbers( getPhoneNumbers( company ) );

        return company;
    }

    /**
     * This method stores given company into database.
     * 
     * @param company A Company to be stored.
     * @return An int representing id of stored company.
     *         If it's zero then store operation failed.
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

    /**
     * This method deletes given company from database.
     * 
     * @param company A Company object to be deleted.
     * @return An int representing id of the company deleted.
     *         If it's zero then operation delete failed.
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

    /**
     * This method checks if given phone number exists in database.
     * 
     * @param phoneNumber A String representing a phone number.
     * @return True if phone number exists in database.
     *         False otherwise.
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

    /**
     * This method retrieves the phone numbers from database
     * for a given company.
     * 
     * @param company A Company object.
     * @return A Set of PhoneNumber.
     */
    public Set<PhoneNumber> getPhoneNumbers( Company company ) {
        String query = "SELECT phone_id, phone_number, is_approved FROM phone_numbers WHERE company_id = ?;";
        Set<PhoneNumber> phoneNumbers = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, company.getId() );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId( result.getInt( "phone_id" ) );
                phoneNumber.setPhoneNumber( result.getString( "phone_number" ) );
                phoneNumber.setIsApproved( result.getBoolean( "is_approved" ) );
                phoneNumbers.add( phoneNumber );
            }
            result.close();

            // Retrieve words for each phone number
            for ( PhoneNumber phoneNumber : phoneNumbers ) {
                phoneNumber.setPhrases( getPhrases( phoneNumber ) );
            }
            return phoneNumbers;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return phoneNumbers;
        }
    }

    /**
     * This method retrieves all phone numbers from database
     * that do not belong to the specified company.
     * 
     * @param company A Company object.
     * @return A Set of String.
     */
    public Set<String> getNonUsablePhoneNumbers( Company company ) {
        String query = "SELECT phone_number FROM phone_numbers WHERE company_id IS NOT ?;";
        Set<String> phoneNumbers = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, company.getId() );
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

    /**
     * This method stores the phone number to given
     * company into database.
     * 
     * @param phoneNumber A PhoneNumber object to be stored.
     * @param company     A Company object.
     * @return An int representing id of stored phone number.
     */
    public int storePhoneNumber( PhoneNumber phoneNumber, Company company ) {
        String insertQuery = "INSERT INTO phone_numbers ( phone_number, is_approved, company_id ) VALUES ( ?, ?, ? );";
        String updateQuery = "UPDATE phone_numbers SET phone_number = ?, is_approved = ?, company_id = ? WHERE phone_id = ?;";
        boolean isUpdate = phoneNumber.getId() > 0;
        int id = phoneNumber.getId();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( isUpdate ? updateQuery : insertQuery )) {
            int index = 1;
            stmt.setString( index++, phoneNumber.getPhoneNumber() );
            stmt.setBoolean( index++, phoneNumber.getIsApproved() );
            stmt.setInt( index++, company.getId() );
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

    /**
     * This method deletes given phone number from database.
     * 
     * @param phoneNumber A String representing a phone number.
     * @return True if delete operation succeed.
     *         False otherwise.
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

    /**
     * This method retrives a set of phrases from database
     * for a given phone number.
     * 
     * @param phoneNumber A PhoneNumber object.
     * @return A Set of String.
     */
    public Set<String> getPhrases( PhoneNumber phoneNumber ) {
        String query = "SELECT id, phrase FROM phrases WHERE phone_id = ?;";
        Set<String> phrases = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement( query )) {
            stmt.setInt( 1, phoneNumber.getId() );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                phrases.add( result.getString( "phrase" ) );
            }
            result.close();
            return phrases;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return phrases;
        }
    }

    /**
     * This method stores a set of phrases into database
     * under a given phone number.
     * 
     * @param phrases     An Iterable of phrases to be stored.
     * @param phoneNumber A PhoneNumber object.
     * @return A List of Integer representing the ids of stored phrases.
     */
    public List<Integer> storePhrases( Iterable<String> phrases, PhoneNumber phoneNumber ) {
        String query = "INSERT INTO phrases ( phrase, phone_id ) VALUES ( ?, ? );";
        List<Integer> ids = new LinkedList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = null;
            ResultSet keys = null;
            for ( String phrase : phrases ) {
                stmt = conn.prepareStatement( query );
                int index = 1;
                stmt.setString( index++, phrase );
                stmt.setInt( index, phoneNumber.getId() );
                stmt.executeUpdate();

                // Get ids
                keys = stmt.getGeneratedKeys();
                while ( keys.next() ) {
                    ids.add( keys.getInt( 1 ) );
                }
                stmt.clearParameters();
            }
            stmt.close();
            keys.close();
            return ids;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return ids;
        }
    }

    /**
     * This method deletes a list of phrases from database.
     * 
     * @param phrases An Iterable of phrases to be deleted.
     * @return True if the delete operation succeeded.
     *         False otherwise.
     */
    public boolean deletePhrases( Iterable<String> phrases ) {
        String query = "DELETE FROM phrases WHERE phrase = ?;";
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = null;
            for ( String phrase : phrases ) {
                stmt = conn.prepareStatement( query );
                stmt.setString( 1, phrase );
                stmt.executeUpdate();
                stmt.clearParameters();
            }
            stmt.close();
            return true;
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return false;
        }
    }
}
