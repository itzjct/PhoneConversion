package Application.Services;

import Application.Domain.*;
import Persistence.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class AppService {
    public final int SALT_LEN = 32;
    public final String HASH_ALGORITHM = "SHA-512";
    public final int NAME_CHAR_MAX = 32;
    public final int EMAIL_CHAR_MAX = 128;
    public final int PW_CHAR_MAX = 128;
    public final int PW_CHAR_MIN = 6;
    private SecureRandom rng;
    private AppDataHandler dataHandler;
    private Pattern emailPattern;

    public AppService() {
        dataHandler = new AppDataHandler();
        rng = new SecureRandom();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        emailPattern = Pattern.compile( emailRegex );
    }

    public boolean validatePhoneNumber( String phoneNumber ) {
        return true;
    }

    public LinkedList<Word> generateWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    public LinkedList<String> validateUser( User user ) {
        LinkedList<String> errors = new LinkedList<String>();

        if ( user.getFirstName() == null || user.getFirstName().equals( "" ) ) {
            errors.addLast( "First name cannot be empty" );
        }
        if ( user.getFirstName() != null && user.getFirstName().length() > NAME_CHAR_MAX ) {
            errors.addLast( "First name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }
        if ( user.getLastName() == null || user.getLastName().equals( "" ) ) {
            errors.addLast( "Last name cannot be empty" );
        }
        if ( user.getLastName() != null && user.getLastName().length() > NAME_CHAR_MAX ) {
            errors.addLast( "Last name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }
        if ( user.getEmail() == null || user.getEmail().equals( "" ) ) {
            errors.addLast( "Email cannot be empty" );
        }
        if ( user.getEmail() != null && user.getEmail().length() > EMAIL_CHAR_MAX ) {
            errors.addLast( "Email cannot exceed " + EMAIL_CHAR_MAX + " characters" );
        }
        if ( user.getEmail() != null && !isValidEmail( user.getEmail() ) ) {
            errors.addLast( "Invalid email" );
        }
        if ( user.getPasswordString() == null || user.getPasswordString().equals( "" ) ) {
            errors.addLast( "Password cannot be empty" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() < PW_CHAR_MIN ) {
            errors.addLast( "Password must be at least " + PW_CHAR_MIN + " characters" );
        }
        if ( user.getPasswordString() != null && user.getPasswordString().length() > PW_CHAR_MAX ) {
            errors.addLast( "Password cannot exceed " + PW_CHAR_MAX + " characters" );
        }

        return errors;
    }

    private boolean isValidEmail( String email ) {
        return emailPattern.matcher( email ).matches();
    }

    public LinkedList<String> validateCompany( Company company ) {
        LinkedList<String> errors = new LinkedList<String>();

        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.addLast( "Company name cannot be empty" );
        }
        if ( company.getName() == null || company.getName().equals( "" ) ) {
            errors.addLast( "Company name cannot exceed " + NAME_CHAR_MAX + " characters" );
        }

        return errors;
    }

    public byte[] getSalt() {
        byte[] salt = new byte[SALT_LEN];
        rng.nextBytes( salt );
        return salt;
    }

    public byte[] hashPassword( String password, byte[] salt ) {
        try {
            MessageDigest md = MessageDigest.getInstance( HASH_ALGORITHM );
            md.update( salt );
            return md.digest( password.getBytes( StandardCharsets.UTF_8 ) );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            return new byte[0];
        }
    }

    public User getUser( String email ) {
        return dataHandler.getUser( email );
    }

    public int storeUser( User user ) {
        return dataHandler.storeUser( user );
    }

    public boolean existsPhoneNumber( String phoneNumber ) {
        return dataHandler.existsPhoneNumber( phoneNumber );
    }

    public int storePhoneNumber( String phoneNumber, int companyId ) {
        return dataHandler.storePhoneNumber( phoneNumber, companyId );
    }

    public LinkedList<Word> getWords( String phoneNumber ) {
        return dataHandler.getWords( phoneNumber );
    }

    public LinkedList<Integer> storeWords( LinkedList<Word> words, String phoneNumber ) {
        return dataHandler.storeWords( words, phoneNumber );
    }
}
