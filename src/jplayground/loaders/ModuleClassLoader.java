package jplayground.loaders;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *	A ClassLoader that is used for loading modules and checking that they don't load into unallowed packages ...
 *	
 */
public class ModuleClassLoader extends URLClassLoader {
	
	public ModuleClassLoader(URL[] urls) {
		super(urls, constructHelp());
	}

	private final static ClassLoader constructHelp() {
		ClassLoader cl = ModuleClassLoader.class.getClassLoader();
		if (cl == null) {
			throw new NullPointerException("getClassLoader returns null !");
		}
		return cl;
	}
	
}
