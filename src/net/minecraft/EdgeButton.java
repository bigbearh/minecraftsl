package net.minecraft;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;

/**
 *	The EdgeButton is an JLabel with Background , with support for 
 *	and to be used as buttons .
 *	
 *	You do not need to port ActionListeners to MouseListeners , it does for you .
 *	
 *	
 *	@author Maik
 *	@version 1.0 + alpha values
 */

public class EdgeButton extends JLabel {
	private static final long serialVersionUID = 1L;
	
	public MouseListener mouse1;
	public Color oldBackgroundMain;
	public Color oldBackground1;
	public Color oldBackground2;
	public boolean blackText = false;

	public boolean isOpaque() {
		return true;
	}
	
	/*public Border getBorder() {
		return new MatteBorder(4, 4, 4, 4, getBackground());
	}*/
	
	/**
	 * This method is an private method for the color changing while hovering .
	 */
	private void addHoverListener() {
		MouseAdapter mouseHoverListener = new MouseAdapter() {
			
			public void mouseEntered(MouseEvent arg0) {
				oldBackground1 = getBackground();
				int red = (getBackground().getRed()+15>255)?255:getBackground().getRed()+15;
				int green = (getBackground().getGreen()+15>255)?255:getBackground().getGreen()+15;
				int blue = (getBackground().getBlue()+15>255)?255:getBackground().getBlue()+15;
				int alpha = getBackground().getAlpha();
				setBackground(new Color(red, green, blue, alpha));
				validate();
				repaint();
			}
			
			public void mouseExited(MouseEvent arg0) {
				setBackground(oldBackgroundMain);
				repaint();
			}
			
			public void mousePressed(MouseEvent arg0) {
				oldBackground2 = getBackground();
				int red = (getBackground().getRed()-25<0)?0:getBackground().getRed()-25;
				int green = (getBackground().getGreen()-25<0)?0:getBackground().getGreen()-25;
				int blue = (getBackground().getBlue()-25<0)?0:getBackground().getBlue()-25;
				int alpha = getBackground().getAlpha();
				setBackground(new Color(red, green, blue, alpha));
				validate();
				repaint();
			}

			public void mouseReleased(MouseEvent arg0) {
				setBackground(oldBackgroundMain);
				validate();
				repaint();
			}
			
		};
		addMouseListener(mouseHoverListener);
		addMouseMotionListener(mouseHoverListener);
	}
	
	/**
	 * Create an EdgeButton .
	 * 
	 * @param str Title / Text 
	 * @param bgC Background Color 
	 */
	public EdgeButton(String str, Color bgC) {
		super(str);
		int red = (bgC.getRed()-15<0)?0:bgC.getRed()-15;
		int green = (bgC.getGreen()-15<0)?0:bgC.getGreen()-15;
		int blue = (bgC.getBlue()-15<0)?0:bgC.getBlue()-15;
		int alpha = bgC.getAlpha();
		setBorder(new MatteBorder(4, 4, 4, 4, new Color(red, green, blue, alpha)));
		setBackground(new Color(red, green, blue, alpha));
		oldBackgroundMain = new Color(red, green, blue, alpha);
		setForeground(Color.white);
		addHoverListener();
	}
	
	/**
	 * Create an EdgeButton .
	 * 
	 * @param str Title / Text 
	 * @param bgC Background Color 
	 * @param fgC Foreground Color ( overridden with color chooser listener ) 
	 */
	public EdgeButton(String str, Color bgC, Color fgC) {
		super(str);
		int red = (bgC.getRed()-15<0)?0:bgC.getRed()-15;
		int green = (bgC.getGreen()-15<0)?0:bgC.getGreen()-15;
		int blue = (bgC.getBlue()-15<0)?0:bgC.getBlue()-15;
		int alpha = bgC.getAlpha();
		setBorder(new MatteBorder(4, 4, 4, 4, new Color(red, green, blue, alpha)));
		setBackground(new Color(red, green, blue, alpha));
		oldBackgroundMain = new Color(red, green, blue, alpha);
		setForeground(fgC);
		addHoverListener();
	}
	
	/**
	 * Create an EdgeButton .
	 * 
	 * @param str Title / Text 
	 * @param ico Icon
	 * @param bgC Background Color 
	 */
	public EdgeButton(String str, Icon ico, Color bgC) {
		super(str);
		int red = (bgC.getRed()-15<0)?0:bgC.getRed()-15;
		int green = (bgC.getGreen()-15<0)?0:bgC.getGreen()-15;
		int blue = (bgC.getBlue()-15<0)?0:bgC.getBlue()-15;
		int alpha = bgC.getAlpha();
		setBorder(new MatteBorder(4, 4, 4, 4, new Color(red, green, blue, alpha)));
		setIcon(ico);
		setBackground(new Color(red, green, blue, alpha));
		oldBackgroundMain = new Color(red, green, blue, alpha);
		setForeground(Color.white);
		addHoverListener();
	}
	
	/**
	 * Create an EdgeButton .
	 * 
	 * @param str Title / Text 
	 * @param ico Icon
	 * @param bgC Background Color 
	 * @param fgC Foreground Color ( overridden with color chooser listener ) 
	 */
	public EdgeButton(String str, Icon ico, Color bgC, Color fgC) {
		super(str);
		int red = (bgC.getRed()-15<0)?0:bgC.getRed()-15;
		int green = (bgC.getGreen()-15<0)?0:bgC.getGreen()-15;
		int blue = (bgC.getBlue()-15<0)?0:bgC.getBlue()-15;
		int alpha = bgC.getAlpha();
		setBorder(new MatteBorder(4, 4, 4, 4, new Color(red, green, blue, alpha)));
		setIcon(ico);
		setBackground(new Color(red, green, blue, alpha));
		oldBackgroundMain = new Color(red, green, blue, alpha);
		setForeground(fgC);
		addHoverListener();
	}
	
	/**
	 * 
	 * Adds an fake action listener . 
	 * It ports action listeners to mouse listeners , adds them later on .
	 * 
	 * @param newAction ActionListener to add
	 */
	public void addActionListener(final ActionListener newAction) {
		mouse1 = new MouseListener() {
			
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					ActionEvent mouseToAction = new ActionEvent("MouseEvent Ported", arg0.getID(), "MouseEventPort "+arg0);
					newAction.actionPerformed(mouseToAction);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		};
		addMouseListener(mouse1);
	}
	
	/**
	 * Removes last fake ActionListener .
	 */
	
	public void removeLastActionListener() {
		removeMouseListener(mouse1);
	}
	
	/**
	 * Frees EdgeButton from all fake ActionListeners .
	 */
	public void removeAllActionListeners() {
		MouseListener[] mouseListeners = getMouseListeners(); 
		int removedListeners = 0;
		while (removedListeners != mouseListeners.length) {
			removeMouseListener(mouseListeners[removedListeners]);
			removedListeners = removedListeners + 1;
		}
	}
	
	/**
	 * Removes ActionListener NR. X
	 * @param listenerAt ActionListener NR. X
	 */
	public void removeActionListener(int listenerAt) {
		MouseListener[] mouseListeners = getMouseListeners(); 
		removeMouseListener(mouseListeners[listenerAt]);
	}

}
