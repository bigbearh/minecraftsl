package net.minecraft.launcher;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

public final class Resources {
	
	public static class Bitmaps {
		
		public static final Bitmap launcherBackground = load("/res/bg.png");
		
		public static final BufferedImage icon32 = loadBufferedImage("/res/icon.png");
		
		public static final Bitmap[][] button = cut("/res/button.png", 128, 32);
		public static final Bitmap[][] checkbox = cut("/res/checkbox.png", 32, 32);
		
		public static final Bitmap wool_black = load("/res/blackw.png");
		public static final Bitmap wool_white = load("/res/whitew.png");
		public static final Bitmap wool_cyan = load("/res/cyanw.png");
		
	    /**
	     * Return the bitmaps for a given piece of art, cut out from a sheet
	     * 
	     * @param string Art piece name
	     * @param w Width of a single bitmap
	     * @param h Height of a single bitmap
	     * @return Bitmap array
	     */
		public static Bitmap[][] cut(String string, int w, int h) {
		    return cut(string, w, h, 0, 0);
		}

	    /**
	     * Return the bitmaps for a given piece of art, cut out from a sheet
	     * 
	     * @param string Art piece name
	     * @param w Width of a single bitmap
	     * @param h Height of a single bitmap
	     * @param bx
	     * @param by
	     * @return Bitmap array
	     */
		public static Bitmap[][] cut(String string, int w, int h, int bx, int by) {
			try {
				BufferedImage bi = ImageIO.read(RES.class
						.getResource(string));

				int xTiles = (bi.getWidth() - bx) / w;
				int yTiles = (bi.getHeight() - by) / h;

				Bitmap[][] result = new Bitmap[xTiles][yTiles];

				for (int x = 0; x < xTiles; x++) {
					for (int y = 0; y < yTiles; y++) {
					    result[x][y] = new Bitmap(w, h);
						bi.getRGB(bx + x * w, by + y * h, w, h,
								result[x][y].pixels, 0, w);
					}
				}

				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		public static Bitmap[][] cutv(String string, int h) {
	        try {
	            BufferedImage bi = ImageIO.read(RES.class.getResource(string));

	            int yTiles = bi.getHeight() / h;

	            int xTiles = 0;
	            Bitmap[][] result = new Bitmap[yTiles][];
	            for (int y = 0; y < yTiles; y++) {
	                List<Bitmap> row = new ArrayList<Bitmap>();
	                int xCursor=0;
	                while (xCursor < bi.getWidth()) {
	                    int w = 0;
	                    while (xCursor + w < bi.getWidth() && bi.getRGB(xCursor + w, y * h) != 0xffed1c24) {
	                        w++;
	                    }
	                    if (w > 0) {
	                        Bitmap bitmap = new Bitmap(w, h);
	                        bi.getRGB(xCursor, y * h, w, h, bitmap.pixels, 0, w );
	                        row.add(bitmap);
	                    }
	                    xCursor += w+1;
	                }
	                if (xTiles < row.size()) xTiles = row.size();
	                result[y] = row.toArray(new Bitmap[0]);
	            }

	            Bitmap[][] resultT = new Bitmap[xTiles][yTiles];
	            for (int x = 0; x < xTiles; x++) {
	                for (int y = 0; y < yTiles; y++) {
	                    try {
	                        resultT[x][y] = result[y][x];
	                    } catch (IndexOutOfBoundsException e) {
	                        resultT[x][y] = null;
	                    }
	                }
	            }

	            return resultT;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
		
		public static int[][] getColors(Bitmap[][] tiles) {
			int[][] result = new int[tiles.length][tiles[0].length];
			for (int y = 0; y < tiles[0].length; y++) {
				for (int x = 0; x < tiles.length; x++) {
					result[x][y] = getColor(tiles[x][y]);
				}
			}
			return result;
		}

		public static int getColor(Bitmap bitmap) {
			int r = 0;
			int g = 0;
			int b = 0;
			for (int i = 0; i < bitmap.pixels.length; i++) {
				int col = bitmap.pixels[i];
				r += (col >> 16) & 0xff;
				g += (col >> 8) & 0xff;
				b += (col) & 0xff;
			}

			r /= bitmap.pixels.length;
			g /= bitmap.pixels.length;
			b /= bitmap.pixels.length;

			return 0xff000000 | r << 16 | g << 8 | b;
		}

		/**
		 * Load a bitmap resource by name
		 * 
		 * @param string Resource name
		 * @return Bitmap on success, null on error
		 */
		public static Bitmap load(String string) {
			try {
				BufferedImage bi = ImageIO.read(RES.class
						.getResource(string));

				int w = bi.getWidth();
				int h = bi.getHeight();

				Bitmap result = new Bitmap(w, h);
				bi.getRGB(0, 0, w, h, result.pixels, 0, w);

				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
		
		/**
		 * Load a bitmap resource by name
		 * 
		 * @param string Resource name
		 * @return BufferedImage on success, null on error
		 */
		public static BufferedImage loadBufferedImage(String string) {
			try {
				BufferedImage bi = ImageIO.read(RES.class
						.getResource(string));
				return bi;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
		
	}
	
	public static class Fonts {
		
		public static Font minecraft = utilizeFont("/res/minecraftf.ttf", false);
		
		public static Font utilizeFont(String sarg) {
			Font alternate = new Font("Tahoma", 0, 8);
			try {
				String s = "";
				int type = -1;
				if (sarg.toLowerCase().endsWith(".ttf")) type = Font.TRUETYPE_FONT;
				Font f = Font.createFont(type, RES.class.getResourceAsStream(sarg));
				f = f.deriveFont(12f);
				return f;
			} catch (Throwable e) {
				return alternate;
			}
		}
		
		public static Font utilizeFont(String sarg, boolean absolute) {
			return utilizeFont(sarg);
		}
		
	}
	
	public static String getJarPath() {
		String path = RES.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = "";
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodedPath;
	}
	
	public static String getJarFolderPath() {
		return getJarPath().substring(0, getJarPath().lastIndexOf("/") + 1);
	}
    
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
	
}
