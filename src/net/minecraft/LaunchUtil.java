package net.minecraft;

import java.applet.Applet;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *	Custom class that lacks of better name . <p>
 *	 
 *	It is the utility for game updating and launching . It contains methods and fields for updating Minecraft and launching it in an applet .
 *	@author Maik
 *	
 */
public final class LaunchUtil {
	
	/**
	 * Class used for internal GameUpdater
	 */
	public static final Class<? extends IGameUpdater> clazz_updater = GameUpdater.class;
	/**
	 * Internal GameUpdater for custom implementations .
	 */
	private static IGameUpdater updater;
	
	/**
	 * Class used for internal Launcher
	 */
	public static final Class<? extends ALauncher> clazz_launcher = Launcher.class;
	/**
	 * Internal Launcher for custom implementations .
	 */
	private static ALauncher launcher;
	
	//GAMEUPDATER FIELDS//
	
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
	
	public static int percentage;
	public static int currentSizeDownload;
	public static int totalSizeDownload;
	public static int currentSizeExtract;
	public static int totalSizeExtract;
	public static URL[] urlList;
	public static ClassLoader classLoader;
	public static Thread loaderThread;
	public static Thread animationThread;
	public static boolean fatalError;
	public static String fatalErrorDescription;
	public static String subtaskMessage = "";
	public static int state = 1;

	public static boolean lzmaSupported = false;
	public static boolean pack200Supported = false;

	public static String[] genericErrorMessage = { "An error occured while loading the applet.", "Please contact support to resolve this issue.", "<placeholder for error message>" };
	public static boolean certificateRefused;
	public static String[] certificateRefusedMessage = { "Permissions for Applet Refused.", "Please accept the permissions dialog to allow", "the applet to continue the loading process." };

	public static boolean natives_loaded = false;
	public static boolean forceUpdate = false;
	public static String latestVersion;
	public static String mainGameUrl;
	public static boolean pauseAskUpdate;
	public static boolean shouldUpdate;
	
	////////////////////////////
	
	//LAUNCHER FIELDS//
	
	public static Map<String, String> customParameters = new HashMap<String, String>();
	public static boolean gameUpdaterStarted = false;
	public static Applet applet;
	public static Image bgImage;
	public static boolean active = false;
	public static int context = 0;
	public static boolean hasMouseListener = false;
	public static VolatileImage img;
	
	////////////////////////
	
	private LaunchUtil() {
	}
	
	/**
	 * Gives a new instance of the default instanceof IGameUpdater class , or if one was existing the existing one ignoring arguments .
	 * @param latestVersion Latest version of Minecraft
	 * @param mainGameUrl Main game url of Minecraft ( minecraft.jar with arguments )
	 * @return Instance of the default IGameUpdater class , otherwise null if something happened .
	 */
	public static IGameUpdater newGameUpdater(String latestVersion, String mainGameUrl) {
		if (updater != null) {
			return updater;
		}
		try {
			LaunchUtil.latestVersion = latestVersion;
			LaunchUtil.mainGameUrl = mainGameUrl;
			updater = clazz_updater.newInstance();
			return updater;
		} catch (Exception e) {
			return updater;
		}
	}
	
	/**
	 * Gives the existing instance of the default class instanceof IGameUpdater .
	 * @return Instance of the default IGameUpdater class , or null if undefined
	 */
	public static IGameUpdater getGameUpdater() {
		return updater;
	}
	
	/**
	 * Gives a new instance of the default instanceof ALauncher class , or if one was existing the existing one ignoring arguments .
	 * @return Instance of the default ALauncher class , otherwise null if something happened .
	 */
	public static ALauncher newLauncher() {
		if (launcher != null) {
			return launcher;
		}
		try {
			launcher = clazz_launcher.newInstance();
			return launcher;
		} catch (Exception e) {
			return launcher;
		}
	}
	
	/**
	 * Gives the existing instance of the default class instanceof ALauncher .
	 * @return Instance of the default ALauncher class , or null if undefined
	 */
	public static ALauncher getLauncher() {
		return launcher;
	}
	
}
