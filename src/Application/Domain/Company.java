package Application.Domain;

import java.util.*;

public class Company {
    private int id;
    private String name;
    private List<PhoneNumber> phoneNumbers;

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

    public List<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers( List<PhoneNumber> phoneNumbers ) {
        this.phoneNumbers = phoneNumbers;
    }
}
