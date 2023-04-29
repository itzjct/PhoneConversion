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
 * PhonesView is the class responsible for displaying
 * the user company's phone numbers view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class PhonesView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Company Phone Numbers" );
    DefaultTableModel phonesModel = new DefaultTableModel();
    JTable phonesTable = new JTable( phonesModel );
    JScrollPane phonesScroll = new JScrollPane( phonesTable );
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
    public PhonesView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( phonesScroll );

        // Setup header component
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        // Panel used to store all other panels
        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
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
        backBtn.addActionListener( x -> onBackClick() );
    }

    /**
     * This method is used to perform
     * initialization logic.
     */
    private void onInit() {
        phoneNumbers = app.getCurrentUser().getCompany().getPhoneNumbers();
        populatePhonesTable();
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
     * phones table.
     */
    private void populatePhonesTable() {
        String[] phoneNumbersArr = phoneNumbers.stream().map( x -> x.toFormattedString() )
                .toArray( String[]::new );
        Arrays.sort( phoneNumbersArr );
        phonesModel.addColumn( "Phone Numbers" );
        for ( String phoneNumber : phoneNumbersArr ) {
            phonesModel.addRow( new Object[] { phoneNumber } );
        }
    }
}
