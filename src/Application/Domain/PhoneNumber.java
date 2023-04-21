package Application.Domain;

import java.util.*;

public class PhoneNumber {
    private int id;
    private String phoneNumber;
    // private Set<Word> words;
    private Set<String> phrases;

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

    public Set<String> getPhrases() {
        return this.phrases;
    }

    public void setPhrases( Set<String> phrases ) {
        this.phrases = phrases;
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
