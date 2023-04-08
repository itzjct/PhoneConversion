package Application.Domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import static java.util.Map.entry;
import java.util.stream.Collectors;

import Persistence.AppDataHandler;

public class App {

    public final String HASH_ALGORITHM = "SHA-512";
    public final int SALT_LEN = 32;
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;

    private Dictionary dic;

    private Map<Character, char[]> numberToChars = Map.ofEntries(
            entry( '0', new char[] { '+' } ),
            entry( '1', new char[] { '\0' } ), // Does 1 have a char associated with it?
            entry( '2', new char[] { 'a', 'b', 'c' } ),
            entry( '3', new char[] { 'd', 'e', 'f' } ),
            entry( '4', new char[] { 'g', 'h', 'i' } ),
            entry( '5', new char[] { 'j', 'k', 'l' } ),
            entry( '6', new char[] { 'm', 'n', 'o' } ),
            entry( '7', new char[] { 'p', 'q', 'r', 's' } ),
            entry( '8', new char[] { 't', 'u', 'v' } ),
            entry( '9', new char[] { 'w', 'x', 'y', 'z' } ) );

    private static final Map<Character, List<Character>> DIGIT_LETTER_MAPPING = Map.of(
            '2', List.of( 'A', 'B', 'C' ),
            '3', List.of( 'D', 'E', 'F' ),
            '4', List.of( 'G', 'H', 'I' ),
            '5', List.of( 'J', 'K', 'L' ),
            '6', List.of( 'M', 'N', 'O' ),
            '7', List.of( 'P', 'Q', 'R', 'S' ),
            '8', List.of( 'T', 'U', 'V' ),
            '9', List.of( 'W', 'X', 'Y', 'Z' ) );

    private User currentUser;
    private String phoneNumber;
    private boolean isUserLoggedIn;
    private SecureRandom rng;
    private Pattern emailPattern;
    private AppDataHandler appDataHandler;

    public App() {
        appDataHandler = new AppDataHandler();
        try {
            dic = new Dictionary(
                    "C:\\Users\\Julian\\Desktop\\School\\Software Engineering\\Project\\db\\dictionary.txt" );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }

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

    public static List<String> phoneNumberToWords( String phoneNumber ) {
        List<List<Character>> letterGroups = phoneNumber.chars()
                .mapToObj( c -> DIGIT_LETTER_MAPPING.get( ( char )c ) )
                .filter( Objects::nonNull )
                .collect( Collectors.toList() );

        return cartesianProduct( letterGroups ).stream()
                .map( list -> list.stream().map( String::valueOf ).collect( Collectors.joining() ) )
                .collect( Collectors.toList() );
    }

    private static List<List<Character>> cartesianProduct( List<List<Character>> lists ) {
        List<List<Character>> resultLists = new ArrayList<>();
        if ( lists.isEmpty() ) {
            resultLists.add( Collections.emptyList() );
            return resultLists;
        }
        else {
            List<Character> firstList = lists.get( 0 );
            List<List<Character>> remainingLists = cartesianProduct( lists.subList( 1, lists.size() ) );
            for ( Character condition : firstList ) {
                for ( List<Character> remainingList : remainingLists ) {
                    ArrayList<Character> resultList = new ArrayList<>();
                    resultList.add( condition );
                    resultList.addAll( remainingList );
                    resultLists.add( resultList );
                }
            }
            return resultLists;
        }
    }

    private List<Word> convertToWord( List<String> listOfString ) {
        List<Word> listOfWords = new LinkedList<>();
        for ( String word : listOfString ) {
            listOfWords.add( new Word( word ) );
        }
        return listOfWords;
    }

    /*
     * This method will generate valid english words
     * for a given phone number
     * 
     * Not finished
     */
    public Map<String, List<Word>> generateWords( String phoneNumber ) {
        String areaCode = phoneNumber.substring( 0, 3 );
        String prefix = phoneNumber.substring( 3, 6 );
        String sufix = phoneNumber.substring( 6, 10 );

        Map<String, List<Word>> wordMap = new TreeMap<>();

        List<String> temp = phoneNumberToWords( areaCode );
        temp = dic.filterValidWords( temp );
        List<Word> areaCodeWords = convertToWord( temp );
        wordMap.put( "AreaCode", areaCodeWords );

        temp = phoneNumberToWords( prefix );
        temp = dic.filterValidWords( temp );
        List<Word> prefixWords = convertToWord( temp );
        wordMap.put( "Prefix", prefixWords );

        temp = phoneNumberToWords( sufix );
        temp = dic.filterValidWords( temp );
        List<Word> sufixWords = convertToWord( temp );
        wordMap.put( "Sufix", sufixWords );

        return wordMap;

        // Perform cartersian product on prefix
        // List<String> product2 = new LinkedList<String>();for(
        // int first = 0;first<numberToChars.get(numbers[3]).length;first++)
        // {
        // for ( int second = 0; second < numberToChars.get( numbers[4] ).length;
        // second++ ) {
        // for ( int third = 0; third < numberToChars.get( numbers[5] ).length; third++
        // ) {
        // String word = "" + numberToChars.get( numbers[3] )[first] +
        // numberToChars.get( numbers[4] )[second]
        // + numberToChars.get( numbers[5] )[third];
        // product2.add( word );
        // }
        // }
        // }

        // // Perform cartersian product on suffix
        // List<String> product3 = new LinkedList<String>();for(
        // int first = 0;first<numberToChars.get(numbers[6]).length;first++)
        // {
        // for ( int second = 0; second < numberToChars.get( numbers[7] ).length;
        // second++ ) {
        // for ( int third = 0; third < numberToChars.get( numbers[8] ).length; third++
        // ) {
        // for ( int fourth = 0; fourth < numberToChars.get( numbers[9] ).length;
        // fourth++ ) {
        // String word = "" + numberToChars.get( numbers[6] )[first] +
        // numberToChars.get( numbers[7] )[second]
        // + numberToChars.get( numbers[8] )[third] + numberToChars.get( numbers[9]
        // )[fourth];
        // product3.add( word );
        // }
        // }
        // }
        // }

        // Perform cartersian product on all parts of phone number

        // Find valid english words

        // Convert results from List of String to Word

        // return new LinkedList<Word>();
    }

    /*
     * This method retrieves the phone numbers associated with
     * a given company
     * 
     * Returns null if Company object is null or if company has invalid id
     */
    public List<String> getPhoneNumbers( Company company ) {
        if ( company == null || company.getId() == 0 ) {
            return null;
        }
        List<String> phoneNumbers = appDataHandler.getPhoneNumbers( company.getId() );
        return phoneNumbers;
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
     * This method retrieves the words associated with a
     * given phone number
     * 
     * Returns null if phone number String is null
     */
    public List<Word> getWords( String phoneNumber ) {
        if ( phoneNumber == null ) {
            return null;
        }
        List<Word> words = appDataHandler.getWords( phoneNumber );
        return words;
    }

    /*
     * This method stores list of words associated with a given
     * phone number
     * 
     * Returns true if operation succeeded and false otherwise
     */
    public boolean storeWords( List<Word> words, String phoneNumber ) {
        List<Integer> ids = appDataHandler.storeWords( words, phoneNumber );

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