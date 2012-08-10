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
import javax.swing.JPanel;

import net.minecraft.res.Skin;

/**
 *	Textured JPanel showing a logo .
 *	
 *	@author Maik
 *	
 */
public class LogoPanel extends TexturedPanel {
	
	public LogoPanel() {
		super();
	    setOpaque(false);
		
		bgImage = Skin.biggrass;
		setPreferredSize(new Dimension(bgImage.getWidth(null)+24, bgImage.getHeight(null)+24));
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void paintComponent(Graphics g2) {
		g2.drawImage(this.bgImage, 12, 12, null);
	}
	
}
