package net.minecraft;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JEditorPane;

import net.minecraft.res.Skin;

/**
 *	JEditorPane with support for black wool background for texts .
 * 	@author Maik
 * 	@author Notch
 *	
 */
public class TexturedEditorPane extends JEditorPane {
	public Image bgImage = Skin.wool_black.getScaledInstance(64, 64, 16);
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
    public void paintComponent(Graphics g) {
		if (bgImage != null) {
			/*Rectangle localRectangle = g.getClipBounds();
			for (int x = localRectangle.x / 48; x <= (localRectangle.x + localRectangle.width) / 48; x++) {
				for (int y = localRectangle.y / 48; y <= (localRectangle.y + localRectangle.height) / 48; y++) {
					g.drawImage(Skin.wool_black, x * 48, y * 48, null);
				}
			}*/
			
			int w = getWidth();
			int h = getHeight();
	    	int ww = bgImage.getWidth(null);
	    	int hh = bgImage.getHeight(null);
	    	g.setColor(new Color(255, 255, 255, 31));
	    	for (int x = 0; x <= w / ww; x++) {
	    		for (int y = 0; y <= h / hh; y++) {
	    			g.drawImage(bgImage, x * ww, y * hh, null);
	    			g.fillRect(x*ww, y*hh, ww, hh);
	    		}
	    	}
			
		}
		else {
			//paramGraphics.setColor(new Color(2039583));
			g.setColor(new Color(0x222222));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		super.paintComponent(g);
    }
	
}
