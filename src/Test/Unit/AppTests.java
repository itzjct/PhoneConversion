// package Test.Unit;

// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.*;

// import Application.Domain.*;
// import Persistence.AppDataHandler;

// import java.util.*;

// public class AppTests {
//     private String connectionString = "jdbc:sqlite:C:/Users/Julian/Desktop/School/Software Engineering/Project/db/word_conversion.db";
//     private AppDataHandler appDataHandler = new AppDataHandler( connectionString );
//     private App app = new App( appDataHandler );

//     private List<Word> getWords() {
//         List<Word> words = new LinkedList<>();
//         words.add( new Word( "t1", false ) );
//         words.add( new Word( "t2", true ) );
//         words.add( new Word( "t3", false ) );
//         return words;
//     }

//     @Test
//     public void getPhoneNumbers_ValidCompany_ReturnsPhoneNumbers() {
//         Company company = appDataHandler.getCompany( "SEARS" );

//         List<String> result = app.getPhoneNumbers( company );

//         assertTrue( result.size() > 0 );
//     }

//     @Test
//     public void getWords_ValidPhoneNumber_ReturnsWords() {
//         String phoneNumber = "1112223333";

//         List<Word> result = app.getWords( phoneNumber );

//         assertTrue( result.size() > 0 );
//     }

//     @Test
//     public void storeWords_ValidWordsAndPhoneNumber_ReturnsTrue() {
//         List<Word> words = getWords();

//         boolean result = app.storeWords( words, "1112223333" );

//         assertTrue( result );
//     }
// }
