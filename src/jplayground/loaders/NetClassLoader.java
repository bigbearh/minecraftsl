package jplayground.loaders;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jplayground.etc.ResourceUtil;

import net.minecraft.http.HTTPClient;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

/**
 *	A ClassLoader that is used over the network containing various utilities .
 *	Modified for HTTPClients in MinecraftSL
 */
public class NetClassLoader extends ClassLoader {
	
	/**
	 * Simple to use pre-constructed instance .
	 */
	public NetClassLoader loader = new NetClassLoader();
	
	public Class askForClass(String name, String url, String urlparams) {
		try {
			byte[] b = {};
			b = HTTPClient.getClassAsBytes(url, urlparams);
			Class c = convert(name, b);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Class convert(String name, byte[] b) throws IOException {
		try {
			Class c = defineClass(name, b, 0, b.length);
			return c;
		} catch (LinkageError e) {
			Class c = null;
			try {
				c = loadClass(name);
			} catch (ClassNotFoundException e1) {
			}
			return c;
		}
	}

	public byte[] convert(Class clazz) throws IOException {
		byte[] b = {};
		//Read class from file
		String path = clazz.getCanonicalName();
		if (path == null) {
			path = clazz.getName();
		}
		while (path.contains("[]")) {
			path = path.replace("[]", "");
		}
		while (path.contains(".")) {
			path = path.replace(".", "/");
		}
		path = "/"+path+".class";
		File f = null;
		try {
			ResourceUtil.urlToFile(clazz.getResource(path));
		} catch (IllegalArgumentException e) {
			while (path.contains("$")) {
				path = path.substring(0, path.length() - 2);
			}
			ResourceUtil.urlToFile(clazz.getResource(path));
		}
		//File and byte input streams
		ArrayList<Byte> ba = new ArrayList<Byte>();
		FileInputStream fis = new FileInputStream(f);
		int r = 0;
		for (int i = 0; r != -1; i++) {
			r = fis.read();
			if (r == -1) break;
			ba.add((byte)r);
		}
		Byte[] bb2 = ba.toArray(new Byte[] {});
		byte[] b2 = new byte[bb2.length];
		for (int i = 0; i < bb2.length; i++) {
			b2[i] = bb2[i];
		}
		fis.close();
		b = b2;
		return b;
	}
	
}
