package Application.Domain;

/**
 * Word is the class representing a word entity.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class Word {

    // Used to store id of word
    private int id;

    // Used to store word value
    private String word;

    // Used to store start index of word within a phone number string
    private int startIndex;

    // Used to store end index of word within a phone number string
    private int endIndex;

    /**
     * Empty constructor.
     */
    public Word() {
    }

    /**
     * This constructor initializes a Word object
     * with the given word value.
     * 
     * @param word A String.
     */
    public Word( String word ) {
        this.word = word;
    }

    /**
     * This constructor initializes a Word object
     * with the given word value, start index, and
     * end index.
     * 
     * @param word       A String.
     * @param startIndex An int.
     * @param endIndex   An int.
     */
    public Word( String word, int startIndex, int endIndex ) {
        this.word = word;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * This method gives access to id field.
     * 
     * @return An int.
     */
    public int getId() {
        return this.id;
    }

    /**
     * This method updates id field.
     * 
     * @param id An int.
     */
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * This method gives access to word field.
     * 
     * @return A String.
     */
    public String getWord() {
        return this.word;
    }

    /**
     * This method updates word field.
     * 
     * @param word A String.
     */
    public void setWord( String word ) {
        this.word = word;
    }

    /**
     * This method gives access to startIndex field.
     * 
     * @return An int.
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * This method updates startIndex field.
     * 
     * @param startIndex An int.
     */
    public void setStartIndex( int startIndex ) {
        this.startIndex = startIndex;
    }

    /**
     * This method gives access to endIndex field.
     * 
     * @return An int.
     */
    public int getEndIndex() {
        return this.endIndex;
    }

    /**
     * This method updates endIndex field.
     * 
     * @param endIndex An Int.
     */
    public void setEndIndex( int endIndex ) {
        this.endIndex = endIndex;
    }

    /**
     * Overriden method. Checks equality based on word field
     * 
     * @param other An Object.
     * @return True if equal.
     *         False if other is null or not equal.
     */
    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof Word ) ) {
            return false;
        }

        Word otherWord = ( Word )other;

        return word.equals( otherWord.getWord() );
    }

    /**
     * Overriden method. Generates a hash code based
     * on the word field.
     * 
     * @return An int.
     */
    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
