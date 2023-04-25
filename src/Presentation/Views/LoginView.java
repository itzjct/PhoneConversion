package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.App;

public class LoginView {

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
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

        JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
        btnPanel.setMaximumSize( new Dimension( 200, 30 ) );
        btnPanel.add( backBtn );
        btnPanel.add( submitBtn );

        JPanel formPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        formPanel.setMaximumSize( new Dimension( 500, 100 ) );
        formPanel.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
        formPanel.add( emailLb );
        formPanel.add( emailTxt );
        formPanel.add( passwordLb );
        formPanel.add( passwordTxt );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.add( formPanel );
        contentPanel.add( btnPanel );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );

        frame.setSize( 500, 220 );
        frame.setVisible( true );
    }
}
