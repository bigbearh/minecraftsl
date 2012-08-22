package net.minecraft;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
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

public class GameUpdater implements IGameUpdater {

	@Override
	public void init() {
		LaunchUtil.state = 1;
		try
		{
			Class.forName("LZMA.LzmaInputStream");
			LaunchUtil.lzmaSupported = true;
		}
		catch (Throwable t) {
		}
		try {
			Pack200.class.getSimpleName();
			LaunchUtil.pack200Supported = true;
		} catch (Throwable t) {
		}
	}

	@Override
	public String generateStacktrace(Exception paramException) {
		StringWriter localStringWriter = new StringWriter();
		PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
		paramException.printStackTrace(localPrintWriter);
		return localStringWriter.toString();
	}

	@Override
	public String getDescriptionForState() {
		switch (LaunchUtil.state) {
		case 1:
			return "Initializing loader";
		case 2:
			return "Determining packages to load";
		case 3:
			return "Checking cache for existing files";
		case 4:
			return "Downloading packages";
		case 5:
			return "Extracting downloaded packages";
		case 6:
			return "Updating classpath";
		case 7:
			return "Switching applet";
		case 8:
			return "Initializing real applet";
		case 9:
			return "Starting real applet";
		case 10:
			return "Done loading";
		}
		return "unknown state";
	}

	@Override
	public String trimExtensionByCapabilities(String paramString) {
		if (!LaunchUtil.pack200Supported) {
			paramString = paramString.replaceAll(".pack", "");
		}
		
		if (!LaunchUtil.lzmaSupported) {
			paramString = paramString.replaceAll(".lzma", "");
		}
		return paramString;
	}

	@Override
	public void loadJarURLs() throws Exception {
		LaunchUtil.state = 2;
		String str1 = "lwjgl.jar, jinput.jar, lwjgl_util.jar, " + LaunchUtil.mainGameUrl;
		str1 = trimExtensionByCapabilities(str1);
		
		StringTokenizer localStringTokenizer = new StringTokenizer(str1, ", ");
		int i = localStringTokenizer.countTokens() + 1;
		
		LaunchUtil.urlList = new URL[i];
		
		URL localURL = new URL("http://s3.amazonaws.com/MinecraftDownload/");
		
		for (int j = 0; j < i - 1; j++) {
			LaunchUtil.urlList[j] = new URL(localURL, localStringTokenizer.nextToken());
		}
		
		String str2 = System.getProperty("os.name");
		String str3 = null;
		
		if (str2.startsWith("Win"))
			str3 = "windows_natives.jar.lzma";
		else if (str2.startsWith("Linux"))
			str3 = "linux_natives.jar.lzma";
		else if (str2.startsWith("Mac"))
			str3 = "macosx_natives.jar.lzma";
		else if ((str2.startsWith("Solaris")) || (str2.startsWith("SunOS")))
			str3 = "solaris_natives.jar.lzma";
		else {
			fatalErrorOccured("OS (" + str2 + ") not supported", null);
		}
		
		if (str3 == null) {
			fatalErrorOccured("no lwjgl natives files found", null);
		} else {
			str3 = trimExtensionByCapabilities(str3);
			LaunchUtil.urlList[(i - 1)] = new URL(localURL, str3);
		}
	}

