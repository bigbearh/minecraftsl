package jplayground.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;

/**
 *	Utility for doing various things like load or save text files or get MD5 checksums .
 */
public final class ResourceUtil {
	
	/**
	 * Load the file f as string .
	 * @param f File to load
	 * @return String that equals the text value of the given file or "" if any problem occurred or file didn't exist .
	 */
    public static String loadTextFile(File f)  {
    	try {
            if(!f.exists())
            {
            	f.getParentFile().mkdirs();
            	f.createNewFile();
                return "";
            }
            String s = "";
            BufferedReader bufferedreader = new BufferedReader(new FileReader(f));
            for(String l = ""; (l = bufferedreader.readLine()) != null;)
            {
                s += l+"\n";
            }
            bufferedreader.close();
            return s;
        } catch(Exception e) {
        	return "";
        }
    }
    
    /**
     * Save the string s into file f .
     * @param s String to save
     * @param f File that will be saved to .
     * @return true if saved properly , false otherwise
     */
    public static boolean saveTextFile(String s, File f) {
        try {
        	if (f.exists()) {
        		f.delete();
        	}
        	if (!f.exists()) {
        		f.getParentFile().mkdirs();
        		f.createNewFile();
        	}
            PrintWriter printwriter = new PrintWriter(new FileWriter(f));
            for (String l : s.split("\n")) {
            	printwriter.println(l);
            }
            printwriter.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Converts the given URL into a file .
     * @param url URL to return
     * @return File that was got with the URL url
     * @throws IllegalArgumentException if the URL can't be converted to an URI by URISyntaxException ( if this URL is not formatted strictly according to
     *		  to RFC2396 and cannot be converted to a URI )
     */
    public static File urlToFile(URL url) throws IllegalArgumentException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            try {
                uri = new URI(url.getProtocol(), url.getUserInfo(), url
                        .getHost(), url.getPort(), url.getPath(), url
                        .getQuery(), url.getRef());
            } catch (URISyntaxException e1) {
                throw new IllegalArgumentException("invalid URL: " + url);
            }
        }
        return new File(uri);
    }
    
    /**
     * Get the URL to the file f
     * @param f File whose URL is returned if possible
     * @return URL of file f
     * @throws MalformedURLException
     */
    public static URL fileToUrl(File f) throws MalformedURLException {
        return f.toURI().toURL();
    }
	
    /**
     * Creates a checksum for file f for the method {@link #getMD5Checksum(File)} .
     * @param f File whose checksum is returned
     * @return File f's checksum
     * @throws Exception
     */
    public static byte[] createChecksum(File f) throws Exception {
    	InputStream fis = new FileInputStream(f);
    	
    	byte[] buffer = new byte[1024];
    	MessageDigest complete = MessageDigest.getInstance("MD5");
    	int numRead;
    	
    	do {
    		numRead = fis.read(buffer);
    		if (numRead > 0) {
    			complete.update(buffer, 0, numRead);
    		}
    	} while (numRead != -1);
    	
    	fis.close();
    	return complete.digest();
    }
    
    /**
     * Creates a MD5 checksum for file f .
     * @param f File whose checksum is returned
     * @return File f's checksum
     * @throws Exception
     */
    public static String getMD5Checksum(File f) throws Exception {
    	if (f.isDirectory()) return getMD5FolderChecksum(f, true);
    	byte[] b = createChecksum(f);
    	String result = "";
    	
    	for (int i = 0; i < b.length; i++) {
    		result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    	}
    	
    	return result;
    }
    
    private static String getMD5FolderChecksum(File f, boolean header) throws Exception {
    	String result = "";
    	if (header) result = "#MD5 List of folder "+f.getName()+":\n";
    	
    	for (File f2 : f.listFiles()) {
    		if (f2.isDirectory()) {
    			result += getMD5FolderChecksum(f2, false);
    			continue;
    		}
    		result += f2.getParentFile().getName()+"/"+f2.getName()+" = "+getMD5Checksum(f2)+"\n";
    	}
    	
    	return result;
    }
    
}
