package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *	JPanel holding the Launcher / Game . Used for possible porting to JLayeredPane or similar because of in-game MinecraftSL menus .
 *	@author Maik
 *	
 */
public class LauncherPanel extends JPanel {
    
	public LauncherPanel() {
		super();
		setLayout(new BorderLayout());
	}
	
}
