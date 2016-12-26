package org.weitblicker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class Options
{
	private static Properties options;
	
    static
    {
        try
        {
            options = new Properties();
            options.load(new FileInputStream(new File(System.getProperty("user.dir") + "/options.properties")));
            for(String key : options.stringPropertyNames())
            {
                System.setProperty(key, options.getProperty(key));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static String getString(String key, String defaultValue) throws IllegalArgumentException
    {
        String value = (String) options.getOrDefault(key, defaultValue);  
        
		// TODO log this system out print 

        if(value == null){
        	throw new IllegalArgumentException("The option value for the key :\""+ key + "\" is not set!");
        }else{
        	if(value.equals(defaultValue)){
        		System.out.println("option key \"" + key + "\" - use default: \"" + defaultValue + "\".");
        	}else{
        		System.out.println("option key \"" + key + "\" - set to: \"" + value + "\".");
        	}
        }
        return value;
    }

    public static void setString(String key, String value)
    {
        try
        {
            options.setProperty(key, value);
            options.store(new FileOutputStream(new File(System.getProperty("user.dir") + "/options.properties")), "weitblick server options");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private static final String BASE_URI_KEY = "weitblickapp-server.BASE_URI";
    private static final String BASE_URI_DEFAULT = "http://localhost:8180/";
    public static final String BASE_URI = getString(BASE_URI_KEY, BASE_URI_DEFAULT);

    private static final String DB_URL_JPA_WEITBLICK_KEY = "weitblickapp-server.DB.url.jpa-weitblick";
    private static final String DB_URL_JPS_WEITBLICK_DEFAULT = "jdbc:mysql://localhost/weitblick";
    public static final String DB_URL_JPA_WEITBLICK = getString(DB_URL_JPA_WEITBLICK_KEY, DB_URL_JPS_WEITBLICK_DEFAULT);
    
    private static final String DB_USER_KEY = "weitblickapp-server.DB.user";
    private static final String DB_USER_DEFAULT = "weitblick";
    public static final String DB_USER = getString(DB_USER_KEY, DB_USER_DEFAULT);
    
    private static final String DB_PASSWORD_KEY = "weitblickapp-server.DB.password";
    public static final String DB_PASSWORD = getString(DB_PASSWORD_KEY, null);
    
    private static final String DB_DDL_GENERATION_KEY = "weitblickapp-server.DB.ddl-generation";
    private static final String DB_DDL_GENERATION_DEFAULT = "create-or-extend-tables";
    public static final String DB_DDL_GENERATION = getString(DB_DDL_GENERATION_KEY, DB_DDL_GENERATION_DEFAULT);
    
    private static final String DEFAULT_LANGUGAE_STR_KEY = "weitblickapp-server.DEFAULT_LANGUAGE";
    private static final String DEFAULT_LANGUGAE_STR_DEFAULT = "en";
    private static final String DEFAULT_LANGUGAE_STR = getString(DEFAULT_LANGUGAE_STR_KEY, DEFAULT_LANGUGAE_STR_DEFAULT);
    public static final Locale DEFAULT_LANGUAGE = new Locale(DEFAULT_LANGUGAE_STR);
    
    private static final String KEYSTORE_LOCATION_KEY = "weitblickapp-server.keystore.location";
    public static final String KEYSTORE_LOCATION = getString(KEYSTORE_LOCATION_KEY, null);
    
    private static final String KEYSTORE_PASSWORD_KEY = "weitblickapp-server.keystore.password";
    public static final String KEYSTORE_PASSWORD = getString(KEYSTORE_PASSWORD_KEY, null);

    private static final String USE_SSL_KEY = "weitblickapp-server.use_ssl";
    private static final String USE_SSL_STR = getString(USE_SSL_KEY, "false");
    public static final boolean USE_SSL = Boolean.valueOf(USE_SSL_STR);

}
