package org.weitblicker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Janis on 24.10.2016.
 */
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

    private static String getString(String key)
    {
        return options.getProperty(key);
    }

    public static void setString(String key, String value)
    {
        try
        {
            options.setProperty(key, value);
            options.store(new FileOutputStream(new File(System.getProperty("user.dir") + "/options.properties")), "immotours-options");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static String BASE_URI = getString("weitblickapp-server.BASE_URI");
    public static String DB_URL_JPA_WEITBLICK = getString("weitblickapp-server.DB.url.jpa-weitblick");
    public static String DB_URL_JPA_APP = getString("weitblickapp-server.DB.url.jpa-app");
    public static String DB_USER = getString("weitblickapp-server.DB.user");
    public static String DB_PASSWORD = getString("weitblickapp-server.DB.password");
    public static String DB_DDL_GENERATION = getString("weitblickapp-server.DB.ddl-generation");
    public static String DEFAULT_LANGUGAE_STR = getString("weitblickapp-server.DEFAULT_LANGUAGE");
    public static Locale DEFAULT_LANGUAGE = new Locale(DEFAULT_LANGUGAE_STR);
}
