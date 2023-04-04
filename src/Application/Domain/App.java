package Application.Domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import static java.util.Map.entry;

import Persistence.AppDataHandler;

public class App {

    public final String HASH_ALGORITHM = "SHA-512";
    public final int SALT_LEN = 32;
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;

    
    private User currentUser;
    private String phoneNumber;
    private boolean isUserLoggedIn;
    private SecureRandom rng;
    private Pattern emailPattern;
    private Map<Character, char[]> numberToChars;
    private AppDataHandler appDataHandler;

    public App() {
        appDataHandler = new AppDataHandler();

        // Create random number generator
        // Used to generate password salts
        rng = new SecureRandom();

        // Regex and pattern to check validity of email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        emailPattern = Pattern.compile( emailRegex );

        // Initialize number to chars map
        numberToChars = Map.ofEntries(
            entry( '0', new char[] { '+' } ),
            entry( '1', new char[] { '\0' } ), // Does 1 have a char associated with it?
            entry( '2', new char[] { 'a', 'b', 'c' } ),
            entry( '3', new char[] { 'd', 'e', 'f' } ),
            entry( '4', new char[] { 'g', 'h', 'i' } ),
            entry( '5', new char[] { 'j', 'k', 'l' } ),
            entry( '6', new char[] { 'm', 'n', 'o' } ),
            entry( '7', new char[] { 'p', 'q', 'r', 's' } ),
            entry( '8', new char[] { 't', 'u', 'v' } ),
            entry( '9', new char[] { 'w', 'x', 'y', 'z' } )
        );
    }

    // Constructor for tests only
    public App( boolean isTest ) {
        // Regex and pattern to check validity of email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        emailPattern = Pattern.compile( emailRegex );
    }

    /*
     * This method authenticates a login
     */
    public List<String> login( User user ) {
        List<String> errors = new LinkedList<String>();

        // Retrieve requested user from db
        User userFromDb = new User();
        try {
            userFromDb = appDataHandler.getUser( user.getEmail() );
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

        // Clear user's text password (for security)
        user.setPasswordString( null );

        // Capture current user
        // User.copy( user, userFromDb );
        currentUser = userFromDb;
        isUserLoggedIn = true;

        return errors;
    }

    /*
     * This method registers a user
     */
    public List<String> register( User user ) {

        List<String> errors = new LinkedList<String>();

        // Generate password salt and hash password
        user.setPasswordSalt( getSalt() );
        user.setPassword( hashPassword( user.getPasswordString(), user.getPasswordSalt() ) );

        // Check if password hashing failed
        if ( user.getPassword().length == 0 ) {
            errors.add( "Hashing failed" );
            return errors;
        }

        // Clear user's text password (for security)
        user.setPasswordString( null );

        // Submit company and user to db
        try {

            // Check if company exists
            Company company = appDataHandler.getCompany( user.getCompany().getName() );

            // If company does not exist in db then add it
            if ( company == null ) {
                int companyId = appDataHandler.storeCompany( user.getCompany() );
                user.getCompany().setId( companyId );
            }

            // If it does exist, then create link between company and user
            else {
                user.setCompany( company );
            }

            // Add user
            user.setId( appDataHandler.storeUser( user ) );
        }
        catch ( Exception ex ) {
            errors.add( ex.getMessage() );
            return errors;
        }

        // Capture current user
        currentUser = user;
        isUserLoggedIn = true;

        return errors;
    }

    /*
     * This method will generate valid english words
     * for a given phone number
     */
    public List<Word> generateWords( String phoneNumber ) {
        // Convert to char array

        // Perform cartersian product on area code

        // Perform cartersian product on prefix

        // Perform cartersian product on suffix

        // 

        return new LinkedList<Word>();
    }

    /*
     * Not implemented
     */
    public List<String> getPhoneNumbers() {
        return new LinkedList<String>();
    }

    /*
     * Not implemented
     */
    public List<Word> getWords() {
        return new LinkedList<Word>();
    }

    /*
     * Not implemented
     */
    public boolean storeWords( List<Word> words ) {
        return true;
    }

    /*
     * This method logs out current user
     */
    public void logout() {
        currentUser = null;
        phoneNumber = null;
        isUserLoggedIn = false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> setPhoneNumber( String phoneNumber ) {

        // Validate phone number

        // Set phone number
        this.phoneNumber = phoneNumber;

        // If any, return error list
        return new LinkedList<String>();
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
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
     * This method validates a given email
     */
    private boolean isValidEmail( String email ) {
        return emailPattern.matcher( email ).matches();
    }

    /*
     * This method validates a given phone number
     */
    public List<String> validatePhoneNumber( String phoneNumber ) {
        List<String> errors = new LinkedList<String>();

        // Check if phone number string is null
        if ( phoneNumber == null ) {
            errors.add( "Phone number cannot be null" );
            return errors;
        }

        // Remove common special chars
        phoneNumber = phoneNumber.replaceAll( "[\\s\\(\\)]", "" );

        // Convert to char array for easier manipulation
        char[] numbers = phoneNumber.toCharArray();

        // Check if numbers is null or contains invalid number
        // of characters
        if ( numbers.length != 10 ) {
            errors.add( "Invalid phone number length" );
            return errors;
        }

        // Check if area code is valid
        int areaCode = numbers[2] * 100 + numbers[1] * 10 + numbers[0];
        if ( areaCode < 200 || areaCode > 999 || areaCode == 911 ) {
            errors.add( "Invalid area code" );
            return errors;
        }

        // Check if prefix is valid
        if ( numbers[3] == 0 || numbers[3] == 1 ) {
            errors.add( "Invalid prefix" );
            return errors;
        }

        // Ensure is not duplicate
        if ( appDataHandler.existsPhoneNumber( phoneNumber ) ) {
            errors.add( "Phone number already in use" );
            return errors;
        }

        return errors;
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
     * This method generates a random password salt
     */
    public byte[] getSalt() {
        byte[] salt = new byte[SALT_LEN];
        rng.nextBytes( salt );
        return salt;
    }
}
