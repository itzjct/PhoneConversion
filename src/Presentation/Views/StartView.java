package Presentation.Views;

import Application.Domain.App;

import javax.swing.*;
import java.awt.*;

public class StartView {

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JLabel header = new JLabel( "Start Menu" );
    JButton loginBtn = new JButton( "Login" );
    JButton registerBtn = new JButton( "Register" );

    public StartView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        header.setAlignmentX( Container.CENTER_ALIGNMENT );
        loginBtn.setAlignmentX( Container.CENTER_ALIGNMENT );
        registerBtn.setAlignmentX( Container.CENTER_ALIGNMENT );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.X_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.add( loginBtn );
        contentPanel.add( registerBtn );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );

        addActionListeners();

        frame.setSize( 400, 100 );
        frame.setVisible( true );
    }

    private void addActionListeners() {
        registerBtn.addActionListener( x -> onRegisterClick() );
        loginBtn.addActionListener( x -> onLoginClick() );
    }

    private void onLoginClick() {
        frame.getContentPane().removeAll();
        RegisterView registerView = new RegisterView( frame, app );
    }

    private void onRegisterClick() {
        frame.getContentPane().removeAll();
        LoginView loginView = new LoginView( frame, app );
    }
}
