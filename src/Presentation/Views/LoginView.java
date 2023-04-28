package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

/**
 * LoginView is the class responsible for displaying
 * the user login view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class LoginView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 400;
    private final int HEIGHT = 200;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Login" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel passwordLb = new JLabel( "Password:" );
    JTextField emailTxt = new JTextField();
    JPasswordField passwordTxt = new JPasswordField();
    JButton backBtn = new JButton( "Back" );
    JButton submitBtn = new JButton( "Submit" );

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public LoginView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Center back and submit buttons
        backBtn.setAlignmentX( Component.CENTER_ALIGNMENT );
        submitBtn.setAlignmentX( Component.CENTER_ALIGNMENT );

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( submitBtn );

        // Panel used to store form components
        JPanel formPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 2 ) );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( passwordLb );
        formPanel.add( passwordTxt );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( formPanel );

        // Setup header component
        header.setAlignmentX( Container.CENTER_ALIGNMENT );
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );

        // Hide error panel initally
        errorPanel.setVisible( false );

        // Panel used to store all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( errorPanel );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        // Add needed listeners
        addListeners();

        // Set up view dimensions
        frame.setSize( WIDTH, HEIGHT );
    }

    /**
     * This method is responsible for adding
     * listeners to needed components.
     */
    private void addListeners() {
        backBtn.addActionListener( x -> onBackClick() );
        submitBtn.addActionListener( x -> onSubmitClick() );
    }

    /**
     * This method is used to perform
     * logic when back button is clicked.
     */
    private void onBackClick() {
        frame.getContentPane().removeAll();
        StartMenuView startMenu = new StartMenuView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when submit button is clicked.
     */
    private void onSubmitClick() {
        clearErrorMessages();
        if ( login() ) {
            frame.getContentPane().removeAll();
            UserMenuView umv = new UserMenuView( frame, app );
        }
    }

    /**
     * This method is used to perform login
     * related logic.
     * 
     * @return True if operation succeeded.
     *         False otherwise.
     */
    private boolean login() {

        // Retrieve email and password strings
        User user = new User();
        user.setEmail( emailTxt.getText().toUpperCase().trim() );
        user.setPasswordString( String.valueOf( passwordTxt.getPassword() ).trim() );

        // Validate login info
        List<String> errors = app.validateLogin( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        // Login user
        errors = app.login( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        return true;
    }

    /**
     * This method is used to display errors within
     * view.
     * 
     * @param errors A List of String.
     */
    private void displayErrorMessages( List<String> errors ) {
        for ( String error : errors ) {
            JLabel label = new JLabel( error );
            label.setForeground( Color.RED );
            label.setHorizontalAlignment( JLabel.CENTER );
            errorPanel.add( label );
        }

        errorPanel.setMaximumSize( new Dimension( WIDTH, errors.size() * 25 ) );
        frame.setSize( WIDTH, HEIGHT + errors.size() * 25 );
        errorPanel.setVisible( true );
    }

    /**
     * This method is used to clear displayed
     * error messages.
     */
    private void clearErrorMessages() {
        errorPanel.removeAll();
        errorPanel.setVisible( false );
        frame.setSize( WIDTH, HEIGHT );
    }
}
