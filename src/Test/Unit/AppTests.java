package Test.Unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import Application.Domain.*;
import Persistence.AppDataHandler;

import java.util.*;

public class AppTests {
    private String connectionString = "jdbc:sqlite:C:/Users/Julian/Desktop/School/Software Engineering/Project/db/word_conversion.db";
    private AppDataHandler appDataHandler = new AppDataHandler( connectionString );
    private App app = new App( appDataHandler );

    @Test
    public void generateWords_ValidPhoneNumber_ReturnsMapOfWord() {
        PhoneNumber phoneNumber = new PhoneNumber( "8652222283" );

        Map<Integer, List<Word>> result = app.generateWords( phoneNumber );

        assertTrue( true );
    }
}
