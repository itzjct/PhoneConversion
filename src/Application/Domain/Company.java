package Application.Domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Company is the class representing a company entity.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class Company {

    // Used to store id of company
    private int id;

    // Used to store name of company
    private String name;

    // Used to store set of phone number objects
    private Set<PhoneNumber> phoneNumbers;

    /**
     * This method gives access to id field.
     * 
     * @return An int
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
     * This method gives access to name field.
     * 
     * @return A String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method updates name field.
     * 
     * @param name A String.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * This method gives access to phoneNumbers field.
     * 
     * @return A Set of PhoneNumber.
     */
    public Set<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    /**
     * This method updates phoneNumbers field.
     * 
     * @param phoneNumbers A Set of PhoneNumber.
     */
    public void setPhoneNumbers( Set<PhoneNumber> phoneNumbers ) {
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * This method retrieves a phone number object from
     * phone numbers set of this object.
     * 
     * @param phoneNumber A String.
     * @return A PhoneNumber object.
     */
    public PhoneNumber getPhoneNumber( String phoneNumber ) {
        if ( !phoneNumbers.contains( new PhoneNumber( phoneNumber ) ) ) {
            return null;
        }
        return phoneNumbers.stream().filter( x -> x.getPhoneNumber().equals( phoneNumber ) ).findFirst().get();
    }

    /**
     * This method retrives a set of approved phone numbers
     * from phone numbers set of this object.
     * 
     * @return A Set of PhoneNumber.
     */
    public Set<PhoneNumber> getApprovedPhoneNumbers() {
        return phoneNumbers.stream().filter( x -> x.getIsApproved() ).collect( Collectors.toSet() );
    }
}
