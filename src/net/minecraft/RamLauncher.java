package net.minecraft;

/**
 *	Class that gets main(...)'ed when setting up ram amount to prevent endless loop of System.exit and new javaw.exes .
 *	@author Maik
 *	
 */
public final class RamLauncher {
	
	public static void main(String[] args) {
		MinecraftSL.main2(args);
	}
		
}
