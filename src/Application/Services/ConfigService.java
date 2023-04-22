package Application.Services;

import java.io.*;
import java.util.*;

/**
 * ConfigService is the class responsible for parsing
 * the configuration options from config file.
 * 
 * @author Julian Ceja
 * @author Nathan Ha
 * @author Jacob Osbourne
 * @author Matt Munsinger
 * @version 1.0
 */

public class ConfigService {

    // Constant specifying config file name
    public static final String FILE_NAME = "config.properties";

    // Constants specifying name of config options
    // within config file
    public static final String DB_BASE = "DB_BASE";
    public static final String DB_PATH = "DB_PATH";
    public static final String DICTIONARY_PATH = "DICTIONARY_PATH";

    // Properties object used to access parsed
    // config options
    private Properties properties;

    /**
     * This constructor is used to read config file
     * and parse its properties
     */
    public ConfigService() {
        try {
            FileInputStream in = new FileInputStream( FILE_NAME );
            properties = new Properties();
            properties.load( in );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }
    }

    /**
     * This method gives access to properties object
     * 
     * @return A Properties object.
     */
    public Properties getProperties() {
        return this.properties;
    }

}
