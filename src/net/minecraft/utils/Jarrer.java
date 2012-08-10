package net.minecraft.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.minecraft.GameUpdater;

public class Jarrer
{
  static final int BUFFER = 2048;
  
  public static boolean jarFoldersContents(File srcFolder, File destJarFile)
  {
    try
    {
      BufferedInputStream origin = null;
      FileOutputStream dest = new FileOutputStream(destJarFile);
      JarOutputStream out = new JarOutputStream(new BufferedOutputStream(dest));

      String[] files = srcFolder.list();
      int filesCount = files.length;
      int done = 0;

      while (done != filesCount) {
        //if (!add(srcFolder.getPath(), files[done], srcFolder.getPath(), out)) {
      	if (!addToJar(srcFolder.getPath(), "", files[done], out)) {
          return false;
        }
        done++;
      }

      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  protected static boolean addToJar(String absolutePath, String relativePath, String fileName, JarOutputStream out) {
    File file = new File(absolutePath + File.separator + fileName);

    //System.out.println("Adding \"" + absolutePath + File.separator + fileName + "\" file");
    if (file.isHidden())
      return true;
    if (file.isDirectory()) {
      absolutePath = absolutePath + "/" + file.getName().replace("\\","/");
      if (relativePath.equals("")) {
        relativePath = file.getName();
      } else {
      	relativePath = relativePath.replace("\\","/") + "/" + file.getName();
      }
      for (String child : file.list()) {
        if (!addToJar(absolutePath, relativePath, child, out)) {
          return false;
        }
      }
      return true;
    }
    try {
      byte[] data = new byte[BUFFER];

      FileInputStream fi = new FileInputStream(file);
      BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
      JarEntry entry = null;
      if (relativePath.equals("") || absolutePath.replace(file.getName(), "").endsWith("/")) {
        entry = new JarEntry(relativePath.replace("\\","/") + fileName.replace("\\","/"));
      } else {
      	entry = new JarEntry(relativePath.replace("\\","/") + "/" + fileName.replace("\\","/"));
      }
      out.putNextEntry(entry);
      int count;
      while ((count = origin.read(data, 0, BUFFER)) != -1)
      {
        //int count;
        out.write(data, 0, count);
      }
      origin.close();
    } catch (Exception ex) {
      Logger.getLogger(Jarrer.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
    return true;
  }
  
  protected static boolean addToZip(String absolutePath, String relativePath, String fileName, ZipOutputStream out) {
    File file = new File(absolutePath + File.separator + fileName);

    //System.out.println("Adding \"" + absolutePath + File.separator + fileName + "\" file");
    if (file.isHidden())
      return true;
    if (file.isDirectory()) {
      absolutePath = absolutePath + File.separator + file.getName();
      if (relativePath.equals("")) {
        relativePath = file.getName();
      } else {
      	relativePath = relativePath + File.separator + file.getName();
      }
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
      ZipEntry entry = null;
      if (relativePath.equals("") || absolutePath.replace(file.getName(), "").endsWith(File.separator)) {
        entry = new ZipEntry(relativePath + fileName);
      } else {
      	entry = new ZipEntry(relativePath + File.separator + fileName);
      }
      out.putNextEntry(entry);
      int count;
      while ((count = origin.read(data, 0, BUFFER)) != -1)
      {
        //int count;
        out.write(data, 0, count);
      }
      origin.close();
    } catch (Exception ex) {
      Logger.getLogger(Jarrer.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
    return true;
  }


  public static boolean jarFolderContents(File srcFolder, File destZipFile)
  {
    return jarFoldersContents(srcFolder, destZipFile);
  }
  
}