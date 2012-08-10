package net.minecraft.modules;

import java.util.ArrayList;

/**
 * Interface for external / extending classes ( Plugins , Mods , Features ) .
 *	@author Maik
 */
public interface ModuleBase {
	
	/**
	 * Module Name
	 * @return Module Name
	 */
	public String name();
	
	/**
	 * Module Version
	 * @return Module Version
	 */
	public String version();
	
	/**
	 * Can this Module run in this environment ?
	 * @param build MinecraftSL Build Number
	 * @param loadedlist List of already loaded Modules
	 * @return "Yes" if can run , "Yes, ..." if needs notification and "No" or "No, ..." if can not run .
	 */
	public String canRun(String build, ArrayList<ModuleBase> loadedlist);
	
	/**
	 * Inits the Module , should not start any tick-like threads because 
	 * they are hard to shutdown when they aren't shut down with stop .
	 */
	public void init();
	
	/**
	 * Stops this Module internally and interrupts threads for safe shutdown , 
	 * externally it gets removed from Modules' list when ejected 
	 */
	public void stop();
	
	/**
	 * Setups the options for the internal OptionModules , ran before initing .
	 */
	public void setupOptions();
	
	/**
	 * Gets the internal OptionModules when existing . Used for the MinecraftSL options menu .
	 * @returns OptionModule[0] if no modules existing , the internal already set-up modules otherwise .
	 */
	public OptionModule[] getOptionModules();
	
	/**
	 * Gets the informations of this Module and of the OptionModule if existing .
	 */
	public ModuleInfo getInfo();
	
}
