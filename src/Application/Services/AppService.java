package Application.Services;

import Application.Domain.*;
import Persistence.*;
import java.util.*;

public class AppService {
    private AppDataHandler dataHandler;

    public AppService() {
        dataHandler = new AppDataHandler();
    }

    public boolean validatePhoneNumber( String phoneNumber ) {
        return true;
    }

    public LinkedList<Word> generateWords( String phoneNumber ) {
        return new LinkedList<Word>();
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
