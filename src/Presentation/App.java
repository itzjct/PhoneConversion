package Presentation;

import java.util.*;
import Application.Domain.*;
import Application.Services.*;

public class App {

    private AppService appService;
    private User currentUser;
    private String selectedPhoneNumber;
    private Scanner input;
    private boolean isStartMenuRunning = true;
    private boolean isUserMenuRunning = true;

    public static void main( String[] args ) {

        // Run app
        App app = new App();
        app.start();
    }

    public App() {
        appService = new AppService();
        input = new Scanner( System.in );
    }

    /*
     * This method handles the logic to start and monitor the
     * execution of the application
     */
    public void start() {

        // Loop to handle start options
        while ( isStartMenuRunning ) {

            // Select a start option
            int selectedStartOption = selectOption( false );

            // Execute the selected start option
            execStartOption( selectedStartOption );

            // Loop to handle user options
            isUserMenuRunning = true;
            while ( isUserMenuRunning ) {

                // Select a user option
                int selectedUserOption = selectOption( true );

                // Execute the selected user option
                execUserOption( selectedUserOption );
            }
        }

        // Close scanner
        input.close();
    }

    /*
     * This method handles logic to allow the selection
     * of a menu option
     * 
     * Return: int representing a menu option
     */
    private int selectOption( boolean isUserMenu ) {

        // Number of options presented
        int numOfOptions = 3;

        // Check number of options if user menu and greet user
        if ( isUserMenu ) {
            System.out.println( "\nWelcome " + currentUser.getFirstName() + "!" );
            numOfOptions = currentUser.isIsAdmin() ? 5 : 4;
        }

        // Store selected option as int
        int selectedOption = 0;

        // Flag used to control continuation of print start options loop
        boolean isError = false;

        // Loop to display and read start options
        do {

            // Print menu options
            if ( isUserMenu ) {
                printUserOptions();
            }
            else {
                printStartOptions();
            }

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
     * This method displays the start options
     */
    private void printStartOptions() {
        printHeader( "Start Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Login" );
        System.out.println( "2: Register" );
        System.out.println( "3: Exit" );
    }

    /*
     * This method executes the given start option
     */
    private void execStartOption( int option ) {
        switch ( option ) {

        // Login option
        case 1:
            while ( login() ) {
            }
            break;

        // Register option
        case 2:
            while ( register() ) {
            }
            break;

        // Exit option
        case 3:
            exit();

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
     * True: if error was encountered
     * False: if no errors were encountered
     */
    private boolean login() {
        printHeader( "Login" );

        boolean isError = false;
        User user = new User();
        do {
            // Temporary objects to store input
            user = new User();

            // Capture registration information
            System.out.print( "Enter email: " );
            user.setEmail( input.nextLine().trim().toUpperCase() );
            System.out.print( "Enter password: " );
            user.setPasswordString( input.nextLine().trim() );

            // Validate login information
            List<String> errors = appService.validateLogin( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // Authenticate user
            errors = appService.authenticateUser( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // User info is valid
            isError = false;
        }
        while ( isError );

        // Clear user's text password (for security)
        user.setPasswordString( null );

        // Store current user
        currentUser = user;

        return false;
    }

    /*
     * This method handles register user logic
     * 
     * Returns:
     * True: if error was encountered
     * False: if no errors were encountered
     */
    private boolean register() {
        printHeader( "Register" );

        boolean isError = false;
        User user = new User();
        do {
            // Temporary objects to store input
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
            List<String> errors = appService.validateUser( user );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // Validate company information
            errors = appService.validateCompany( user.getCompany() );
            if ( errors.size() > 0 ) {
                printErrorMsgs( errors );
                isError = true;
                continue;
            }

            // User info is valid
            isError = false;
        }
        while ( isError );

        // Generate password salt and hash password
        user.setPasswordSalt( appService.getSalt() );
        user.setPassword( appService.hashPassword( user.getPasswordString(), user.getPasswordSalt() ) );

        // If password hashing failed then return null
        if ( user.getPassword().length == 0 ) {
            return true;
        }

        // Clear user's text password (for security)
        user.setPasswordString( null );

        // Submit company and user to db
        try {

            // Check if company exists
            int companyId = appService.existsCompany( user.getCompany().getName() );

            // If company does not exist in db then add it
            if ( companyId == 0 ) {
                companyId = appService.storeCompany( user.getCompany() );
            }

            // Create link between company and user
            user.getCompany().setId( companyId );

            // Add user
            user.setId( appService.storeUser( user ) );

            // Load company's data
            user.setCompany( appService.getCompany( companyId ) );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );

            // If db submission failed then return null
            return true;
        }

        // Store current user
        currentUser = user;

        return false;
    }

    /*
     * This method displays the user options
     */
    private void printUserOptions() {
        printHeader( "User Menu" );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Exit" );
        System.out.println( "2: Logout" );
        System.out.println( "3: Generate Words" );
        System.out.println( "4: View Company Phone Numbers" );
        if ( currentUser.getIsAdmin() ) {
            System.out.println( "5: Approve Words" );
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

            // Logout option
        case 2:
            logout();
            break;

        // Generate words option
        case 3:
            while ( generateWords() ) {
            }
            break;

        // View phone numbers option
        case 4:
            while ( viewPhoneNumbers() ) {
            }
            break;

        // Approve words option
        case 5:
            while ( approveWords() ) {
            }
            break;

        // Unknown option
        default:
            System.out.println( "Invalid option parsed!" );
            exit();
        }
    }

    /*
     * This method logs out the current user
     */
    private void logout() {
        currentUser = null;
        isUserMenuRunning = false;
    }

    /*
     * Not implemented
     */
    private boolean generateWords() {
        printHeader( "Generate Words" );
        System.out.println( "NOT IMPLEMENTED" );
        return false;
    }

    /*
     * Not implemented
     */
    private boolean viewPhoneNumbers() {
        printHeader( "View Phone Numbers" );
        System.out.println( "NOT IMPLEMENTED" );
        return false;
    }

    /*
     * Not implemented
     */
    private boolean approveWords() {
        printHeader( "Approve Words" );
        System.out.println( "NOT IMPLEMENTED" );
        return false;
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
        if ( lo > hi ) {
            return n >= hi && n <= lo;
        }
        return n >= lo && n <= hi;
    }

    /*
     * This method prints a header
     */
    private void printHeader( String title ) {
        System.out.println( "========== " + title + "==========" );
    }

    /*
     * This method gracefully exits application
     */
    private void exit() {
        System.out.println( "Exiting..." );
        System.exit( 0 );
    }

}
