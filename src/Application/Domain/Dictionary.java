package Application.Domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Dictionary {
    private Set<String> words;

    public Dictionary( String path ) throws IOException {
        words = new HashSet<>( Files.lines( Paths.get( path ) )
                .map( String::toUpperCase ) // Convert words to uppercase
                .collect( Collectors.toList() ) );
    }

    public List<String> filterValidWords( List<String> candidates ) {
        return candidates.stream()
                .filter( words::contains )
                .collect( Collectors.toList() );
    }

    public List<String> filterCompanyRelatedWords( List<String> candidates, List<String> companyKeywords ) {
        return candidates.stream()
                .filter( word -> companyKeywords.stream().anyMatch( keyword -> word.contains( keyword.toUpperCase() ) ) )
                .collect( Collectors.toList() );
    }
}
