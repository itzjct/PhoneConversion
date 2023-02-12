package Presentation;

import java.util.*;
import Application.Domain.*;
import Application.Services.*;

public class App {

    private AppService appService;
    private User currentUser;
    private String selectedPhoneNumber;

    public static void main( String[] args ) {

        // Run app
        App app = new App();
        app.start();
    }

    public App() {
        appService = new AppService();
    }

    /*
     * This method handles the logic to start and monitor the
     * execution of the application
     */
    public void start() {

        // Scanner used to read input from user
        Scanner input = new Scanner( System.in );

        // Flag used to stop execution
        boolean isRunning = true;

        // Loop to handle start options
        while ( isRunning ) {

            // Select a start option
            int selectedOption = selectStartOption( input );

            // Execute the selected option
            isRunning = execStartOption( selectedOption );
        }

        input.close();
    }

    /*
     * This method handles logic to allow the selection
     * of a start option
     */
    private int selectStartOption( Scanner input ) {

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
            // perform login()
            break;
        case 2:
            System.out.println( "Register chosen" );
            // perform register()
            break;
        case 3:
            System.out.println( "Exiting..." );
            return false;
        default:
            System.out.println( "Invalid option parsed! \nExiting..." );
            return false;
        }
        return true;
    }

    // This method ensures a value is within given a range
    private boolean isValidRange( int n, int lo, int hi ) {
        if ( lo > hi ) {
            return n >= hi && n <= lo;
        }
        return n >= lo && n <= hi;
    }
}
