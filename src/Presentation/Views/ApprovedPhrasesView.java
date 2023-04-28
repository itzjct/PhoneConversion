package Presentation.Views;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

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

public class ApprovedPhrasesView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Approved Phrases" );
    DefaultTableModel phrasesModel = new DefaultTableModel();
    JTable phrasesTable = new JTable( phrasesModel );
    JScrollPane tableScroll = new JScrollPane( phrasesTable );
    JButton backBtn = new JButton( "Back" );

    // Declare and/or instantiate needed domain objects
    // or data structures
    Set<PhoneNumber> phoneNumbers;

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public ApprovedPhrasesView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        // Panel used to hold main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( tableScroll );

        // Setup header component
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        // Hide error panel initally
        errorPanel.setVisible( false );

        // Panel used to hold all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( errorPanel );
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
        getApprovedPhoneNumbers();
        populatePhrasesTable();
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
     * This method populates the phoneNumbers field
     * with the current user's company approved phone
     * numbers.
     */
    private void getApprovedPhoneNumbers() {
        phoneNumbers = app.getCurrentUser().getCompany().getApprovedPhoneNumbers();
    }

    /**
     * This method is used to populate
     * phrases table.
     */
    private void populatePhrasesTable() {
        phrasesModel.addColumn( "Phone Number" );
        phrasesModel.addColumn( "Approved Phrase" );
        for ( PhoneNumber phoneNumber : phoneNumbers ) {
            phrasesModel.addRow( new Object[] { phoneNumber.toFormattedString(), phoneNumber.getPhrases().get( 0 ) } );
        }
    }
}
