package Application.Domain;

import java.util.*;
import static java.util.Map.entry;

public class Word {
    private int id;
    private String word;
    private int belongsTo;

    public Word() {
    }

    public Word( String word ) {
        this.word = word;
    }

    public Word( String word, int belongsTo ) {
        this.word = word;
        this.belongsTo = belongsTo;
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

    public int getBelongsTo() {
        return this.belongsTo;
    }

    public void setBelongsTo( int belongsTo ) {
        this.belongsTo = belongsTo;
    }

    public static Map<Integer, List<Word>> getWordMap() {
        return Map.ofEntries(
                entry( 0, new LinkedList<Word>() ),
                entry( 1, new LinkedList<Word>() ),
                entry( 2, new LinkedList<Word>() ) );
    }
}
