package net.minecraft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.border.Border;

import net.minecraft.res.Skin;


/**
 *	EdgeButton showing a logo .
 *	
 *	@author Maik
 *	
 */
public class LogoButton extends EdgeButton {
	private static final long serialVersionUID = 1L;
	
	public LogoButton() {
		super("", new Color(63, 95, 127, 159));
		
		Image bgImage = Skin.biggrass;
		setPreferredSize(new Dimension(bgImage.getWidth(null)+24, bgImage.getHeight(null)+24));
		setIcon(new ImageIcon(bgImage));
		setIconTextGap(0);
		this.setHorizontalAlignment(CENTER);
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
