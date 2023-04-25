package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

public class UserMenuView {

    private final int WIDTH = 600;
    private final int HEIGHT = 200;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel();
    JButton generatePhrasesBtn = new JButton( "Generate Phrases" );
    JButton viewPhoneNumbersBtn = new JButton( "View Phone Numbers" );
    JButton viewPhrasesBtn = new JButton( "View Approved Phrases" );
    JButton approvePhrasesBtn = new JButton( "Approve Phrases" );
    JButton logoutBtn = new JButton( "Logout" );
    JButton accountBtn = new JButton( "Account" );

    public UserMenuView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( logoutBtn );
        btnPanel.add( accountBtn );

        JPanel optionsPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        optionsPanel.setMaximumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 2 ) );
        optionsPanel.add( generatePhrasesBtn );
        optionsPanel.add( viewPhoneNumbersBtn );
        optionsPanel.add( viewPhrasesBtn );
        optionsPanel.add( approvePhrasesBtn );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        contentPanel.add( optionsPanel );

        header.setText( "Welcome " + app.getCurrentUser().getFirstName() + "!" );
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
        logoutBtn.addActionListener( x -> onLogoutClick() );
        accountBtn.addActionListener( x -> onAccountClick() );
    }

    private void onLogoutClick() {
        app.logout();
        frame.getContentPane().removeAll();
        StartMenuView smv = new StartMenuView( frame, app );
    }

    private void onAccountClick() {
        frame.getContentPane().removeAll();
        AccountView av = new AccountView( frame, app );
    }
}
