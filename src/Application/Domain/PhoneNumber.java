package Application.Domain;

import java.util.*;

public class PhoneNumber {
    private int id;
    private String phoneNumber;
    private Set<Word> words;

    public PhoneNumber() {

    }

    public PhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return this.id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Word> getWords() {
        return this.words;
    }

    public void setWords( Set<Word> words ) {
        this.words = words;
    }
}
