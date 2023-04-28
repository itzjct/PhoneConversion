package Presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.Views.*;

/**
 * AppGUI is the class responsible for displaying graphical
 * content and interacting with user.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class AppGUI {

    // Constant used to specify height of commonly used
    // graphical components
    public static final int LINE_HEIGHT = 35;

    /**
     * Main method
     * 
     * @param args A String array
     */
    public static void main( String[] args ) {

        // Start GUI
        start();
    }

    /**
     * This method loads business logic object, globally
     * used settings, and creates the frame and content
     * panel components used to display different views.
     */
    public static void start() {

        // Create instance of App object
        App app = new App();

        // Set globally used settings
        UIManager.put( "OptionPane.minimumSize", new Dimension( 250, 100 ) );
        UIManager.put( "Panel.background", Color.WHITE );
        UIManager.put( "RadioButton.background", Color.WHITE );
        UIManager.put( "Button.background", Color.LIGHT_GRAY );

        // Create content panel
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setAlignmentX( Container.CENTER_ALIGNMENT );
        panel.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );

        // Create frame
        JFrame frame = new JFrame( "Phone Conversion" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( panel );
        frame.setResizable( false );

        // Load start view
        StartMenuView startView = new StartMenuView( frame, app );
    }
}
