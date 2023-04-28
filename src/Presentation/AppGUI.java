package Presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Application.Domain.*;
import Presentation.Views.*;

public class AppGUI {

    public static final int LINE_HEIGHT = 35;

    public static void main( String[] args ) {
        start();
    }

    public static void start() {
        App app = new App();

        UIManager.put( "OptionPane.minimumSize", new Dimension( 250, 100 ) );
        UIManager.put( "Panel.background", Color.WHITE );
        UIManager.put( "RadioButton.background", Color.WHITE );
        UIManager.put( "Button.background", Color.LIGHT_GRAY );

        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setAlignmentX( Container.CENTER_ALIGNMENT );
        panel.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );

        JFrame frame = new JFrame( "Phone Conversion" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( panel );
        frame.setResizable( false );

        StartMenuView startView = new StartMenuView( frame, app );
    }
}
