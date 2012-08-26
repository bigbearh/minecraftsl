package net.minecraft;

import java.awt.Dimension;
import java.awt.Graphics;

import net.minecraft.res.Skin;

/**
 *	Textured JPanel showing a logo .
 *	
 *	@author Maik
 *	
 */
public class LogoPanel extends TexturedPanel {
	private static final long serialVersionUID = 1L;

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
