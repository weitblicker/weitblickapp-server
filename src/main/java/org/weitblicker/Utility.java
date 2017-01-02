package org.weitblicker;

import java.util.IllformedLocaleException;
import java.util.Locale;

public class Utility {
	
    public static Locale getLanguage(String language) throws IllformedLocaleException{
    	Locale.Builder languageBuilder = new Locale.Builder();
		languageBuilder.setLanguage(language);
		return languageBuilder.build();
    }

}
