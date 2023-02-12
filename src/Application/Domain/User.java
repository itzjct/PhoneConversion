package Application.Domain;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Company company;
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

    public String getPassword() {
        return this.password;
    }

    public void setPassword( String password ) {
        this.password = password;
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
}
