package Application.Services;

import Application.Domain.*;
import Persistence.*;
import java.util.*;

public class AppService {
    private AppDataHandler dataHandler;

    public AppService( String connectionString ) {
        dataHandler = new AppDataHandler( connectionString );
    }

    public boolean validatePhoneNumber( String phoneNumber ) {
        return true;
    }

    public Iterable<String> generateWords( String phoneNumber ) {
        return new LinkedList<String>();
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

    
}
