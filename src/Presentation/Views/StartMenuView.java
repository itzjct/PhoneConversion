package Presentation.Views;

import Application.Domain.App;
import Presentation.AppGUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * StartMenuView is the class responsible for displaying
 * the user menu view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class StartMenuView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 400;
    private final int HEIGHT = 190;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Phone Conversion" );
    JButton loginBtn = new JButton( "Login" );
    JButton registerBtn = new JButton( "Register" );
    JLabel messageLb = new JLabel( "Select an option to begin:" );

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public StartMenuView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Center login and register buttons
        loginBtn.setAlignmentX( Container.CENTER_ALIGNMENT );
        registerBtn.setAlignmentX( Container.CENTER_ALIGNMENT );

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( loginBtn );
        btnPanel.add( registerBtn );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( messageLb );

        // Setup header component
        header.setFont( new Font( "Arial", Font.BOLD, 20 ) );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );
        header.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );

        // Panel used to hold header/title components
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout( new BoxLayout( headerPanel, BoxLayout.Y_AXIS ) );
        headerPanel.setMaximumSize( new Dimension( WIDTH, 50 ) );
        headerPanel.add( new JSeparator() );
        headerPanel.add( header );
        headerPanel.add( new JSeparator() );

        // Panel used to store all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( headerPanel );
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
        registerBtn.addActionListener( x -> onRegisterClick() );
        loginBtn.addActionListener( x -> onLoginClick() );
    }

    /**
     * This method is used to perform
     * logic when register button is clicked.
     */
    private void onRegisterClick() {
        frame.getContentPane().removeAll();
        RegisterView registerView = new RegisterView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when login button is clicked.
     */
    private void onLoginClick() {
        frame.getContentPane().removeAll();
        LoginView loginView = new LoginView( frame, app );
    }
}
