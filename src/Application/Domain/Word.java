package Application.Domain;

import java.util.*;
import static java.util.Map.entry;

public class Word {
    public static final int BELONGS_AREA = 0;
    public static final int BELONGS_PREFIX = 1;
    public static final int BELONGS_SUFIX = 2;
    public static final int BELONGS_AREA_PREFIX = 3;
    public static final int BELONGS_PREFIX_SUFIX = 4;
    public static final int BELONGS_ALL = 5;

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
