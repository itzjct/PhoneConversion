package Application.Domain;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordString;
    private byte[] password;
    private byte[] passwordSalt;
    private Company company = new Company();
    private boolean isAdmin;

    public int getId() {
        return this.id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPasswordString() {
        return this.passwordString;
    }

    public void setPasswordString( String passwordString ) {
        this.passwordString = passwordString;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword( byte[] password ) {
        this.password = password;
    }

    public byte[] getPasswordSalt() {
        return this.passwordSalt;
    }

    public void setPasswordSalt( byte[] passwordSalt ) {
        this.passwordSalt = passwordSalt;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany( Company company ) {
        this.company = company;
    }

    public boolean isIsAdmin() {
        return this.isAdmin;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin( boolean isAdmin ) {
        this.isAdmin = isAdmin;
    }

    public static void copy( User to, User from ) {
        to.setId( from.getId() );
        to.setFirstName( from.getFirstName() );
        to.setLastName( from.getLastName() );
        to.setEmail( from.getEmail() );
        to.setPassword( from.getPassword() );
        to.setPasswordSalt( from.getPasswordSalt() );
        to.setIsAdmin( from.getIsAdmin() );
        Company.copy( to.getCompany(), from.getCompany() );
    }
}
