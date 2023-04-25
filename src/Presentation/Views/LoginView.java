package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

public class LoginView {

    private final int WIDTH = 400;
    private final int HEIGHT = 200;

    App app;
    JFrame frame;
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

    }
}
