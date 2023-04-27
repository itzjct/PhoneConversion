package Presentation.Views;

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

public class PhonesView {

    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Company Phone Numbers" );
    DefaultTableModel phonesModel = new DefaultTableModel();
    JTable phonesTable = new JTable( phonesModel );
    JScrollPane phonesScroll = new JScrollPane( phonesTable );
    JButton backBtn = new JButton( "Back" );
    Set<PhoneNumber> phoneNumbers;

    public PhonesView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( phonesScroll );

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
        phoneNumbers = app.getCurrentUser().getCompany().getPhoneNumbers();
        populatePhonesTable();
    }

    private void populatePhonesTable() {
        String[] phoneNumbersArr = phoneNumbers.stream().map( x -> x.getPhoneNumber() )
                .toArray( String[]::new );
        Arrays.sort( phoneNumbersArr );
        phonesModel.addColumn( "Phone Numbers" );
        for ( String phoneNumber : phoneNumbersArr ) {
            phonesModel.addRow( new Object[] { phoneNumber } );
        }
    }
}
