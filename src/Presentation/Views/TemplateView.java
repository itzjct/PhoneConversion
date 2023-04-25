package Presentation.Views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.*;

public class TemplateView {

    private final int WIDTH = 400;
    private final int HEIGHT = 200;

    App app;
    JFrame frame;
    JPanel contentPanel = new JPanel();
    JPanel btnPanel = new JPanel( new GridLayout( 1, 2, 5, 0 ) );
    JLabel header = new JLabel( "Text" );

    public TemplateView( JFrame passedFrame, App passedApp ) {
        app = passedApp;
        frame = passedFrame;

        btnPanel.setMaximumSize( new Dimension( 200, AppGUI.LINE_HEIGHT ) );
        btnPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );

        contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
        contentPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );

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

    }
}
