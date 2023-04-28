package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import Application.Domain.*;
import Presentation.*;

/**
 * GenerateView is the class responsible for displaying
 * the generate phrases view.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class GenerateView {

    // Constants used to specify dimensions
    // of this view
    private final int WIDTH = 1000;
    private final int HEIGHT = 500;

    // Declare and/or instantiate needed components
    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Generate Words" );
    JLabel phoneLb = new JLabel( "Phone Number:" );
    JTextField phoneTxt = new JTextField();
    JButton genBtn = new JButton( "Generate" );
    JButton deleteBtn = new JButton( "Delete" );
    JButton clearBtn = new JButton( "Clear" );
    JButton addBtn = new JButton( ">" );
    JButton backBtn = new JButton( "Back" );
    JButton saveBtn = new JButton( "Save" );
    DefaultTableModel wordsModel = new DefaultTableModel();
    DefaultTableModel phrasesModel = new DefaultTableModel();
    DefaultTableModel selectedPhrasesModel = new DefaultTableModel();
    JTable wordsTable = new JTable( wordsModel );
    JTable phrasesTable = new JTable( phrasesModel );
    JTable selectedPhrasesTable = new JTable( selectedPhrasesModel );
    JScrollPane wordsScroll = new JScrollPane( wordsTable );
    JScrollPane phraseScroll = new JScrollPane( phrasesTable );
    JScrollPane selectedScroll = new JScrollPane( selectedPhrasesTable );

    // Declare and/or instantiate needed domain objects
    // or data structures
    PhoneNumber phoneNumber;
    Word selecteWord = new Word();
    Set<Word> words = new HashSet<>();
    List<String> phrases = new LinkedList<>();
    Set<String> selectedPhrases = new TreeSet<>();
    boolean isResetting;

    // Create a list selection listener for the words table.
    // This allows designated tasks to occur upon selection
    // of a row in the words table
    ListSelectionListener wordsListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() && !isResetting ) {
                generatePhrases();
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
    public GenerateView( JFrame passedFrame, App passedApp ) {

        // Store frame and app globally
        app = passedApp;
        frame = passedFrame;

        // Panel used to store phone number label, text field, and generate button
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout( new BoxLayout( phonePanel, BoxLayout.X_AXIS ) );
        phonePanel.setMaximumSize( new Dimension( WIDTH / 2, AppGUI.LINE_HEIGHT ) );
        phonePanel.add( phoneLb );
        phonePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        phonePanel.add( phoneTxt );
        phonePanel.add( Box.createRigidArea( new Dimension( 20, 0 ) ) );
        phonePanel.add( genBtn );

        // Panel used to store delete and clear buttons
        JPanel selectBtnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        selectBtnPanel.setMaximumSize( new Dimension( WIDTH, AppGUI.LINE_HEIGHT ) );
        selectBtnPanel.add( deleteBtn );
        selectBtnPanel.add( clearBtn );

        // Panel used to store add and selected phrases table
        JPanel selectedPanel = new JPanel();
        selectedPanel.setLayout( new BoxLayout( selectedPanel, BoxLayout.X_AXIS ) );
        selectedPanel.add( addBtn );
        selectedPanel.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
        selectedPanel.add( selectedScroll );

        // Panel used to store the different tables used
        JPanel scrollsPanel = new JPanel( new GridLayout( 1, 3, 5, 0 ) );
        scrollsPanel.add( wordsScroll );
        scrollsPanel.add( phraseScroll );
        scrollsPanel.add( selectedPanel );

        // Panel used to store bottom buttons
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( saveBtn );

        // Panel used to store main content components
        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( phonePanel );
        contentPanel.add( selectBtnPanel );
        contentPanel.add( scrollsPanel );

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
        genBtn.addActionListener( x -> onGenerateClick() );
        addBtn.addActionListener( x -> onAddClick() );
        clearBtn.addActionListener( x -> onClearClick() );
        deleteBtn.addActionListener( x -> onDeleteClick() );
        saveBtn.addActionListener( x -> onSaveClick() );
        backBtn.addActionListener( x -> onBackClick() );
        wordsTable.getSelectionModel().addListSelectionListener( wordsListListener );
    }

    /**
     * This method is used to perform
     * initialization logic.
     */
    private void onInit() {
        populateWordsTable();
        populatePhrasesTable();
        populateSelectedPhrasesTable();
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
     * This method is used to perform
     * logic when save button is clicked.
     */
    private void onSaveClick() {
        clearErrorMessages();

        // Display error if no phrases were selected
        if ( selectedPhrases.isEmpty() || phoneNumber == null ) {
            displayErrorMessages( Arrays.asList( "No phrases to save" ) );
            return;
        }

        // Set selected phone number as unapproved. This is needed
        // to allow regeneration of words for an already approved
        // phone number.
        phoneNumber.setIsApproved( false );

        // Store phone number and selected phrases to database
        int id = app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany() );
        phoneNumber.setId( id );
        app.storePhrases( selectedPhrases, phoneNumber );

        // Reset view
        resetView();

        // Display confirmation message
        JOptionPane.showMessageDialog( frame, "Success", "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * This method is used to perform
     * logic when delete button is clicked.
     */
    private void onDeleteClick() {
        clearErrorMessages();

        // Retrieve selected phrases from selected phrases table
        int[] selectedPhrasesIndices = selectedPhrasesTable.getSelectedRows();
        List<String> tempPhrases = new LinkedList<>();
        for ( int i : selectedPhrasesIndices ) {
            tempPhrases.add( phrasesTable.getValueAt( i, 0 ).toString() );
        }
        if ( tempPhrases.isEmpty() ) {
            displayErrorMessages( Arrays.asList( "No phrase to delete" ) );
            return;
        }

        // Remove selected phrases from selected phrases table
        selectedPhrases.removeAll( tempPhrases );

        // Display changes to phrases table
        populateSelectedPhrasesTable();
    }

    /**
     * This method is used to perform
     * logic when clear button is clicked.
     */
    private void onClearClick() {
        clearErrorMessages();
        selectedPhrases.clear();
        populateSelectedPhrasesTable();
    }

    /**
     * This method is used to perform
     * logic when add button is clicked.
     */
    private void onAddClick() {
        clearErrorMessages();

        // Retrieves selected phrases from phrases table
        int[] selectedPhrasesIndices = phrasesTable.getSelectedRows();
        List<String> tempPhrases = new LinkedList<>();
        for ( int i : selectedPhrasesIndices ) {
            tempPhrases.add( phrasesTable.getValueAt( i, 0 ).toString() );
        }
        if ( tempPhrases.isEmpty() ) {
            displayErrorMessages( Arrays.asList( "No phrase to add" ) );
            return;
        }

        // Add selected phrases to selected phrases table
        for ( String phrase : tempPhrases ) {
            selectedPhrases.add( phrase );
        }

        // Display changes to selected phrases table
        populateSelectedPhrasesTable();
    }

    /**
     * This method is used to perform
     * logic when generate button is clicked.
     */
    private void onGenerateClick() {
        clearErrorMessages();

        // Retrieve phone number
        boolean success = getPhoneNumber();
        if ( !success ) {
            return;
        }

        // Generate possible words for selected phone number
        words = app.generateWords( phoneNumber );

        // Display words
        populateWordsTable();
    }

    /**
     * This method retrieves phone number from
     * phone number text field and populates
     * phoneNumber field.
     * 
     * @return True if operation succeeded.
     *         False otherwise.
     */
    private boolean getPhoneNumber() {

        // Retrieve phone number string
        String phoneNumberStr = phoneTxt.getText().trim();

        // Validate phone number
        List<String> errors = app.validatePhoneNumber( phoneNumberStr );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        // Check that given phone number does not belong to
        // another company
        Set<String> phoneNumbers = app.getNonUsablePhoneNumbers( app.getCurrentUser().getCompany() );
        if ( phoneNumbers.contains( phoneNumberStr ) ) {
            displayErrorMessages( Arrays.asList( "Phone number already belongs to another company" ) );
            return false;
        }

        // Check if phone number already belongs to this company.
        // If not, create new instance
        phoneNumber = app.getCurrentUser().getCompany().getPhoneNumber( phoneNumberStr );
        if ( phoneNumber == null ) {
            phoneNumber = new PhoneNumber( phoneNumberStr );
        }

        return true;
    }

    /**
     * This method is used to generate possible
     * phrases that include the selected word.
     */
    private void generatePhrases() {
        selecteWord = words.stream()
                .filter( x -> x.getWord().equals( wordsTable.getValueAt( wordsTable.getSelectedRow(), 0 ).toString() ) )
                .findFirst().get();
        List<List<Word>> tempPhrases = app.generatePhrases( selecteWord, words );
        phrases = app.generateNumericPhrases( tempPhrases, phoneNumber.getPhoneNumber() );
    }

    /**
     * This method is used to populate
     * words table.
     */
    private void populateWordsTable() {
        String[] wordsArr = words.stream().map( x -> x.getWord() ).toArray( String[]::new );
        Arrays.sort( wordsArr );
        wordsModel.setColumnCount( 0 );
        wordsModel.setRowCount( 0 );
        wordsModel.addColumn( "Generated Words" );
        for ( String word : wordsArr ) {
            wordsModel.addRow( new Object[] { word } );
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
        phrasesModel.addColumn( "Phrases" + ( selecteWord.getWord() != null ? " containing " + selecteWord.getWord() : "" ) );
        for ( String phrase : phrasesArr ) {
            phrasesModel.addRow( new Object[] { phrase } );
        }
    }

    /**
     * This method is used to populate
     * selected phrases table.
     */
    private void populateSelectedPhrasesTable() {
        String[] selectedPhrasesArr = selectedPhrases.toArray( String[]::new );
        Arrays.sort( selectedPhrasesArr );
        selectedPhrasesModel.setColumnCount( 0 );
        selectedPhrasesModel.setRowCount( 0 );
        selectedPhrasesModel.addColumn( "Selected Phrases" );
        for ( String phrase : selectedPhrasesArr ) {
            selectedPhrasesModel.addRow( new Object[] { phrase } );
        }
    }

    /**
     * This method is used to reset view
     * components to their original state.
     */
    private void resetView() {
        isResetting = true;
        phoneTxt.setText( "" );
        words.clear();
        phrases.clear();
        selectedPhrases.clear();
        populateWordsTable();
        populatePhrasesTable();
        populateSelectedPhrasesTable();
        isResetting = false;
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
