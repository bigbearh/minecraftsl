package net.minecraft.res;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *	Resource utility class , used for storing resources or style color fields .
 *	
 *	@author Maik
 */
public final class Skin {
	
	public static BufferedImage icon;
	public static BufferedImage wool_black;
	public static BufferedImage wool_white;
	public static BufferedImage wool_blue;
	public static BufferedImage character;
	public static BufferedImage logo;
	public static BufferedImage biggrass;
	public static Color button;
	
	static {
		try {
			icon = ImageIO.read(Skin.class.getResource("favicon.png"));
			wool_black = ImageIO.read(Skin.class.getResource("woolblack.png"));
			wool_white = ImageIO.read(Skin.class.getResource("woolwhite.png"));
			wool_blue = ImageIO.read(Skin.class.getResource("woolblue.png"));
			character = ImageIO.read(Skin.class.getResource("char.png"));
			logo = ImageIO.read(Skin.class.getResource("logo.png"));
			biggrass = ImageIO.read(Skin.class.getResource("biggrass.png"));
			button = new Color(0x222222);
		} catch (Exception e) {
			throw new RuntimeException("Can not init resources !", e);
		}
	}

	public static URL getResourceURL(String string) {
		return Skin.class.getResource(string);
	}
	
}
