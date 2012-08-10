package net.minecraft;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import net.minecraft.res.Skin;

/**
 *	Class from the good old MinecraftSL days - Textured checkboxes . <p>
 *	
 * Ported the class style for readibility .
 *	
 *	@author Maik
 *	
 */
public class TexturedCheckbox extends JCheckBox {
	protected Icon untickedi = new ImageIcon(Skin.getResourceURL("woolb.png"));
	protected Icon tickedi = new ImageIcon(Skin.getResourceURL("woolb2.png"));
	protected Icon disabledi = new ImageIcon(Skin.getResourceURL("woolb3.png"));
	protected Icon hoveri = new ImageIcon(Skin.getResourceURL("woolb4.png"));
	protected Icon disabledtickedi = new ImageIcon(Skin.getResourceURL("woolb5.png"));
	protected boolean ticked = false;
	protected boolean opaque = false;
	
	public TexturedCheckbox(String string) {
		super(string);
		setForeground(Color.WHITE);
		setIcon(untickedi);
		setDisabledIcon(disabledi);
		setRolloverIcon(hoveri);
		setSelectedIcon(tickedi);
		setDisabledSelectedIcon(disabledtickedi);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (ticked) {
					ticked = false;
				} else if (!ticked) {
					ticked = true;
				}
			}
		});
	}
	
	public boolean isTicked() {
		return ticked;
	}
	
	@Override
	public boolean isOpaque() {
		return opaque;
	}
	
	@Override
	public void setOpaque(boolean myopaque) {
		opaque = myopaque;
	}
}