package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

public class UserMenuView {

    private final int WIDTH = 600;
    private final int HEIGHT = 300;

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
        // btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        btnPanel.add( logoutBtn );
        btnPanel.add( accountBtn );

        JPanel optionsPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        optionsPanel.setMinimumSize( new Dimension( 500, AppGUI.LINE_HEIGHT * 4 ) );
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
        header.setFont( new Font( "Arial", Font.BOLD, 14 ) );

        JSeparator separator = new JSeparator();
        separator.setMaximumSize( new Dimension( WIDTH, 1 ) );

        Container mainPanel = frame.getContentPane();
        mainPanel.add( header );
        mainPanel.add( contentPanel );
        mainPanel.add( Box.createRigidArea( new Dimension( WIDTH, 20 ) ) );
        mainPanel.add( separator );
        mainPanel.add( Box.createRigidArea( new Dimension( WIDTH, 10 ) ) );
        mainPanel.add( btnPanel );

        addActionListeners();
        onInit();

        frame.setSize( WIDTH, HEIGHT );
        frame.setVisible( true );
    }

    private void addActionListeners() {
        logoutBtn.addActionListener( x -> onLogoutClick() );
        accountBtn.addActionListener( x -> onAccountClick() );
        generatePhrasesBtn.addActionListener( x -> onGeneratePhrasesClick() );
        viewPhoneNumbersBtn.addActionListener( x -> onViewPhoneNumbersClick() );
        viewPhrasesBtn.addActionListener( x -> onViewApprovedPhrasesClick() );
        approvePhrasesBtn.addActionListener( x -> onApprovePhrasesClick() );
    }

    private void onInit() {
        app.updateState();
        approvePhrasesBtn.setEnabled( app.getCurrentUser().getIsAdmin() );
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

    private void onGeneratePhrasesClick() {
        frame.getContentPane().removeAll();
        GenerateView gv = new GenerateView( frame, app );
    }

    private void onViewPhoneNumbersClick() {
        frame.getContentPane().removeAll();
        PhonesView pv = new PhonesView( frame, app );
    }

    private void onViewApprovedPhrasesClick() {
        frame.getContentPane().removeAll();
        ApprovedPhrasesView apv = new ApprovedPhrasesView( frame, app );
    }

    private void onApprovePhrasesClick() {
        frame.getContentPane().removeAll();
        ApproveView av = new ApproveView( frame, app );
    }
}
