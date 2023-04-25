package Presentation.Views;

import java.util.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.App;
import Presentation.*;

public class RegisterView {

    private final int WIDTH = 400;
    private final int HEIGHT = 375;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Register" );
    JLabel firstNameLb = new JLabel( "First Name:" );
    JLabel lastNameLb = new JLabel( "Last Name:" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel passwordLb = new JLabel( "Password:" );
    JLabel passwordConfirmLb = new JLabel( "Confirm Password:" );
    JLabel companyLb = new JLabel( "Company:" );
    JLabel roleLb = new JLabel( "Role:" );
    JTextField firstNameTxt = new JTextField();
    JTextField lastNameTxt = new JTextField();
    JTextField emailTxt = new JTextField();
    JPasswordField passwordTxt = new JPasswordField();
    JPasswordField passwordConfirmTxt = new JPasswordField();
    JTextField companyTxt = new JTextField();
    JRadioButton normalRb = new JRadioButton( "Normal" );
    JRadioButton adminRb = new JRadioButton( "Admin" );
    JButton backBtn = new JButton( "Back" );
    JButton submitBtn = new JButton( "Submit" );

    public RegisterView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add( normalRb );
        roleGroup.add( adminRb );

        JPanel rolePanel = new JPanel( new GridLayout( 0, 2 ) );
        rolePanel.add( normalRb );
        rolePanel.add( adminRb );

        backBtn.setAlignmentX( Component.CENTER_ALIGNMENT );
        submitBtn.setAlignmentX( Component.CENTER_ALIGNMENT );

        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.add( backBtn );
        btnPanel.add( submitBtn );

        JPanel formPanel = new JPanel( new GridLayout( 7, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 7 ) );
        formPanel.add( firstNameLb );
        formPanel.add( firstNameTxt );
        formPanel.add( lastNameLb );
        formPanel.add( lastNameTxt );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( passwordLb );
        formPanel.add( passwordTxt );
        formPanel.add( passwordConfirmLb );
        formPanel.add( passwordConfirmTxt );
        formPanel.add( companyLb );
        formPanel.add( companyTxt );
        formPanel.add( roleLb );
        formPanel.add( rolePanel );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( formPanel );

        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        errorPanel.setVisible( false );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        addActionListeners();

        frame.setSize( WIDTH, HEIGHT );
        frame.setVisible( true );
    }

    public void addActionListeners() {
        backBtn.addActionListener( x -> onBackClick() );
        submitBtn.addActionListener( x -> onSubmitClick() );
    }

    public void onBackClick() {
        frame.getContentPane().removeAll();
        StartMenuView startMenu = new StartMenuView( frame, app );
    }

    public void onSubmitClick() {
        clearErrorMessages();
    }

    private void displayErrorMessages( List<String> errors ) {
        for ( String error : errors ) {
            JLabel label = new JLabel( error );
            label.setForeground( Color.RED );
            label.setHorizontalAlignment( JLabel.CENTER );
            errorPanel.add( label );
        }
        errorPanel.setMaximumSize( new Dimension( WIDTH, errors.size() * 20 ) );
        frame.setSize( WIDTH, HEIGHT + errors.size() * 20 );
        errorPanel.setVisible( true );
    }

    private void clearErrorMessages() {
        errorPanel.setVisible( false );
        frame.setSize( WIDTH, HEIGHT );
    }
}
