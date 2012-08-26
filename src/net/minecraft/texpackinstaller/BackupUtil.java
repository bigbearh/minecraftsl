package net.minecraft.texpackinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.minecraft.Options;

public class BackupUtil {
	public static final String TEXPACKS_BACKUP_EXTENSION = "mctexpacks";
	public static final String TEXPACKS_EXTENSION = "zip";
	public static final String DATE_TIME_FORMAT = "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS";

	public static void uninstallPacks() {
		deleteFileDir(new File(Options.getMCDir(), "texturepacks"));
	}

	public static void backupPacks(File zipDestiny) {
		ArrayList<File> contents = new ArrayList<File>();

		File f = new File(Options.getMCDir(), "texturepacks");
		if (f.exists()) {
			contents.add(f);
		}
		File[] source = (File[]) contents.toArray(new File[contents.size()]);
		backupContents(source, zipDestiny, "texpacks_backup", "mctexpacks");
	}

	public static void restorePacks(File zipSource) {
		File destiny = Options.getMCDir();
		restoreContents(zipSource, destiny, "texpacks_backup");
	}

	public static void installPacks(File filePack) {
		try {
			copyFile(filePack, new File(new File(Options.getMCDir(),
					"texturepacks"), filePack.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void backupFile(File source, File zipDestiny,
			String genericName, String extension) {
		File generic = new File(Options.getSystemTempDir(), genericName);
		if (!source.exists()) {
			throw new IllegalArgumentException("Source file does not exist: "
					+ source.getName());
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

	public static void restoreFile(File zipSource, File destiny,
			String genericName) {
		File generic = new File(Options.getSystemTempDir(), genericName);
		if (generic.exists()) {
			deleteFileDir(generic);
		}
		Zipper.unzipFolder(zipSource, Options.getSystemTempDir());
		if (!generic.exists()) {
			throw new IllegalStateException(
					"Wrong content in zip file -> not found: "
							+ generic.getName());
		}
		if (destiny.exists()) {
			deleteFileDir(destiny);
		}
		if ((destiny.getParentFile() != null)
				&& (!destiny.getParentFile().exists())) {
			destiny.getParentFile().mkdirs();
		}
		generic.renameTo(destiny);
	}

	public static void copyFile(File fromFileName, File toFileName)
			throws IOException {
		File fromFile = fromFileName;
		File toFile = toFileName;

		if (!fromFile.exists())
			throw new IOException("FileCopy: no such source file: "
					+ fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: can't copy directory: "
					+ fromFileName);
		if (!fromFile.canRead()) {
			throw new IOException("FileCopy: source file is unreadable: "
					+ fromFileName);
		}
		if (toFile.isDirectory()) {
			toFile = new File(toFile, fromFile.getName());
		}
		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new IOException(
						"FileCopy: destination file is unwriteable: "
								+ toFileName);
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(System.in));
			int result = JOptionPane.showConfirmDialog(null,
					"Overwrite existing file \n" + toFile.getName() + "\n?",
					"Overwrite ?", 0, 2);
			if (result == 1 || result == -1)
				throw new IOException(
						"FileCopy: existing file was not overwritten.");
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException(
						"FileCopy: destination directory doesn't exist: "
								+ parent);
			if (dir.isFile())
				throw new IOException(
						"FileCopy: destination is not a directory: " + parent);
			if (!dir.canWrite()) {
				throw new IOException(
						"FileCopy: destination directory is unwriteable: "
								+ parent);
			}
		}
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1) {
				// int bytesRead;
				to.write(buffer, 0, bytesRead);
			}
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException localIOException) {
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException localIOException1) {
				}
		}
	}

	public static void backupContents(File[] folderContents, File zipDestiny,
			String genericName, String extension) {
		File[] arrayOfFile = folderContents;
		int j = folderContents.length;
		for (int i = 0; i < j; i++) {
			File content = arrayOfFile[i];
			if (!content.exists()) {
				throw new IllegalArgumentException(
						"You sent me a folder content that doesnt exist : "
								+ content.getName());
			}
		}

		if (!zipDestiny.getName().endsWith("." + extension)) {
			zipDestiny = new File(zipDestiny.getPath() + "." + extension);
		}
		Zipper.zipFolders(folderContents, zipDestiny, genericName);
	}

	public static void restoreContents(File zipSource, File folderDestiny,
			String genericName) {
		File genericFolder = new File(Options.getSystemTempDir(), genericName);
		if (genericFolder.exists()) {
			deleteFileDir(genericFolder);
		}
		if (!folderDestiny.exists()) {
			folderDestiny.mkdirs();
		}
		if (!folderDestiny.isDirectory()) {
			throw new IllegalArgumentException(
					"The destiny folder must be a directory!");
		}
		Zipper.unzipFolder(zipSource, Options.getSystemTempDir());
		if (!genericFolder.exists()) {
			throw new IllegalStateException(
					"Wrong content in zip file -> not found: "
							+ genericFolder.getName());
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

	public static class GameFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f == null) {
				return false;
			}
			if (f.isDirectory()) {
				return true;
			}
			String ext = BackupUtil.getExtension(f);

			return (ext != null) && (ext.equalsIgnoreCase("zip"));
		}

		public String getDescription() {
			return "Minecraft Texturepack";
		}
	}

	public static class WorldFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f == null) {
				return false;
			}
			if (f.isDirectory()) {
				return true;
			}
			String ext = BackupUtil.getExtension(f);

			return (ext != null) && (ext.equalsIgnoreCase("mctexpacks"));
		}

		public String getDescription() {
			return "Minecraft Texturepack-Pack";
		}
	}

}