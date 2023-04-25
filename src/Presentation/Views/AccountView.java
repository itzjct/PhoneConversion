package Presentation.Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

public class AccountView {

    private final int WIDTH = 600;
    private final int HEIGHT = 270;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Account Info" );
    JLabel nameLb = new JLabel( "Name:" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel companyLb = new JLabel( "Company:" );
    JLabel roleLb = new JLabel( "Role:" );
    JTextField nameTxt = new JTextField();
    JTextField emailTxt = new JTextField();
    JTextField companyTxt = new JTextField();
    JTextField roleTxt = new JTextField();
    JButton backBtn = new JButton( "Back" );

    public AccountView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        nameTxt.setEditable( false );
        emailTxt.setEditable( false );
        companyTxt.setEditable( false );
        roleTxt.setEditable( false );

        JPanel formPanel = new JPanel( new GridLayout( 4, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 4 ) );
        formPanel.add( nameLb );
        formPanel.add( nameTxt );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( companyLb );
        formPanel.add( companyTxt );
        formPanel.add( roleLb );
        formPanel.add( roleTxt );

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( formPanel );

        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        populateTextFields();

        addActionListeners();

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

    private void populateTextFields() {
        User user = app.getCurrentUser();
        nameTxt.setText( user.getFirstName() + " " + user.getLastName() );
        emailTxt.setText( user.getEmail() );
        companyTxt.setText( user.getCompany().getName() );
        roleTxt.setText( user.getIsAdmin() ? "Admin" : "Normal" );
    }
}
