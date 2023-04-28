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

public class ApproveView {

    private final int WIDTH = 600;
    private final int HEIGHT = 400;

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
    List<String> phrases = new LinkedList<>();
    Set<PhoneNumber> phoneNumbers;
    PhoneNumber selectedPhoneNumber;
    boolean isResetting;

    ListSelectionListener phonesListListener = new ListSelectionListener() {
        @Override
        public void valueChanged( ListSelectionEvent e ) {
            if ( !e.getValueIsAdjusting() && !isResetting ) {
                getPhrases();
                populatePhrasesScroll();
            }
        }
    };

    public ApproveView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

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

        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );
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
        populatePhrasesScroll();
        phonesTable.getSelectionModel().addListSelectionListener( phonesListListener );
    }

    private void populatePhonesScroll() {
        String[] phoneNumbersArr = phoneNumbers.stream().map( x -> x.getPhoneNumber() )
                .toArray( String[]::new );
        Arrays.sort( phoneNumbersArr );
        phonesModel.setColumnCount( 0 );
        phonesModel.setRowCount( 0 );
        phonesModel.addColumn( "Unapproved Phone Numbers" );
        for ( String phoneNumber : phoneNumbersArr ) {
            phonesModel.addRow( new Object[] { phoneNumber } );
        }
    }

    private void getPhrases() {
        selectedPhoneNumber = phoneNumbers.stream()
                .filter( x -> x.getPhoneNumber().equals( phonesTable.getValueAt( phonesTable.getSelectedRow(), 0 ) ) )
                .findFirst().get();
        phrases = selectedPhoneNumber.getPhrases();
    }

    private void populatePhrasesScroll() {
        String[] phrasesArr = phrases.toArray( String[]::new );
        Arrays.sort( phrasesArr );
        phrasesModel.setColumnCount( 0 );
        phrasesModel.setRowCount( 0 );
        phrasesModel.addColumn( "Candidate Phrases" );
        for ( String phrase : phrasesArr ) {
            phrasesModel.addRow( new Object[] { phrase } );
        }
    }

    public void resetView() {
        isResetting = true;
        phonesTable.getSelectionModel().removeListSelectionListener( phonesListListener );
        phrases.clear();
        phoneNumbers.clear();
        onInit();
        isResetting = true;
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
