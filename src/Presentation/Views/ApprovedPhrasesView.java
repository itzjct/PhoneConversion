package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Application.Domain.*;
import Presentation.*;

public class ApprovedPhrasesView {

    private final int WIDTH = 400;
    private final int HEIGHT = 400;

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
    Set<PhoneNumber> phoneNumbers;

    public ApprovedPhrasesView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( tableScroll );

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
    }

    private void onBackClick() {
        frame.getContentPane().removeAll();
        UserMenuView umv = new UserMenuView( frame, app );
    }

    private void onInit() {
        getApprovedPhoneNumbers();
        populatePhrasesTable();
    }

    private void getApprovedPhoneNumbers() {
        phoneNumbers = app.getCurrentUser().getCompany().getApprovedPhoneNumbers();
    }

    private void populatePhrasesTable() {
        phrasesModel.addColumn( "Phone Number" );
        phrasesModel.addColumn( "Approved Phrase" );
        for ( PhoneNumber phoneNumber : phoneNumbers ) {
            phrasesModel.addRow( new Object[] { phoneNumber.getPhoneNumber(), phoneNumber.getPhrases().get( 0 ) } );
        }
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
