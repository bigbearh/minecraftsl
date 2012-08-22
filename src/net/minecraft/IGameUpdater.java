package net.minecraft;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;

import net.minecraft.Options.EnumOSMappingHelper;

/**
 *	Interface for GameUpdaters , inspired by Markus Notch Persson's  "GameUpdater.class"
 *	@author Maik
 *	@author Notch ( inspired by )
 *	
 */
public interface IGameUpdater extends Runnable {
	
	
	public static final int STATE_INIT = 1;
	public static final int STATE_DETERMINING_PACKAGES = 2;
	public static final int STATE_CHECKING_CACHE = 3;
	public static final int STATE_DOWNLOADING = 4;
	public static final int STATE_EXTRACTING_PACKAGES = 5;
	public static final int STATE_UPDATING_CLASSPATH = 6;
	public static final int STATE_SWITCHING_APPLET = 7;
	public static final int STATE_INITIALIZE_REAL_APPLET = 8;
	public static final int STATE_START_REAL_APPLET = 9;
	public static final int STATE_DONE = 10;
	
	//Constructor : public IGameUpdater(String latestVersion, String mainGameUrl) {...}
	
	public void init();

	public String generateStacktrace(Exception paramException);
	
	public String getDescriptionForState();
	
	public String trimExtensionByCapabilities(String paramString)	;
	
	public void loadJarURLs() throws Exception;
	
	public void run();
	
	public void checkShouldUpdate();
	
	public String readVersionFile(File versionFile);
	
	public void writeVersionFile(File versionFile, String version) throws Exception;
	
	public void updateClassPath(File binDir) throws Exception;
	
	public void unloadNatives(String nativeDir);
	
	public Applet createApplet() throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	public void downloadJars(String binDir) throws Exception;
	
	public InputStream getJarInputStream(String jarName, final URLConnection jarConnection) throws Exception;
	
	public void extractLZMA(String from, String to) throws Exception;
	
	public void extractPack(String from, String to) throws Exception;
	
	public void extractJars(String binDir) throws Exception;
	
	public void extractNatives(String binDir) throws Exception;
	
	//Usually static
	public void validateCertificateChain(Certificate[] array1, Certificate[] array2) throws Exception;
	
	public String getJarName(URL jarURL);
	
	public String getFileName(URL fileURL);
	
	public void fatalErrorOccured(String description, Exception exception);
	
	/**
	 * @deprecated Use canForceOffline(); because of checking for natives , use this only for checking for the version file .
	 */
	public boolean canPlayOffline();
	
	//Special method list for Launcher , added by community//
	
	/**
	 * Checks if offline mode can be forced . First checks if can play in offline mode , then if it can force offline mode .
	 * @return true if offline mode can be forced , false if some files are missing .
	 */
	public boolean canForceOffline();
	
	/**
	 * Gets the OS natives and adds the file prefix .
	 * @param dir Prefix to add ( Directory )
	 * @return List of natives in directory dir
	 */
	public File[] getOSNatives(File dir);

	/**
	 * Utility method to check if various files exist in the directory dir .
	 * @param dir Directory to check files in
	 * @param list Files that should exist to return true
	 * @return true if all files from the list are existing in dir or in subfolders of it
	 */
	//Usually static
	public boolean contains(File dir, File... list);
	
	/**
	 * Utility method to check if various files exist in the directory dir . <p>
	 * 
	 * Private method , ran internally .
	 * @param dir Directory to check files in
	 * @param list Files that should exist
	 * @return count of existing files of the given list in the given dir
	 */
	//Usually static
	public int contains0(File dir, File... list);
	
}