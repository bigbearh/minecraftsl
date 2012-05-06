package net.minecraft.launcher;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JTextField;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.LayoutManager;
import java.awt.Paint;

import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import javax.swing.JEditorPane;
import java.awt.GridLayout;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.awt.Color;
import com.jgoodies.forms.factories.FormFactory;
import com.sun.awt.AWTUtilities;
import com.sun.awt.AWTUtilities.Translucency;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/*
 * TODO @todo ( TODOs ) : 
 * - Make it force updating 
 * - Make it check for updates 
 */

// TODO @todo NOTE FOR THE PAGES WRITTEN AS TEXTS ( NOT AS HTMLs ) : 
/*
 * - Add "plain*" on beginning , where * is an linebreak ( in java \n ) 
 * - Do line wrapping on your own 
 * - Safe space in width , height may be buggy so do not use too long texts 
 */

public class MinecraftSL extends JFrame {

	private static final String build = "1 beta 1"; // Only AngelDE98 should update that one when releasing new builds !!!
	
	private static Color clear = new Color(255, 255, 255, 0);
	private static Color alpha_pink = new Color(255, 0, 255, 0);
	private static Color alpha_green = new Color(0, 255, 0, 0);
	private static Color pink = new Color(255, 0, 255, 255);
	private static Color green = new Color(0, 255, 0, 255);
	
	public static MinecraftSL instance;
	private JPasswordField passField;
	private JTextField userField;
	
	private static String username;
	private static String password;
	
	//private TexturedPanel mainpanel = new TexturedPanel();
	private JPanel mainpanel = new JPanel();
	private JEditorPane newsPane = new TexturedEditorPane();
	private JScrollPane newsScrollPane = new JScrollPane();
	private JLabel errorLabel = new JLabel("");
	private FakeCheckbox update = new FakeCheckbox("Update");
	
	private final JLabel playerLabel = new JLabel("");
	
	private int tick = 0;
	private int tick2 = 0;
	private int playerid = 0;
	
	private int updatetick = 0;
	
	private Thread tickThread;
	private boolean dotick = true;
	
	private Cursor cursor;
	
	private static Random rand;
	
	private boolean loggedIn = false;
	
	public static boolean launch = false;
	
	public JPanel borderpanel;
	
