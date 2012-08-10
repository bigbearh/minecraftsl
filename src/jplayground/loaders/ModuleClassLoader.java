package jplayground.loaders;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.ArrayList;

import jplayground.etc.ResourceUtil;

import net.minecraft.http.HTTPClient;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

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
