package net.minecraft.modules;

import net.minecraft.TransparentPanel;

/**
 *	Sub-Module class for modules that are handled as options . ModuleBases can contain multipile of them , inited with setupOptions() or init() of ModuleBase .
 *	@author Maik
 *	
 */
public abstract class OptionModule {
	
	public static String name;
	
	/**
	 * Gets the label for the JLabel next to the button in the OptionsMenu
	 * @return Usually the name when not overridden
	 */
	public String getMenuLabel() {
		return name;
	}
	
	/**
	 * Gets the label for the button in the OptionsMenu to open this OptionModule's menu .
	 * @return Usually the name when not overridden
	 */
	public String getMenuButtonLabel() {
		return name;
	}
	
	/**
	 * Gets the menu to show in OptionsMenu if the button with the label getMenuButtonLabel gets triggered .
	 * @return The option menu of this OptionModule
	 */
	public abstract TransparentPanel getMenu();
	
}
