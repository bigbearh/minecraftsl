package net.minecraft;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import net.minecraft.res.Skin;

/** 
 * Textured JPanel with gradient paintings left-right . 
 */
public class TexturedGradientPanel extends TexturedPanel { 
	private static final long serialVersionUID = 1L;

	protected BufferedImage img;
	public Image bgImage;
	public int raww = 32;
	public int rawh = 32;
	
	public TexturedGradientPanel() {
		setOpaque(true);
	    setBorder(null);
	    this.bgImage = Skin.wool_black.getScaledInstance(32, 32, BufferedImage.SCALE_DEFAULT);
	}
	
	public TexturedGradientPanel(LayoutManager layout) {
		setLayout(layout);
	    setOpaque(true);
	    setBorder(null);
	    this.bgImage = Skin.wool_black.getScaledInstance(32, 32, BufferedImage.SCALE_DEFAULT);
	}
	
	public TexturedGradientPanel(Image b) {
		setOpaque(true);
		setBorder(null);
		this.bgImage = b;
	}
	
	public TexturedGradientPanel(LayoutManager layout, Image b) {
		setLayout(layout);
		setOpaque(true);
		setBorder(null);
		this.bgImage = b;
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void paintComponent(Graphics g2) {
	    int w = getWidth() + 1;
	    int h = getHeight() + 1;
	    if ((this.img == null) || (this.img.getWidth(null) != w) || (this.img.getHeight(null) != h)) {
	    	this.img = (BufferedImage) createImage(w, h);
	    	
	    	Graphics g = this.img.getGraphics();
	    	int ww = raww;
	    	int hh = rawh;
	    	for (int x = 0; x <= w / ww; x++) {
	    		for (int y = 0; y <= h / hh; y++) {
	    			g.drawImage(this.bgImage, x * ww / 2, y * hh / 2, null);
	    		}
	    	}
	    	if (g instanceof Graphics2D) {
	    		Graphics2D gg = (Graphics2D)g;
	    		int gw = 1;
	    		gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(553648127, true), new Point2D.Float(gw, 0.0F), new Color(0, true)));
	    		gg.fillRect(0, 0, gw, h);
	    		
	    		gw = w;
	    		gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(0, true), new Point2D.Float(gw, 0.0F), new Color(1610612736, true)));
	    		gg.fillRect(0, 0, gw, h);
	    	}
	    	g.dispose();
	   	}
	   	g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
	}
}
