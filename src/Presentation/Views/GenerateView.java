package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    JScrollPane wordScroll = new JScrollPane();
    JScrollPane phraseScroll = new JScrollPane();
    JScrollPane selectedScroll = new JScrollPane();
    JList<String> wordList = new JList<>();
    JList<String> phrasesList = new JList<>();
    JList<String> selectedPhrasesList = new JList<>();
    PhoneNumber phoneNumber = new PhoneNumber();
    Word selecteWord = new Word();
    Set<Word> words = new HashSet<>();
    List<String> phrases = new LinkedList<>();
    Set<String> selectedPhrases = new TreeSet<>();

    ListSelectionListener wordsListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() ) {
                generatePhrases();
                populatePhrasesScroll();
            }
        }
    };

    public GenerateView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        wordList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        wordList.setLayoutOrientation( JList.HORIZONTAL_WRAP );
        // wordList.setVisibleRowCount( -1 );
        phrasesList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        phrasesList.setLayoutOrientation( JList.VERTICAL_WRAP );
        // phrasesList.setVisibleRowCount( -1 );
        selectedPhrasesList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        selectedPhrasesList.setLayoutOrientation( JList.VERTICAL_WRAP );
        // selectedPhrasesList.setVisibleRowCount( -1 );

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
        selectedPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        selectedPanel.add( selectedScroll );

        JPanel scrollsPanel = new JPanel( new GridLayout( 1, 3, 5, 0 ) );
        scrollsPanel.add( wordScroll );
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

        errorPanel.setVisible( false );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( errorPanel );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        addActionListeners();

        frame.setSize( WIDTH, HEIGHT );
        frame.setVisible( true );
    }

    private void addActionListeners() {
        genBtn.addActionListener( x -> onGenerateClick() );
        addBtn.addActionListener( x -> onAddClick() );
        clearBtn.addActionListener( x -> onClearClick() );
        deleteBtn.addActionListener( x -> onDeleteClick() );
    }

    private void onDeleteClick() {
        List<String> tempPhrases = selectedPhrasesList.getSelectedValuesList();
        if ( tempPhrases.isEmpty() ) {
            displayErrorMessages( Arrays.asList( "No phrase to delete" ) );
            return;
        }

        selectedPhrases.removeAll( tempPhrases );

        populateSelectedPhrasesScroll();
    }

    private void onClearClick() {
        selectedPhrases.clear();
        populateSelectedPhrasesScroll();
    }

    private void onAddClick() {
        clearErrorMessages();

        List<String> tempPhrases = phrasesList.getSelectedValuesList();
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
        wordList.removeListSelectionListener( wordsListListener );

        boolean success = getPhoneNumber();
        if ( !success ) {
            return;
        }

        words = app.generateWords( phoneNumber );

        populateWordsScroll();
        wordList.addListSelectionListener( wordsListListener );
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

        // Set the current user's company as owner of given phone number
        phoneNumber = app.getCurrentUser().getCompany().getPhoneNumber( phoneNumberStr );
        if ( phoneNumber == null ) {
            phoneNumber = new PhoneNumber( phoneNumberStr );
            int id = app.storePhoneNumber( phoneNumber, app.getCurrentUser().getCompany() );
            if ( id == 0 ) {
                displayErrorMessages( Arrays.asList( "Failure to create link between entered phone number and "
                        + app.getCurrentUser().getCompany().getName() ) );
                return false;
            }
            phoneNumber.setId( id );
        }

        return true;
    }

    private void generatePhrases() {
        selecteWord = words.stream().filter( x -> x.getWord().equals( wordList.getSelectedValue() ) ).findFirst().get();
        List<List<Word>> tempPhrases = app.generatePhrases( selecteWord, words );
        phrases = app.generateNumericPhrases( tempPhrases, phoneNumber.getPhoneNumber() );
    }

    private void populateWordsScroll() {
        String[] wordsArr = words.stream().map( x -> x.getWord() ).toArray( String[]::new );
        Arrays.sort( wordsArr );
        wordList = new JList<>( wordsArr );
        wordScroll.setViewportView( wordList );
    }

    private void populatePhrasesScroll() {
        String[] phrasesArr = phrases.toArray( String[]::new );
        Arrays.sort( phrasesArr );
        phrasesList = new JList<>( phrasesArr );
        phraseScroll.setViewportView( phrasesList );
    }

    private void populateSelectedPhrasesScroll() {
        String[] selectedPhrasesArr = selectedPhrases.toArray( String[]::new );
        Arrays.sort( selectedPhrasesArr );
        selectedPhrasesList = new JList<>( selectedPhrasesArr );
        selectedScroll.setViewportView( selectedPhrasesList );
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
