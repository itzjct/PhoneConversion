package Application.Domain;

import java.util.*;
import static java.util.Map.entry;

public class Word {

    private int id;
    private String word;
    private int startIndex;
    private int endIndex;

    public Word() {
    }

    public Word( String word ) {
        this.word = word;
    }

    public Word( String word, int startIndex, int endIndex ) {
        this.word = word;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getId() {
        return this.id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord( String word ) {
        this.word = word;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex( int startIndex ) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex( int endIndex ) {
        this.endIndex = endIndex;
    }

    // public boolean overlaps( Word other ) {
    //     return this.startIndex <= other.endIndex && this.endIndex >= other.startIndex;
    // }

    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof Word ) ) {
            return false;
        }

        Word otherWord = ( Word )other;

        return word.equals( otherWord.getWord() );
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
