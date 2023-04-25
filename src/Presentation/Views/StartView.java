package Presentation.Views;

import Application.Domain.App;
import Presentation.AppGUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StartView {

    private final int WIDTH = 400;
    private final int HEIGHT = 150;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Start Menu" );
    JButton loginBtn = new JButton( "Login" );
    JButton registerBtn = new JButton( "Register" );
    JLabel messageLb = new JLabel( "Select an option to begin" );

    public StartView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        loginBtn.setAlignmentX( Container.CENTER_ALIGNMENT );
        registerBtn.setAlignmentX( Container.CENTER_ALIGNMENT );

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( loginBtn );
        btnPanel.add( registerBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( messageLb );

        header.setAlignmentX( Container.CENTER_ALIGNMENT );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( btnPanel );

        addActionListeners();

        frame.setSize( WIDTH, HEIGHT );
        frame.setVisible( true );
    }

    private void addActionListeners() {
        registerBtn.addActionListener( x -> onRegisterClick() );
        loginBtn.addActionListener( x -> onLoginClick() );
    }

    private void onRegisterClick() {
        frame.getContentPane().removeAll();
        RegisterView registerView = new RegisterView( frame, app );
    }

    private void onLoginClick() {
        frame.getContentPane().removeAll();
        LoginView loginView = new LoginView( frame, app );
    }
}
