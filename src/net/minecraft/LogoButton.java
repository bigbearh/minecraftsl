package net.minecraft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.minecraft.res.Skin;

/**
 *	EdgeButton showing a logo .
 *	
 *	@author Maik
 *	
 */
public class LogoButton extends EdgeButton {
	
	public LogoButton() {
		super("", new Color(63, 95, 127, 159));
		
		Image bgImage = Skin.biggrass;
		setPreferredSize(new Dimension(bgImage.getWidth(null)+24, bgImage.getHeight(null)+24));
		setIcon(new ImageIcon(bgImage));
		setIconTextGap(0);
		this.setHorizontalAlignment(this.CENTER);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public Border getBorder() {
		return null;
	}
	
}
