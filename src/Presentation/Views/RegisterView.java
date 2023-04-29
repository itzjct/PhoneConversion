package Presentation.Views;

import java.util.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

/**
 * RegisterView is the class responsible for displaying
 * the user register view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class RegisterView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 400;
    private final int HEIGHT = 375;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Register" );
    JLabel firstNameLb = new JLabel( "First Name:" );
    JLabel lastNameLb = new JLabel( "Last Name:" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel passwordLb = new JLabel( "Password:" );
    JLabel passwordConfirmLb = new JLabel( "Confirm Password:" );
    JLabel companyLb = new JLabel( "Company:" );
    JLabel roleLb = new JLabel( "Role:" );
    JTextField firstNameTxt = new JTextField();
    JTextField lastNameTxt = new JTextField();
    JTextField emailTxt = new JTextField();
    JPasswordField passwordTxt = new JPasswordField();
    JPasswordField passwordConfirmTxt = new JPasswordField();
    JTextField companyTxt = new JTextField();
    ButtonGroup roleGroup = new ButtonGroup();
    JRadioButton normalRb = new JRadioButton( "Normal" );
    JRadioButton adminRb = new JRadioButton( "Admin" );
    JButton backBtn = new JButton( "Back" );
    JButton submitBtn = new JButton( "Submit" );

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public RegisterView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Add normal and admin radio buttons to role group
        roleGroup.add( normalRb );
        roleGroup.add( adminRb );

        // Panel used to store radio buttons
        JPanel rolePanel = new JPanel( new GridLayout( 0, 2 ) );
        rolePanel.add( normalRb );
        rolePanel.add( adminRb );

        // Center back and submit buttons
        backBtn.setAlignmentX( Component.CENTER_ALIGNMENT );
        submitBtn.setAlignmentX( Component.CENTER_ALIGNMENT );

        // Panel used to store bottom buttons
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.add( backBtn );
        btnPanel.add( submitBtn );

        // Panel used to store form components
        JPanel formPanel = new JPanel( new GridLayout( 7, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 7 ) );
        formPanel.add( firstNameLb );
        formPanel.add( firstNameTxt );
        formPanel.add( lastNameLb );
        formPanel.add( lastNameTxt );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( passwordLb );
        formPanel.add( passwordTxt );
        formPanel.add( passwordConfirmLb );
        formPanel.add( passwordConfirmTxt );
        formPanel.add( companyLb );
        formPanel.add( companyTxt );
        formPanel.add( roleLb );
        formPanel.add( rolePanel );

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
        if ( register() ) {
            frame.getContentPane().removeAll();
            UserMenuView umv = new UserMenuView( frame, app );
        }
    }

    /**
     * This method is used to perform register
     * related logic.
     * 
     * @return True if operation succeeded.
     *         False otherwise.
     */
    private boolean register() {
        List<String> errors = new LinkedList<>();

        // Retrieve role
        String role = getRole();
        if ( role == null ) {
            errors.add( "Role cannot be empty" );
            displayErrorMessages( errors );
            return false;
        }

        // Retrieve user and company info
        User user = new User();
        user.setFirstName( firstNameTxt.getText().toUpperCase().trim() );
        user.setLastName( lastNameTxt.getText().toUpperCase().trim() );
        user.setEmail( emailTxt.getText().toUpperCase().trim() );
        user.setPasswordString( String.valueOf( passwordTxt.getPassword() ) );
        user.setIsAdmin( role.equals( "Admin" ) );
        user.getCompany().setName( companyTxt.getText().toUpperCase().trim() );

        // Validate user info
        errors = app.validateUser( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        // Validate company info
        errors = app.validateCompany( user.getCompany() );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        // Check password and confirm password
        // fields match
        if ( !Arrays.equals( passwordTxt.getPassword(), passwordConfirmTxt.getPassword() ) ) {
            displayErrorMessages( Arrays.asList( "Passwords do not match" ) );
            return false;
        }

        // Register user
        errors = app.register( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        return true;
    }

    /**
     * This method retrieves role value from
     * role radio buttons.
     * 
     * @return A String.
     */
    private String getRole() {
        for ( Enumeration<AbstractButton> buttons = roleGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();

            if ( button.isSelected() ) {
                return button.getText();
            }
        }

        return null;
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
