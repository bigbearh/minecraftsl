package net.minecraft.tempmcmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;

import net.minecraft.LaunchUtil;
import net.minecraft.Options;

public final class ModListHelper {
	
	public static DefaultListModel<String> lmO = new DefaultListModel<String>();
	
	public static void loadModList() {
		lmO.clear();
		File listf = new File(Options.getMCDir(), "mcsl_mods.txt");
		
		try {
			
			if (!listf.exists()) {
				listf.createNewFile();
			}
			
			BufferedReader bufferedreader = new BufferedReader(new FileReader(listf));
			for(String s = ""; (s = bufferedreader.readLine()) != null;) {
				try {
					if (s.equals("")||s.equals(" ")||s.equals("\n")) {  
					} else { 
						lmO.addElement(s);
					}
				} catch(Exception exception1) {
					System.out.println((new StringBuilder()).append("Skipping bad line : ").append(s).toString());
					exception1.printStackTrace();
				}
			}
			
			bufferedreader.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static void saveModList() {
		File listf = new File(Options.getMCDir(), "mcsl_mods.txt");
		
		try {
			if (!listf.exists()) {
				listf.createNewFile();
			}
			
			PrintWriter printwriter = new PrintWriter(new FileWriter(listf));
			Object[] lines = lmO.toArray();
			int done = 0;
			while (done != lines.length) {
				printwriter.println((new StringBuilder()).append(lines[done].toString()).toString());
				done = done + 1;
			}
			printwriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DefaultListModel<String> loadModList2() {
		DefaultListModel<String> toReturn = new DefaultListModel<String>();
		File listf = new File(Options.getMCDir(), "mcsl_mods.txt");
    
		try {
			
			if (!listf.exists()) {
				listf.createNewFile();
			}
			
			BufferedReader bufferedreader = new BufferedReader(new FileReader(listf));
			for(String s = ""; (s = bufferedreader.readLine()) != null;) {
				try {
					if (s.equals("")||s.equals(" ")||s.equals("\n")) {  
					} else { 
						toReturn.addElement(s);
					}
				} catch(Exception exception1) {
					System.out.println((new StringBuilder()).append("Skipping bad line : ").append(s).toString());
					exception1.printStackTrace();
				}
			}
			
			bufferedreader.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return toReturn;
	} 
	
	public static void saveModList2() {
		File listf = new File(Options.getMCDir(), "mcsl_mods_2.txt");
		
		try {
			if (!listf.exists()) {
				listf.createNewFile();
			}
	    	
			PrintWriter printwriter = new PrintWriter(new FileWriter(listf));
			Object[] lines = lmO.toArray();
			int done = 0;
			while (done != lines.length) {
				printwriter.println((new StringBuilder()).append(lines[done].toString()).toString());
				done = done + 1;
			}
			printwriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	public static long loadLastModified() {
		long toReturn = 0L;
		File listf = new File(Options.getMCDir(), "mcsl_mods_lastmodified.txt");
		
		try {
			if (!listf.exists()) {
				return -1L;
			}
			
			BufferedReader bufferedreader = new BufferedReader(new FileReader(listf));
			for(String s = ""; (s = bufferedreader.readLine()) != null;) {
				try {
					toReturn = Long.parseLong(s);
				} catch(Exception exception1) {
					System.out.println((new StringBuilder()).append("Skipping bad line : ").append(s).toString());
					exception1.printStackTrace();
				}
			}
			
			bufferedreader.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return toReturn;
	}
	
	public static void saveLastModified(long lastModified) {
		File listf = new File(Options.getMCDir(), "mcsl_mods_lastmodified.txt");
		
		try {
			if (!listf.exists()) {
				listf.createNewFile();
			}
			
			PrintWriter printwriter = new PrintWriter(new FileWriter(listf));
			printwriter.println((new StringBuilder()).append(lastModified+"").toString());
			printwriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkSameListObjects(DefaultListModel<String> lm1, DefaultListModel<String> lm2) {
		Object array[] = lm1.toArray();
		for (Object obj:array) {
			if (!lm2.contains(obj)) {
				return false;
			}
		}
		return true;
	} 
	
	public static String patchMods() {
		String toReturn = "minecraft.jar";
		File jarFileOrig = new File(Options.getMCDir(), "bin"+File.separator+"minecraft.jar");
		File jarFile = new File(Options.getMCDir(), "bin"+File.separator+"temp-mc.jar");
		if ((((loadLastModified() != jarFileOrig.lastModified()) || (!checkSameListObjects(lmO, loadModList2()))) || (!jarFile.exists())) && (!lmO.isEmpty())) {
			toReturn = "temp-mc.jar";
			Object[] modsListO = lmO.toArray();
			String[] modsList = new String[modsListO.length]; 
			int done = 0;
			
			while (done != modsList.length) {
				System.out.println(modsListO[done].toString());
				modsList[done] = modsListO[done].toString();
				done++;
			}
			if (jarFile.exists()) {
				net.minecraft.utils.BackupUtil.deleteFile(jarFile);
			}
			
			System.out.println("Trying to delete ... the temp folder");
			LaunchUtil.subtaskMessage = "Deleting TEMP-MC folder ...";
			File tempFold = new File(Options.getSystemTempDir(), "TEMP-MC");
			net.minecraft.utils.BackupUtil.deleteDirectory(tempFold);
			
			tempFold.mkdirs();
			
			System.out.println("Unzipping Minecraft.jar");
			LaunchUtil.subtaskMessage = "Unzipping Minecraft.jar ( may take it's time )";
			Zipper.unzipFolder(jarFileOrig, tempFold);
			LaunchUtil.subtaskMessage = "Deleting META-INF";
			System.out.println("Deleted META-INF : "+net.minecraft.utils.BackupUtil.deleteDirectory(new File(tempFold, "META-INF")));
			
			done = 0;
			while (done != modsList.length) {
				System.out.println("Unzipping :"+modsList[done]);
				LaunchUtil.subtaskMessage = "Unzipping Mod :"+modsList[done];
				Zipper.unzipFolder(new File(modsList[done]), tempFold);
				done++;
			}
			
			LaunchUtil.subtaskMessage = "Archiving TEMP-MC to temp-mc.jar";
			Jarrer.jarFolderContents(tempFold, jarFile);
			LaunchUtil.subtaskMessage = "Deleting leftovers of TEMP-MC";
			net.minecraft.utils.BackupUtil.deleteDirectory(tempFold);
			saveLastModified(jarFileOrig.lastModified());
			saveModList2();
		}
		System.out.println(jarFile.exists());
		if (jarFile.exists()) {
			toReturn = "temp-mc.jar";
		}
		return toReturn;
	}
	
	public static void patchMod(File modZip) throws IOException {
		File jarFile = new File(Options.getMCDir(), "bin"+File.separator+"minecraft.jar");
		File jarFileBackup = new File(Options.getMCDir(), "bin"+File.separator+"_minecraft.jar");
		
		if (jarFileBackup.exists()) {
			net.minecraft.utils.BackupUtil.deleteFile(jarFileBackup);
		}
		try {
			net.minecraft.utils.BackupUtil.copyFile(jarFile, jarFileBackup);
		} catch (IOException e) {
			throw e;
		}
		
		System.out.println("Trying to delete ... the temp folder");
		File tempFold = new File(Options.getSystemTempDir(), "TEMP-MC-MOD");
		net.minecraft.utils.BackupUtil.deleteDirectory(tempFold);
		
		tempFold.mkdirs();
		
		System.out.println("Unzipping Minecraft.jar");
		net.minecraft.utils.Zipper.unzipFolder(jarFile, tempFold);
		LaunchUtil.subtaskMessage = "Deleting META-INF";
		System.out.println("Deleted META-INF : "+net.minecraft.utils.BackupUtil.deleteDirectory(new File(tempFold, "META-INF")));
		
		System.out.println("Unzipping : "+modZip.getName());
		net.minecraft.utils.Zipper.unzipFolder(modZip, tempFold);
		
		net.minecraft.utils.Jarrer.jarFolderContents(tempFold, jarFile);
		net.minecraft.utils.BackupUtil.deleteDirectory(tempFold);
	}
	
}
