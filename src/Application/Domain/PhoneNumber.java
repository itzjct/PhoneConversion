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

    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof PhoneNumber ) ) {
            return false;
        }

        PhoneNumber otherPhone = ( PhoneNumber )other;

        return phoneNumber.equals( otherPhone.getPhoneNumber() );
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }
}
