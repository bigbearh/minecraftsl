package net.minecraft.backupmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import net.minecraft.MinecraftSL;
import net.minecraft.Options;
import net.minecraft.OptionsMenu;
import net.minecraft.TransparentPanel;

public class MinecraftBackupManager extends TransparentPanel {
	private static final long serialVersionUID = 1L;

	DefaultListModel<String> wlm = new DefaultListModel<String>();
	JList<String> worldsList = new JList<String>(wlm);
	public static JLabel lastfile = new JLabel("Extracting idle.avi ...");
	
	public MinecraftBackupManager() {
		JButton exitb = new JButton("Exit");
		JButton backupworld = new JButton("Backup World to file");
		JButton restoreworld = new JButton("Restore World from file");
		JButton backupgame = new JButton("Backup whole game");
		JButton restoregame = new JButton("Restore whole game");
		JButton removegame = new JButton("Uninstall whole game (!)");
		File dirWorlds = new File(Options.getMCDir(), "saves");
		String[] childrenWorlds = dirWorlds.list();
		System.out.println("Listing all files");
		//dirWorlds = new File(Options.getMCDir(), "saves");
		//childrenWorlds = dirWorlds.list();
		wlm = new DefaultListModel<String>();
		if (childrenWorlds != null) {
		  for (int i=0; i<childrenWorlds.length; i++) {
		    String filename = childrenWorlds[i];
		    wlm.addElement(filename);
		  }
		}
		setLayout(new BorderLayout());
		worldsList = new JList<String>(wlm);
		if (worldsList.getSelectedIndex() == -1) {
		  worldsList.setSelectedIndex(0);
		}
		final JPanel listSubPanel = new JPanel(new GridLayout(2, 2));
		final JScrollPane worldsListPane = new JScrollPane(worldsList);
		JButton addFolder = new JButton("Add new ( empty ) World");
		JButton refreshB = new JButton("Refresh list");
		JButton delFolder = new JButton("Delete selected World");
		listSubPanel.add(addFolder);
		listSubPanel.add(delFolder);
		listSubPanel.add(refreshB);
		final JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(worldsListPane, "Center");
		listPanel.add(listSubPanel, "South");
		removeAll();
		add(listPanel, "East");
		
		TransparentPanel centerPanel = new TransparentPanel(new GridLayout(0, 1));
		TransparentPanel buttonsPanel = new TransparentPanel(new GridLayout(0, 1));
		TransparentPanel descPanel = new TransparentPanel(new GridLayout(0, 1));
		
		exitb.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	OptionsMenu.back();
	      }
		});
		addFolder.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	  new Thread() { public void run() {
	    	  String worldname = JOptionPane.showInputDialog(MinecraftSL.instance, "World Name :", "World Creation", -1);
	    	  boolean foldermade = new File(new File(Options.getMCDir(), "saves"), worldname).mkdirs();
	    	  System.out.println(foldermade);
	    	  if (foldermade) {
	   			System.out.println("Listing all files");
	   			File dirWorlds2 = new File(Options.getMCDir(), "saves");
	   			String[] childrenWorlds2 = dirWorlds2.list();
	   			wlm = new DefaultListModel<String>();
	   			if (childrenWorlds2 != null) {
	   			  for (int i=0; i<childrenWorlds2.length; i++) {
	   			    String filename = childrenWorlds2[i];
	   			    wlm.addElement(filename);
	   			  }
	   			}
	   			worldsList = new JList<String>(wlm);
	   			JScrollPane worldsListPane2 = new JScrollPane(worldsList);
	   			listPanel.removeAll();
	   			listPanel.add(worldsListPane2, "Center");
	   			listPanel.add(listSubPanel, "South");
	   			listPanel.validate();
	   			listPanel.repaint();
	    	    JOptionPane.showMessageDialog(MinecraftSL.instance, "Done !", "World Creation", -1);
	    	  } else {
	    		JOptionPane.showMessageDialog(MinecraftSL.instance, "It went wrong :(", "World Creation", 0);
	    	  }
	    	  } }.start();
	      }
		});
		delFolder.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	  new Thread() { public void run() {
	 		  System.out.println("Listing all files");
	    	  File dirWorlds3 = new File(Options.getMCDir(), "saves");
	   		  String[] childrenWorlds3 = dirWorlds3.list();
	   		  wlm = new DefaultListModel<String>();
	   		  if (childrenWorlds3 != null) {
	   		    for (int i=0; i<childrenWorlds3.length; i++) {
	   		      String filename = childrenWorlds3[i];
	   		      wlm.addElement(filename);
	   		    }
	   		  }
	    	  String worldname = childrenWorlds3[worldsList.getSelectedIndex()];
	    	  int accepted = JOptionPane.showConfirmDialog(MinecraftSL.instance, "Are you sure that you want to delete the world "+worldname+" ?\n" + "Can't Undo It ! ( Unless you have an backup )", "Are you sure ? ( World Deletion )", 0, 2);
	    	  if ((accepted == 1) || (accepted == -1)) { 
	    		return;
	    	  }
	    	  File folder = new File(new File(Options.getMCDir(), "saves"), worldname);
	    	  if (net.minecraft.utils.BackupUtil.deleteDirectory(folder)) {
	    		System.out.println("Listing all files");
	    		File dirWorlds2 = new File(Options.getMCDir(), "saves");
	    		String[] childrenWorlds2 = dirWorlds2.list();
	    		wlm = new DefaultListModel<String>();
	    		if (childrenWorlds2 != null) {
	    		  for (int i=0; i<childrenWorlds2.length; i++) {
	    		    String filename = childrenWorlds2[i];
	    		    wlm.addElement(filename);
	    		  }
	    		}
	    		worldsList = new JList<String>(wlm);
	    		JScrollPane worldsListPane2 = new JScrollPane(worldsList);
	    		listPanel.removeAll();
	    		listPanel.add(worldsListPane2, "Center");
	    		listPanel.add(listSubPanel, "South");
	    		listPanel.validate();
	    		listPanel.repaint();
	    	    JOptionPane.showMessageDialog(MinecraftSL.instance, "Done !", "World Deletion", -1);
	    	  } else {
	    		JOptionPane.showMessageDialog(MinecraftSL.instance, "It went wrong ...", "World Deletion", 0);
	    	  }
	    	  } }.start();
	      }
		});
		refreshB.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	    	int index = worldsList.getSelectedIndex();
	  		System.out.println("Listing all files");
			File dirWorlds2 = new File(Options.getMCDir(), "saves");
			String[] childrenWorlds2 = dirWorlds2.list();
			wlm = new DefaultListModel<String>();
			if (childrenWorlds2 != null) {
			  for (int i=0; i<childrenWorlds2.length; i++) {
			    String filename = childrenWorlds2[i];
			    wlm.addElement(filename);
			  }
			}
			worldsList = new JList<String>(wlm);
			if ((wlm.getSize() >= index) || (wlm.getSize() == index)) {
			  worldsList.setSelectedIndex(index);
			} else {
			  worldsList.setSelectedIndex(0);
			}
			JScrollPane worldsListPane2 = new JScrollPane(worldsList);
			listPanel.removeAll();
			listPanel.add(worldsListPane2, "Center");
			listPanel.add(listSubPanel, "South");
			listPanel.validate();
			listPanel.repaint();
	    	} }.start();
	      }
		});
		backupworld.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	 		System.out.println("Listing all files");
	    	File dirWorlds2 = new File(Options.getMCDir(), "saves");
	   		String[] childrenWorlds2 = dirWorlds2.list();
	   		wlm = new DefaultListModel<String>();
	   		if (childrenWorlds2 != null) {
	   		  for (int i=0; i<childrenWorlds2.length; i++) {
	   		    String filename = childrenWorlds2[i];
	   		    wlm.addElement(filename);
	   		  }
	   		}
	    	String world = childrenWorlds2[worldsList.getSelectedIndex()];
	    	System.out.println(worldsList.getSelectedIndex());
	    	if (!BackupUtil.getWorldNFolder(world).exists()) {
	    	  JOptionPane.showMessageDialog(MinecraftSL.instance, "Sorry , but the savestate got removed ... ?!", "World Backup", 0);
	    	  return;
	    	}
	    	JFileChooser save = new JFileChooser();
	    	Calendar now = GregorianCalendar.getInstance();
	    	save.setFileSelectionMode(0);
	    	save.setSelectedFile(new File(String.format("" + world + ".mcworld", new Object[] { now })));
	    	
	    	save.setFileFilter(new BackupUtil.WorldFileFilter());
	    	int result = save.showSaveDialog(MinecraftSL.instance);
	    	if (result != 0) {
	    	  return;
	    	}
	    	File f = save.getSelectedFile();
	    	 if (f == null) {
	    	   return;
	    	 }
	        BackupUtil.backupWorld(world, f);
	    	JOptionPane.showMessageDialog(MinecraftSL.instance, "Done!", "World Backup", -1);
	    	} }.start();
	      }
		});
		restoreworld.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	 		System.out.println("Listing all files");
	    	File dirWorlds2 = new File(Options.getMCDir(), "saves");
	   		String[] childrenWorlds2 = dirWorlds2.list();
	   		wlm = new DefaultListModel<String>();
	   		if (childrenWorlds2 != null) {
	   		  for (int i=0; i<childrenWorlds2.length; i++) {
	   		    String filename = childrenWorlds2[i];
	   		    wlm.addElement(filename);
	   		  }
	   		}
	   		
	   		String toworld = childrenWorlds2[worldsList.getSelectedIndex()]; 
	    	int result = JOptionPane.showConfirmDialog(MinecraftSL.instance, "Are you sure that you want to overwrite the world when existing ?\n" + "Can't Undo It ! ( Unless you have an backup )", "Are you sure ? ( World Restoration )", 0, 2);
	    	
	    	if ((result == 1) || (result == -1)) {
	    	  return;
	    	}
	    	
	    	JFileChooser save = new JFileChooser();
	    	save.setFileSelectionMode(0);
	    	save.setFileFilter(new BackupUtil.WorldFileFilter());
	    	save.showOpenDialog(MinecraftSL.instance);
	    	File f = save.getSelectedFile();
	    	if (f == null)
	    	  return;
	    	try
	    	{
	    	  BackupUtil.restoreWorld(f, toworld);
	    	 } catch (IllegalStateException ex) {
	    	   JOptionPane.showMessageDialog(MinecraftSL.instance, "Failed !\nInvalid Zip Contents !\nThere went something wrong . Check everything .\n"+ex, "World Restoration", 0);
	    	
	    	  return;
	    	}
	    	JOptionPane.showMessageDialog(MinecraftSL.instance, "Done!", "World Restoration", -1);
	    	} }.start();
	      }
		});
		backupgame.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	    	JFileChooser save = new JFileChooser();
	    	Calendar now = GregorianCalendar.getInstance();
	    	save.setSelectedFile(new File(String.format("MCGame_%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS" + "_Backup." + "mcgame", new Object[] { now })));
	    	save.setFileSelectionMode(0);
	    	save.setFileFilter(new BackupUtil.GameFileFilter());
	    	int result = save.showSaveDialog(MinecraftSL.instance);
	    	if (result != 0) {
	    	  return;
	    	}
	    	File f = save.getSelectedFile();
	    	if (f == null) {
	    	  return;
	    	}
	    	BackupUtil.backupGame(f);
	    	JOptionPane.showMessageDialog(MinecraftSL.instance, "Done!", "Game Backup", -1);
	    	} }.start();
	      }
		});
		restoregame.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	    	int result = JOptionPane.showConfirmDialog(MinecraftSL.instance, "Are you sure that you want to restore WHOLE Minecraft ?\nIT MIGHT OVERWRITE ALL YOUR DATA\nMake sure you have your most recent save games backed up before this !", "Are you sure ? (Full Game Restoration)", 0, 2);
	    	
	    	if ((result == 1) || (result == -1)) {
	    	  return;
	    	}
	    	JFileChooser save = new JFileChooser();
	    	save.setFileSelectionMode(0);
	    	save.setFileFilter(new BackupUtil.GameFileFilter());
	    	save.showOpenDialog(MinecraftSL.instance);
	    	File f = save.getSelectedFile();
	    	if ((f == null) || (!f.exists()))
	    	  return;
	    	try
	    	{
	    	  BackupUtil.restoreGame(f);
	    	} catch (IllegalStateException ex) {
	    	  JOptionPane.showMessageDialog(MinecraftSL.instance, "Failed !\nInvalid Zip Contents!\nthe game folder inside must have 'minecraft_backup' as name .", "Game Restoration", 0);
	    	
	    	  return;
	    	}
	    	JOptionPane.showMessageDialog(MinecraftSL.instance, "Done !", "Game Restoration", -1);
	    	} }.start();
	      }
		});
		removegame.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) {
	    	new Thread() { public void run() {
	    	int result = JOptionPane.showConfirmDialog(MinecraftSL.instance, "Are you sure that you want to uninstall Minecraft?\nCan't Undo It! (unless you have backup, hehe)", "Are you sure? (Uninstallation)", 0, 2);
	    	
	    	if ((result == 1) || (result == -1)) {
	    	  return;
	    	}
	    	BackupUtil.uninstallGame();
	    	JOptionPane.showMessageDialog(MinecraftSL.instance, "Done!", "Uninstallation", -1);
	    	} }.start();
	      }
		});
		
		buttonsPanel.removeAll();
		buttonsPanel.add(backupworld);
		buttonsPanel.add(removegame);
		buttonsPanel.add(restoreworld);
		buttonsPanel.add(backupgame);
		buttonsPanel.add(restoregame);
		//descPanel.add(new JLabel("", 4));
		descPanel.removeAll();
		descPanel.add(new JLabel("Welcome to the Minecraft Backup Manager v.1.4b3"), "West");
		descPanel.add(new JLabel("This is the Launcher Menu . On the Left are"), "West");
		descPanel.add(new JLabel("all the Functions and on the Right are the"), "West");
		descPanel.add(new JLabel("Worlds which can be selected for backuping or"), "West");
		descPanel.add(new JLabel("where the backups can be restored into ."), "West");
		descPanel.add(new JLabel("Files made from versions older than 1.4 are"), "West");
		descPanel.add(new JLabel("needing to be formatted or manually with any"), "West");
		descPanel.add(new JLabel("zip file manager to be restored and then backed"), "West");
		descPanel.add(new JLabel("up . Just remember to make an Backup of the"), "West");
		descPanel.add(new JLabel("Game before updating or installing mods and"), "West");
		descPanel.add(new JLabel("selecting the right world before doing anything !"), "West");
		
		centerPanel.removeAll();
		TransparentPanel topPanel = new TransparentPanel(new BorderLayout());
		topPanel.add(buttonsPanel, "West");
		topPanel.add(descPanel, "Center");
		TransparentPanel bottomPanel = new TransparentPanel(new BorderLayout());
		bottomPanel.setBorder(new BevelBorder(1, new Color(0x000000), new Color(0x242424), new Color(0x888888), new Color(0x242424)));
		centerPanel.add(topPanel, "Center");
		bottomPanel.add(OptionsMenu.progress, "Center");
		bottomPanel.add(lastfile, "North");
		centerPanel.add(bottomPanel, "South");
		add(exitb, "South");
		add(centerPanel, "West");
	}
	
}
