package net.minecraft.backupmanager;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import net.minecraft.Options;

public class BackupUtil
{

	public static final String WORLD_BACKUP_EXTENSION = "mcworld";
  public static final String WORLD_BACKUP_GEN_NAME = "world_backup";
  public static final String GAME_BACKUP_EXTENSION = "mcgame";
  public static final String GAME_BACKUP_GEN_NAME = "minecraft_backup";
  public static final String DATE_TIME_FORMAT = "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS";

  public static void uninstallGame()
  {
    deleteFileDir(Options.getMCDir());
  }

  public static void backupGame(File zipDestiny)
  {
    ArrayList<File> contents = new ArrayList<File>();

    File f = Options.getMCDir();
    if (f.exists()) {
      contents.add(f);
    }
    File[] source = (File[])contents.toArray(new File[contents.size()]);
    backupContents(source, zipDestiny, "minecraft_backup", "mcgame");
  }

  public static void restoreGame(File zipSource) {
    File destiny = Options.getMCDir().getParentFile();
    restoreContents(zipSource, destiny, "minecraft_backup");
  }

  public static void backupWorld(String world, File destZip)
  {
    File source = new File(new File(Options.getMCDir(), "saves"), world);
    backupFile(source, destZip, "world_backup", "mcworld");
  }

  public static void restoreWorld(File zipSource, String toworld) {
    File destiny = new File(new File(Options.getMCDir(), "saves"), toworld);
    restoreFile(zipSource, destiny, "world_backup");
  }

  public static void backupFile(File source, File zipDestiny, String genericName, String extension) {
    File generic = new File(Options.getSystemTempDir(), genericName);
    if (!source.exists()) {
      throw new IllegalArgumentException("Source file does not exist: " + source.getName());
    }
    if (!zipDestiny.getName().endsWith("." + extension)) {
      zipDestiny = new File(zipDestiny.getPath() + "." + extension);
    }
    if (generic.exists()) {
      deleteFileDir(generic);
    }
    source.renameTo(generic);
    Zipper.zipFolder(generic, zipDestiny);
    generic.renameTo(source);
  }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if ((i > 0) && (i < s.length() - 1)) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }

  public static boolean deleteFileDir(File file) {
    if (!file.exists()) {
      return false;
    }
    if (file.isFile())
      return file.delete();
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        deleteFileDir(f);
      }
      return file.delete();
    }
    return file.delete();
  }

  public static void restoreFile(File zipSource, File destiny, String genericName) {
    File generic = new File(Options.getSystemTempDir(), genericName);
    if (generic.exists()) {
      deleteFileDir(generic);
    }
    Zipper.unzipFolder(zipSource, Options.getSystemTempDir());
    if (!generic.exists()) {
      throw new IllegalStateException("Wrong content in zip file -> not found: " + generic.getName());
    }
    if (destiny.exists()) {
      deleteFileDir(destiny);
    }
    if ((destiny.getParentFile() != null) && (!destiny.getParentFile().exists())) {
      destiny.getParentFile().mkdirs();
    }
    generic.renameTo(destiny);
  }

  public static void backupContents(File[] folderContents, File zipDestiny, String genericName, String extension) {
    File[] arrayOfFile = folderContents; int j = folderContents.length; for (int i = 0; i < j; i++) { File content = arrayOfFile[i];
      if (!content.exists()) {
        throw new IllegalArgumentException("You sent me a folder content that doesnt exist : " + content.getName());
      }
    }

    if (!zipDestiny.getName().endsWith("." + extension)) {
      zipDestiny = new File(zipDestiny.getPath() + "." + extension);
    }
    Zipper.zipFolders(folderContents, zipDestiny, genericName);
  }

  public static void restoreContents(File zipSource, File folderDestiny, String genericName) {
    File genericFolder = new File(Options.getSystemTempDir(), genericName);
    if (genericFolder.exists()) {
      deleteFileDir(genericFolder);
    }
    if (!folderDestiny.exists()) {
      folderDestiny.mkdirs();
    }
    if (!folderDestiny.isDirectory()) {
      throw new IllegalArgumentException("The destiny folder must be a directory!");
    }
    Zipper.unzipFolder(zipSource, Options.getSystemTempDir());
    if (!genericFolder.exists()) {
      throw new IllegalStateException("Wrong content in zip file -> not found: " + genericFolder.getName());
    }
    File[] generics = genericFolder.listFiles();
    for (File generic : generics) {
      File destiny = new File(folderDestiny, generic.getName());
      if (destiny.exists()) {
        deleteFileDir(destiny);
      }
      generic.renameTo(destiny);
    }
  }

  public static class GameFileFilter extends FileFilter
  {
    public boolean accept(File f)
    {
      if (f == null) {
        return false;
      }
      if (f.isDirectory()) {
        return true;
      }
      String ext = BackupUtil.getExtension(f);

      return (ext != null) && (ext.equalsIgnoreCase("mcgame"));
    }

    public String getDescription()
    {
      return "Minecraft Game files";
    }
  }

  public static class WorldFileFilter extends FileFilter
  {
    public boolean accept(File f)
    {
      if (f == null) {
        return false;
      }
      if (f.isDirectory()) {
        return true;
      }
      String ext = BackupUtil.getExtension(f);

      return (ext != null) && (ext.equalsIgnoreCase("mcworld"));
    }

    public String getDescription()
    {
      return "Minecraft World files";
    }
  }
  
  public static 
  class JarFileFilter extends FileFilter {
    public boolean accept(File f)
    {
      if (f == null) {
        return false;
      }
      if (f.isDirectory()) {
        return true;
      }
      String ext = BackupUtil.getExtension(f);

      return (ext != null) && (ext.equalsIgnoreCase("jar"));
    }

    public String getDescription()
    {
      return "Minecraft Main Game files";
    }
  }

public static File getWorldNFolder(String world) {
	File fullPath;
	fullPath = new File(new File(Options.getMCDir(), "saves"), world);
	return fullPath;
}
}