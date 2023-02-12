package Persistence;

import Application.Domain.*;
import java.util.*;

public class AppDataHandler {
    private final String connectionString = "";

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

    public Map<String, LinkedList<Word>> getPhoneNumbers( int companyId ) {
        return new TreeMap<String, LinkedList<Word>>();
    }

    public LinkedList<Integer> storePhoneNumbers( Map<String, LinkedList<Word>> phoneNumbers, int companyId ) {
        return new LinkedList<Integer>();
    }

    public LinkedList<Word> getWords( String phoneNumber ) {
        return new LinkedList<Word>();
    }

    public LinkedList<Integer> storeWords( LinkedList<Word> words, String phoneNumber ) {
        return new LinkedList<Integer>();
    }
}
