package Application.Domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.Map.entry;

import Application.Services.ConfigService;
import Persistence.AppDataHandler;

/**
 * App is the class responsible executing business
 * logic of the current project.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class App {

    // Constant specifying password hashing algorithm
    public final String HASH_ALGORITHM = "SHA-512";

    // Contant specifying password salt length
    public final int SALT_LEN = 32;

    // Contansts specifying min and max values
    // for login/register validation
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;

    // Map used to create mapping from phone number
    // digits to characters
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

    // User object representing currently
    // logged in user
    private User currentUser;

    // Flag used to detect if a user is logged in
    private boolean isUserLoggedIn;

    // SecureRandom object used as a random
    // number generator to create password salt
    private SecureRandom rng;

    // Pattern object to validate emails
    private Pattern emailPattern;

    // AppDataHandler object to interact with
    // persistence (data access) layer
    private AppDataHandler appDataHandler;

    // Dictionary object used to validate
    // words
    private Dictionary dic;

    /**
     * This constructor handles initialization of needed
     * application-related logic.
     */
    public App() {

        // Load config service
        ConfigService configService = new ConfigService();

        // Create instance of data handler
        appDataHandler = new AppDataHandler( configService );

        // Load dictionary file path from config file
        String dictionaryPath = configService.getProperties().getProperty( ConfigService.DICTIONARY_PATH );
        dic = new Dictionary( dictionaryPath );

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
    // public App( AppDataHandler appDataHandler ) {
    // this.appDataHandler = appDataHandler;
    // dic = new Dictionary(
    // "C:\\Users\\Julian\\Desktop\\School\\Software
    // Engineering\\Project\\db\\dictionary.txt" );

    // // Create random number generator
    // // Used to generate password salts
    // rng = new SecureRandom();

    // // Regex and pattern to check validity of email
    // String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
    // "[a-zA-Z0-9_+&*-]+)*@" +
    // "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
    // "A-Z]{2,7}$";
    // emailPattern = Pattern.compile( emailRegex );
    // }

    /**
     * This method updates the state of the App
     * (current user) object.
     */
    public void updateState() {
        currentUser.setCompany( appDataHandler.getCompany( currentUser.getCompany().getId() ) );
    }

    /**
     * This method authenticates a login. If returned list
     * is empty, then no errors were found.
     * 
     * @param user A User object.
     * @return A List of String representing errors found.
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

    /**
     * This method registers a user. If returned list
     * is empty, then no errors were found.
     * 
     * @param user A User object.
     * @return A List of String representing errors found.
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

    /**
     * This method performs cartesian product on a fixed-size
     * char array. Uses digit to character mappings defined in
     * numberToChars map.
     * 
     * @param numbers A char array of length 3 or 4.
     * @return A List of String.
     */
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

    /**
     * This method performs cartersian product between two lists
     * of string.
     * 
     * @param l1 A List of String.
     * @param l2 A List of String.
     * @return A List of String.
     */
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

    /**
     * This method finds "sub-words", which are valid
     * English words substrings part of a larger string.
     * 
     * @param words A List of String to be scanned.
     * @return A Set of Word.
     */
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

    /**
     * This method will generate valid english words
     * for a given phone number.
     * 
     * @param phoneNumber A PhoneNumber object.
     * @return A Set of Word.
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

        return allWords;
    }

    /**
     * This method generates different possible phrase combinations
     * that include the specified word. It returns A 2D list of Word objects.
     * The outer list holds the different phrases generated.
     * The inner lists hold Word objects specifying the location
     * of each word within a phone number string.
     * 
     * @param word  A Word object representing the specified word
     *              to be included in all phrases generated.
     * @param words A Set of Word representing the set of words
     *              that were generated alongside the specified word.
     * @return A 2D List of Word.
     */
    public List<List<Word>> generatePhrases( Word word, Set<Word> words ) {

        // Current result
        List<List<Word>> result = new LinkedList<>();

        // Generate all possible phrases generated to the right
        // of the current word, including the current word.
        List<List<Word>> toRight = generatePhrases( word, words, Arrays.asList( word ), true );

        // Generate all possible phrases generated to the left
        // of the current word, WITHOUT including the current word.
        List<List<Word>> toLeft = generatePhrases( word, words, new LinkedList<>(), false );

        // Remove invalid item (byproduct of algorithm)
        toLeft.remove( 0 );

        // Add all phrases found to the right, to the result list.
        // Since they already include the target word, they are valid
        // phrases.
        result.addAll( toRight );

        // Perform cartersian product between all phrases found to the left
        // against all found to the right. Phrases found to the left do not
        // include target word, thus, need to be appended to a phrase that
        // include the target word to become valid phrases.
        for ( List<Word> left : toLeft ) {
            for ( List<Word> right : toRight ) {
                List<Word> temp = new LinkedList<>( left );
                temp.addAll( right );
                result.add( temp );
            }
        }
        return result;
    }

    /**
     * This recursive support method aids in generating different possible phrase
     * combinations
     * that include the specified word. It returns A 2D list of Word objects.
     * The outer list holds the different phrases generated.
     * The inner lists hold Word objects specifying the location
     * of each word within a phone number string.
     * 
     * @param word         A Word object specifying the current target word.
     * @param words        A Set of Word used as a word bank to discover
     *                     the location (left or right) of neighboring words
     *                     around the target word
     * @param currentWords A List of Word used to build phrases including
     *                     the current word
     * @param isRight      A flag used to specify the direction where the
     *                     algorithm should find neighboring words.
     *                     True means towards right side of target word.
     *                     False means to the left side of the target word.
     * @return A 2D List of Word.
     */
    private List<List<Word>> generatePhrases( Word word, Set<Word> words, List<Word> currentWords, boolean isRight ) {

        // Current result
        List<List<Word>> result = new LinkedList<>();

        // Add the current phrase to the result list
        result.add( currentWords );

        // Find neighboring words to the left/right
        // of the target word
        Predicate<Word> filterCondition = x -> isRight ? x.getStartIndex() == word.getEndIndex() + 1
                : x.getEndIndex() == word.getStartIndex() - 1;
        List<Word> nextWords = words.stream().filter( filterCondition ).collect( Collectors.toList() );

        // If no neighbor words are found, return
        // current result
        if ( nextWords.isEmpty() ) {
            return result;
        }

        // For each neighboring word found, append it
        // to the current phrase and perform recursive call
        // to find all possible phrases.
        for ( Word currWord : nextWords ) {

            // Make a copy of current phrase
            LinkedList<Word> newCurrentWords = new LinkedList<>( currentWords );

            // If direction is headed right, append
            // current neighboring word to end of phrase
            // to create a new phrase
            if ( isRight ) {
                newCurrentWords.addLast( currWord );
            }

            // If direction is headed left, append
            // current neighboring word to start of phrase
            // to create a new phrase
            else {
                newCurrentWords.addFirst( currWord );
            }

            // Continue finding all possible phrase combinations in the
            // direction specified. Pass the current neigboring word as the
            // new target word and the new phrase.
            List<List<Word>> phrases = generatePhrases( currWord, words, newCurrentWords, isRight );

            // Add current results to overall result
            result.addAll( phrases );
        }

        return result;
    }

    /**
     * This method takes a list of phrases and embeds them
     * within a phone number string depending on the position
     * of each word. The outer list holds the different phrases generated.
     * The inner lists hold Word objects specifying the location
     * 
     * @param phrases     A 2D List of Word containing the phrases to embed.
     * @param phoneNumber A String representing the phone number
     *                    against which the phrases will be embedded.
     * @return A List of String representing the embedded
     *         "numeric phrases"
     */
    public List<String> generateNumericPhrases( List<List<Word>> phrases, String phoneNumber ) {

        // Initialize result
        List<String> result = new LinkedList<>();

        // Iterate through each phrase
        for ( int i = 0; i < phrases.size(); i++ ) {
            List<Word> phrase = phrases.get( i );

            // Create new string builder
            StringBuilder sb = new StringBuilder();

            // Iterate through each character in the phone number string
            for ( int j = 0; j < phoneNumber.length(); j++ ) {

                // If the current character index is not part of
                // any word in the current phrase then append
                // current character from phone number to string
                // builder
                if ( j < phrase.get( 0 ).getStartIndex() || j > phrase.get( phrase.size() - 1 ).getEndIndex() ) {
                    sb.append( phoneNumber.charAt( j ) );
                }

                // Otherwise, append all words in the phrase.
                // All words can be appended at once since they
                // are sequential (there are no gaps between words
                // in any phrase).
                else {
                    for ( int k = 0; k < phrase.size(); k++ ) {
                        sb.append( phrase.get( k ).getWord() );
                    }

                    // Update current character index after appending
                    // all words
                    j = phrase.get( phrase.size() - 1 ).getEndIndex();
                }
            }

            // Add newly created numeric phrase to result
            result.add( sb.toString() );
        }
        return result;
    }

    /**
     * This method calls data handler to retrieve all phone numbers
     * that do not belong to the specified company.
     * 
     * @param companyId A Company object.
     * @return A Set of String.
     */
    public Set<String> getNonUsablePhoneNumbers( Company company ) {
        return appDataHandler.getNonUsablePhoneNumbers( company );
    }

    /**
     * This method calls data handler to store the given phone number
     * with the specified company.
     * 
     * @param phoneNumber A PhoneNumber object.
     * @param companyId   An Company object.
     * @return An int representing the id of the stored phone number.
     */
    public int storePhoneNumber( PhoneNumber phoneNumber, Company company ) {
        return appDataHandler.storePhoneNumber( phoneNumber, company );
    }

    /**
     * This method calls data handler to store list of phrases
     * associated with a given phone number.
     * 
     * @param phrases     An Iterable of String representing phrases to be stored.
     * @param phoneNumber A PhoneNumber object.
     * @return A List of Integer representing the ids of the stored phrases.
     */
    public List<Integer> storePhrases( Iterable<String> phrases, PhoneNumber phoneNumber ) {
        return appDataHandler.storePhrases( phrases, phoneNumber );
    }

    /**
     * This method calls data handler to delete a list of
     * phrases.
     * 
     * @param phrases An Iterable of string representing phrases to be deleted.
     * @return True if the operation succeed.
     *         False otherwise.
     */
    public boolean deletePhrases( Iterable<String> phrases ) {
        return appDataHandler.deletePhrases( phrases );
    }

    /**
     * This method logs out current user.
     */
    public void logout() {
        currentUser = null;
        isUserLoggedIn = false;
    }

    /**
     * This method returns the current user.
     * 
     * @return A User object.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * This method returns a boolean specifying
     * whether a user is logged in.
     * 
     * @return True if a user is logged in.
     *         False otherwise.
     */
    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    /**
     * This method validates login user data and returns
     * a list of errors. If no errors are found, it returns
     * an empty list.
     * 
     * @param user A User object to be validated.
     * @return A List of String.
     */
    public List<String> validateLogin( User user ) {
        List<String> errors = new LinkedList<String>();

        // Check if email is empty
        if ( user.getEmail() == null || user.getEmail().equals( "" ) ) {
            errors.add( "Email cannot be empty" );
        }

        // Check if password string is empty
        if ( user.getPasswordString() == null || user.getPasswordString().equals( "" ) ) {
            errors.add( "Password cannot be empty" );
        }

        // Check if password meets min length requirement
        if ( user.getPasswordString() != null && user.getPasswordString().length() < PW_CHAR_MIN ) {
            errors.add( "Invalid password" );
        }

        // Check if password meets max length requirement
        if ( user.getPasswordString() != null && user.getPasswordString().length() > PW_CHAR_MAX ) {
            errors.add( "Invalid password" );
        }

        return errors;
    }

    /**
     * This method validates register user data and returns
     * a list of errors. If no errors are found, it returns
     * an empty list.
     * 
     * @param user A User object to be validated.
     * @return A List of String.
     */
    public List<String> validateUser( User user ) {
        List<String> errors = new LinkedList<String>();

        // Check if first name is empty
        if ( user.getFirstName() == null || user.getFirstName().equals( "" ) ) {
            errors.add( "First name cannot be empty" );
        }

        // Check if first name meets max length requirement
        if ( user.getFirstName() != null && user.getFirstName().length() > NAME_CHAR_MAX ) {
            errors.add( "First name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }

        // Check if last name is empty
        if ( user.getLastName() == null || user.getLastName().equals( "" ) ) {
            errors.add( "Last name cannot be empty" );
        }

        // Check if last name meets max length requirement
        if ( user.getLastName() != null && user.getLastName().length() > NAME_CHAR_MAX ) {
            errors.add( "Last name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }

        // Check if email is empty
        if ( user.getEmail() == null || user.getEmail().equals( "" ) ) {
            errors.add( "Email cannot be empty" );
        }

        // Check if email meets max length requirement
        if ( user.getEmail() != null && user.getEmail().length() > EMAIL_CHAR_MAX ) {
            errors.add( "Email cannot exceed " + EMAIL_CHAR_MAX + " characters" );
        }

        // Check if email follows valid pattern
        if ( user.getEmail() != null && !isValidEmail( user.getEmail() ) ) {
            errors.add( "Invalid email" );
        }

        // Check if password string is empty
        if ( user.getPasswordString() == null || user.getPasswordString().equals( "" ) ) {
            errors.add( "Password cannot be empty" );
        }

        // Check if password string meets min length requirement
        if ( user.getPasswordString() != null && user.getPasswordString().length() < PW_CHAR_MIN ) {
            errors.add( "Password must be at least " + PW_CHAR_MIN + " characters" );
        }

        // Check if password string meets max length requirement
        if ( user.getPasswordString() != null && user.getPasswordString().length() > PW_CHAR_MAX ) {
            errors.add( "Password cannot exceed " + PW_CHAR_MAX + " characters" );
        }

        return errors;
    }

    /**
     * This method validates a given company and returns
     * a list of errors. If no errors are found, it returns
     * an empty list.
     * 
     * @param company A Company object to be validated.
     * @return A List of String.
     */
    public List<String> validateCompany( Company company ) {
        List<String> errors = new LinkedList<String>();

        // Check if company name is empty
        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.add( "Company name cannot be empty" );
        }

        // Check if company name meets max length requirement
        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.add( "Company name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }

        return errors;
    }

    /**
     * This method validates a given email string by
     * matching it against an email pattern.
     * 
     * @param email A String representing an email address.
     * @return True if is valid.
     *         False otherwise.
     */
    private boolean isValidEmail( String email ) {
        return emailPattern.matcher( email ).matches();
    }

    /**
     * This method validates a given phone number and returns
     * a list of errors. If no errors are found, it returns
     * an empty list.
     * 
     * @param phoneNumber A PhoneNumber object to be validated.
     * @return A List of String.
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

    /**
     * This method calls data handler to check if a given
     * phone number already exists and returns
     * a list of errors. If no errors are found, it returns
     * an empty list.
     * 
     * @param phoneNumber A String representing a phone number.
     * @return A List of String.
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

    /**
     * This method hashes a given password with a given salt.
     * 
     * @param password A String representing a password.
     * @param salt     A byte array representing a password salt.
     * @return A byte array representing a hashed password.
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

    /**
     * This method generates a random password salt.
     * 
     * @return A byte array.
     */
    public byte[] getSalt() {
        byte[] salt = new byte[SALT_LEN];
        rng.nextBytes( salt );
        return salt;
    }
}