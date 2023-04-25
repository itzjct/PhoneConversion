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

public class LoginView {

    private final int WIDTH = 400;
    private final int HEIGHT = 200;

    App app;
    JFrame frame;
    JPanel errorPanel = new JPanel( new GridLayout( 0, 1 ) );
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Login" );
    JLabel emailLb = new JLabel( "Email:" );
    JLabel passwordLb = new JLabel( "Password:" );
    JTextField emailTxt = new JTextField();
    JPasswordField passwordTxt = new JPasswordField();
    JButton backBtn = new JButton( "Back" );
    JButton submitBtn = new JButton( "Submit" );

    public LoginView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        backBtn.setAlignmentX( Component.CENTER_ALIGNMENT );
        submitBtn.setAlignmentX( Component.CENTER_ALIGNMENT );

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( backBtn );
        btnPanel.add( submitBtn );

        JPanel formPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 2 ) );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( passwordLb );
        formPanel.add( passwordTxt );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( formPanel );

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
        backBtn.addActionListener( x -> onBackClick() );
        submitBtn.addActionListener( x -> onSubmitClick() );
    }

    private void onBackClick() {
        frame.getContentPane().removeAll();
        StartMenuView startMenu = new StartMenuView( frame, app );
    }

    private void onSubmitClick() {
        clearErrorMessages();
        if ( login() ) {
            frame.getContentPane().removeAll();
            UserMenuView umv = new UserMenuView( frame, app );
        }
    }

    private boolean login() {
        User user = new User();
        user.setEmail( emailTxt.getText().toUpperCase().trim() );
        user.setPasswordString( String.valueOf( passwordTxt.getPassword() ).trim() );

        List<String> errors = app.validateLogin( user );
        if ( errors.size() > 0 ) {
            displayErrorMessages( errors );
            return false;
        }

        errors = app.login( user );
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
}
