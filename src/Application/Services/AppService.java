package Application.Services;

import Application.Domain.*;
import Persistence.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class AppService {
    public final int SALT_LEN = 32;
    public final String HASH_ALGORITHM = "SHA-512";
    public SecureRandom rng;
    private AppDataHandler dataHandler;

    public AppService() {
        dataHandler = new AppDataHandler();
        rng = new SecureRandom();
    }

    public boolean validatePhoneNumber( String phoneNumber ) {
        return true;
    }

    public LinkedList<Word> generateWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    public LinkedList<String> validateUser( User user ) {
        LinkedList<String> errors = new LinkedList<String>();
        return errors;
    }

    public LinkedList<String> validateCompany( Company company ) {
        LinkedList<String> errors = new LinkedList<String>();
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

    public int addPhoneNumber( String phoneNumber, int companyId ) {
        Map<String, LinkedList<Word>> tempMap = new TreeMap<String, LinkedList<Word>>();
        tempMap.put( phoneNumber, null );
        return ( dataHandler.storePhoneNumbers( tempMap, companyId ) ).getFirst();
    }

    public LinkedList<Word> getWords( String phoneNumber ) {
        return dataHandler.getWords( phoneNumber );
    }

    public LinkedList<Integer> storeWords( LinkedList<Word> words, String phoneNumber ) {
        return dataHandler.storeWords( words, phoneNumber );
    }
}
