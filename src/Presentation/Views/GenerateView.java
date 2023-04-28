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

public class GenerateView {

    private final int WIDTH = 1000;
    private final int HEIGHT = 500;

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
    PhoneNumber phoneNumber;
    Word selecteWord = new Word();
    Set<Word> words = new HashSet<>();
    List<String> phrases = new LinkedList<>();
    Set<String> selectedPhrases = new TreeSet<>();
    boolean isResetting;

    ListSelectionListener wordsListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() && !isResetting ) {
                generatePhrases();
                populatePhrasesScroll();
            }
        }
    };

    public GenerateView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        JPanel phonePanel = new JPanel();
        phonePanel.setLayout( new BoxLayout( phonePanel, BoxLayout.X_AXIS ) );
        phonePanel.setMaximumSize( new Dimension( WIDTH / 2, AppGUI.LINE_HEIGHT ) );
        phonePanel.add( phoneLb );
        phonePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        phonePanel.add( phoneTxt );
        phonePanel.add( Box.createRigidArea( new Dimension( 20, 0 ) ) );
        phonePanel.add( genBtn );

        JPanel selectBtnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        selectBtnPanel.setMaximumSize( new Dimension( WIDTH, AppGUI.LINE_HEIGHT ) );
        selectBtnPanel.add( deleteBtn );
        selectBtnPanel.add( clearBtn );

        JPanel selectedPanel = new JPanel();
        selectedPanel.setLayout( new BoxLayout( selectedPanel, BoxLayout.X_AXIS ) );
        selectedPanel.add( addBtn );
        selectedPanel.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
        selectedPanel.add( selectedScroll );

        JPanel scrollsPanel = new JPanel( new GridLayout( 1, 3, 5, 0 ) );
        scrollsPanel.add( wordsScroll );
        scrollsPanel.add( phraseScroll );
        scrollsPanel.add( selectedPanel );

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( saveBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( phonePanel );
        contentPanel.add( selectBtnPanel );
        contentPanel.add( scrollsPanel );

        header.setAlignmentX( Container.CENTER_ALIGNMENT );
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );

        errorPanel.setVisible( false );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( errorPanel );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        addActionListeners();
        onInit();

        frame.setSize( WIDTH, HEIGHT );
        frame.setVisible( true );
    }

    private void addActionListeners() {
        genBtn.addActionListener( x -> onGenerateClick() );
        addBtn.addActionListener( x -> onAddClick() );
        clearBtn.addActionListener( x -> onClearClick() );
        deleteBtn.addActionListener( x -> onDeleteClick() );
        saveBtn.addActionListener( x -> onSaveClick() );
        backBtn.addActionListener( x -> onBackClick() );
        wordsTable.getSelectionModel().addListSelectionListener( wordsListListener );
    }

    private void onInit() {
        populateWordsScroll();
        populatePhrasesScroll();
        populateSelectedPhrasesScroll();
    }

    private void onBackClick() {
        frame.getContentPane().removeAll();
        UserMenuView umv = new UserMenuView( frame, app );
    }

    private void onSaveClick() {
        clearErrorMessages();

        if ( selectedPhrases.isEmpty() || phoneNumber == null ) {
            displayErrorMessages( Arrays.asList( "No phrases to save" ) );
            return;
        }

        phoneNumber.setIsApproved( false );

        int id = app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany() );
        phoneNumber.setId( id );
        app.storePhrases( selectedPhrases, phoneNumber );

        resetView();
        JOptionPane.showMessageDialog( frame, "Success", "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    private void onDeleteClick() {
        clearErrorMessages();

        int[] selectedPhrasesIndices = selectedPhrasesTable.getSelectedRows();
        List<String> tempPhrases = new LinkedList<>();
        for ( int i : selectedPhrasesIndices ) {
            tempPhrases.add( phrasesTable.getValueAt( i, 0 ).toString() );
        }
        if ( tempPhrases.isEmpty() ) {
            displayErrorMessages( Arrays.asList( "No phrase to delete" ) );
            return;
        }

        selectedPhrases.removeAll( tempPhrases );

        populateSelectedPhrasesScroll();
    }

    private void onClearClick() {
        clearErrorMessages();
        selectedPhrases.clear();
        populateSelectedPhrasesScroll();
    }

    private void onAddClick() {
        clearErrorMessages();

        int[] selectedPhrasesIndices = phrasesTable.getSelectedRows();
        List<String> tempPhrases = new LinkedList<>();
        for ( int i : selectedPhrasesIndices ) {
            tempPhrases.add( phrasesTable.getValueAt( i, 0 ).toString() );
        }
        if ( tempPhrases.isEmpty() ) {
            displayErrorMessages( Arrays.asList( "No phrase to add" ) );
            return;
        }

        for ( String phrase : tempPhrases ) {
            selectedPhrases.add( phrase );
        }

        populateSelectedPhrasesScroll();
    }

    private void onGenerateClick() {
        clearErrorMessages();

        boolean success = getPhoneNumber();
        if ( !success ) {
            return;
        }

        words = app.generateWords( phoneNumber );

        populateWordsScroll();
    }

    private boolean getPhoneNumber() {
        String phoneNumberStr = phoneTxt.getText().trim();
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

    private void generatePhrases() {
        selecteWord = words.stream()
                .filter( x -> x.getWord().equals( wordsTable.getValueAt( wordsTable.getSelectedRow(), 0 ).toString() ) )
                .findFirst().get();
        List<List<Word>> tempPhrases = app.generatePhrases( selecteWord, words );
        phrases = app.generateNumericPhrases( tempPhrases, phoneNumber.getPhoneNumber() );
    }

    private void populateWordsScroll() {
        String[] wordsArr = words.stream().map( x -> x.getWord() ).toArray( String[]::new );
        Arrays.sort( wordsArr );
        wordsModel.setColumnCount( 0 );
        wordsModel.setRowCount( 0 );
        wordsModel.addColumn( "Generated Words" );
        for ( String word : wordsArr ) {
            wordsModel.addRow( new Object[] { word } );
        }
    }

    private void populatePhrasesScroll() {
        String[] phrasesArr = phrases.toArray( String[]::new );
        Arrays.sort( phrasesArr );
        phrasesModel.setColumnCount( 0 );
        phrasesModel.setRowCount( 0 );
        phrasesModel.addColumn( "Phrases" + ( selecteWord.getWord() != null ? " containing " + selecteWord.getWord() : "" ) );
        for ( String phrase : phrasesArr ) {
            phrasesModel.addRow( new Object[] { phrase } );
        }
    }

    private void populateSelectedPhrasesScroll() {
        String[] selectedPhrasesArr = selectedPhrases.toArray( String[]::new );
        Arrays.sort( selectedPhrasesArr );
        selectedPhrasesModel.setColumnCount( 0 );
        selectedPhrasesModel.setRowCount( 0 );
        selectedPhrasesModel.addColumn( "Selected Phrases" );
        for ( String phrase : selectedPhrasesArr ) {
            selectedPhrasesModel.addRow( new Object[] { phrase } );
        }
    }

    private void resetView() {
        isResetting = true;
        phoneTxt.setText( "" );
        words.clear();
        phrases.clear();
        selectedPhrases.clear();
        populateWordsScroll();
        populatePhrasesScroll();
        populateSelectedPhrasesScroll();
        isResetting = false;
    }

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

    private void clearErrorMessages() {
        errorPanel.removeAll();
        errorPanel.setVisible( false );
        frame.setSize( WIDTH, HEIGHT );
    }
}
