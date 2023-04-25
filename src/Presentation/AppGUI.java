package Presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.views.*;

public class AppGUI {

    public static void main( String[] args ) {
        start();
    }

    public static void start() {
        App app = new App();

        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setAlignmentX( Container.CENTER_ALIGNMENT );
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

        JFrame frame = new JFrame( "Phone Conversion" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( panel );
        
        // StartView startView = new StartView( frame, app );
        RegisterView registerView = new RegisterView( frame, app );
    }
}
