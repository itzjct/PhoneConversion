package Application.Domain;

/**
 * User is the class representing a user entity.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class User {

    // Used to store id of user
    private int id;

    // Used to store first name of user
    private String firstName;

    // Used to store last name of user
    private String lastName;

    // Used to store email of user
    private String email;

    // Used to store string password of user
    private String passwordString;

    // Used to store byte password of user
    private byte[] password;

    // Used to store password salt of user
    private byte[] passwordSalt;

    // Used to store company info of user
    private Company company = new Company();

    // Flag used to specify admin role of user
    private boolean isAdmin;

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
     * This method gives access to firstName field.
     * 
     * @return A String.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * This method updates firstName field
     * 
     * @param firstName A String.
     */
    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    /**
     * This method gives access to LastName field.
     * 
     * @return A String.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * This method updates lastName field.
     * 
     * @param lastName A String.
     */
    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    /**
     * This method gives access to email field.
     * 
     * @return A String.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * This method updates email field.
     * 
     * @param email A string.
     */
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * This method gives access to passwordString field.
     * 
     * @return A String.
     */
    public String getPasswordString() {
        return this.passwordString;
    }

    /**
     * This method updates passwordString field.
     * 
     * @param passwordString A String.
     */
    public void setPasswordString( String passwordString ) {
        this.passwordString = passwordString;
    }

    /**
     * This method gives access to password field.
     * 
     * @return A byte array.
     */
    public byte[] getPassword() {
        return this.password;
    }

    /**
     * This method updates password field.
     * 
     * @param password A byte array.
     */
    public void setPassword( byte[] password ) {
        this.password = password;
    }

    /**
     * This method gives access passwordSalt field.
     * 
     * @return A byte array.
     */
    public byte[] getPasswordSalt() {
        return this.passwordSalt;
    }

    /**
     * This method updates passwordSalt field.
     * 
     * @param passwordSalt A byte array.
     */
    public void setPasswordSalt( byte[] passwordSalt ) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * This method gives access to company field.
     * 
     * @return A Company object.
     */
    public Company getCompany() {
        return this.company;
    }

    /**
     * This method updates company field.
     * 
     * @param company A Company field.
     */
    public void setCompany( Company company ) {
        this.company = company;
    }

    /**
     * This method gives access to isAdmin field.
     * 
     * @return A boolean.
     */
    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    /**
     * This method updates isAdmin field.
     * 
     * @param isAdmin A boolean.
     */
    public void setIsAdmin( boolean isAdmin ) {
        this.isAdmin = isAdmin;
    }
}
