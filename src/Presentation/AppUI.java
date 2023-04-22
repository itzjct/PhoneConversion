package Presentation;

import java.util.*;
import java.util.stream.Collectors;

import Application.Domain.*;

/**
 * AppUI is the class responsible for interacting with
 * user in the current project.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class AppUI {

    // Constants specifying different menu types
    // displayed
    public final int START_MENU = 0;
    public final int USER_MENU = 1;
    public final int ADMIN_MENU = 2;

    // App object responsible for executing
    // business logic
    private App app;

    // Scanner object used to read user input
    private Scanner input;

    // Flag used to control start menu loop
    private boolean isStartMenuRunning;

    // Flag used to control user menu loop
    private boolean isUserMenuRunning;

    /**
     * Main driver.
     */
    public static void main( String[] args ) {

        // Run app
        AppUI app = new AppUI();
        app.start();
    }

    /**
     * This constructor handles initialization of needed
     * application-related logic.
     */
    public AppUI() {
        app = new App();
        input = new Scanner( System.in );
    }

    /**
     * This method handles the logic to start and monitor the
     * execution of the application.
     */
    public void start() {

        // Initialize menu flags to true
        isStartMenuRunning = true;
        isUserMenuRunning = true;

        // Loop to handle start menu options
        while ( isStartMenuRunning ) {

            // Select a start option
            int selectedStartOption = selectOption( START_MENU );

            // Execute the selected start option
            // and determine if user menu should be displayed
            isUserMenuRunning = execStartOption( selectedStartOption );

            // Loop to handle user menu options
            while ( isUserMenuRunning ) {

                // Select a user (or admin) option
                // **ENSURE CURRENT USER IS NEVER NULL HERE**
                int selectedUserOption = selectOption( app.getCurrentUser().getIsAdmin() ? ADMIN_MENU : USER_MENU );

                // Execute the selected user option
                execUserOption( selectedUserOption );
            }
        }
    }

    /**
     * This method handles logic to allow the selection
     * of a menu option.
     * 
     * @param menuType An int representing the menu type.
     * @return An int representing a menu option.
     */
    private int selectOption( int menuType ) {

        // Number of options presented
        int numOfOptions = getMenuOptionsNumber( menuType );

        // Store selected option as int
        int selectedOption = 0;

        // Flag used to control menu execution
        boolean isError = false;

        // Print corresponding menu
        do {

            // Print menu options
            printMenu( menuType );

            try {

                // Read and parse input
                String unparsedOption = input.nextLine();
                selectedOption = Integer.parseInt( unparsedOption );

                // Ensure input is valid
                if ( !isValidRange( selectedOption, 1, numOfOptions ) ) {
                    printErrorMsgs( Arrays.asList( selectedOption + " is not an option!" ) );
                    isError = true;
                }

                // Selected option is valid, exit loop
                else {
                    isError = false;
                }
            }

            // Error parsing input to int
            catch ( NumberFormatException ex ) {
                printErrorMsgs( Arrays.asList( "Invalid input entered!" ) );
                isError = true;
            }

            // Any other errors
            catch ( Exception ex ) {
                System.out.println( ex.getMessage() ); // remove
                printErrorMsgs( Arrays.asList( "Something went wrong. Try again!" ) );
                isError = true;
            }
        }
        while ( isError );

        return selectedOption;
    }

    /**
     * This method returns the number of options for
     * a given menu type. Used to determine boundaries
     * when user is selecting an option.
     * 
     * @param menuType An int representing the menu type.
     * @return An int representing the number of menu options.
     */
    private int getMenuOptionsNumber( int menuType ) {
        switch ( menuType ) {
        // Start menu
        case START_MENU:
            return 3;

        // User menu
        case USER_MENU:
            return 5;

        // User admin menu
        case ADMIN_MENU:
            return 6;

        // Invalid menu
        default:
            System.out.println( "Invalid menu type" );
            return 0;
        }
    }

    /**
     * This method prints out the corresponding menu options
     * from a given menu type.
     * 
     * @param menuType An int representing the menu type.
     */
    private void printMenu( int menuType ) {
        switch ( menuType ) {
        case START_MENU:
            printStartMenu();
            break;
        case USER_MENU:
            printUserMenu( false );
            break;
        case ADMIN_MENU:
            printUserMenu( true );
            break;
        default:
            System.out.println( "Invalid menu type" );
        }
    }

    /**
     * This method displays the start menu.
     */
    private void printStartMenu() {
        printHeader( "Start Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Exit" );
        System.out.println( "2: Login" );
        System.out.println( "3: Register" );
    }

    /**
     * This method displays the user/admin menu.
     * 
     * @param isAdmin A boolean specifying if current
     *                user possess admin role.
     */
    private void printUserMenu( boolean isAdmin ) {
        printHeader( "User Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Exit" );
        System.out.println( "2: Logout" );
        System.out.println( "3: Generate Words" );
        System.out.println( "4: View Company Phone Numbers" );
        System.out.println( "5: View Approved Phrases" );
        if ( isAdmin ) {
            System.out.println( "6: Approve Phrases" );
        }

        // Update App object's state. Needed to keep
        // phone numbers and words up to date.
        app.updateState();
    }

    /**
     * This method executes the given start option.
     * 
     * @param option An int representing the menu
     *               option to execute
     * @return True if operation successed.
     *         False if operation failed.
     */
    private boolean execStartOption( int option ) {
        switch ( option ) {

        // Exit option
        case 1:
            exit();
            return false;

        // Login option
        case 2:
            return login();

        // Register option
        case 3:
            return register();

        // Unknown option
        default:
            System.out.println( "Invalid option parsed!" );
            exit();
            return false;
        }
    }

    /**
     * This method executes the given user option.
     * 
     * @param option An int representing the menu
     *               option to execute
     */
    private void execUserOption( int option ) {
        switch ( option ) {

        // Exit option
        case 1:
            exit();
            break;

        // Logout option
        case 2:
            logout();
            break;

        // Generate words option
        case 3:
            generateWords();
            break;

        // View phone numbers option
        case 4:
            viewPhoneNumbers();
            break;

        // View approved phrases
        case 5:
            viewApprovedPhrases();
            break;

        // Approve phrases option
        case 6:
            approvePhrases();
            break;

        // Unknown option
        default:
            System.out.println( "Invalid option parsed!" );
            exit();
        }
    }

    /**
     * This method handles login user logic.
     * 
     * @return True if operation successed.
     *         False if operation failed.
     */
    private boolean login() {
        printHeader( "Login" );

        // Flag used to detect errors in loop
        boolean isError = false;

        // Max number of login tries
        int numOfTries = 5;

        // Temporary objects to store input
        User user = new User();
        do {
            // Decrease number of tries
            numOfTries--;

            user = new User();

            // Capture registration information
            System.out.print( "Enter email: " );
            user.setEmail( input.nextLine().trim().toUpperCase() );
            System.out.print( "Enter password: " );
            user.setPasswordString( input.nextLine().trim() );

            // Validate login information
            List<String> errors = app.validateLogin( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // Authenticate user
            errors = app.login( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // User info is valid
            isError = false;
        }
        while ( isError && numOfTries >= 0 );

        // Check if number of tries exhausted
        if ( numOfTries < 0 ) {
            printErrorMsgs( Arrays.asList( "Number of login attempts exhausted!" ) );
            return false;
        }

        return true;
    }

    /**
     * This method handles register user logic.
     * 
     * @return True if operation successed.
     *         False if operation failed.
     */
    private boolean register() {
        printHeader( "Register" );

        // Flag used to detect errors in loop
        boolean isError = false;

        // Max number of login tries
        int numOfTries = 5;

        // Temporary objects to store input
        User user = new User();
        do {
            // Decrease number of tries
            numOfTries--;
            user = new User();

            // Capture registration information
            System.out.print( "Enter first name: " );
            user.setFirstName( input.nextLine().trim().toUpperCase() );
            System.out.print( "Enter last name: " );
            user.setLastName( input.nextLine().trim().toUpperCase() );
            System.out.print( "Enter email: " );
            user.setEmail( input.nextLine().trim().toUpperCase() );
            System.out.print( "Enter password: " );
            user.setPasswordString( input.nextLine().trim() );
            System.out.print( "Enter company name: " );
            user.getCompany().setName( input.nextLine().trim().toUpperCase() );
            System.out.print( "Are you an admin?(y/n): " );
            String isAdmin = input.nextLine().trim();
            user.setIsAdmin( isAdmin.equals( "y" ) );

            // Validate user information
            List<String> errors = app.validateUser( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // Validate company information
            errors = app.validateCompany( user.getCompany() );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // Register user
            errors = app.register( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // User info is valid
            isError = false;
        }
        while ( isError && numOfTries >= 0 );

        // Check if number of tries exhausted
        if ( numOfTries < 0 ) {
            printErrorMsgs( Arrays.asList( "Number of register attempts exhausted!" ) );
            return false;
        }

        return true;
    }

    /**
     * This method logs out the current user and
     * returns to start menu.
     */
    private void logout() {
        app.logout();
        isUserMenuRunning = false;
    }

    /**
     * This method displays possibles phrases built from a set of words.
     * It then has the user select zero or more candidate phrases to be
     * approved later.
     * 
     * @param words       A Set of Word
     * @param phoneNumber A PhoneNumber object
     * @return A Set of String representing the user's
     *         selected phrases
     */
    private Set<String> selectPhrases( Set<Word> words, PhoneNumber phoneNumber ) {
        // Set to store chosen phrases
        Set<String> phraseChoices = new HashSet<>();

        // Display words
        List<Word> wordsList = words.stream().collect( Collectors.toList() );
        boolean isDone = false;
        while ( !isDone ) {
            displayWords( wordsList );
            System.out.print( "Enter a word or 0 to cancel: " );
            String selection = input.nextLine().trim().toUpperCase();
            if ( selection.equals( "0" ) ) {
                break;
            }
            if ( !words.contains( new Word( selection ) ) ) {
                printErrorMsgs( Arrays.asList( "Word not found. Try again" ) );
                continue;
            }

            // Find selected Word object
            Word selectedWord = words.stream().filter( x -> x.getWord().equals( selection ) ).findFirst().get();

            // Build possible phrases that include selected word
            List<List<Word>> phrases = app.generatePhrases( selectedWord, words );
            List<String> numericPhrases = app.generateNumericPhrases( phrases, phoneNumber.getPhoneNumber() );

            // Display phrases
            displayPhrases( numericPhrases );

            // Prompt user to select phrase(s)
            boolean isError = true;
            while ( isError ) {
                System.out.print( "Enter indices of desired phrases or 0 to cancel: " );

                // Read and parse input
                String inputString = input.nextLine().trim();
                String[] inputArr = inputString.split( " " );
                Set<Integer> phraseChoicesIndices = new TreeSet<>();

                // Validate input
                isError = false;
                int index = -1;
                for ( int j = 0; j < inputArr.length; j++ ) {
                    index = Integer.valueOf( inputArr[j] ) - 1;

                    // Check if zero is found
                    if ( index == -1 && inputArr.length > 1 ) {
                        printErrorMsgs( Arrays.asList( "Cannot select 0 and other indices" ) );
                        isError = true;
                        break;
                    }

                    // Check index boundary
                    if ( index < -1 || index >= phrases.size() ) {
                        printErrorMsgs( Arrays.asList( "Index " + ( index + 1 ) + " is out of bounds" ) );
                        isError = true;
                        break;
                    }

                    // Store choice
                    phraseChoicesIndices.add( index );
                }

                // If error was found in input then restart while loop
                if ( isError ) {
                    continue;
                }

                // If user entered 0, then break out of while loop
                if ( index == -1 ) {
                    break;
                }

                // Retrieve phrases from chosen indices
                for ( int i : phraseChoicesIndices ) {
                    phraseChoices.add( numericPhrases.get( i ) );
                }
            }

            // Prompt user for different phrase selection
            System.out.println( "Would you like to select phrases from a different word? (y/n): " );
            String againSelection = input.nextLine().trim().toUpperCase();
            if ( !againSelection.equals( "Y" ) ) {
                isDone = true;
            }
        }
        return phraseChoices;
    }

    /**
     * This method displays a list of generates words from a
     * given phone number. It then calls support method selectPhrases()
     * to display possible phrase combinations built from these
     * words.
     */
    private void generateWords() {
        printHeader( "Generate Words" );

        // Prompt for phone number
        String phoneNumberStr = promptPhoneNumber();
        if ( phoneNumberStr == null ) {
            return;
        }

        // Check that given phone number does not belong to
        // another company
        // ** May use a set instead of list **
        Set<String> phoneNumbers = app.getNonUsablePhoneNumbers( app.getCurrentUser().getCompany().getId() );
        if ( phoneNumbers.contains( phoneNumberStr ) ) {
            printErrorMsgs( Arrays.asList( "Phone number already belongs to another company" ) );
            return;
        }

        // Set the current user's company as owner of given phone number
        PhoneNumber phoneNumber = app.getCurrentUser().getCompany().getPhoneNumber( phoneNumberStr );
        if ( phoneNumber == null ) {
            phoneNumber = new PhoneNumber( phoneNumberStr );
            int id = app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany().getId() );
            if ( id == 0 ) {
                printErrorMsgs( Arrays.asList(
                        "Failure to create link between entered phone number and "
                                + app.getCurrentUser().getCompany().getName() ) );
                return;
            }
            phoneNumber.setId( id );
        }

        // Generate words
        Set<Word> words = app.generateWords( phoneNumber );

        // Select phrases from generated words
        Set<String> phraseChoices = selectPhrases( words, phoneNumber );

        // If an existing phone number has a phrase already approved,
        // disapprove the phrase flag, since new candidate phrases
        // were generated
        phoneNumber.setIsApproved( false );

        // Display phrases chosen
        System.out.println( "You've chosen: " );
        displayPhrases( phraseChoices.stream().toList() );

        // Persist changes
        app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany().getId() );
        app.storePhrases( phraseChoices, phoneNumber );

        // Block until user press enter
        blockUntilEnter();
    }

    /**
     * This method displays the phone numbers associated
     * with the current user's company.
     */
    private void viewPhoneNumbers() {
        printHeader( "View Phone Numbers" );

        // Set number of columns to display
        double columnsToDisplay = 2.0;

        // Retrieve phone numbers for a given company
        List<PhoneNumber> phoneNumbers = app.getCurrentUser().getCompany().getPhoneNumbers().stream().toList();
        if ( phoneNumbers == null ) {
            printErrorMsgs( Arrays.asList( "Could not retrieve phone numbers. Try again!" ) );
            return;
        }

        // Format output
        for ( int i = 0, c = 0; i < Math.ceil( phoneNumbers.size() / columnsToDisplay ); i++ ) {
            for ( int j = 0; j < columnsToDisplay && c < phoneNumbers.size(); j++, c++ ) {
                String phoneNumber = phoneNumbers.get( c ).getPhoneNumber();
                StringBuilder sb = new StringBuilder();
                sb.append( '(' );
                sb.append( phoneNumber.substring( 0, 3 ) );
                sb.append( ") " );
                sb.append( phoneNumber.substring( 3, 6 ) );
                sb.append( '-' );
                sb.append( phoneNumber.substring( 6, 10 ) );
                System.out.printf( "%s\t\t", sb.toString() );
            }
            System.out.println();
        }

        // Block until user press enter
        blockUntilEnter();
    }

    /**
     * This method approves selected words. Can only
     * be accessed by an admin.
     */
    private void approvePhrases() {
        printHeader( "Approve Phrases" );
        List<String> selectedPhrase = new LinkedList<>();
        int lineno = 1;
        boolean done = true;
        // Prompt for phone number
        String phoneString = promptPhoneNumber();
        if ( phoneString == null ) {
            return;
        }

        // Check that given phone number belongs to current's
        // user company
        PhoneNumber phoneNumber = app.getCurrentUser().getCompany().getPhoneNumber( phoneString );
        if ( phoneNumber == null ) {
            printErrorMsgs( Arrays.asList( "Phone number does not belong to " + app.getCurrentUser().getCompany().getName() ) );
            return;
        }

        // Display candidate phrases
        String[] temp = new String[phoneNumber.getPhrases().size()];
        for ( String phrase : phoneNumber.getPhrases() ) {
            System.out.printf( "%-4d %s\n", lineno, phrase );
            temp[lineno - 1] = phrase;
            lineno++;
        }

        // Prompt user to select one phrase
        while ( done ) {
            System.out.println( "Enter the line number of the Phrase you would like to approve: " );
            int choice = input.nextInt();
            input.nextLine();
            if ( choice < 1 || choice > phoneNumber.getPhrases().size() ) {
                System.out.printf( "Enter a valid number, between 1 and %d\n", phoneNumber.getPhrases().size() );
            }
            else {
                selectedPhrase.add( temp[choice - 1] );
                done = false;

            }
        }
        System.out.print( "You've chosen: " + selectedPhrase.get( 0 ) );

        // Set flag to show phone number has an approved phrase
        phoneNumber.setIsApproved( true );

        // Persist changes
        app.deletePhrases( phoneNumber.getPhrases() );
        app.storePhrases( selectedPhrase, phoneNumber );
        app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany().getId() );

        blockUntilEnter();
    }

    /**
     * This method displays approved phrases for
     * the current user's company phone numbers.
     */
    private void viewApprovedPhrases() {
        printHeader( "View Approved Phrases" );

        // Get set of phone numbers that have an approved phrase
        // from the current user' company
        Set<PhoneNumber> phoneNumbers = app.getCurrentUser().getCompany().getApprovedPhoneNumbers();

        // If set is empty display message
        if ( phoneNumbers.size() == 0 ) {
            printErrorMsgs( Arrays.asList( "No phone numbers with approved phrases!" ) );
            return;
        }

        // Display phone number along with approved phrase
        // for each item
        System.out.printf( "%-25S%-15S\n", "Phone Number", "Phrase" );
        System.out.println( "-".repeat( 40 ) );
        for ( PhoneNumber phoneNumber : phoneNumbers ) {
            StringBuilder sb = new StringBuilder();
            sb.append( '(' );
            sb.append( phoneNumber.getPhoneNumber().substring( 0, 3 ) );
            sb.append( ") " );
            sb.append( phoneNumber.getPhoneNumber().substring( 3, 6 ) );
            sb.append( '-' );
            sb.append( phoneNumber.getPhoneNumber().substring( 6, 10 ) );
            System.out.printf( "%-25S%-15S\n", sb.toString(),
                    phoneNumber.getPhrases().stream().toList().get( 0 ) );
        }

        blockUntilEnter();
    }

    /**
     * This method prompts user for a phone number
     * and returns it to caller.
     * 
     * Returns:
     * A String if phone number is valid.
     * Null otherwise.
     */
    private String promptPhoneNumber() {

        // Prompt for phone number
        System.out.print( "Enter phone number: " );
        String phoneNumber = null;

        try {
            phoneNumber = input.nextLine().trim();
        }
        catch ( Exception ex ) {
            printErrorMsgs( Arrays.asList( "No input found" ) );
            return null;
        }

        // Validate phone number
        List<String> errors = app.validatePhoneNumber( phoneNumber );
        if ( errors.size() > 0 ) {
            printErrorMsgs( errors );
            return null;
        }

        return phoneNumber;
    }

    /**
     * This method displays a list of words.
     */
    private void displayWords( List<Word> words ) {
        int cols = 4;
        for ( int i = 0; i < words.size(); ) {
            for ( int j = 0; j < cols && i < words.size(); j++, i++ ) {
                System.out.printf( "%-15s", words.get( i ).getWord() );
            }
            System.out.println();
        }
    }

    /**
     * This method displays a list of phrases.
     */
    public void displayPhrases( List<String> phrases ) {
        for ( int i = 0; i < phrases.size(); i++ ) {
            System.out.printf( "%-6s%s\n", i + 1 + ":", phrases.get( i ) );
        }
    }

    /**
     * This method blocks execution until Enter
     * key is pressed.
     */
    private void blockUntilEnter() {
        System.out.println( "\nPress Enter to continue..." );
        input.nextLine();
    }

    /**
     * This method prints a list of error messages to console.
     */
    private void printErrorMsgs( List<String> errors ) {
        for ( String msg : errors ) {
            System.out.println( msg );
        }
    }

    /**
     * This method ensures a value is within given a range.
     */
    private boolean isValidRange( int n, int lo, int hi ) {
        return lo > hi ? n >= hi && n <= lo : n >= lo && n <= hi;
    }

    /**
     * This method prints a header.
     */
    private void printHeader( String title ) {
        System.out.println( "\n========== " + title + " ==========" );
    }

    /**
     * This method gracefully exits application.
     */
    private void exit() {
        input.close();
        System.out.println( "Exiting..." );
        System.exit( 0 );
    }

}