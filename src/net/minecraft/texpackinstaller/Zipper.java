package net.minecraft.texpackinstaller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {
	static final int BUFFER = 2048;
	
	public static boolean zipFolders(File[] srcFolders, File destZipFile) {
		return zipFolders(srcFolders, destZipFile, "");
	}
	
	public static boolean zipFolders(File[] srcFolders, File destZipFile, String inFolderName) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(destZipFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			
			File[] arrayOfFile = srcFolders; int j = srcFolders.length; for (int i = 0; i < j; i++) { File fileFolder = arrayOfFile[i];
				if (!addToZip(fileFolder.getParentFile().getPath(), inFolderName, fileFolder.getName(), out)) {
					return false;
				}
			}
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	protected static boolean addToZip(String absolutePath, String relativePath, String fileName, ZipOutputStream out) {
		File file = new File(absolutePath + File.separator + fileName);
		
		System.out.println("Adding \"" + absolutePath + File.separator + fileName + "\" file");
		if (file.isHidden())
			return true;
		if (file.isDirectory()) {
			absolutePath = absolutePath + File.separator + file.getName();
			relativePath = relativePath + File.separator + file.getName();
			for (String child : file.list()) {
				if (!addToZip(absolutePath, relativePath, child, out)) {
					return false;
				}
			}
			return true;
		}
		try {
			byte[] data = new byte[BUFFER];
			
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(relativePath + File.separator + fileName);
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				//int count;
				out.write(data, 0, count);
			}
			origin.close();
		} catch (Exception ex) {
			Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}
	
	public static boolean zipFolder(File srcFolder, File destZipFile) {
		File[] ar = new File[1];
		ar[0] = srcFolder;
		return zipFolders(ar, destZipFile);
	}
	
	public static void unzipFolder(File zipFile, File destFolder) {
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				//ZipEntry entry;
				System.out.println("Extracting: " + entry);
				
				byte[] data = new byte[BUFFER];
				
				File f = new File(destFolder + File.separator + entry.getName());
				if ((f.getParentFile() != null) && (!f.getParentFile().exists())) {
					f.getParentFile().mkdirs();
				}
				if (!f.exists()) {
					f.createNewFile();
					f.getParent();
				}
				FileOutputStream fos = new FileOutputStream(f);
				dest = new BufferedOutputStream(fos, BUFFER);
				int count;
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					//int count;
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}