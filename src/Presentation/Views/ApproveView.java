package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Application.Domain.*;
import Presentation.*;

public class ApproveView {

    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Validate Phrases" );
    JScrollPane phonesScroll = new JScrollPane();
    JScrollPane phrasesScroll = new JScrollPane();
    JButton approveBtn = new JButton( "Approve" );
    JList<String> phonesList = new JList<>();
    JList<String> phrasesList = new JList<>();
    JButton backBtn = new JButton( "Back" );
    JButton saveBtn = new JButton( "Save" );
    Set<String> phrases = new TreeSet<>();
    Set<PhoneNumber> phoneNumbers;
    PhoneNumber selectedPhoneNumber;

    ListSelectionListener phonesListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() ) {
                getPhrases();
                populatePhrasesScroll();
            }
        }
    };

    public ApproveView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        phonesList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        phrasesList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        JPanel scrollsPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
        scrollsPanel.add( phonesScroll );
        scrollsPanel.add( phrasesScroll );

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( approveBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( scrollsPanel );

        header.setAlignmentX( Container.CENTER_ALIGNMENT );

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
        backBtn.addActionListener( x -> onBackClick() );
        approveBtn.addActionListener( x -> onApproveClick() );
    }

    private void onApproveClick() {
        clearErrorMessages();

        String approvedPhrase = phrasesList.getSelectedValue();
        if ( approvedPhrase == null ) {
            displayErrorMessages( Arrays.asList( "No phrase to approve" ) );
            return;
        }

        // Set flag to show phone number has an approved phrase
        selectedPhoneNumber.setIsApproved( true );

        // Persist changes
        app.deletePhrases( selectedPhoneNumber.getPhrases() );
        app.storePhrases( Arrays.asList( approvedPhrase ), selectedPhoneNumber );
        app.storePhoneNumber( selectedPhoneNumber, app.getCurrentUser().getCompany() );

        resetView();

        JOptionPane.showMessageDialog( frame, "Approved: " + approvedPhrase, "Information", JOptionPane.INFORMATION_MESSAGE );
    }

    private void onBackClick() {
        frame.getContentPane().removeAll();
        UserMenuView umv = new UserMenuView( frame, app );
    }

    private void onInit() {
        phoneNumbers = app.getCurrentUser().getCompany().getPhoneNumbers().stream().filter( x -> !x.getIsApproved() )
                .collect( Collectors.toSet() );
        populatePhonesScroll();
        phonesList.addListSelectionListener( phonesListListener );
    }

    private void populatePhonesScroll() {
        String[] phoneNumbersArr = phoneNumbers.stream().map( x -> x.getPhoneNumber() )
                .toArray( String[]::new );
        Arrays.sort( phoneNumbersArr );
        phonesList = new JList<>( phoneNumbersArr );
        phonesScroll.setViewportView( phonesList );
    }

    private void getPhrases() {
        selectedPhoneNumber = phoneNumbers.stream().filter( x -> x.getPhoneNumber().equals( phonesList.getSelectedValue() ) )
                .findFirst().get();
        phrases = selectedPhoneNumber.getPhrases();
    }

    private void populatePhrasesScroll() {
        String[] phrasesArr = phrases.toArray( String[]::new );
        Arrays.sort( phrasesArr );
        phrasesList = new JList<>( phrasesArr );
        phrasesScroll.setViewportView( phrasesList );
    }

    public void resetView() {
        phonesList.removeListSelectionListener( phonesListListener );
        phrases.clear();
        populatePhrasesScroll();
        onInit();
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
