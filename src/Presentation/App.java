package Presentation;

import java.util.*;
import Application.Domain.*;
import Application.Services.*;

public class App {

    private AppService appService;
    private User currentUser;
    private String selectedPhoneNumber;
    private Scanner input;
    boolean isStartLoopRunning = true;
    boolean isUserLoopRunning = true;

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
        while ( isStartLoopRunning ) {

            // Select a start option
            int selectedStartOption = selectStartOption();

            // Execute the selected start option
            execStartOption( selectedStartOption );

            // Loop to handle user options
            isUserLoopRunning = true;
            while ( isUserLoopRunning ) {

                // Select a user option
                int selectedUserOption = selectUserOptions();

                // Execute the selected user option
                execUserOption( selectedUserOption );
            }
        }

        // Close scanner
        input.close();
    }

    /*
     * This method handles logic to allow the selection
     * of a start option
     */
    private int selectStartOption() {

        // Number of options presented to user (may change in future)
        int numOfOptions = 3;

        // Store selected option as int
        int selectedOption = 0;

        // Flag used to control continuation of print start options loop
        boolean isError = false;

        // Loop to display and read start options
        do {

            // Print start options
            printStartOptions();

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
        case 1:
            while ( login() ) {
            }
            break;
        case 2:
            while ( register() ) {
            }
            break;
        case 3:
            exit();
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

            // If company does not exist in db then add it
            boolean isCompanyExist = appService.existsCompany( user.getCompany().getName() );
            if ( !isCompanyExist ) {
                int companyId = appService.storeCompany( user.getCompany() );
                user.getCompany().setId( companyId );
            }

            // Add user
            user.setId( appService.storeUser( user ) );
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

    public int selectUserOptions() {

        // Print greetings
        System.out.println( "\nWelcome " + currentUser.getFirstName() + "!" );

        // Number of options presented to user (may change in future)
        int numOfOptions = currentUser.isIsAdmin() ? 5 : 4;

        // Store selected option as int
        int selectedOption = 0;

        // Flag used to control continuation of print user options loop
        boolean isError = false;

        // Loop to display and read start options
        do {

            // Print start options
            printUserOptions();

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
        case 1:
            exit();
        case 2:
            logout();
            break;
        case 3:
            while ( generateWords() ) {
            }
            break;
        case 4:
            while ( viewPhoneNumbers() ) {
            }
            break;
        case 5:
            while ( approveWords() ) {
            }
            break;
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
        isUserLoopRunning = false;
    }

    private boolean generateWords() {
        printHeader( "Generate Words" );
        return false;
    }

    private boolean viewPhoneNumbers() {
        printHeader( "View Phone Numbers" );
        return false;
    }

    private boolean approveWords() {
        printHeader( "Approve Words" );
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
