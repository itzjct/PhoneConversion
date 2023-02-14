package Application.Services;

import Application.Domain.*;
import Persistence.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class AppService {
    public final int SALT_LEN = 32;
    public final String HASH_ALGORITHM = "SHA-512";
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;
    private SecureRandom rng;
    private AppDataHandler dataHandler;
    private Pattern emailPattern;

    public AppService() {

        // Initialize data handler
        dataHandler = new AppDataHandler();

        // Create random number generator
        // Used to generate password salts
        rng = new SecureRandom();

        // Regex and pattern to check validity of email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        emailPattern = Pattern.compile( emailRegex );
    }

    /*
     * Not implemented
     */
    public boolean validatePhoneNumber( String phoneNumber ) {
        return true;
    }

    /*
     * Not implemented
     */
    public List<Word> generateWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    /*
     * This method authenticates user
     */
    public List<String> authenticateUser( User user ) {
        List<String> errors = new LinkedList<String>();

        // Retrieve requested user from db
        User userFromDb = new User();
        try {
            userFromDb = dataHandler.getUser( user.getEmail() );
        }
        catch ( Exception ex ) {
            errors.add( ex.getMessage() );
            return errors;
        }

        // Given email does not correspond to user in db
        if ( userFromDb == null ) {
            errors.add( "Email not found" );
            return errors;
        }

        // Hash password and validate
        byte[] hashedPassword = hashPassword( user.getPasswordString(), userFromDb.getPasswordSalt() );
        if ( !Arrays.equals( hashedPassword, userFromDb.getPassword() ) ) {
            errors.add( "Invalid password" );
            return errors;
        }

        // User authenticated, retrieve all data
        User.copy( user, userFromDb );

        return errors;
    }

    /*
     * This method validates login user data
     */
    public List<String> validateLogin( User user ) {
        List<String> errors = new LinkedList<String>();

        if ( user.getEmail() == null || user.getEmail().equals( "" ) ) {
            errors.add( "Email cannot be empty" );
        }
        if ( user.getPasswordString() == null || user.getPasswordString().equals( "" ) ) {
            errors.add( "Password cannot be empty" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() < PW_CHAR_MIN ) {
            errors.add( "Invalid password" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() > PW_CHAR_MAX ) {
            errors.add( "Invalid password" );
        }

        return errors;
    }

    /*
     * This method validates register user data
     */
    public List<String> validateUser( User user ) {
        List<String> errors = new LinkedList<String>();

        if ( user.getFirstName() == null || user.getFirstName().equals( "" ) ) {
            errors.add( "First name cannot be empty" );
        }
        if ( user.getFirstName() != null && user.getFirstName().length() > NAME_CHAR_MAX ) {
            errors.add( "First name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }
        if ( user.getLastName() == null || user.getLastName().equals( "" ) ) {
            errors.add( "Last name cannot be empty" );
        }
        if ( user.getLastName() != null && user.getLastName().length() > NAME_CHAR_MAX ) {
            errors.add( "Last name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }
        if ( user.getEmail() == null || user.getEmail().equals( "" ) ) {
            errors.add( "Email cannot be empty" );
        }
        if ( user.getEmail() != null && user.getEmail().length() > EMAIL_CHAR_MAX ) {
            errors.add( "Email cannot exceed " + EMAIL_CHAR_MAX + " characters" );
        }
        if ( user.getEmail() != null && !isValidEmail( user.getEmail() ) ) {
            errors.add( "Invalid email" );
        }
        if ( user.getPasswordString() == null || user.getPasswordString().equals( "" ) ) {
            errors.add( "Password cannot be empty" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() < PW_CHAR_MIN ) {
            errors.add( "Password must be at least " + PW_CHAR_MIN + " characters" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() > PW_CHAR_MAX ) {
            errors.add( "Password cannot exceed " + PW_CHAR_MAX + " characters" );
        }

        return errors;
    }

    /*
     * This method validates a given email
     */
    private boolean isValidEmail( String email ) {
        return emailPattern.matcher( email ).matches();
    }

    /*
     * This method validates a given company
     */
    public List<String> validateCompany( Company company ) {
        List<String> errors = new LinkedList<String>();

        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.add( "Company name cannot be empty" );
        }
        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.add( "Company name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }

        return errors;
    }

    /*
     * This method generates a random password salt
     */
    public byte[] getSalt() {
        byte[] salt = new byte[SALT_LEN];
        rng.nextBytes( salt );
        return salt;
    }

    /*
     * This method hashes a given password with a given salt
     */
    public byte[] hashPassword( String password, byte[] salt ) {
        try {
            MessageDigest md = MessageDigest.getInstance( HASH_ALGORITHM );
            md.update( salt );
            return md.digest( password.getBytes( StandardCharsets.UTF_8 ) );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return new byte[0];
        }
    }

    /*
     * This method calls data handler to retrieve user from given mail
     */
    public User getUser( String email ) {
        return dataHandler.getUser( email );
    }

    /*
     * This method calls data handler to store given user
     */
    public int storeUser( User user ) {
        return dataHandler.storeUser( user );
    }

    /*
     * This method calls data handler to check if given company exists
     */
    public int existsCompany( String name ) {
        return dataHandler.existsCompany( name );
    }

    /*
     * This method calls data handler to retrieve company from given id
     */
    public Company getCompany( int companyId ) {
        return dataHandler.getCompany( companyId );
    }

    /*
     * This method calls data handler to store given user
     */
    public int storeCompany( Company company ) {
        return dataHandler.storeCompany( company );
    }

    /*
     * This method calls data handler to check if given phone number exists
     */
    public boolean existsPhoneNumber( String phoneNumber ) {
        return dataHandler.existsPhoneNumber( phoneNumber );
    }

    /*
     * This method calls data handler to store phone number for given company id
     */
    public boolean storePhoneNumber( String phoneNumber, int companyId ) {
        return dataHandler.storePhoneNumber( phoneNumber, companyId );
    }

    /*
     * This method calls data handler to get words from given phone number
     */
    public List<Word> getWords( String phoneNumber ) {
        return dataHandler.getWords( phoneNumber );
    }

    /*
     * This method calls data handler to store words for given phone number
     */
    public List<Integer> storeWords( List<Word> words, String phoneNumber ) {
        return dataHandler.storeWords( words, phoneNumber );
    }
}
