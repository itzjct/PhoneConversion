package Application.Domain;

import java.util.*;

public class Company {
    private int id;
    private String name;
    private Map<String, List<Word>> phoneNumbersToWords = new TreeMap<String, List<Word>>();

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

    public Map<String, List<Word>> getPhoneNumbersToWords() {
        return this.phoneNumbersToWords;
    }

    public void setPhoneNumbersToWords( Map<String, List<Word>> phoneNumbersToWords ) {
        this.phoneNumbersToWords = phoneNumbersToWords;
    }

    public static void copy( Company to, Company from ) {
        to.setId( from.getId() );
        to.setName( from.getName() );
        to.getPhoneNumbersToWords().clear();
        for ( Map.Entry<String, List<Word>> entry: from.getPhoneNumbersToWords().entrySet() ) {
            to.getPhoneNumbersToWords().put( entry.getKey(), entry.getValue() );
        }
    }
}
