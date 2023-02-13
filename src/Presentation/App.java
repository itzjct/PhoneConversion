package Presentation;

import java.util.*;
import Application.Domain.*;
import Application.Services.*;

public class App {

    private AppService appService;
    private User currentUser;
    private String selectedPhoneNumber;
    private Scanner input;

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

        // Flag used to stop execution
        boolean isRunning = true;

        // Loop to handle start options
        while ( isRunning ) {

            // Select a start option
            int selectedOption = selectStartOption();

            // Execute the selected option
            isRunning = execStartOption( selectedOption );
            if ( !isRunning ) {
                System.out.println( "Exiting..." );
                continue;
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

        // Initial message
        String message = "Welcome!:";

        // Number of options presented to user (may change in future)
        int numOfOptions = 3;

        // Store selected option as int
        int selectedOption = 0;

        // Flag used to control continuation of print start options loop
        boolean isError = false;

        // Loop to display and read start options
        do {

            // Print start options
            printStartOptions( message );

            try {

                // Read and parse input
                String unparsedOption = input.nextLine();
                selectedOption = Integer.parseInt( unparsedOption );

                // Ensure input is valid
                if ( !isValidRange( selectedOption, 1, numOfOptions ) ) {
                    message = selectedOption + " is not an option!";
                    isError = true;
                }

                // Selected option is valid, exit loop
                else {
                    isError = false;
                }
            }

            // Error parsing input to int
            catch ( NumberFormatException ex ) {
                message = "Invalid input entered!";
                isError = true;
            }

            // Any other errors
            catch ( Exception ex ) {
                System.out.println( ex.getMessage() ); // remove
                message = "Something went wrong. Try again!";
                isError = true;
            }
        }
        while ( isError );

        return selectedOption;
    }

    /*
     * This method displays the start options
     */
    private void printStartOptions( String message ) {
        System.out.println( message );
        System.out.println( "Enter the corresponding option number:" );
        System.out.println( "1: Login" );
        System.out.println( "2: Register" );
        System.out.println( "3: Exit" );
    }

    /*
     * This method executes the given start option
     */
    private boolean execStartOption( int option ) {
        switch ( option ) {
        case 1:
            System.out.println( "Login chosen" );
            currentUser = login();
            return currentUser != null;
        case 2:
            System.out.println( "Register chosen" );
            currentUser = register();
            return currentUser != null;
        case 3:
            return false;
        default:
            System.out.println( "Invalid option parsed!" );
            return false;
        }
    }

    // This method ensures a value is within given a range
    private boolean isValidRange( int n, int lo, int hi ) {
        if ( lo > hi ) {
            return n >= hi && n <= lo;
        }
        return n >= lo && n <= hi;
    }

    private User login() {
        return null;
    }

    /*
     * This method handles register user logic
     */
    private User register() {

        boolean isError = false;
        User user = new User();
        do {
            // Temporary objects to store input
            user = new User();

            // Capture registration information
            System.out.print( "Enter first name: " );
            user.setFirstName( input.nextLine().trim() );
            System.out.print( "Enter last name: " );
            user.setLastName( input.nextLine().trim() );
            System.out.print( "Enter email: " );
            user.setEmail( input.nextLine().trim() );
            System.out.print( "Enter password: " );
            user.setPasswordString( input.nextLine().trim() );
            System.out.print( "Enter company name: " );
            user.getCompany().setName( input.nextLine().trim() );
            System.out.print( "Are you an admin?(y/n): " );
            String isAdmin = input.nextLine().trim();
            user.setIsAdmin( isAdmin.equals( "y" ) );

            // Validate user information
            LinkedList<String> errors = appService.validateUser( user );
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
            return null;
        }

        // Clear user's text password (for security)
        user.setPasswordString( null );

        // Submit user to db
        try {
            user.setId( appService.storeUser( user ) );
        }
        catch ( Exception ex ) {
            System.out.println( "Error submitting user to database" );

            // If db submission failed then return null
            return null;
        }

        return user;
    }

    /*
     * This method prints a list of error messages to console
     */
    private void printErrorMsgs( LinkedList<String> errors ) {
        for ( String msg : errors ) {
            System.out.println( msg );
        }
    }
}
