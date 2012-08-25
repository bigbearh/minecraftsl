package net.minecraft.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BackupUtil {
	public static final String PACKAGE_EXTENSION = "zip";
	public static final String DATE_TIME_FORMAT = "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS";

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if ((i > 0) && (i < s.length() - 1)) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	// If targetLocation does not exist, it will be created.
	public static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void moveDirectory(File sourceLocation, File targetLocation)
			throws IOException {
		sourceLocation.renameTo(targetLocation);
	}

	public static boolean deleteDirectory(File dir) {
		boolean doreturn = false;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(dir, children[i]));
				if (!success) {
					doreturn = false;
				} else {
					doreturn = true;
				}
			}
		} else {
			doreturn = dir.delete();
		}
		return doreturn;
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
			System.out.print("Overwrite existing file " + toFile.getName()
					+ "? (Y/N): ");
			System.out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String response = in.readLine();
			if ((!response.equals("Y")) && (!response.equals("y")))
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

	public static void downloadFile(String filedown, File filedest)
			throws MalformedURLException, IOException {
		URL download = new URL(filedown);
		ReadableByteChannel rbc = Channels.newChannel(download.openStream());
		FileOutputStream fos = new FileOutputStream(filedest);
		try {
			fos.getChannel().transferFrom(rbc, 0L, 16777216L);
		} finally {
			fos.close();
		}
	}

	public static void deleteFile(File file) {
		boolean success = (file.delete());
		if (success) {
			System.out.println(file + " deleted .");
		} else {
			System.out.println(file + " could not be deleted . ");
		}
	}

	public static void makeDirectory(File dir) {
		try {
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					System.err.println("Something went well : " + dir
							+ " created");
				} else {
					System.err.println("Found error , folder not created : "
							+ dir);
					System.err.println("TryAgain outputs : " + dir.mkdirs());
				}
			} else {
				System.out.println("Folder / Folders existing ... skipping");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}
}