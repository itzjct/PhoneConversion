package Application.Domain;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Word is the class responsible for reading and maintaining
 * a set of valid English words.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class Dictionary {

    // Used to store valid English words
    private Set<String> words;

    /**
     * This constructor is used to read dictionary txt file
     * and load set of words.
     * 
     * @param path A String representing path of dictionary file.
     */
    public Dictionary( String path ) {
        try {
            words = new HashSet<>( Files.lines( Paths.get( path ) )
                    .map( String::toUpperCase )
                    .collect( Collectors.toList() ) );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            words = new HashSet<>();
        }
    }

    /**
     * This method filters a list of candidate words and returns
     * only those found in the set of valid English words.
     * 
     * @param candidates A List of String to be filtered.
     * @return A List of String.
     */
    public List<String> filterValidWords( List<String> candidates ) {
        return candidates.stream()
                .filter( words::contains )
                .collect( Collectors.toList() );
    }

    /**
     * This method checks if a given word is found
     * within set of valid English words.
     * 
     * @param word A String.
     * @return True if word is found within word set.
     *         False otherwise.
     */
    public boolean contains( String word ) {
        return words.contains( word );
    }
}
