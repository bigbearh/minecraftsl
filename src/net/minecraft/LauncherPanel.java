package net.minecraft;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 *	JPanel holding the Launcher / Game . Used for possible porting to JLayeredPane or similar because of in-game MinecraftSL menus .
 *	@author Maik
 *	
 */
public class LauncherPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public LauncherPanel() {
		super();
		setLayout(new BorderLayout());
	}
	
}
