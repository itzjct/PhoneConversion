package Test.Unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import Application.Domain.*;
import Persistence.AppDataHandler;
import Presentation.AppGUI;

import java.util.*;

public class AppTests {
    private String connectionString = "jdbc:sqlite:C:/Users/Julian/Desktop/School/Software Engineering/Project/db/word_conversion.db";
    private AppDataHandler appDataHandler = new AppDataHandler( connectionString );
    private App app = new App( appDataHandler );

    @Test
    public void generateWords_ValidPhoneNumber_ReturnsMapOfWord() {
        PhoneNumber phoneNumber = new PhoneNumber( "8652255669" );

        Set<Word> result = app.generateWords( phoneNumber );

        assertTrue( true );
    }

    @Test
    public void test() {
        PhoneNumber phoneNumber = new PhoneNumber( "8652255669" );
        Set<Word> words = app.generateWords( phoneNumber );
        // Word word = words.stream().filter( x -> x.getStartIndex() == 6 &&
        // x.getEndIndex() == 9 ).findFirst().get();
        Word word = words.stream().filter( x -> x.getStartIndex() == 3 &&
                x.getEndIndex() == 5 ).findFirst().get();
        // Word word = words.stream().filter( x -> x.getStartIndex() == 0 &&
        // x.getEndIndex() == 2 ).findFirst().get();
        // Word word = words.stream().filter( x -> x.getWord().equals( "CALL" )
        // ).findFirst().get();

        List<List<Word>> result = app.generatePhrases( word, words );

        for ( List<Word> i : result ) {
            for ( Word j : i ) {
                System.out.print( j.getWord() + " " );
            }
            System.out.println();
        }

        assertTrue( true );
    }
}
