package net.minecraft.launcher;

import java.awt.Image;

public class Button {

    public static final int BUTTON_WIDTH = 128;
    public static final int BUTTON_HEIGHT = 32;
    
	private final int id;

	private String label;

    private Bitmap mainBitmap = null;
    private Bitmap rightBorderBitmap = null;
    private Bitmap middleBitmap = null;
    
    public int w = BUTTON_WIDTH;
    public int h = BUTTON_HEIGHT;

	public Button(int id, String label) {
		this.id = id;
		this.label = label;
	}

    public Button(int id, String label,int w, int h) {
        this.id = id;
        this.label = label;
        this.w = w;
        this.h = h;
    }
    
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	private Bitmap getBitmap(int bitmapId) {
	    
		Bitmap b = new Bitmap(w, h);
		
	    // Default width button
	    if (w == BUTTON_WIDTH) {
            b.blit(Resources.Bitmaps.button[0][bitmapId], 0, 0);
	    }
	    
	    // Custom width buttons
	    else {
    	    // Cut button textures
    	    if (mainBitmap != Resources.Bitmaps.button[0][bitmapId]) {
    	        mainBitmap = Resources.Bitmaps.button[0][bitmapId];
    	        rightBorderBitmap = new Bitmap(16, BUTTON_HEIGHT);
    	        rightBorderBitmap.blit(mainBitmap, - BUTTON_WIDTH + 16, 0);
                middleBitmap = new Bitmap(16, BUTTON_HEIGHT);
                middleBitmap.blit(mainBitmap, -32, 0);
    	    }
    	    
    	    // Draw button
            b.blit(mainBitmap, 0, 0, 16, h);
            for (int x = 16; x < w - 16; x+=16) {
                b.blit(middleBitmap, x, 0);
            }
            b.blit(rightBorderBitmap, w - 16, 0);
	    }
	    
	    Bitmap text = TextFactory.toBitmap(label, 0xffffffff, Resources.Fonts.minecraft.deriveFont(8f), true);
	    
	    text = Bitmap.scaleBitmapFactor(text, 2);
	    
	    b.blit(text, w / 2 - text.w / 2 + 8, h / 16);
	    
	    return b;
	}

	public int getId() {
		return id;
	}

	public Bitmap[] getBitmaps() {
		Bitmap[] bmps = new Bitmap[3];
		bmps[0] = getBitmap(0);
		bmps[1] = getBitmap(1);
		bmps[2] = getBitmap(2);
		return bmps;
	}
}
