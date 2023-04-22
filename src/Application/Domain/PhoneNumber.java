package Application.Domain;

import java.util.*;

/**
 * PhoneNumber is the class representing a phone number entity.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class PhoneNumber {

    // Used to store id of phone number
    private int id;

    // Used to store phone number value
    private String phoneNumber;

    // Flag used specifying if phone number has been approved
    private boolean isApproved;

    // Used to store phrases relating to this phone number
    private Set<String> phrases;

    /**
     * Empty constructor
     */
    public PhoneNumber() {
    }

    /**
     * This constructor is used to initialize a PhoneNumber
     * object with the given phone number value
     * 
     * @param phoneNumber
     */
    public PhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This method is used to access id field.
     * 
     * @return An int.
     */
    public int getId() {
        return this.id;
    }

    /**
     * This method is used to update id field.
     * 
     * @param id An int.
     */
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * This method is used to access phoneNumber field.
     * 
     * @return A String.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * This method is used to update phoneNumber field.
     * 
     * @param phoneNumber A String.
     */
    public void setPhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This method is used to access isApproved field.
     * 
     * @return A boolean.
     */
    public boolean getIsApproved() {
        return this.isApproved;
    }

    /**
     * This method is used to update isApproved field.
     * 
     * @param isApproved A boolean.
     */
    public void setIsApproved( boolean isApproved ) {
        this.isApproved = isApproved;
    }

    /**
     * This method is used to access phrases field.
     * 
     * @return A Set of String.
     */
    public Set<String> getPhrases() {
        return this.phrases;
    }

    /**
     * This method is used to update phrases field.
     * 
     * @param phrases A Set of String.
     */
    public void setPhrases( Set<String> phrases ) {
        this.phrases = phrases;
    }

    /**
     * Overriden method. Checks equality based on phoneNumber
     * field.
     * 
     * @param other An Object.
     * @return True if equal.
     *         False if other null or not equal.
     */
    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof PhoneNumber ) ) {
            return false;
        }

        PhoneNumber otherPhone = ( PhoneNumber )other;

        return phoneNumber.equals( otherPhone.getPhoneNumber() );
    }

    /**
     * Overriden method. Generates a hash code based
     * on the phoneNumber field.
     * 
     * @return An int.
     */
    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }
}
