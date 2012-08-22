package net.minecraft.minecrack;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.minecraft.MinecraftSL;
import net.minecraft.Options;
import net.minecraft.OptionsMenu;
import net.minecraft.TransparentPanel;
import net.minecraft.tempmcmod.ModListHelper;
import net.minecraft.utils.BackupUtil;
import net.minecraft.utils.Jarrer;
import net.minecraft.utils.Zipper;

/**
 *	 Minecrack is our partner for providing skins in HD or cape and ear hosting , NOT cracking Minecraft . It uses HEX-Editing for patching Minecraft in offline mode .
 *	@author Maik
 *	
 */
public class MinecrackInstaller extends TransparentPanel {
	static File tempFolder = null;
	
	public MinecrackInstaller() {
		JPanel northPanel = new JPanel();
		setLayout(new BorderLayout());
		
		JButton exitb = new JButton("Exit");
		exitb.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	OptionsMenu.back();
	      }
		});
		northPanel.add(exitb);
		JButton patchmod = new JButton("Replace class files ( Online , faster )");
		patchmod.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	installAsMod();
	      }
		});
		if (MinecraftSL.isOfflineMode()) {
			patchmod.setEnabled(false);
		}
		northPanel.add(patchmod);
		JButton patchhex = new JButton("Patch class files ( Offline , slower )");
		patchhex.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	installAsHexEdit();
	      }
		});
		northPanel.add(patchhex);
		JButton unpatchhex = new JButton("Uninstall per HEX ( Offline )");
		unpatchhex.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	uninstallAsHexEdit();
	      }
		});
		northPanel.add(unpatchhex);
		
		add(northPanel, "Center");
		
	}
	
	static File getTempFolder() {
		if (tempFolder == null) {
			tempFolder = new File(System.getProperties().getProperty("java.io.tmpdir"), "MCINSTLR");
		}
		if (!tempFolder.exists()) {
			tempFolder.mkdirs();
		}
		return tempFolder;
	}
	
	public static void installAsMod() {
		boolean success = false;
		String exception = "UNKNOWN";
		try {
			BackupUtil.downloadFile("https://sites.google.com/site/minecraftsloader/downloads/Minecrack_MinecraftSL2.zip", new File(getTempFolder(), "Minecrack_MinecraftSL2.zip"));
			ModListHelper.patchMod(new File(getTempFolder(), "Minecrack_MinecraftSL2.zip"));
			
			success = true;
			
			System.out.println(getTempFolder());
			System.out.println(new File(getTempFolder(), "Minecrack_MinecraftSL.zip"));
			System.out.println(new File(Options.getMCDir(), "bin"));
			
		} catch (Exception e) {
			exception = ""+e+"";
			e.printStackTrace();
		}
		
		if ((!success)) {
			JOptionPane.showMessageDialog(null, "Failed ! Error : "+exception+"\nPlease create a new report issue on the github page !\nReporting errors is verry helpfull !", "Minecrack Installer", JOptionPane.OK_OPTION);
		}else{
			JOptionPane.showMessageDialog(null, "Done", "Minecrack Installer", JOptionPane.OK_OPTION);
		}
	}
	
	public static void installAsHexEdit() {
		boolean success = false;
		String exception = "UNKNOWN";
		try {
			new File(Options.getSystemTempDir(), "TEMP-MINECRACK").mkdirs();
			File jarFile = new File(Options.getMCDir(), "bin"+File.separator+"minecraft.jar");
			File jarFileBackup = new File(Options.getMCDir(), "bin"+File.separator+"_minecraft.jar");
			
			if (jarFileBackup.exists()) {
				BackupUtil.deleteFile(jarFileBackup);
			}
			try {
				BackupUtil.copyFile(jarFile, jarFileBackup);
			} catch (IOException e) {
				throw e;
			}
			
			System.out.println("Trying to delete ... the temp folder");
			File tempFold = new File(Options.getSystemTempDir(), "TEMP-MINECRACK");
			BackupUtil.deleteDirectory(tempFold);
			
			tempFold.mkdirs();
			
			System.out.println("Unzipping Minecraft.jar");
			Zipper.unzipFolder(jarFile, tempFold);
			System.out.println("Deleted META-INF : "+BackupUtil.deleteDirectory(new File(tempFold, "META-INF")));
			
			ArrayList<File> topatch = new ArrayList<File>();
			System.out.println("Searching for classes to patch ...");
			HashMap<String, String> map = new HashMap<String, String>();
			//1.7.3 map , really usefull for getting originals ...
			/*
			 * class      new        orig
			 * dc.class http://minecrack.fr.nf/mc/minecrackpic/ http://s3.amazonaws.com/MinecraftSkins/
			 * gs.class http://minecrack.fr.nf/mc/minecloakspic/ http://s3.amazonaws.com/MinecraftCloaks/
			 * xz.class http://minecrack.fr.nf/mc/minecrackpic/ http://s3.amazonaws.com/MinecraftSkins/
			 * 
			 */
			map.put("http://s3.amazonaws.com/MinecraftSkins/", "http://minecrack.fr.nf/mc/minecrackpic/"); //dc
			map.put("http://s3.amazonaws.com/MinecraftCloaks/", "http://minecrack.fr.nf/mc/minecloakspic/"); //gs
			//map.put("http://s3.amazonaws.com/MinecraftSkins/", "http://minecrack.fr.nf/mc/minecrackpic/"); //xz , duplicate of dc ?
			searchPatch(topatch, tempFold, jarFile, map);
			
			Jarrer.jarFolderContents(tempFold, jarFile);
			BackupUtil.deleteDirectory(tempFold);
			
			success = true;
		} catch (Exception e) {
			exception = ""+e+"";
			e.printStackTrace();
		}
		
		if ((!success)) {
			JOptionPane.showMessageDialog(null, "Failed ! Error : "+exception+"\nPlease create a new report issue on the github page !\nReporting errors is verry helpfull !", "Minecrack Installer", JOptionPane.OK_OPTION);
		}else{
			JOptionPane.showMessageDialog(null, "Done", "Minecrack Installer", JOptionPane.OK_OPTION);
		}
	}
	
	public static void uninstallAsHexEdit() {
		boolean success = false;
		String exception = "UNKNOWN";
		try {
			new File(Options.getSystemTempDir(), "TEMP-MINECRACK").mkdirs();
			File jarFile = new File(Options.getMCDir(), "bin"+File.separator+"minecraft.jar");
			File jarFileBackup = new File(Options.getMCDir(), "bin"+File.separator+"_minecraft.jar");
			
			if (jarFileBackup.exists()) {
				BackupUtil.deleteFile(jarFileBackup);
			}
			try {
				BackupUtil.copyFile(jarFile, jarFileBackup);
			} catch (IOException e) {
				throw e;
			}
			
			System.out.println("Trying to delete ... the temp folder");
			File tempFold = new File(Options.getSystemTempDir(), "TEMP-MINECRACK");
			BackupUtil.deleteDirectory(tempFold);
			
			tempFold.mkdirs();
			
			System.out.println("Unzipping Minecraft.jar");
			Zipper.unzipFolder(jarFile, tempFold);
			System.out.println("Deleted META-INF : "+BackupUtil.deleteDirectory(new File(tempFold, "META-INF")));
			
			ArrayList<File> topatch = new ArrayList<File>();
			System.out.println("Searching for classes to patch ...");
			HashMap<String, String> map = new HashMap<String, String>();
			//1.7.3 map , really usefull for getting originals ...
			/*
			 * class      new        orig
			 * dc.class http://minecrack.fr.nf/mc/minecrackpic/ http://s3.amazonaws.com/MinecraftSkins/
			 * gs.class http://minecrack.fr.nf/mc/minecloakspic/ http://s3.amazonaws.com/MinecraftCloaks/
			 * xz.class http://minecrack.fr.nf/mc/minecrackpic/ http://s3.amazonaws.com/MinecraftSkins/
			 * 
			 */
			map.put("http://minecrack.fr.nf/mc/minecrackpic/", "http://s3.amazonaws.com/MinecraftSkins/"); //dc
			map.put("http://minecrack.fr.nf/mc/minecloakspic/", "http://s3.amazonaws.com/MinecraftCloaks/"); //gs
			//map.put("http://minecrack.fr.nf/mc/minecrackpic/", "http://s3.amazonaws.com/MinecraftSkins/"); //xz , duplicate of dc ?
			
			searchPatch(topatch, tempFold, jarFile, map);
			
			Jarrer.jarFolderContents(tempFold, jarFile);
			BackupUtil.deleteDirectory(tempFold);
			
			success = true;
		} catch (Exception e) {
			exception = ""+e+"";
			e.printStackTrace();
		}
		
		if ((!success)) {
			JOptionPane.showMessageDialog(null, "Failed ! Error : "+exception+"\nPlease create a new report issue on the github page !\nReporting errors is verry helpfull !", "Minecrack Installer", JOptionPane.OK_OPTION);
		}else{
			JOptionPane.showMessageDialog(null, "Done", "Minecrack Installer", JOptionPane.OK_OPTION);
		}
	}

	private static void searchPatch(ArrayList<File> list, File tempFold, File jarFile, Map<String, String> map) {
		for (File f : tempFold.listFiles()) {
			if (f.isDirectory()) {
				//searchPatch(list, f, jarFile);
				// Files to patch are in the root of the jar !
				continue;
			}
			if (!f.getName().toLowerCase().endsWith(".class")) {
				continue;
			}
			try {
				FileInputStream fis = new FileInputStream(f);
				byte[] b = new byte[fis.available()];
				fis.read(b);
				fis.close();
				for (Entry<String, String> entry : map.entrySet()) {
					if (contains(b, entry.getKey())) {
						System.out.println("Trying to patch "+f.getName());
						System.out.println("    replacing "+entry.getKey()+" with "+entry.getValue());
						b = replace(b, entry.getKey(), entry.getValue(), true);
					}
				}
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(b);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static byte[] replace(byte[] barg, String key, String value, boolean strictreplace) {
		ArrayList<Byte> b = new ArrayList<Byte>();
		for (int i = 0; i < barg.length; i++) {
			b.add(barg[i]);
		}
		boolean isb = false;
		int is = 0;
		int ii = 0;
		int il = 0;
		for (int i = 0; i < b.size(); i++) {
			char c = (char) (int) b.get(i);
			if (c == key.charAt(il)) {
				if (!isb) {
					is = i;
				}
				isb = true;
				ii = i;
				il++;
			} else {
				il = 0;
				isb = false;
			}
			if (il == key.length()) {
				//il++;
				// Start replacing
				for (int j = is; j < is+il; j++) {
					byte newb = (byte) (int) value.charAt(j-is);
					if (!strictreplace) {
						if (j < is+il) {
							//Replacing because no risk of corruption
							b.set(j, newb);
						} else {
							//Replacing could cause corruption , adding
							b.add(j, newb);
						}
					} else {
						//Replacing needed , risk of corruption ignored
						b.set(j, newb);
					}
				}
				il = 0;
			}
		}
		Byte[] bigb = b.toArray(new Byte[0]);
		byte[] retb = new byte[bigb.length];
		for (int i = 0; i < bigb.length; i++) {
			retb[i] = bigb[i];
		}
		return retb;
	}

	private static boolean contains(byte[] barg, String key) {
		boolean bb = false;
		byte[] b = barg.clone();
		int ii = 0;
		for (int i = 0; i < b.length; i++) {
			char c = (char) (int) b[i];
			if (c == key.charAt(ii)) {
				ii++;
			} else {
				ii = 0;
			}
			if (ii == key.length()) {
				return true;
			}
		}
		return bb;
	}
}