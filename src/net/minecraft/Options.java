package net.minecraft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import net.minecraft.modules.Modules;

/**
 *	Class used for saving or loading options . Also directory util class .
 * 	@author Maik
 *	
 */
public final class Options {
	private static Properties properties = new Properties();
    
	private static File dir;
	private static File mcdir;
	public static File optionsFile = new File(getDir(), "options.txt");
	
	static void clear() {
		properties.clear();
	}
	
	public static void setup() {
		addProp("startpage", "http://minecraftsl.tumblr.com");
		addProp("ram_amount", "1024");
	}
	
	public static void addProp(String key, String newvalue) {
		Object value = properties.get(key);
		if (value == null) {
			properties.setProperty(key, newvalue);
		}
	}
	
	public static void load() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(optionsFile));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println("No file found - retry ...");
			save();
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setup();
		save();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			System.out.println("Key: "+key+"; Value: "+value);
		}
	}
	
	public static void save() {
		BufferedOutputStream stream;
		try {
			if (!optionsFile.exists()) {
				System.out.println("No file found , creating ...");
				optionsFile.createNewFile();
			}
			stream = new BufferedOutputStream(new FileOutputStream(optionsFile));
			String comments = "";
			properties.store(stream, comments);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
    
    public static Boolean getAsBoolean(String key, String defaultValue) {
        return Boolean.parseBoolean(get(key, defaultValue));
    }

    public static float getAsFloat(String key) {
        return Float.parseFloat(get(key));
    }
    
    public static float getAsFloat(String key, String defaultValue) {
        return Float.parseFloat(get(key, defaultValue));
    }
    
    public static int getAsInteger(String key) {
        return Integer.parseInt(get(key));
    }
    
    public static int getAsInteger(String key, Integer defaultValue) {
        return Integer.parseInt(get(key, Integer.toString(defaultValue)));
    }
    
	public static void set(String key, String value) {
		properties.setProperty(key, value);
	}
	
    public static void set(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    public static void set(String key, Integer value) {
    	properties.setProperty(key, String.valueOf(value));
    }

	public static EnumOS getOs() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return EnumOS.windows;
		}
		if (s.contains("mac")) {
			return EnumOS.macos;
		}
		if (s.contains("solaris")) {
			return EnumOS.solaris;
		}
		if (s.contains("sunos")) {
			return EnumOS.solaris;
		}
		if (s.contains("linux")) {
			return EnumOS.linux;
		}
		if (s.contains("unix")) {
			return EnumOS.linux;
		} else {
			return EnumOS.unknown;
		}
	}
	
	public static File getDir() {
		if (dir == null) {
			dir = getAppDir("minecraftsl");
		}
		return dir;
	}
	
	public static File getAppDir(String s) {
		String s1 = System.getProperty("user.home", ".");
		File file;
		switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
		case 1: // '\001'
		case 2: // '\002'
			file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
			break;

		case 3: // '\003'
			String s2 = System.getenv("APPDATA");
			if (s2 != null) {
				file = new File(s2, (new StringBuilder()).append(".").append(s).append('/').toString());
			} else {
				file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
			}
			break;

		case 4: // '\004'
			file = new File(s1, (new StringBuilder()).append("Library/Application Support/").append(s).toString());
			break;

		default:
			file = new File(s1, (new StringBuilder()).append(s).append('/').toString());
			break;
		}
		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException((new StringBuilder()).append("The working directory could not be created: ").append(file).toString());
		} else {
			return file;
		}
	}
	
	public static File getMCDir() {
		if (mcdir == null) {
			mcdir = getAppDir("minecraft");
		}
		return mcdir;
	}
	
	public static File getTempDir() {
	    File tempDir = new File(getDir(), "temp");
	    if (!tempDir.exists()) {
	    	tempDir.mkdirs();
	    }
	    return tempDir;
	}
	
	  public static File getSystemTempDir()
	  {
	    File tempDir = new File(System.getProperties().getProperty("java.io.tmpdir"), "minecraftsl");
	    if (!tempDir.exists()) {
	    	tempDir.mkdirs();
	    }
	    return tempDir;
	  }

	  public static File getModulesDir() {
		  File exDir = new File(getDir(), "modules");
		  if (!exDir.exists()) {
		  	exDir.mkdirs();
		  }
		  return exDir;
	  }
	
	public static enum EnumOS {
		linux,
	    solaris,
	    windows,
	    macos,
	    unknown
	}
	
	public static class EnumOSMappingHelper {
		public static final int enumOSMappingArray[]; /* synthetic field */
		
		static 
		{
			enumOSMappingArray = new int[EnumOS.values().length];
			try
			{
				enumOSMappingArray[EnumOS.linux.ordinal()] = 1;
			}
			catch(NoSuchFieldError nosuchfielderror) { }
			try
			{
				enumOSMappingArray[EnumOS.solaris.ordinal()] = 2;
			}
			catch(NoSuchFieldError nosuchfielderror1) { }
			try
			{
				enumOSMappingArray[EnumOS.windows.ordinal()] = 3;
			}
			catch(NoSuchFieldError nosuchfielderror2) { }
			try
			{
				enumOSMappingArray[EnumOS.macos.ordinal()] = 4;
			}
			catch(NoSuchFieldError nosuchfielderror3) { }
		}
	}
	
}
