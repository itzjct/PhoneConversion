package Persistence;

import Application.Domain.*;
import java.util.*;

public class AppDataHandler {
    private String connectionString;

    public AppDataHandler( String connectionString ) {
        this.connectionString = connectionString;
    }

    public User getUser( String email ) {
        return new User();
    }

    public int storeUser( User user ) {
        return 0;
    }

    public Company getCompany( int userId ) {
        return new Company();
    }

    public int storeCompany( Company company ) {
        return 0;
    }

    public boolean existsPhoneNumber( String phoneNumber ) {
        return false;
    }

    public Map<String, Iterable<Word>> getPhoneNumbers( int companyId ) {
        return new TreeMap<String, Iterable<Word>>();
    }

    public Iterable<Integer> storePhoneNumbers( Map<String, Iterable<Word>> phoneNumbers, int companyId ) {
        return new LinkedList<Integer>();
    }

    public Iterable<Word> getWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    public Iterable<Integer> storeWords( Iterable<Word> words, String phoneNumber ) {
        return new LinkedList<Integer>();
    }
}
