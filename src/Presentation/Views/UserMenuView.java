package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

/**
 * UserMenuView is the class responsible for displaying
 * the user menu view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class UserMenuView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 600;
    private final int HEIGHT = 300;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel();
    JButton generatePhrasesBtn = new JButton( "Generate Phrases" );
    JButton viewPhoneNumbersBtn = new JButton( "View Phone Numbers" );
    JButton viewPhrasesBtn = new JButton( "View Approved Phrases" );
    JButton approvePhrasesBtn = new JButton( "Approve Phrases" );
    JButton logoutBtn = new JButton( "Logout" );
    JButton accountBtn = new JButton( "Account" );

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public UserMenuView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.add( logoutBtn );
        btnPanel.add( accountBtn );

        // Panel used to store menu option components
        JPanel optionsPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        optionsPanel.setMinimumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 4 ) );
        optionsPanel.add( generatePhrasesBtn );
        optionsPanel.add( viewPhoneNumbersBtn );
        optionsPanel.add( viewPhrasesBtn );
        optionsPanel.add( approvePhrasesBtn );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( optionsPanel );

        // Setup header component
        header.setText( "Welcome " + app.getCurrentUser().getFirstName() + "!" );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );

        // Line separating menu options and bottom buttons
        JSeparator separator = new JSeparator();
        separator.setMaximumSize( new Dimension( WIDTH, 1 ) );

        // Panel used to store all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( Box.createRigidArea( new Dimension( WIDTH, 20 ) ) );
        mainPanel.add( separator );
        mainPanel.add( Box.createRigidArea( new Dimension( WIDTH, 10 ) ) );
        mainPanel.add( btnPanel );

        // Add needed listeners
        addListeners();

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
        logoutBtn.addActionListener( x -> onLogoutClick() );
        accountBtn.addActionListener( x -> onAccountClick() );
        generatePhrasesBtn.addActionListener( x -> onGeneratePhrasesClick() );
        viewPhoneNumbersBtn.addActionListener( x -> onViewPhoneNumbersClick() );
        viewPhrasesBtn.addActionListener( x -> onViewApprovedPhrasesClick() );
        approvePhrasesBtn.addActionListener( x -> onApprovePhrasesClick() );
    }

    /**
     * This method is used to perform
     * initialization logic.
     */
    private void onInit() {
        app.updateState();
        approvePhrasesBtn.setEnabled( app.getCurrentUser().getIsAdmin() );
    }

    /**
     * This method is used to perform
     * logic when logout button is clicked.
     */
    private void onLogoutClick() {
        app.logout();
        frame.getContentPane().removeAll();
        StartMenuView smv = new StartMenuView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when account button is clicked.
     */
    private void onAccountClick() {
        frame.getContentPane().removeAll();
        AccountView av = new AccountView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when generate phrases button
     * is clicked.
     */
    private void onGeneratePhrasesClick() {
        frame.getContentPane().removeAll();
        GenerateView gv = new GenerateView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when view phone numbers button
     * is clicked.
     */
    private void onViewPhoneNumbersClick() {
        frame.getContentPane().removeAll();
        PhonesView pv = new PhonesView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when view approved phrases button
     * is clicked.
     */
    private void onViewApprovedPhrasesClick() {
        frame.getContentPane().removeAll();
        ApprovedPhrasesView apv = new ApprovedPhrasesView( frame, app );
    }

    /**
     * This method is used to perform
     * logic when approve phrases button
     * is clicked.
     */
    private void onApprovePhrasesClick() {
        frame.getContentPane().removeAll();
        ApproveView av = new ApproveView( frame, app );
    }
}
