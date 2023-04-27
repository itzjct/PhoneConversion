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
        // panel.setBackground( Color.WHITE );

        JFrame frame = new JFrame( "Phone Conversion" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( panel );
        frame.setResizable( false );

        StartMenuView startView = new StartMenuView( frame, app );
        // RegisterView registerView = new RegisterView( frame, app );
        // LoginView loginView = new LoginView( frame, app );
        // UserMenuView umv = new UserMenuView( frame, app );
        // GenerateView gv = new GenerateView( frame, app );
        // ApproveView av = new ApproveView( frame, app );
        // PhonesView pv = new PhonesVie/w( frame, app );
        // ApprovedPhrasesView apv = new ApprovedPhrasesView( frame, app );
    }
}
