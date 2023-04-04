package Presentation;

import java.util.*;
import Application.Domain.*;

public class AppGUI {

    public final int START_MENU = 0;
    public final int USER_MENU = 1;
    public final int ADMIN_MENU = 2;

    private App app;
    private Scanner input;
    private boolean isStartMenuRunning;
    private boolean isUserMenuRunning;

    public static void main( String[] args ) {

        // Run app
        AppGUI app = new AppGUI();
        app.start();
    }

    public AppGUI() {
        app = new App();
        input = new Scanner( System.in );
    }

    /*
     * This method handles the logic to start and monitor the
     * execution of the application
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

    /*
     * This method handles logic to allow the selection
     * of a menu option
     * 
     * Return: int representing a menu option
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

    /*
     * This method returns the number of options for
     * a given menu type. Used to determine boundaries
     * when user is selecting an option
     */
    private int getMenuOptionsNumber( int menuType ) {
        switch ( menuType ) {
        // Start menu
        case START_MENU:
            return 3;

        // User menu
        case USER_MENU:
            return 4;

        // User admin menu
        case ADMIN_MENU:
            return 5;

        // Invalid menu
        default:
            System.out.println( "Invalid menu type" );
            return 0;
        }
    }

    /*
     * This method prints out the corresponding menu options
     * from a given menu type
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

    /*
     * This method displays the start menu
     */
    private void printStartMenu() {
        printHeader( "Start Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Exit" );
        System.out.println( "2: Login" );
        System.out.println( "3: Register" );
    }

    /*
     * This method displays the user menu
     */
    private void printUserMenu( boolean isAdmin ) {
        printHeader( "User Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Exit" );
        System.out.println( "2: Logout" );
        System.out.println( "3: Generate Words" );
        System.out.println( "4: View Company Phone Numbers" );
        System.out.println( "5: View Words" );
        if ( isAdmin ) {
            System.out.println( "6: Approve Words" );
        }
    }

    /*
     * This method executes the given start option
     * 
     * Returns:
     * True: if operation successed
     * False: if operation failed
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

    /*
     * This method executes the given user option
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

        // View words option
        case 5:
            viewWords();
            break;

        // Approve words option
        case 6:
            approveWords();
            break;

        // Unknown option
        default:
            System.out.println( "Invalid option parsed!" );
            exit();
        }
    }

    /*
     * This method handles login user logic
     * 
     * Returns:
     * True: if operation successed
     * False: if operation failed
     */
    private boolean login() {
        printHeader( "Login" );

        boolean isError = false;
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

    /*
     * This method handles register user logic
     * 
     * Returns:
     * True: if operation successed
     * False: if operation failed
     */
    private boolean register() {
        printHeader( "Register" );

        boolean isError = false;
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

    /*
     * This method logs out the current user
     */
    private void logout() {
        app.logout();
        isUserMenuRunning = false;
    }

    /*
     * Not implemented
     */
    private void generateWords() {
        printHeader( "Generate Words" );
        System.out.println( "NOT IMPLEMENTED" );

        // Prompt for phone number

        // Validate phone number

        // Generate words
    }

    /*
     * This method displays the phone numbers associated
     * with the current user's company
     */
    private void viewPhoneNumbers() {
        printHeader( "View Phone Numbers" );

        // Set number of columns to display
        double columnsToDisplay = 2.0;

        // Retrieve phone numbers for a given company
        List<String> phoneNumbers = app.getPhoneNumbers( app.getCurrentUser().getCompany() );
        if ( phoneNumbers == null ) {
            printErrorMsgs( Arrays.asList( "Could not retrieve phone numbers. Try again!" ) );
            return;
        }

        // Format output
        for ( int i = 0, c = 0; i < Math.ceil( phoneNumbers.size() / columnsToDisplay ); i++ ) {
            for ( int j = 0; j < columnsToDisplay && c < phoneNumbers.size(); j++, c++ ) {
                String phoneNumber = phoneNumbers.get( c );
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
        System.out.println( "\nPress Enter to continue..." );
        input.nextLine();
    }

    /*
     * This method displays the words associated with a
     * given phone number
     */
    private void viewWords() {
        printHeader( "View Words" );

        // Prompt for phone number
        System.out.print( "Enter phone number: " );
        String phoneNumber = input.nextLine().trim();

        // Validate phone number
        // List<String> errors = app.validatePhoneNumber( phoneNumber );
        // if ( errors.size() > 0 ) {
        //     printErrorMsgs( errors );
        //     return;
        // }

        // Check that given phone number belongs to current's
        // user company
        // ** May use a set instead of list **
        List<String> phoneNumbers = app.getPhoneNumbers( app.getCurrentUser().getCompany() );
        if ( !phoneNumbers.contains( phoneNumber ) ) {
            printErrorMsgs( Arrays.asList( "Phone number does not belong to " + app.getCurrentUser().getCompany().getName() ) );
            return;
        }

        // Retrieve words
        List<Word> words = app.getWords( phoneNumber );
        if ( words.size() == 0 ) {
            printErrorMsgs( Arrays.asList( "No words found" ) );
            return;
        }
        for ( Word word : words ) {
            System.out.println( word.getWord() );
        }

        // Block until user press enter
        System.out.println( "\nPress Enter to continue..." );
        input.nextLine();
    }

    /*
     * Not implemented
     */
    private void approveWords() {
        printHeader( "Approve Words" );
        System.out.println( "NOT IMPLEMENTED" );
    }

    /*
     * This method prints a list of error messages to console
     */
    private void printErrorMsgs( List<String> errors ) {
        for ( String msg : errors ) {
            System.out.println( msg );
        }
    }

    /*
     * This method ensures a value is within given a range
     */
    private boolean isValidRange( int n, int lo, int hi ) {
        return lo > hi ? n >= hi && n <= lo : n >= lo && n <= hi;
    }

    /*
     * This method prints a header
     */
    private void printHeader( String title ) {
        System.out.println( "\n========== " + title + " ==========" );
    }

    /*
     * This method gracefully exits application
     */
    private void exit() {
        input.close();
        System.out.println( "Exiting..." );
        System.exit( 0 );
    }

}
