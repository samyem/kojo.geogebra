/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.main;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Stores user settings and options as preferences.
 *  
 * @author Markus Hohenwarter
 * @date May 16, 2007
 */
public class GeoGebraPreferences {		
	
	public static final String AUTHOR = "author";
	
	// worksheet export dialog
	public static final String EXPORT_WS_RIGHT_CLICK = "export_ws_right_click";
	public static final String EXPORT_WS_LABEL_DRAGS = "export_ws_label_drags";
	public static final String EXPORT_WS_RESET_ICON = "export_ws_reset_icon";
	public static final String EXPORT_WS_FRAME_POSSIBLE = "export_ws_frame_possible";
	public static final String EXPORT_WS_SHOW_MENUBAR = "export_ws_show_menubar";
	public static final String EXPORT_WS_SHOW_TOOLBAR = "export_ws_show_toolbar";
	public static final String EXPORT_WS_SHOW_TOOLBAR_HELP = "export_ws_show_toolbar_help";
	public static final String EXPORT_WS_SHOW_INPUT_FIELD = "export_ws_show_input_field";
	//public static final String EXPORT_WS_OFFLINE_ARCHIVE = "export_ws_offline_archive";	
	public static final String EXPORT_WS_GGB_FILE = "export_ws_ggb_file";
	public static final String EXPORT_WS_SAVE_PRINT = "export_ws_save_print";
	
	// picture export dialog
	public static final String EXPORT_PIC_FORMAT = "export_pic_format";
	public static final String EXPORT_PIC_DPI = "export_pic_dpi";
	// public  final String EXPORT_PIC_SCALE = "export_pic_scale";
	
	// print preview dialog
	public static final String PRINT_ORIENTATION = "print_orientation";
	public static final String PRINT_SHOW_SCALE = "print_show_scale";		
	
	
	 // preferences node name for GeoGebra 	 
	 private  Preferences ggbPrefs;
	  {
		  try {
			  ggbPrefs = Preferences.userRoot().node("/geogebra");
		  } catch (Exception e) {
			  // thrown when running unsigned JAR
			  ggbPrefs = null;
		  }
	 }	 
	 private  String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
     
    // special preference keys
	private  final String XML_USER_PREFERENCES = "xml_user_preferences";	
	private  final String TOOLS_FILE_GGT = "tools_file_ggt";	
	private  final String APP_LOCALE = "app_locale";	
	private  final String APP_CURRENT_IMAGE_PATH = "app_current_image_path";
	private  final String APP_FILE_ = "app_file_";		
		
	
	private static GeoGebraPreferences singleton;
	
	public synchronized static GeoGebraPreferences getPref() {
		if (singleton == null)
			singleton = new GeoGebraPreferences();
		return singleton;
	}
	
	
	public  String loadPreference(String key, String defaultValue) {
		return ggbPrefs.get(key, defaultValue);
	}
	
	public  void savePreference(String key, String value) {
		if (key != null && value != null)
			ggbPrefs.put(key, value);
	}
	
	
	/**
     * Returns the path of the first file in the file list 
     */
    public  File getDefaultFilePath() {      	    	
    	File file = new File(ggbPrefs.get(APP_FILE_ + "1", ""));
    	if (file.exists())
    		return file.getParentFile();
    	else
    		return null;
    }       
    
    /**
     * Returns the default image path
     */
    public  File getDefaultImagePath() {      	
    	// image path
		String pathName = ggbPrefs.get(APP_CURRENT_IMAGE_PATH, null);
		if (pathName != null)
			return new File(pathName);
		else
			return null;
    }
    
