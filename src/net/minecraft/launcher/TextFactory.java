package net.minecraft.launcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class TextFactory {
	
	  public static Bitmap toBitmap(String s, int color, Font f, boolean antialias) {
		  return toGradientBitmap(s, new Color(color).brighter().brighter().getRGB(), new Color(color).darker().darker().getRGB(), f, antialias);
	  }
	
	  public static Bitmap toGradientBitmap(String s, int color1, int color2, Font f, boolean antialias) {
		  
		  BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		  Graphics2D g = bi.createGraphics();
		  if (antialias)
			  g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		  else
			  g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		  g.setFont(f);
		  int w1 = g.getFontMetrics().getWidths()[g.getFont().getSize()] + 4;
		  int w = w1 * s.length();
		  int h1 = g.getFontMetrics().getHeight();
		  int h = h1 + 8;
		  g.dispose();
		  if (w == 0) {
			  w1 = g.getFont().getSize()*6;
			  w = w1 * s.length();
		  }
		  if (w == 0) {
			  w = 128;
		  }
		  bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		  g = bi.createGraphics();
		  g.setFont(f);
		  if (antialias)
			  g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		  else
			  g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		  g.setPaint(new GradientPaint(0, 0, new Color(color1), 0, h*2, new Color(color2)));
		  g.drawString(s, 0, h1);
		  g.dispose();
		  
		  return Bitmap.convert(bi);
	  }
	  
	  public static Bitmap toGradientBitmap(String s, int color1, int color2, boolean antialias) {
		  // Tahoma or g.getFont().getFontName() ?
		  //Font f = new Font("Tahoma", 0, 12);
		  // Better UBUNTUL
		  Font f = Resources.Fonts.minecraft;
		  return toGradientBitmap(s, color1, color2, f, antialias);
	  }
	  
	  public static Bitmap toBitmap(String s, int color, boolean antialias) {
		  // Tahoma or g.getFont().getFontName() ?
		  //Font f = new Font("Tahoma", 0, 12);
		  // Better UBUNTUL
		  Font f = Resources.Fonts.minecraft;
		  return toBitmap(s, color, f, antialias);
	  }
	
}
