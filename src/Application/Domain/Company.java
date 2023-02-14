package Application.Domain;

import java.util.*;

public class Company {
    private int id;
    private String name;
    private Map<String, LinkedList<Word>> phoneNumbersToWords = new TreeMap<String, LinkedList<Word>>();

    public int getId() {
        return this.id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Map<String, LinkedList<Word>> getPhoneNumbersToWords() {
        return this.phoneNumbersToWords;
    }

    public void setPhoneNumbersToWords( Map<String, LinkedList<Word>> phoneNumbersToWords ) {
        this.phoneNumbersToWords = phoneNumbersToWords;
    }

    public static void copy( Company to, Company from ) {
        to.setId( from.getId() );
        to.setName( from.getName() );
        to.getPhoneNumbersToWords().clear();
        for ( Map.Entry<String, LinkedList<Word>> entry: from.getPhoneNumbersToWords().entrySet() ) {
            to.getPhoneNumbersToWords().put( entry.getKey(), entry.getValue() );
        }
    }
}
