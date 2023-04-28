package Presentation.Views;

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
 * AccountView is the class responsible for displaying
 * the user account view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class AccountView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 600;
    private final int HEIGHT = 270;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Account Info" );
    JLabel nameLb = new JLabel( "Name:" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel companyLb = new JLabel( "Company:" );
    JLabel roleLb = new JLabel( "Role:" );
    JTextField nameTxt = new JTextField();
    JTextField emailTxt = new JTextField();
    JTextField companyTxt = new JTextField();
    JTextField roleTxt = new JTextField();
    JButton backBtn = new JButton( "Back" );

    /**
     * This constructor is used to load components
     * and logic related to account view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public AccountView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Setup text fields to read-only
        nameTxt.setEditable( false );
        emailTxt.setEditable( false );
        companyTxt.setEditable( false );
        roleTxt.setEditable( false );

        // Panel used to store form components
        JPanel formPanel = new JPanel( new GridLayout( 4, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 4 ) );
        formPanel.add( nameLb );
        formPanel.add( nameTxt );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( companyLb );
        formPanel.add( companyTxt );
        formPanel.add( roleLb );
        formPanel.add( roleTxt );

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        // Panel used to hold main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( formPanel );

        // Setup header component
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        // Panel used to hold all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        // Perform initialization logic
        onInit();

        // Set up view dimensions
        frame.setSize( WIDTH, HEIGHT );
    }

    /**
     * This method is responsible for adding
     * listeners to needed components.
     */
    private void addListeners() {
        backBtn.addActionListener( x -> onBackClick() );
    }

    /**
     * This method is used to perform
     * initialization logic.
     */
    private void onInit() {
        addListeners();
        populateTextFields();
    }

    /**
     * This method is used to perform
     * logic when back button is clicked.
     */
    private void onBackClick() {
        frame.getContentPane().removeAll();
        UserMenuView umv = new UserMenuView( frame, app );
    }

    /**
     * This method is used to populate
     * text fields displaying account info.
     */
    private void populateTextFields() {
        User user = app.getCurrentUser();
        nameTxt.setText( user.getFirstName() + " " + user.getLastName() );
        emailTxt.setText( user.getEmail() );
        companyTxt.setText( user.getCompany().getName() );
        roleTxt.setText( user.getIsAdmin() ? "Admin" : "Normal" );
    }
}
