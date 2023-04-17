package Application.Domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Map.entry;

import Persistence.AppDataHandler;

public class App {

    public final String HASH_ALGORITHM = "SHA-512";
    public final int SALT_LEN = 32;
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;

    private Map<Character, char[]> numberToChars = Map.ofEntries(
            entry( '0', new char[] { '+' } ),
            entry( '1', new char[] { '\0' } ), // Does 1 have a char associated with it?
            entry( '2', new char[] { 'A', 'B', 'C' } ),
            entry( '3', new char[] { 'D', 'E', 'F' } ),
            entry( '4', new char[] { 'G', 'H', 'I' } ),
            entry( '5', new char[] { 'J', 'K', 'L' } ),
            entry( '6', new char[] { 'M', 'N', 'O' } ),
            entry( '7', new char[] { 'P', 'Q', 'R', 'S' } ),
            entry( '8', new char[] { 'T', 'U', 'V' } ),
            entry( '9', new char[] { 'W', 'X', 'Y', 'Z' } ) );

    private User currentUser;
    private String phoneNumber;
    private boolean isUserLoggedIn;
    private SecureRandom rng;
    private Pattern emailPattern;
    private AppDataHandler appDataHandler;
    private Dictionary dic;

    public App() {
        appDataHandler = new AppDataHandler();
        dic = new Dictionary(
                "C:\\Users\\Julian\\Desktop\\School\\Software Engineering\\Project\\db\\dictionary.txt" );

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

    // Constructor for tests only
    public App( AppDataHandler appDataHandler ) {
        this.appDataHandler = appDataHandler;
        dic = new Dictionary(
                "C:\\Users\\Julian\\Desktop\\School\\Software Engineering\\Project\\db\\dictionary.txt" );

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

    public void updateState() {
        currentUser.setCompany( appDataHandler.getCompany( currentUser.getCompany().getId() ) );
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

    public List<String> cartesianProduct( char[] numbers ) {

        // Check if char array is valid length
        if ( numbers.length < 3 || numbers.length > 4 ) {
            return null;
        }

        // Perform cartesian product
        List<String> result = new LinkedList<>();
        for ( int first = 0; first < numberToChars.get( numbers[0] ).length; first++ ) {
            for ( int second = 0; second < numberToChars.get( numbers[1] ).length; second++ ) {
                for ( int third = 0; third < numberToChars.get( numbers[2] ).length; third++ ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( numberToChars.get( numbers[0] )[first] );
                    sb.append( numberToChars.get( numbers[1] )[second] );
                    sb.append( numberToChars.get( numbers[2] )[third] );
                    if ( numbers.length == 4 ) {
                        for ( int fourth = 0; fourth < numberToChars.get( numbers[3] ).length; fourth++ ) {
                            sb.append( numberToChars.get( numbers[3] )[fourth] );
                            result.add( sb.toString() );
                            sb.deleteCharAt( 3 );
                        }
                    }
                    else {
                        result.add( sb.toString() );
                    }
                }
            }
        }

        return result;
    }

    public List<String> cartesianProductWords( List<String> l1, List<String> l2 ) {
        List<String> result = new LinkedList<>();
        for ( String s1 : l1 ) {
            for ( String s2 : l2 ) {
                StringBuilder sb = new StringBuilder();
                sb.append( s1 );
                sb.append( s2 );
                result.add( sb.toString() );
            }
        }
        return result;
    }

    public Set<Word> findSubWords( List<String> words ) {
        Set<Word> result = new HashSet<>();

        for ( String word : words ) {
            for ( int length = 3; length <= word.length(); length++ ) {
                for ( int start = 0, end = length; end <= word.length(); start++, end++ ) {
                    String subWord = word.substring( start, end );
                    if ( dic.contains( subWord ) ) {
                        Word newWord = new Word( subWord, start, end - 1 );
                        result.add( newWord );
                    }
                }
            }
        }

        return result;
    }

    /*
     * This method will generate valid english words
     * for a given phone number
     * 
     * Not finished
     */
    public Set<Word> generateWords( PhoneNumber phoneNumber ) {

        // Convert to char array
        char[] numbers = phoneNumber.getPhoneNumber().toCharArray();

        // Perform cartersian product on all parts of phone number
        List<String> areaCode = cartesianProduct( Arrays.copyOfRange( numbers, 0, 3 ) );
        List<String> prefix = cartesianProduct( Arrays.copyOfRange( numbers, 3, 6 ) );
        List<String> sufix = cartesianProduct( Arrays.copyOfRange( numbers, 6, 10 ) );
        List<String> areaCodeAndPrefix = cartesianProductWords( areaCode, prefix );
        List<String> all = cartesianProductWords( areaCodeAndPrefix, sufix );

        // Find valid english words
        Set<Word> allWords = findSubWords( all );
        // for ( Word word : allWords ) {
        // System.out.println( word.getWord() );
        // }

        return allWords;
    }

    public List<List<Word>> generatePhrases( Word word, Set<Word> words ) {
        List<List<Word>> result = new LinkedList<>();
        List<List<Word>> toRight = generatePhrases( word, words, Arrays.asList( word ), true );
        List<List<Word>> toLeft = generatePhrases( word, words, new LinkedList<>(), false );
        toLeft.remove( 0 );
        result.addAll( toRight );

        for ( List<Word> left : toLeft ) {
            for ( List<Word> right : toRight ) {
                List<Word> temp = new LinkedList<>( left );
                temp.addAll( right );
                result.add( temp );
            }
        }
        return result;
    }

    private List<List<Word>> generatePhrases( Word word, Set<Word> words, List<Word> currentWords, boolean isRight ) {
        List<List<Word>> result = new LinkedList<>();
        result.add( currentWords );

        Predicate<Word> filterCondition = x -> isRight ? x.getStartIndex() == word.getEndIndex() + 1
                : x.getEndIndex() == word.getStartIndex() - 1;
        List<Word> nextWords = words.stream().filter( filterCondition ).collect( Collectors.toList() );
        if ( nextWords.isEmpty() ) {
            return result;
        }
        for ( Word currWord : nextWords ) {
            LinkedList<Word> newCurrentWords = new LinkedList<>( currentWords );
            if ( isRight ) {
                newCurrentWords.addLast( currWord );
            }
            else {
                newCurrentWords.addFirst( currWord );
            }
            List<List<Word>> phrases = generatePhrases( currWord, words, newCurrentWords, isRight );
            result.addAll( phrases );
        }

        return result;
    }

    public String buildString( String... strings ) {
        StringBuilder sb = new StringBuilder();
        for ( String s : strings ) {
            sb.append( s );
        }
        return sb.toString();
    }

    /*
     * This method retrieves all phone numbers that do not belong
     * to the specified company
     * 
     * Returns a Set of String
     */
    public Set<String> getNonUsablePhoneNumbers( int companyId ) {
        return appDataHandler.getNonUsablePhoneNumbers( companyId );
    }

    /*
     * This method stores the phone number to given company into db
     */
    public int storePhoneNumber( PhoneNumber phoneNumber, int companyId ) {
        return appDataHandler.storePhoneNumber( phoneNumber, companyId );
    }

    /*
     * This method stores list of words associated with a given
     * phone number
     * 
     * Returns true if operation succeeded and false otherwise
     */
    public boolean storeWords( Map<Integer, List<Word>> words, int phoneId ) {
        List<Integer> ids = appDataHandler.storeWords( words, phoneId );

        // If all words were successfully stored, the size of ids such
        // match the size of words
        return ids.size() == words.size();
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

        // Check if numbers is null or contains invalid number
        // of characters
        if ( phoneNumber.length() != 10 ) {
            errors.add( "Invalid phone number length" );
            return errors;
        }

        // Check if area code is valid
        try {
            int areaCode = Integer.valueOf( phoneNumber.substring( 0, 3 ) );
            if ( areaCode < 200 || areaCode > 999 || areaCode == 911 ) {
                errors.add( "Invalid area code" );
                return errors;
            }
        }
        catch ( Exception ex ) {
            errors.add( ex.getMessage() );
            return errors;
        }

        // Check if prefix is valid
        if ( phoneNumber.charAt( 3 ) == '0' || phoneNumber.charAt( 3 ) == '1' ) {
            errors.add( "Invalid prefix" );
            return errors;
        }

        return errors;
    }

    /*
     * This method checks if a given phone number already
     * exists in database
     */
    public List<String> existsPhoneNumber( String phoneNumber ) {
        List<String> errors = new LinkedList<String>();

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