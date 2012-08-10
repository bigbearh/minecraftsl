package net.minecraft.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 *	Class for handling HTTP calls , util functions or .get(...) requests .
 *	@author Maik
 *	@see HTTPServer
 */
public final class HTTPClient {
	
	/**
	 * Opens an link in new browser window , 
	 * uses standard browser 
	 * @param uri URI to open ( link ) 
	 */
	public static void openLink(URI uri) {
		try {
			Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { uri });
		} catch (Throwable e) {
			System.out.println("Failed to open link " + uri.toString());
		}
	}
	
	/**
	 * Get HTTP call return as string ( Website ? )
	 * @param targetURL URL of the target
	 * @param urlParameters Parameters ( http://a.b.com?x=y... )
	 * @param type Content type ( "" or null if default type )
	 * @return Empty string if execution failed or response , which can be empty , too .
	 */
	public static String executePost(String targetURL, String urlParameters, String type) {
		HttpURLConnection connection = null;
		String str = "";
		try {
			//
			URL url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", (type != null && !type.trim().equals(""))?type:"application/x-www-form-urlencoded");
			
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			connection.connect();
			
		    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.flush();
		    wr.close();
		    
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    
		    StringBuffer response = new StringBuffer();
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	//String line;
		        response.append(line);
		        response.append('\r');
		    }
		    rd.close();
		    
		    str = response.toString();
		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
	    	if (connection != null) {
	    		connection.disconnect();
	    	}
	    }
		return str;
	}
	
	/**
	 * Gets the class from the given url and converts it into an array of bytes . Useful for modules loading classes over the net .
	 * @param targetURL url to get the class from
	 * @param urlParameters url parameters to give ( http://a.b.com?x=y... )
	 * @return byte[] containing classdata convertable with NetClassLoader
	 */
	public static byte[] getClassAsBytes(String targetURL, String urlParameters) {
		byte[] data = new byte[] {};
		HttpURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			connection.connect();
			
		    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.flush();
		    wr.close();
		    
		    InputStream is = connection.getInputStream();
		    data = new byte[is.available()];
		    is.read(data);
		    
		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
	    	if (connection != null) {
	    		connection.disconnect();
	    	}
	    }
		return data;
	}
	
}
