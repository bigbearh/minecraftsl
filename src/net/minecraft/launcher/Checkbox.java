package net.minecraft.launcher;

public class Checkbox {
	private final int id;

	private String label;
	public boolean checked = false;

	public static final int WIDTH = 140;
	public static final int HEIGHT = 32;
	
	private int w = WIDTH;
	private int h = HEIGHT;

	public Checkbox(int id, String label) {
		this(id, label, false);
	}

    public int getId()
    {
        return id;
    }
    
    public void setLabel(String label){
    	this.label = label;
    }
	public Checkbox(int id, String label, boolean checked) {
		this.id = id;
		this.label = label;
		this.checked = checked;
	}
	
	public Bitmap getBitmap(int state) { 
		Bitmap b = new Bitmap(w, h);
		if (state == 0 || state == 1) {
			if (state == 1)
				b.blit(Resources.Bitmaps.checkbox[1][1], 0, 0);
			else
				b.blit(Resources.Bitmaps.checkbox[0][1], 0, 0);
		} else {
			if (state == 2)
				b.blit(Resources.Bitmaps.checkbox[1][0], 0, 0);
			else
				b.blit(Resources.Bitmaps.checkbox[0][0], 0, 0);
		}
		
		Bitmap text = TextFactory.toBitmap(label, 0xffffffff, Resources.Fonts.minecraft.deriveFont(8f), true);
		
		text = Bitmap.scaleBitmapFactor(text, 2);
		
		b.blit(text, 36, h / 16 - 4);
		
		return b;
	}

	public Bitmap[] getBitmaps() {
		Bitmap[] bmps = new Bitmap[4];
		bmps[0] = getBitmap(0);
		bmps[1] = getBitmap(1);
		bmps[2] = getBitmap(2);
		bmps[3] = getBitmap(3);
		return bmps;
	}
}