    /**
     * Saves the currently set locale.
     */
    public  void saveDefaultImagePath(File imgPath) {    
    	try {
    		if (imgPath != null)
    			ggbPrefs.put(APP_CURRENT_IMAGE_PATH, imgPath.getCanonicalPath());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Returns the default locale
     */
    public  Locale getDefaultLocale() {      	
    	// language
    	String strLocale = ggbPrefs.get(APP_LOCALE, null);
    	if (strLocale != null) 
    		return Application.getLocale(strLocale);
    	else
    		return null;    	
    }
    
    /**
     * Saves the currently set locale.
     */
    public  void saveDefaultLocale(Locale locale) {    
    	// save locale (language)
    	ggbPrefs.put(APP_LOCALE, locale.toString());
    }
    
    /**
     * Loads the names of the four last used files from the preferences backing store.
     */
    public  void loadFileList() {
    	// load last four files
    	for (int i=4; i >= 1; i--) {	
    		File file = new File(ggbPrefs.get(APP_FILE_ + i, ""));
    		Application.addToFileList(file);	    		
    	}				    	
    }  
    	
    /**
     * Saves the names of the four last used files.
     */
    public  void saveFileList() {
    	try {    		    		    		    	
	    	// save last four files
	    	for (int i=1; i <= 4; i++) {	    		
	    		File file = Application.getFromFileList(i-1);
	    		if (file != null)
	    			ggbPrefs.put(APP_FILE_ + i, file.getCanonicalPath());
	    		else
	    			ggbPrefs.put(APP_FILE_ + i, "");
	    	}				    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }    
   
	/**
     * Inits factory default XML by taking the preferences XML of this
     * virign application    		
     */
    public  void initDefaultXML(Application app) {    	    	
    	if (XML_GGB_FACTORY_DEFAULT == null)
    		XML_GGB_FACTORY_DEFAULT = app.getPreferencesXML();    
    }
    
    /**
     * Saves preferences by taking the application's current values. 
     */
    public  void saveXMLPreferences(Application app) {
    	// preferences xml
    	String xml = app.getPreferencesXML();
    	ggbPrefs.put(XML_USER_PREFERENCES, xml);  
    
    	// store current tools including icon images as ggt file (byte array)
    	putByteArray(ggbPrefs, TOOLS_FILE_GGT, app.getMacroFileAsByteArray());
    	
    	try {
    		ggbPrefs.flush();
    	} catch (Exception e) {
    		Application.debug(e+"");
    	}
    }
    
    /**
     * Breaks up byte array value into pieces and calls prefs.putByteArray(prefs, key+k, piece_k)
     * for every piece.
     */
    private  void putByteArray(Preferences prefs, String key, byte [] value) {
    	// byte array must not be longer than 3/4 of max value length
    	int max_length = (int) Math.floor(Preferences.MAX_VALUE_LENGTH * 0.75);
    	
    	// value array is small enough
    	if (value == null || value.length < max_length) {
    		ggbPrefs.putByteArray(key, value);

    		// remove possible old part keys
    		int partCount = 0;    		
    		while (true) {
    			byte [] temp = ggbPrefs.getByteArray(key+partCount, null);
    			if (temp != null) {
    				ggbPrefs.remove(key+partCount);
    				partCount++;
    			} else
    				break;
    		}
    	} 
    	
    	// break value array up into smaller pieces
    	else {
    		// delete key value    		
    		ggbPrefs.remove(key);
  
    		byte [] bytePart = new byte[max_length];
    		int pos = 0;
    		int partCount = 0;
    		while (pos + max_length <= value.length) {        			
    			for (int k=0; k < max_length; k++, pos++) {
    				bytePart[k] = value[pos];
    			}        			    		
    			
    			// put piece key + partCount
    			partCount++;
    			ggbPrefs.putByteArray(key + partCount, bytePart);
    		}
    		
    		// write last part
    		if (pos < value.length) {    			
    			bytePart = new byte[value.length - pos];
    			
    			for (int k=0; pos < value.length; k++, pos++) {
    				bytePart[k] = value[pos];
    			} 

    			// put piece key + partCount
    			partCount++;
    			ggbPrefs.putByteArray(key + partCount, bytePart);
    		}
    	}
    	
    	try {
    		ggbPrefs.flush();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Breaks up byte array value into pieces and calls prefs.putByteArray(prefs, key+k, piece_k)
     * for every piece.
     */
    private  byte [] getByteArray(Preferences prefs, String key, byte [] def) {    	    	
    	byte [] ret = ggbPrefs.getByteArray(key, null);
    	    
    	if (ret != null) {
    		// no parts: return byte array
    		return ret;
    	} 
    	// we had parts, get them and glue them together
    	else {    		    		    
    		try {
    			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    		int partCount = 1;
	    		while (true) {
	    			ret = ggbPrefs.getByteArray(key + partCount, null);
	    			if (ret != null) {
	    	    		bos.write(ret);
	    				partCount++;
	    			} else {
	    				break;
	    			}
	    		}		    		
	    		bos.flush();
	    		if (bos.size() > 0)
		        	ret = bos.toByteArray();
    		} 
    		catch (Exception e) {
    			e.printStackTrace();
    			ret = null;
    		}    	
    		    
    		if (ret != null)
	        	return ret;
    		else
    			return def;
    	}
    }
    
    /**
     * Loads XML preferences (empty construction with GUI and kernel settings) and sets application accordingly.
     * This method clears the current construction in the application.
     * Note: the XML string used is the same as for ggb files. 
     */
    public  void loadXMLPreferences(Application app) {  
    	app.setWaitCursor();  
    	
    	// load this preferences xml file in application
    	try {    		      		    	    
    		// load tools from ggt file (byte array)
        	byte [] ggtFile = getByteArray(ggbPrefs, TOOLS_FILE_GGT, null);
        	app.loadMacroFileFromByteArray(ggtFile, true);
        	    		
    		// load preferences xml
        	String xml = ggbPrefs.get(XML_USER_PREFERENCES, XML_GGB_FACTORY_DEFAULT);        
    		app.setXML(xml, true);	    		    	
    	} catch (Exception e) {	    		
    		e.printStackTrace();
    	}    	

    	app.setDefaultCursor();
    }
    
    
    
    /**
     * Clears all user preferences.   
     */
    public  void clearPreferences() {
    	try {
    		ggbPrefs.clear();    		
    		ggbPrefs.flush();
    	} catch (Exception e) {
    		Application.debug(e+"");
    	}
    }
    	     	 
}