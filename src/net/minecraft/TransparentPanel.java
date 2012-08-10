package net.minecraft;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel {
	
	public TransparentPanel() {
		super();
	}
	
	public TransparentPanel(LayoutManager layout) {
		super(layout);
	}
	
	public TransparentPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}
	
	public TransparentPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
}
