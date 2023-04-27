package Presentation.Views;

import java.util.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
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
    ButtonGroup roleGroup = new ButtonGroup();
    JRadioButton normalRb = new JRadioButton( "Normal" );
    JRadioButton adminRb = new JRadioButton( "Admin" );
    JButton backBtn = new JButton( "Back" );
    JButton submitBtn = new JButton( "Submit" );

    public RegisterView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

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
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );

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
        backBtn.addActionListener( x -> onBackClick() );
        submitBtn.addActionListener( x -> onSubmitClick() );
    }

    private void onBackClick() {
        frame.getContentPane().removeAll();
        StartMenuView startMenu = new StartMenuView( frame, app );
    }

    private void onSubmitClick() {
        clearErrorMessages();
        if ( register() ) {
            frame.getContentPane().removeAll();
            UserMenuView umv = new UserMenuView( frame, app );
        }
    }

    private boolean register() {
        List<String> errors = new LinkedList<>();

        String role = getSelectedButtonText();
        if ( role == null ) {
            errors.add( "Role cannot be empty" );
            displayErrorMessages( errors );
            return false;
        }

        User user = new User();
        user.setFirstName( firstNameTxt.getText().toUpperCase().trim() );
        user.setLastName( lastNameTxt.getText().toUpperCase().trim() );
        user.setEmail( emailTxt.getText().toUpperCase().trim() );
        user.setPasswordString( String.valueOf( passwordTxt.getPassword() ) );
        user.setIsAdmin( role.equals( "Admin" ) );
        user.getCompany().setName( companyTxt.getText().toUpperCase().trim() );

        errors = app.validateUser( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        errors = app.validateCompany( user.getCompany() );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        if ( !Arrays.equals( passwordTxt.getPassword(), passwordConfirmTxt.getPassword() ) ) {
            displayErrorMessages( Arrays.asList( "Passwords do not match" ) );
            return false;
        }

        errors = app.register( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        return true;
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

    private String getSelectedButtonText() {
        for ( Enumeration<AbstractButton> buttons = roleGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();

            if ( button.isSelected() ) {
                return button.getText();
            }
        }

        return null;
    }
}