	private MinecraftSL() {
		setTitle("MinecraftSL b"+build);
		
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		rand = new Random();
		rand = new Random(rand.nextLong());
		rand = new Random(rand.nextLong());
		
		setSize(854, 480);
		setResizable(false);
		borderpanel = addCustomBorder();
		
		setIconImage(Resources.Bitmaps.icon32);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setContentPane(borderpanel);
		mainpanel.setLayout(new BorderLayout(0, 0));
		mainpanel.setBackground(clear);
		
		TexturedPanel subMainPane = new TexturedPanel();
		subMainPane.setBackground(clear);
		subMainPane.setBorder(new MatteBorder(1, 2, 3, 4, clear));
		mainpanel.add(subMainPane, BorderLayout.EAST);
		FormLayout fl_subMainPane = new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("126px:grow"),},
			new RowSpec[] {
				RowSpec.decode("120px"),
				RowSpec.decode("49px"),
				//RowSpec.decode("56px"),
				RowSpec.decode("32px"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(32px;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("max(32px;default)"),});
		subMainPane.setLayout(fl_subMainPane);
		
		FakeButton loginButton = new FakeButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = userField.getText();
				password = new String(passField.getPassword());
				handleLogin(username, password);
			}
		});
		playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateIcon(playerid);
		playerLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		subMainPane.add(playerLabel, "1, 1");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setForeground(Color.RED);
		
		subMainPane.add(errorLabel, "1, 3, fill, default");
		
		userField = new JTextField();
		subMainPane.add(userField, "1, 4, fill, default");
		userField.setColumns(15);
		
		passField = new JPasswordField();
		subMainPane.add(passField, "1, 5, fill, default");
		passField.setColumns(15);
		
		userField.setBackground(Color.lightGray);
		passField.setBackground(Color.lightGray);
		
		update.setBorder(getDownBorder());
		
		subMainPane.add(update, "1, 7");
		
		loginButton.setBorder(getDownBorder());
		
		subMainPane.add(loginButton, "1, 8, fill, fill");
		
		newsPane.setBackground(Color.WHITE);
		//newsPane.setContentType("text/html");
		newsPane.setContentType("text/plain");
		newsPane.setEditable(false);
		//newsPane.setText(htmlify("ERROR : News not initalized"));
		newsPane.setText("ERROR : News not initalized");
		
		newsPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent he) {
				if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						openLink(he.getURL().toURI());
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
		});
		
		Thread newsThread = new Thread("launcher.newsthread") {
			public void run() {
				
				//newsPane.setText(htmlify("Loading news ..."));
				newsPane.setText("Loading news ...");
		    	try {
		    		/*newsPane.setPage("http://minecraftsl.tumblr.com");
		    		String s = newsPane.getText();
		    		newsPane = new JEditorPane();
		    		newsPane.setContentType("text/html");
		    		newsPane.setText(s);
		    		System.out.println(s);
		    		newsScrollPane.setViewportView(newsPane);*/
		    		
	    			newsPane = new JEditorPane();
	    			newsPane.setEditable(false);
	  				newsPane.setContentType("text/html");
	  				newsPane.setPage("http://minecraftsl.tumblr.com");
	  				newsScrollPane.setViewportView(newsPane);
		  		} catch (Throwable e) {
		   			e.printStackTrace();
		   			//newsPane.setText(htmlify("Failed to update news : "+e));
		   			newsPane = new TexturedEditorPane();
		   			newsPane.setText("Failed to update news : "+e);
		   			newsScrollPane.setViewportView(newsPane);
		  		}
			}
		};
		newsThread.setDaemon(true);
		newsThread.start();
		
		newsScrollPane.setBorder(new MatteBorder(0, 0, 0, 4, clear));
		newsScrollPane.setBackground(clear);
		newsScrollPane.setViewportView(newsPane);
		mainpanel.add(newsScrollPane, BorderLayout.CENTER);
		
		mainpanel.setBorder(new MatteBorder(0, 4, 0, 4, clear));
	}

	public Border getDownBorder() {
		return new MatteBorder(0, 0, 8, 0, clear);
	}

	private static String getMultilineTest(int lc) {
		String lines = "1:90899108908590821905890490912089059089019885781400109458182903458009125";
		for (int i = 2; i < lc+1; i++) {
			lines += "\n"+i;
		}
		return lines;
	}

	private JPanel addCustomBorder() {
		// TODO : Add Anti-Aliasing , fixed thru other idea 
	    if (checkTranslucencyMode(Translucency.PERPIXEL_TRANSPARENT) &&
	    		checkTranslucencyMode(Translucency.PERPIXEL_TRANSLUCENT)) {
	    } else {
	    	clear = Color.black;
	    	return mainpanel;
	    }
		setUndecorated(true);
        AWTUtilities.setWindowOpaque(this, false);
	    setBackground(alpha_pink);
        int bordersize = 8;
	    int headersize = 44;
	    Dimension size = getSize();
	    Dimension newsize = new Dimension(size.width, size.height);
	    setSize(newsize);
	    Bitmap borderBG = Resources.Bitmaps.launcherBackground;
	    //borderBG = Bitmap.scaleBitmap(borderBG, 256, 256);
	    //borderBG.twist180();
	    //borderBG.replace(0xff000000, 0x00000000);
	    BorderPanel borderpanel = new BorderPanel(Resources.Bitmaps.launcherBackground);
	    borderpanel.setBackground(clear);
	    borderpanel.setOpaque(false);
	    borderpanel.setLayout(new BorderLayout());
	    borderpanel.setBackground(clear);
	    borderpanel.add(mainpanel, "Center");
	    
	    final FakeButton minimizeButton = new FakeButton("_", 32);
	    minimizeButton.setPreferredSize(new Dimension(32, 24));
	    minimizeButton.setHorizontalAlignment(0);
	    minimizeButton.setToolTipText("Minimize");
	    final FakeButton exitButton = new FakeButton("X", 32);
	    exitButton.setPreferredSize(new Dimension(48, 24));
	    exitButton.setHorizontalAlignment(0);
	    exitButton.setToolTipText("Exit");
	    
	    final JPanel northPanel = new JPanel(new BorderLayout());
	    northPanel.setBackground(clear);
	    final JLabel titleL = new JLabel("");
		Bitmap fontbitmap = TextFactory.toBitmap(getTitle(), 0xffffffff, Resources.Fonts.minecraft.deriveFont(8f), true);
		fontbitmap = Bitmap.scaleBitmapFactor(fontbitmap, 2);
		ImageIcon titleimg = new ImageIcon(Bitmap.convert(fontbitmap));
		titleL.setIcon(titleimg);
	    final JLabel faviconL = new JLabel("");
	    try {
	    	faviconL.setIcon(new ImageIcon(getIconImage()));
	    } catch (Exception e1) { 
	    	try {
	    		faviconL.setIcon(new ImageIcon(Resources.Bitmaps.icon32));
	    	} catch (Exception e2) {
	    		faviconL.setText("    ");
	    	}
	    };
	    faviconL.setBorder(new MatteBorder(0, 2, 8, 0, clear));
	    JPanel leftPanel = new JPanel();
	    leftPanel.setBackground(clear);
	    leftPanel.add(faviconL, "West");
	    leftPanel.add(titleL, "East");
	    northPanel.add(leftPanel, "West");
	    JPanel rightPanel = new JPanel();
	    rightPanel.setBackground(clear);
	    rightPanel.add(minimizeButton);
	    rightPanel.add(exitButton);
	    northPanel.add(rightPanel, "East");
	    //northPanel.setBackground(Color.black);
	    borderpanel.add(northPanel, "North");
	    northPanel.setPreferredSize(new Dimension(newsize.width, headersize));
	    borderpanel.setPreferredSize(getSize());
	    borderpanel.add(mainpanel, "Center");
	    JPanel southborder = new JPanel();
	    southborder.setPreferredSize(new Dimension(bordersize, bordersize));
	    southborder.setBackground(clear);
	    borderpanel.add(southborder, "South");
	    JPanel westborder = new JPanel();
	    westborder.setPreferredSize(new Dimension(bordersize, bordersize));
	    westborder.setBackground(clear);
	    borderpanel.add(westborder, "West");
	    int bordersize2 = (bordersize/4)*3;
	    JPanel northborder = new JPanel();
	    northborder.setPreferredSize(new Dimension(bordersize2, bordersize2));
	    northborder.setBackground(clear);
	    northPanel.add(northborder, "North");
	    JPanel eastborder = new JPanel();
	    eastborder.setPreferredSize(new Dimension(bordersize, bordersize));
	    eastborder.setBackground(clear);
	    borderpanel.add(eastborder, "East");
	    
	    final Action exitAction = new AbstractAction("Exit") {

	        {
	          putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
	        }

	        public void actionPerformed(ActionEvent e) {
	        	dispose();
	        }
	      };
	      
	      final JPopupMenu popupMain = new JPopupMenu();
	      popupMain.add(new JMenuItem(exitAction));
	      
	      MouseAdapter mouseListener = new MouseAdapter() {

	        int x, y;

	        public void mousePressed(MouseEvent e) {
	            if (e.getButton() == MouseEvent.BUTTON1) {
	                x = e.getX();
	                y = e.getY();
	            }
	        }

	        public void mouseDragged(MouseEvent e) {
	            if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
	                setLocation(e.getXOnScreen() - x, e.getYOnScreen() - y);
	            }
	        }

	        public void mouseReleased(MouseEvent e) {
	        }
	      };
	      
	      northPanel.addMouseListener(mouseListener);
	      northPanel.addMouseMotionListener(mouseListener);
	      
	    	exitButton.addActionListener(new ActionListener()
	        {
	          public void actionPerformed(ActionEvent e) {
	            exitAction.actionPerformed(e);
	          }
	      	});
	      	
	      	minimizeButton.addActionListener(new ActionListener()
	        {
	          public void actionPerformed(ActionEvent e) {
	            setState(Frame.ICONIFIED);
	          }
	      	});
	    
		return borderpanel;
	}
	
	public static boolean checkTranslucencyMode(Translucency arg) {
		if (!AWTUtilities.isTranslucencySupported(arg)) {
			System.err.println("'" + arg
					+ "' translucency mode isn't supported.");
			return false;
		}
		return true;
	}

	public static void main(String[] args) { 
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	      }
	      catch (Exception localException) {
	      }
		instance = new MinecraftSL();
		instance.setVisible(true);
		instance.tickThread();
	}
	
	private static void handleLogin(String user, String pass) { 
		EnumError error = validateAcc(user, pass);
	}
	
	private static void handleLogin(String user, String pass, EnumError error) { 
		String[] args = {};
		if (error != EnumError.valid) {
			error.handleError();
		} else {
			instance.loggedIn = true;
			instance.dispose();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (updateGame()) {
				GameUpdater.run();
			} else {
				launch = true;
			}
		}
	}
	
	private static boolean updateGame() {
		boolean b = false;
		if (instance.update.isSelected()) b = true;
		// TODO : Implement checking for latest version on servers ... 
		return b;
	}

	private static EnumError validateAcc(String user, String pass) {
		EnumError error = EnumError.loading;
		// TODO : Implement checking on servers ... 
		// TODO : Implement playing offline if avaible 
		error = EnumError.invalid;
		handleLogin(user, pass, error);
		return error;
	}

	public static enum EnumError { 
		valid, failed, notregi, invalid, loading;
		
		private String name = getFullName();
		
		public static String getFullName(EnumError error) {
			String name = "Invalid enum";
			if (error == valid) name = "OK";
			if (error == failed) name = "Failed Check";
			if (error == notregi) name = "Account not found";
			if (error == invalid) name = "Wrong Password";
			if (error == loading) name = "Loading ...";
			// No " user not premium " , guys !!! 
			return name; 
		}
		
		public String getFullName() {
			String name = "";
			name = getFullName(this);
			return name; 
		}
		
		public void handleError() { 
			String error = getFullName();
			Bitmap fontbitmap = TextFactory.toGradientBitmap(error, 0xffdd0000, 0xff550000, true);
			ImageIcon errorimg = new ImageIcon(Bitmap.convert(fontbitmap));
			instance.errorLabel.setIcon(errorimg);
		}
	};
	
	/**
	 * Opens an link in new browser window , 
	 * uses standard browser 
	 * @param uri URI to open ( link ) 
	 */
	public static void openLink(URI uri) {
		try {
			Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { uri });
		} catch (Throwable e) {
			System.out.println("Failed to open link " + uri.toString());
		}
	}
	
	private void tickThread() { 
		if (tickThread != null) tickThread.interrupt();
		tickThread = new Thread("Launcher TickThread") { 
			public void run() { 
				while (dotick) { 
					tick();
					try { 
						sleep(5);
					} catch (Exception e) {
					}
				}
			}
		};
		tickThread.start();
	}
	
	private void tick() { 
		if (isVisible() && !launch) { 
			tick++;
			if (tick == 60) { 
				tick = 0;
				playerid++;
				if (playerid == 4) { 
					playerid = 0;
				}
			}
			tick2++;
			if (tick2 == 8 * 60 * 4) { 
				tick2 = 0;
			}
			updateIcon(playerid);
			
			updatetick++;
			if (updatetick == 100000) { 
				updatetick = 0;
			}
			if (updatetick == 0) {
				if (borderpanel != null) {
					borderpanel.repaint();
				}
			}
		} else { 
			dotick = false;
			tickThread.interrupt();
		}
	}
	
	private void updateIcon(int playerid) { 
		int frame = tick / 10;
		int dir = tick2 / (60 * 4);
		/*
		Bitmap rawPlayer = Resources.Bitmaps.getPlayer(playerid)[frame][dir];
		Image player = Bitmap.convert(rawPlayer);
		playerLabel.setIcon(new ImageIcon(player.getScaledInstance(rawPlayer.w * 2, rawPlayer.h * 2, 16)));
		*/
		// TODO : Rendering of 3D Players ...
	}
	
	
	/** 
	 * Execute HTML post . 
	 * Get HTML page source as string . 
	 */
	  public static String executePost(String targetURL, String urlParameters)
	  {
	    HttpURLConnection connection = null;
	    String str0 = "Error - Unknown";
	    try
	    {
	      URL url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	      connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");

	      connection.setUseCaches(false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      try {
	    	  connection.connect();
	      } catch (Exception e) {
	    	  return "Error - "+e.toString().replace(":", ":\n");
	      }

	      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	      wr.writeBytes(urlParameters);
	      wr.flush();
	      wr.close();

	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	      StringBuffer response = new StringBuffer();
	      String line;
	      while ((line = rd.readLine()) != null)
	      {
	        //String line;
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();

	      String str1 = response.toString();
	      //String str1 = str1;
	      String str2 = str1;
	      if (str1.trim().equalsIgnoreCase("")) {
	      	str2 = "Error - Clear Page";
	      }
	      str0 = str2;
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      //return null;
	      str0 = "Error - "+e.toString().replace(":", ":\n");
	    }
	    finally
	    {
	      if (connection != null)
	        connection.disconnect(); 
	    }
	    if (str0.equals(null) || str0 == null) str0 = "Error - No internet connection ?";
	    return str0;
	    //throw localObject;
	  }
	  
	  private Bitmap pixelBitmap(int c) { 
		  Bitmap b = new Bitmap(1, 1);
		  b.pixels[0] = c;
		  return b;
	  }
	
	/** 
	 * Textured JPanels 
	 *	modified for external bitmaps ( Resources.Bitmaps.xxx ) 
	 */
	public class TexturedPanel extends JPanel { 
		  private BufferedImage img;
		  private Image bgImage = Bitmap.convert(Resources.Bitmaps.wool_black).getScaledInstance(32, 32, BufferedImage.SCALE_DEFAULT);
		  private int raww = 32;
		  private int rawh = 32;

		  public TexturedPanel()
		  {
		    setOpaque(true);
		    setBorder(null);
		  }

		public TexturedPanel(LayoutManager layout)
		  {
			setLayout(layout);
		    setOpaque(true);
		    setBorder(null);
		  }
		  
		  public TexturedPanel(Bitmap b)
		  {
		    setOpaque(true);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(b);
		  }
		  
		  public TexturedPanel(LayoutManager layout, Bitmap b)
		  {
			setLayout(layout);
		    setOpaque(true);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(b);
		  }

		  public void update(Graphics g) {
		    paint(g);
		  }

		  public void paintComponent(Graphics g2) {
		    int w = getWidth() + 1;
		    int h = getHeight() + 1;
		    if ((this.img == null) || (this.img.getWidth(null) != w) || (this.img.getHeight(null) != h)) {
		      this.img = (BufferedImage) createImage(w, h);
		      
		      // TODO : Test alpha channels
		      // TODO : Add shadow support ... 
		      Bitmap pb = Bitmap.convert(img);
		      pb.replace(pink.getRGB(), clear.getRGB());
		      img = Bitmap.convert(pb);
		      
		      Graphics g = this.img.getGraphics();
		      int ww = raww;
		      int hh = rawh;
		      for (int x = 0; x <= w / ww; x++) {
		        for (int y = 0; y <= h / hh; y++) {
		          g.drawImage(this.bgImage, x * ww / 2, y * hh / 2, null);
		        }
		      }
		      if (g instanceof Graphics2D) {
		        Graphics2D gg = (Graphics2D)g;
		        int gh = 1;
		        gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(553648127, true), new Point2D.Float(0.0F, gh), new Color(0, true)));
		        gg.fillRect(0, 0, w, gh);

		        gh = h;
		        gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(0, true), new Point2D.Float(0.0F, gh), new Color(1610612736, true)));
		        gg.fillRect(0, 0, w, gh);
		      }
		      g.dispose();
		    }
		    g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
		  }
	}
	
	/** 
	 * Textured JEditorPanes 
	 *	Modified TexturedPanel 
	 */
	public class TexturedEditorPane extends JEditorPane { 
		  private Image img;
		  private Image bgImage;
		  public boolean drawbg = true;
		  private String text = "";
		  private boolean forcerepaint = false;

		  public TexturedEditorPane()
		  {
		    setOpaque(false);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(Resources.Bitmaps.wool_black).getScaledInstance(32, 32, 16);
		  }
		  
		  public TexturedEditorPane(Bitmap b)
		  {
		    setOpaque(false);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(b).getScaledInstance(32, 32, 16);
		  }

		  @Override
		  public void update(Graphics g) {
		    if (drawbg) paint(g);
		    else  super.paint(g);
		  }

		  @Override
		  public void paintComponent(Graphics g2) {
			if (drawbg) {
				int w = getWidth() / 2 + 1;
		    	int h = getHeight() / 2 + 1;
		    	if ((this.img == null) || (this.img.getWidth(null) != w) || (this.img.getHeight(null) != h) || (forcerepaint)) {
		    		forcerepaint = false;
		    	this.img = createImage(w, h);
		      	
		      	Graphics g = this.img.getGraphics();
		      	for (int x = 0; x <= w / 32; x++) {
		    	  for (int y = 0; y <= h / 32; y++)
		        	g.drawImage(this.bgImage, x * 32, y * 32, 32, 32, null);
		      	}
		      	if (g instanceof Graphics2D) {
		      		Graphics2D gg = (Graphics2D)g;
		      		gg.setColor(new Color(0, 0, 0, 127));
		        	gg.fillRect(0, 0, w, h);
		      	}
		      	
		      	String text = this.text;
		      	
		      	Bitmap cb = TextFactory.toBitmap("O", 0xffffffff, Resources.Fonts.minecraft.deriveFont(8f), true);
				int linecount = (Integer) linify(text)[0];
				String[] lines = (String[]) linify(text)[1];
				Bitmap fontbitmap = new Bitmap((cb.w * text.length())+4, 
						(cb.h+16)*linecount);
				// TODO : Rewrite the buggy multiline code ... 
				fontbitmap.clear(0x00000000);
				int i = 0;
				for (String line : lines) {
					fontbitmap.blit(TextFactory.toBitmap(line, 0xffffffff, Resources.Fonts.minecraft.deriveFont(8f), true), 4, 8 + (cb.h+16)*i/2);
					i++;
				}
				fontbitmap.replace(0xff000000, 0x00000000);
				BufferedImage fontimg = Bitmap.convert(fontbitmap);
				Bitmap shadow = new Bitmap(fontbitmap.w + 1, fontbitmap.h + 1);
				shadow.colorBlit(fontbitmap, 1, 1, 0xff000000);
				BufferedImage shadowimg = Bitmap.convert(shadow);
		    	g.drawImage(shadowimg, 0, 0, shadow.w, shadow.h, null);
		    	g.drawImage(fontimg, 0, 0, fontbitmap.w, fontbitmap.h, null);
		    	String fakelines = "";
		    	for (int line = 0; line < linecount; line++) {
		    		i = line;
		    		while (i > 3) {
		    			i-=3;
		    		}
		    		if (i == 1) fakelines += "\n";
		    		else fakelines += "\n\n";
		    	}
		    	if (!super.getText().equals(fakelines)) super.setText(fakelines);
		    	setSize(shadow.w + 8, getHeight());
		    	w = getWidth() / 2 + 1;
		    	h = getHeight() / 2 + 1;
		      	g.dispose();
		    	}
				
		    	g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
			} else {
				int w = getWidth() / 2 + 1;
		    	int h = getHeight() / 2 + 1;
		    	Color c = g2.getColor();
		    	g2.setColor(Color.white);
		    	g2.fillRect(0, 0, w, h);
		    	g2.setColor(c);
				  super.paintComponent(g2);
			}
		  }
		  
			public void forceSetText(String s) {
				super.setText(s);
			}

			@Override
			public void setText(String text) {
				//super.setText(text);
				this.img = null;
				if (getContentType().equals("text/plain")) {
					this.text = text;
					Graphics g = getGraphics();
					if (g == null) {
						return;
					}
					paintComponent(g);
				} else {
					//this.text = text;
					Graphics g = getGraphics();
					if (g == null) {
						return;
					}
					paintComponent(g);
					super.setText(text);
				}
			}
		  
	}
	
	/** 
	 * Textured JPanels for Window Borders
	 * Modified TexturedPanels
	 */
	public class BorderPanel extends JPanel { 
		  private BufferedImage img;
		  private Image bgImage = Bitmap.convert(Resources.Bitmaps.wool_black);
		  private int raww = 32;
		  private int rawh = 32;

		  public BorderPanel()
		  {
		    setOpaque(true);
		    setBorder(null);
		  }

		public BorderPanel(LayoutManager layout)
		  {
			setLayout(layout);
		    setOpaque(true);
		    setBorder(null);
		  }
		  
		  public BorderPanel(Bitmap b)
		  {
		    setOpaque(true);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(b);
		  }
		  
		  public BorderPanel(LayoutManager layout, Bitmap b)
		  {
			setLayout(layout);
		    setOpaque(true);
		    setBorder(null);
		    this.bgImage = Bitmap.convert(b);
		  }

		  public void update(Graphics g) {
		    paint(g);
		  }

		  public void paintComponent(Graphics g2) {
		    int w = getWidth() + 1;
		    int h = getHeight() + 1;
		    //if ((this.img == null) || (this.img.getWidth(null) != w) || (this.img.getHeight(null) != h)) {
		      //this.img = (BufferedImage) createImage(w, h);
		      this.img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		    	
		      // FIXME : Add alpha channel to create(d)Image(w, h) , hopefully JRE 8 fixes that :O 
		      // TODO : Add shadow support ... 
		      Bitmap pb = Bitmap.convert(img);
		      //pb.replace(pink.getRGB(), clear.getRGB());
		      img = Bitmap.convert(pb);
		      
		      Graphics g = this.img.getGraphics();
		      int ww = raww;
		      int hh = rawh;
		      g.drawImage(this.bgImage, 0, 0, w, h, null);
		      g.dispose();
		    //}
		    g2.drawImage(this.img, 0, 0, w, h, null);
		  }
	}

	/**
	 * Counts the lines of text in string so and splits it into lines ... 
	 * For the return : It's Object[] . You got to cast [0] to int and [1] to string .
	 * @param so String to operate with 
	 * @return How many lines ( normally \n ( \\n ) ) the string has [0] and the line array itself [1][x] 
	 */
	private Object[] linify(String so) {
		String s = so;
		String[] sa = s.split("\n");
		int count = 1 + sa.length;
		Object[] result = {count, sa};
		return result;
	}

	@Override
	public void dispose() {
		super.dispose();
		dotick = false;
	}
	
}
