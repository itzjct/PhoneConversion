package Application.Domain;

import java.util.*;

public class Company {
    private int id;
    private String name;
    private Map<String, Map<Integer, List<Word>>> phoneNumbersToWords = new TreeMap<>();

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

    public Map<String, Map<Integer, List<Word>>> getPhoneNumbersToWords() {
        return this.phoneNumbersToWords;
    }

    public void setPhoneNumbersToWords( Map<String, Map<Integer, List<Word>>> phoneNumbersToWords ) {
        this.phoneNumbersToWords = phoneNumbersToWords;
    }
}
