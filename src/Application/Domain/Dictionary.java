package Application.Domain;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Dictionary {
    private Set<String> words;

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

    public List<String> filterValidWords( List<String> candidates ) {
        return candidates.stream()
                .filter( words::contains )
                .collect( Collectors.toList() );
    }

    public boolean contains( String word ) {
        return words.contains( word );
    }
}
