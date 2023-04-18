package Application.Domain;

import java.util.*;

public class Company {
    private int id;
    private String name;
    private Set<PhoneNumber> phoneNumbers;

    public int getId() {
        return this.id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Set<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers( Set<PhoneNumber> phoneNumbers ) {
        this.phoneNumbers = phoneNumbers;
    }

    public PhoneNumber getPhoneNumber( String phoneNumber ) {
        if ( !phoneNumbers.contains( new PhoneNumber( phoneNumber ) ) ) {
            return null;
        }
        return phoneNumbers.stream().filter( x -> x.getPhoneNumber().equals( phoneNumber ) ).findFirst().get();
    }
}