	@Override
	public void run() {
		init();
		LaunchUtil.state = 3;
		
		LaunchUtil.percentage = 5;
		try {
			loadJarURLs();
			
			String str = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return Options.getMCDir() + File.separator + "bin" + File.separator;
				}
			});
			File localFile1 = new File(str);
			
			if (!localFile1.exists()) {
				localFile1.mkdirs();
			}
			
			if (LaunchUtil.latestVersion != null) {
				File localFile2 = new File(localFile1, "version");
				boolean checkshouldupdate = true;
				
				int i = 0;
				if ((!LaunchUtil.forceUpdate) && (localFile2.exists()) && (
						(LaunchUtil.latestVersion.equals("-1")) || (LaunchUtil.latestVersion.equals(readVersionFile(localFile2))))) {
					i = 1;
					LaunchUtil.percentage = 90;
					checkshouldupdate = false;
				}
				
				if ((LaunchUtil.forceUpdate) || (i == 0)) {
					LaunchUtil.shouldUpdate = true;
				}
				if ((!LaunchUtil.forceUpdate) && (localFile2.exists()) && checkshouldupdate) {
					checkShouldUpdate();
				}
				if (LaunchUtil.shouldUpdate)	{
					writeVersionFile(localFile2, "");
					
					downloadJars(str);
					extractJars(str);
					extractNatives(str);
					
					if (LaunchUtil.latestVersion != null) {
						LaunchUtil.percentage = 90;
						writeVersionFile(localFile2, LaunchUtil.latestVersion);
					}
				} else {
					i = 1;
					LaunchUtil.percentage = 90;
				}
			}
			
			updateClassPath(localFile1);
			LaunchUtil.state = 10;
		} catch (AccessControlException localAccessControlException) {
			fatalErrorOccured(localAccessControlException.getMessage(), localAccessControlException);
			LaunchUtil.certificateRefused = true;
		} catch (Exception localException) {
			fatalErrorOccured(localException.getMessage(), localException);
		} finally {
			LaunchUtil.loaderThread = null;
		}
	}

	@Override
	public void checkShouldUpdate() {
		LaunchUtil.pauseAskUpdate = true;
		while (LaunchUtil.pauseAskUpdate)
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
	}

	@Override
	public String readVersionFile(File versionFile) {
		String str = "";
		try {
			DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(versionFile));
			str = localDataInputStream.readUTF();
			localDataInputStream.close();
			return str;
		} catch (Exception e) {
			return str;
		}
	}

	@Override
	public void writeVersionFile(File versionFile, String version)
			throws Exception {
		DataOutputStream localDataOutputStream = new DataOutputStream(new FileOutputStream(versionFile));
		localDataOutputStream.writeUTF(version);
		localDataOutputStream.close();
	}

	@Override
	public void updateClassPath(File binDir) throws Exception {
		LaunchUtil.state = 6;
		
		LaunchUtil.percentage = 95;
		
		URL[] arrayOfURL = new URL[LaunchUtil.urlList.length];
		for (int i = 0; i < LaunchUtil.urlList.length; i++) {
			arrayOfURL[i] = new File(binDir, getJarName(LaunchUtil.urlList[i])).toURI().toURL();
		}
		
		if (LaunchUtil.classLoader == null) {
			LaunchUtil.classLoader = new URLClassLoader(arrayOfURL) {
				protected PermissionCollection getPermissions(CodeSource paramCodeSource) {
					PermissionCollection localPermissionCollection = null;
					try {
						Method localMethod = SecureClassLoader.class.getDeclaredMethod("getPermissions", new Class[] { CodeSource.class });
						
						localMethod.setAccessible(true);
						localPermissionCollection = (PermissionCollection)localMethod.invoke(getClass().getClassLoader(), new Object[] { paramCodeSource });
						
						String str = "www.minecraft.net";
						
						if ((str != null) && (str.length() > 0))
						{
							localPermissionCollection.add(new SocketPermission(str, "connect,accept"));
						} else if (!paramCodeSource.getLocation().getProtocol().equals("file"));
						localPermissionCollection.add(new FilePermission("<<ALL FILES>>", "read"));
					}
					catch (Exception localException) {
						localException.printStackTrace();
					}
					
					return localPermissionCollection;
				}
			};
		}
		String str = binDir.getAbsolutePath();
		if (!str.endsWith(File.separator)) str = str + File.separator;
		unloadNatives(str);
		
		System.setProperty("org.lwjgl.librarypath", str + "natives");
		System.setProperty("net.java.games.input.librarypath", str + "natives");
		
		LaunchUtil.natives_loaded = true;
	}

	@Override
	public void unloadNatives(String nativeDir) {
		if (!LaunchUtil.natives_loaded) {
			return;
		}
		try {
			Field localField = ClassLoader.class.getDeclaredField("loadedLibraryNames");
			localField.setAccessible(true);
			Vector localVector = (Vector)localField.get(getClass().getClassLoader());
			
			String str1 = new File(nativeDir).getCanonicalPath();
			
			for (int i = 0; i < localVector.size(); i++) {
				String str2 = (String)localVector.get(i);
				
				if (str2.startsWith(str1)) {
					localVector.remove(i);
					i--;
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	@Override
	public Applet createApplet() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Class localClass = LaunchUtil.classLoader.loadClass("net.minecraft.client.MinecraftApplet");
		return (Applet)localClass.newInstance();
	}

	@Override
	public void downloadJars(String binDir) throws Exception {
		File localFile = new File(binDir, "md5s");
		Properties localProperties = new Properties();
		if (localFile.exists()) {
			try {
				FileInputStream localFileInputStream = new FileInputStream(localFile);
				localProperties.load(localFileInputStream);
				localFileInputStream.close();
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
		}
		LaunchUtil.state = 4;
		
		int[] arrayOfInt = new int[LaunchUtil.urlList.length];
		boolean[] arrayOfBoolean = new boolean[LaunchUtil.urlList.length];
		URLConnection localURLConnection;
		Object localObject;
		for (int i = 0; i < LaunchUtil.urlList.length; i++) {
			localURLConnection = LaunchUtil.urlList[i].openConnection();
			localURLConnection.setDefaultUseCaches(false);
			arrayOfBoolean[i] = false;
			if ((localURLConnection instanceof HttpURLConnection)) {
				((HttpURLConnection)localURLConnection).setRequestMethod("HEAD");
				
				localObject = "\"" + localProperties.getProperty(getFileName(LaunchUtil.urlList[i])) + "\"";
				
				if ((!LaunchUtil.forceUpdate) && (localObject != null)) localURLConnection.setRequestProperty("If-None-Match", (String)localObject);
				
				int j = ((HttpURLConnection)localURLConnection).getResponseCode();
				if (j / 100 == 3) {
					arrayOfBoolean[i] = true;
				}
			}
			arrayOfInt[i] = localURLConnection.getContentLength();
			LaunchUtil.totalSizeDownload += arrayOfInt[i];
		}
		
		int i = LaunchUtil.percentage = 10;
		
		localObject = new byte[65536];
		for (int j = 0; j < LaunchUtil.urlList.length; j++) {
			if (arrayOfBoolean[j] != false) {
				LaunchUtil.percentage = (i + arrayOfInt[j] * 45 / LaunchUtil.totalSizeDownload);
			} else {
				try {
					localProperties.remove(getFileName(LaunchUtil.urlList[j]));
					localProperties.store(new FileOutputStream(localFile), "md5 hashes for downloaded files");
				} catch (Exception localException2) {
					localException2.printStackTrace();
				}
				
				int k = 0;
				int m = 3;
				int n = 1;
				
				while (n != 0) {
					n = 0;
					
					localURLConnection = LaunchUtil.urlList[j].openConnection();
					
					String str1 = "";
					
					if ((localURLConnection instanceof HttpURLConnection)) {
						localURLConnection.setRequestProperty("Cache-Control", "no-cache");
						
						localURLConnection.connect();
						
						str1 = localURLConnection.getHeaderField("ETag");
						str1 = str1.substring(1, str1.length() - 1);
					}
					
					String str2 = getFileName(LaunchUtil.urlList[j]);
					InputStream localInputStream = getJarInputStream(str2, localURLConnection);
					FileOutputStream localFileOutputStream = new FileOutputStream(binDir + str2);
					
					long l1 = System.currentTimeMillis();
					int i2 = 0;
					int i3 = 0;
					String str3 = "";
					
					MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
					int i1;
					while ((i1 = localInputStream.read((byte[]) localObject, 0, ((byte[]) localObject).length)) != -1) {
						localFileOutputStream.write((byte[]) localObject, 0, i1);
						localMessageDigest.update((byte[]) localObject, 0, i1);
						LaunchUtil.currentSizeDownload += i1;
						i3 += i1;
						LaunchUtil.percentage = (i + LaunchUtil.currentSizeDownload * 45 / LaunchUtil.totalSizeDownload);
						LaunchUtil.subtaskMessage = ("Retrieving: " + str2 + " " + LaunchUtil.currentSizeDownload * 100 / LaunchUtil.totalSizeDownload + "%");
						
						i2 += i1;
						long l2 = System.currentTimeMillis() - l1;
						
						if (l2 >= 1000L) {
							float f = i2 / (float)l2;
							f = (int)(f * 100.0F) / 100.0F;
							str3 = " @ " + f + " KB/sec";
							i2 = 0;
							l1 += 1000L;
						}
						
						LaunchUtil.subtaskMessage += str3;
					}
					
					localInputStream.close();
					localFileOutputStream.close();
					String str4 = new BigInteger(1, localMessageDigest.digest()).toString(16);
					while (str4.length() < 32) {
						str4 = "0" + str4;
					}
					boolean bool = true;
					if (str1 != null) {
						bool = str4.equals(str1);
					}
					
					if ((localURLConnection instanceof HttpURLConnection)) {
						if ((bool) && ((i3 == arrayOfInt[j]) || (arrayOfInt[j] <= 0))) {
							try {
								localProperties.setProperty(getFileName(LaunchUtil.urlList[j]), str1);
								localProperties.store(new FileOutputStream(localFile), "md5 hashes for downloaded files");
							} catch (Exception localException3) {
								localException3.printStackTrace();
							}
						} else {
							k++;
							if (k < m) {
								n = 1;
								LaunchUtil.currentSizeDownload -= i3;
							} else {
								throw new Exception("failed to download " + str2);
							}
						}
					}
				}
			}
		}
		
		LaunchUtil.subtaskMessage = "";
	}

	@Override
	public InputStream getJarInputStream(String jarName,
			final URLConnection jarConnection) throws Exception {
		final InputStream[] jarStream = new InputStream[1];
		
		for (int i = 0; (i < 3) && (jarStream[0] == null); i++) {
			Thread getThread = new Thread() {
				public void run() {
					try {
						jarStream[0] = jarConnection.getInputStream();
					}
					catch (IOException localIOException)
					{
					}
				}
			};
			getThread.setName("JarInputStreamThread");
			getThread.start();
			
			int j = 0;
			while ((jarStream[0] == null) && (j++ < 5)) {
				try {
					getThread.join(1000L);
				} catch (InterruptedException localInterruptedException1) {
				}
			}
			if (jarStream[0] != null) continue;
			try {
				getThread.interrupt();
				getThread.join();
			}
			catch (InterruptedException localInterruptedException2)
			{
			}
		}
		
		if (jarStream[0] == null) {
			if (jarName.equals("minecraft.jar")) {
				throw new Exception("Unable to download " + jarName + " (minecraft jar!)");
			}
			throw new Exception("Unable to download " + jarName);
		}
		
		return jarStream[0];
	}

	@Override
	public void extractLZMA(String from, String to) throws Exception {
		File fromFile = new File(from);
		if (!fromFile.exists()) throw new IOException("File "+from+" doesn't exist !");
		FileInputStream fromStream = new FileInputStream(fromFile);
		
		Class lzmaClass = Class.forName("LZMA.LzmaInputStream");
		Constructor lzmaConstructor = lzmaClass.getDeclaredConstructor(new Class[] { InputStream.class });
		
		InputStream lzmaStream = (InputStream)lzmaConstructor.newInstance(new Object[] { fromStream });
		
		FileOutputStream toStream = new FileOutputStream(to);
		
		byte[] data = new byte[lzmaStream.available()];
		lzmaStream.read(data);
		toStream.write(data);
		
		fromStream.close();
		toStream.close();
		
		fromFile.delete();
	}

	@Override
	public void extractPack(String from, String to) throws Exception {
		File fromFile = new File(from);
		if (!fromFile.exists()) return;
		
		FileOutputStream toFileStream = new FileOutputStream(to);
    	JarOutputStream toJarStream = new JarOutputStream(toFileStream);
    	
    	Pack200.Unpacker localUnpacker = Pack200.newUnpacker();
    	localUnpacker.unpack(fromFile, toJarStream);
    	toJarStream.close();
    	
    	fromFile.delete();
	}

	@Override
	public void extractJars(String binDir) throws Exception {
		LaunchUtil.state = 5;
		
		float f = 10.0F / LaunchUtil.urlList.length;
		
		for (int i = 0; i < LaunchUtil.urlList.length; i++) {
			LaunchUtil.percentage = (55 + (int)(f * (i + 1)));
			String str = getFileName(LaunchUtil.urlList[i]);
			
			if (str.endsWith(".pack.lzma")) {
				LaunchUtil.subtaskMessage = ("Extracting: " + str + " to " + str.replaceAll(".lzma", ""));
				extractLZMA(binDir + str, binDir + str.replaceAll(".lzma", ""));
				
				LaunchUtil.subtaskMessage = ("Extracting: " + str.replaceAll(".lzma", "") + " to " + str.replaceAll(".pack.lzma", ""));
				extractPack(binDir + str.replaceAll(".lzma", ""), binDir + str.replaceAll(".pack.lzma", ""));
			} else if (str.endsWith(".pack")) {
				LaunchUtil.subtaskMessage = ("Extracting: " + str + " to " + str.replace(".pack", ""));
				extractPack(binDir + str, binDir + str.replace(".pack", ""));
			} else if (str.endsWith(".lzma")) {
				LaunchUtil.subtaskMessage = ("Extracting: " + str + " to " + str.replace(".lzma", ""));
				extractLZMA(binDir + str, binDir + str.replace(".lzma", ""));
			}
		}
	}

	@Override
	public void extractNatives(String binDir) throws Exception {
		LaunchUtil.state = 5;
		
		int i = LaunchUtil.percentage;
		
		String str = getJarName(LaunchUtil.urlList[(LaunchUtil.urlList.length - 1)]);
		
		Certificate[] arrayOfCertificate = LaunchUtil.class.getProtectionDomain().getCodeSource().getCertificates();
		
		if (arrayOfCertificate == null) {
			URL url = LaunchUtil.class.getProtectionDomain().getCodeSource().getLocation();
			
			JarURLConnection jarUrl = (JarURLConnection) new URL("jar:" + url.toString() + "!/net/minecraft/LaunchUtil.class").openConnection();
			jarUrl.setDefaultUseCaches(true);
			try {
				arrayOfCertificate = jarUrl.getCertificates();
			} catch (Exception localException) {
			}
		}
		File nativesDir = new File(binDir + "natives");
		if (!nativesDir.exists()) {
			nativesDir.mkdir();
		}
		
		File jarFile = new File(binDir + str);
		if (!jarFile.exists()) return;
		JarFile localJarFile = new JarFile(jarFile, true);
		Enumeration<JarEntry> entries = localJarFile.entries();
		
		LaunchUtil.totalSizeExtract = 0;
		
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			if ((entry.isDirectory()) || (entry.getName().indexOf('/') != -1)) {
				continue;
			}
			LaunchUtil.totalSizeExtract = (int)(LaunchUtil.totalSizeExtract + entry.getSize());
		}
		
		LaunchUtil.currentSizeExtract = 0;
		
		entries = localJarFile.entries();
		
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			if ((entry.isDirectory()) || (entry.getName().indexOf('/') != -1)) {
				continue;
			}
			File localFile = new File(binDir + "natives" + File.separator + entry.getName());
			if ((localFile.exists()) && 
					(!localFile.delete()))
			{
				continue;
			}
			
			InputStream entryStream = localJarFile.getInputStream(localJarFile.getEntry(entry.getName()));
			FileOutputStream fileStream = new FileOutputStream(binDir + "natives" + File.separator + entry.getName());
			
			byte[] arrayOfByte = new byte[65536];
			int j;
			while ((j = entryStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1) {
				fileStream.write(arrayOfByte, 0, j);
				LaunchUtil.currentSizeExtract += j;
				
				LaunchUtil.percentage = (i + LaunchUtil.currentSizeExtract * 20 / LaunchUtil.totalSizeExtract);
				LaunchUtil.subtaskMessage = ("Extracting: " + entry.getName() + " " + LaunchUtil.currentSizeExtract * 100 / LaunchUtil.totalSizeExtract + "%");
			}
			
			validateCertificateChain(arrayOfCertificate, entry.getCertificates());
			
			entryStream.close();
			fileStream.close();
		}
		LaunchUtil.subtaskMessage = "";
		
		localJarFile.close();
		
		File tmpFile = new File(binDir + str);
		tmpFile.delete();
	}

	@Override
	public void validateCertificateChain(Certificate[] array1,
			Certificate[] array2) throws Exception {
		if (array1 == null) return;
		if (array2 == null) throw new Exception("Unable to validate certificate chain. Native entry did not have a certificate chain at all");
		
		if (array1.length != array2.length) throw new Exception("Unable to validate certificate chain. Chain differs in length [" + array1.length + " vs " + array2.length + "]");
		
		for (int i = 0; i < array1.length; i++)
			if (!array1[i].equals(array2[i]))
				throw new Exception("Certificate mismatch: " + array1[i] + " != " + array2[i]);
	}

	@Override
	public String getJarName(URL jarURL) {
		String str = jarURL.getFile();
		
		if (str.contains("?")) {
			str = str.substring(0, str.indexOf("?"));
		}
		if (str.endsWith(".pack.lzma"))
			str = str.replaceAll(".pack.lzma", "");
		else if (str.endsWith(".pack"))
			str = str.replaceAll(".pack", "");
		else if (str.endsWith(".lzma")) {
			str = str.replaceAll(".lzma", "");
		}
		
		return str.substring(str.lastIndexOf('/') + 1);
	}

	@Override
	public String getFileName(URL fileURL) {
		String str = fileURL.getFile();
		if (str.contains("?")) {
			str = str.substring(0, str.indexOf("?"));
		}
		return str.substring(str.lastIndexOf('/') + 1);
	}

	@Override
	public void fatalErrorOccured(String description, Exception exception) {
		LaunchUtil.fatalError = true;
		LaunchUtil.fatalErrorDescription = ("Fatal error occured (" + LaunchUtil.state + "): " + (description==null?exception:description));
		System.out.println(LaunchUtil.fatalErrorDescription);
		if (exception != null)
			System.err.println(generateStacktrace(exception));
	}

	@Override
	@Deprecated
	public boolean canPlayOffline() {
		try {
			String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return Options.getMCDir() + File.separator + "bin" + File.separator;
				}
			});
			File localFile = new File(path);
			if (!localFile.exists()) return false;
			
			localFile = new File(localFile, "version");
			if (!localFile.exists()) return false;
			
			if (localFile.exists()) {
				localFile.setReadable(true);
				String str2 = readVersionFile(localFile);
				if ((str2 != null) && (str2.length() > 0))
					return true;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			return false;
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean canForceOffline() {
		try {
			String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return Options.getMCDir() + File.separator + "bin" + File.separator;
				}
			});
			File bin = new File(path);
			if (!bin.exists()) return false;
			File nat = new File(bin, "natives");
			if (!nat.exists()) return false;
			
			File[] osnatlist = new File[0];
			osnatlist = getOSNatives(nat);
			
			File[] list = new File[osnatlist.length+4];
			list[0] = new File(bin, "minecraft.jar");
			list[0] = new File(bin, "jinput.jar");
			list[0] = new File(bin, "lwjgl.jar");
			list[0] = new File(bin, "lwjgl_util.jar");
			System.arraycopy(osnatlist, 0, list, 1, osnatlist.length);
			return contains(bin, list);
		} catch (Exception localException) {
			localException.printStackTrace();
			return false;
		}
	}
	
	@Override
	public File[] getOSNatives(File dir) {
		ArrayList<File> list = new ArrayList<File>();
		switch (Options.getOs()) {
		case unknown:
			break;
			
		case windows:
			list.add(new File(dir, "jinput-dx8.dll"));
			list.add(new File(dir, "jinput-dx8_64.dll"));
			list.add(new File(dir, "jinput-raw.dll"));
			list.add(new File(dir, "jinput-raw_64.dll"));
			list.add(new File(dir, "lwjgl.dll"));
			list.add(new File(dir, "lwjgl64.dll"));
			list.add(new File(dir, "OpenAL32.dll"));
			list.add(new File(dir, "OpenAL64.dll"));
			break;

		case macos:
			list.add(new File(dir, "libjinput-osx.jnilib"));
			list.add(new File(dir, "liblwjgl.jnilib"));
			list.add(new File(dir, "openal.dylib"));
			break;

		case solaris:
			list.add(new File(dir, "liblwjgl.so"));
			list.add(new File(dir, "liblwjgl64.so"));
			list.add(new File(dir, "libopenal.so"));
			list.add(new File(dir, "libopenal64.so"));
			break;
			
		case linux:
			list.add(new File(dir, "libjinput-linux.so"));
			list.add(new File(dir, "libjinput-linux64.so"));
			list.add(new File(dir, "liblwjgl.so"));
			list.add(new File(dir, "liblwjgl64.so"));
			list.add(new File(dir, "libopenal.so"));
			list.add(new File(dir, "libopenal64.so"));
			break;
			
		default:
			break;
		}
		return list.toArray(new File[0]);
	}

	@Override
	public boolean contains(File dir, File... list) {
		int existing = 0;
		int needed = list.length;
		existing = contains0(dir, list);
		return existing >= needed;
	}
	
	@Override
	public int contains0(File dir, File... list) {
		int existing = 0;
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				existing += contains0(f, list);
				continue;
			}
			for (File ff : list) {
				if (ff == null) {
					continue;
				}
				if (f.getPath().equals(ff.getPath())) {
					existing++;
				}
			}
		}
		return existing;
	}

}
