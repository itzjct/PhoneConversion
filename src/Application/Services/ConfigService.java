package Application.Services;

import java.io.*;
import java.util.*;

public class ConfigService {
    public static final String DB_BASE = "DB_BASE";
    public static final String DB_PATH = "DB_PATH";
    public static final String DICTIONARY_PATH = "DICTIONARY_PATH";

    private Properties properties;

    public ConfigService() {
        try {
            String configPath = "config.properties";
            FileInputStream in = new FileInputStream( configPath );
            properties = new Properties();
            properties.load( in );
        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }
    }

    public Properties getProperties() {
        return this.properties;
    }

}
