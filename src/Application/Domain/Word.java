package Application.Domain;

public class Word {
    private int id;
    private String word;
    private boolean isApproved;

    public Word() {
    }

    public Word( String word ) {
        this.word = word;
    }

    public Word( String word, boolean isApproved ) {
        this.word = word;
        this.isApproved = isApproved;
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

    public boolean isIsApproved() {
        return this.isApproved;
    }

    public boolean getIsApproved() {
        return this.isApproved;
    }

    public void setIsApproved( boolean isApproved ) {
        this.isApproved = isApproved;
    }
}
