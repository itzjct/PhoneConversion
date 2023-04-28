package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import Application.Domain.*;
import Presentation.*;

/**
 * ApprovedView is the class responsible for displaying
 * the user approved phrases view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class ApproveView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Validate Phrases" );
    DefaultTableModel phonesModel = new DefaultTableModel();
    DefaultTableModel phrasesModel = new DefaultTableModel();
    JTable phonesTable = new JTable( phonesModel );
    JTable phrasesTable = new JTable( phrasesModel );
    JScrollPane phonesScroll = new JScrollPane( phonesTable );
    JScrollPane phrasesScroll = new JScrollPane( phrasesTable );
    JButton approveBtn = new JButton( "Approve" );
    JButton backBtn = new JButton( "Back" );
    JButton saveBtn = new JButton( "Save" );

    // Declare and/or instantiate needed domain objects
    // or data structures
    List<String> phrases = new LinkedList<>();
    Set<PhoneNumber> phoneNumbers;
    PhoneNumber selectedPhoneNumber;
    boolean isResetting;

    // Create a list selection listener for the phones table.
    // This allows designated tasks to occur upon selection
    // of a row in the phones table
    ListSelectionListener phonesListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() && !isResetting ) {
                getPhrases();
                populatePhrasesTable();
            }
        }
    };

    /**
     * This constructor is used to load components
     * and logic related to approved phrases view.
     * 
     * @param passedFrame A JFrame object to reutilize between views.
     * @param passedApp   An App object to handle business logic.
     */
    public ApproveView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Panel used to store phones and phrases scroll panes
        JPanel scrollsPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
        scrollsPanel.add( phonesScroll );
        scrollsPanel.add( phrasesScroll );

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( approveBtn );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( scrollsPanel );

        // Setup header component
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );
        header.setAlignmentX( Container.CENTER_ALIGNMENT );

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
        approveBtn.addActionListener( x -> onApproveClick() );
    }

    /**
     * This method is used to perform
     * initialization logic.
     */
    private void onInit() {
        phoneNumbers = app.getCurrentUser().getCompany().getPhoneNumbers().stream().filter( x -> !x.getIsApproved() )
                .collect( Collectors.toSet() );
        populatePhonesTable();
        populatePhrasesTable();
        phonesTable.getSelectionModel().addListSelectionListener( phonesListListener );
    }

    /**
     * This method is used to perform
     * logic when approve button is clicked.
     */
    private void onApproveClick() {
        clearErrorMessages();

        // Retrieves selected phrases from phrases table
        int selectedPhrasesIndex = phrasesTable.getSelectedRow();
        if ( selectedPhrasesIndex == -1 ) {
            displayErrorMessages( Arrays.asList( "No phrase to approve" ) );
            return;
        }
        String approvedPhrase = phrasesTable.getValueAt( selectedPhrasesIndex, 0 ).toString();

        // Approve phrase
        selectedPhoneNumber.setIsApproved( true );

        // Persist changes
        app.deletePhrases( selectedPhoneNumber.getPhrases() );
        app.storePhrases( Arrays.asList( approvedPhrase ), selectedPhoneNumber );
        app.storePhoneNumber( selectedPhoneNumber, app.getCurrentUser().getCompany() );

        // Reset view
        resetView();

        // Display confirmation message
        JOptionPane.showMessageDialog( frame, "Approved: " + approvedPhrase, "Information", JOptionPane.INFORMATION_MESSAGE );
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
     * This method is used to retrieve phrases
     * corresponding to selected phone number.
     */
    private void getPhrases() {
        selectedPhoneNumber = phoneNumbers.stream()
                .filter( x -> x.toFormattedString().equals( phonesTable.getValueAt( phonesTable.getSelectedRow(), 0 ) ) )
                .findFirst().get();
        phrases = selectedPhoneNumber.getPhrases();
    }

    /**
     * This method is used to populate
     * phones table.
     */
    private void populatePhonesTable() {
        String[] phoneNumbersArr = phoneNumbers.stream().map( x -> x.toFormattedString() )
                .toArray( String[]::new );
        Arrays.sort( phoneNumbersArr );
        phonesModel.setColumnCount( 0 );
        phonesModel.setRowCount( 0 );
        phonesModel.addColumn( "Unapproved Phone Numbers" );
        for ( String phoneNumber : phoneNumbersArr ) {
            phonesModel.addRow( new Object[] { phoneNumber } );
        }
    }

    /**
     * This method is used to populate
     * phrases table.
     */
    private void populatePhrasesTable() {
        String[] phrasesArr = phrases.toArray( String[]::new );
        Arrays.sort( phrasesArr );
        phrasesModel.setColumnCount( 0 );
        phrasesModel.setRowCount( 0 );
        phrasesModel.addColumn( "Candidate Phrases" );
        for ( String phrase : phrasesArr ) {
            phrasesModel.addRow( new Object[] { phrase } );
        }
    }

    /**
     * This method is used to reset view
     * components to their original state.
     */
    public void resetView() {
        isResetting = true;
        phonesTable.getSelectionModel().removeListSelectionListener( phonesListListener );
        phrases.clear();
        phoneNumbers.clear();
        onInit();
        isResetting = true;
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
