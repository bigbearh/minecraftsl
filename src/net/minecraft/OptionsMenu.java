package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.minecraft.backupmanager.MinecraftBackupManager;
import net.minecraft.console.OutputConsole;
import net.minecraft.http.HTTPClient;
import net.minecraft.minecrack.MinecrackInstaller;
import net.minecraft.modules.ModuleBase;
import net.minecraft.modules.Modules;
import net.minecraft.modules.OptionModule;
import net.minecraft.res.Skin;

/**
 *	Simple menu to set up options .
 *	@author Maik
 *	
 */
public final class OptionsMenu extends TexturedPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 *	Subclass for JPanels holding components with one single host-defined border .
	 * 	@author Maik
	 *	
	 */
	public class JBorderPane extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public JBorderPane(Component c) {
			super();
			setLayout(new BorderLayout());
			add(c, "Center");
			setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		}
		
	}
	
	/**
	 * Open JProgressBar for various progresses , shared along all classes .
	 */
	public static JProgressBar progress = new JProgressBar(0);
	
	private JPanel mainpanel = new JPanel();

	private static OptionsMenu lastInstance;
	private static JPanel lastMainPanel;
	
	public OptionsMenu() {
		super();
		bgImage = Skin.wool_white;
		setLayout(new BorderLayout());
	    setBorder(new EmptyBorder(8, 12, 12, 12));
		mainpanel.setOpaque(false);
		mainpanel.setLayout(new BorderLayout(0, 8));
		
		//Add everything ...
		
		JPanel topPanel = new TexturedPanel(Skin.wool_black);
		topPanel.setLayout(new BorderLayout());
		JBorderPane topBorder = new JBorderPane(topPanel);
	    JLabel label = new JLabel("Launcher Options", 0);
	    label.setForeground(new Color(0xffffff));
	    label.setBorder(new EmptyBorder(8, 0, 16, 0));
	    label.setFont(new Font("Default", 1, 16));
	    JPanel topSouthPanel = new JPanel(new BorderLayout());
	    topSouthPanel.setOpaque(false);
	    topSouthPanel.add(label, "Center");
	    topPanel.add(topSouthPanel, "South");
	    mainpanel.add(topBorder, "North");
	    
	    //Init OptionsMenu
	    JPanel optionsPanel = new TexturedPanel(Skin.wool_blue);
	    optionsPanel.setLayout(new BorderLayout());
	    JPanel labelPanel = new JPanel();
	    labelPanel.setOpaque(false);
	    labelPanel.setLayout(new GridLayout(0, 1));
	    JPanel fieldPanel = new JPanel();
	    fieldPanel.setOpaque(false);
	    fieldPanel.setLayout(new GridLayout(0, 1));
	    optionsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
	    optionsPanel.add(labelPanel, "West");
	    optionsPanel.add(fieldPanel, "Center");
		
	    //Add all the needed options + modules + save option + ...
	    addOptions(labelPanel, fieldPanel);
	    
	    JBorderPane optionsBorder = new JBorderPane(optionsPanel);
	    mainpanel.add(optionsBorder, "Center");
	    
	    add(mainpanel, "Center");
	    
	    lastInstance = this;
	    
	}
	
	private static class TransparentButton extends JButton {
		private static final long serialVersionUID = 1L;

		public TransparentButton(String s) {
			super(s);
		}
		
		@Override
		public boolean isOpaque() {
			return false;
		}
	}
	
	//Private method to add all needed options to the OptionsMenu .
	private void addOptions(final JPanel labelPanel, final JPanel fieldPanel) {
		
		labelPanel.add(new JLabel("Backup Utility: ", 4));
		TransparentButton openManager = new TransparentButton("Backup Utility");
		fieldPanel.add(openManager);
		openManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMainPanel(new MinecraftBackupManager());
			}
		});
		
		labelPanel.add(new JLabel("Minecrack Installer v.1.1: ", 4));
		TransparentButton openMinecrack = new TransparentButton("Minecrack Installer");
		fieldPanel.add(openMinecrack);
		openMinecrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMainPanel(new MinecrackInstaller());
			}
		});
		
		labelPanel.add(new JLabel("Output Console:", 4));
		TransparentButton openConsole = new TransparentButton("Show Output Console");
		fieldPanel.add(openConsole);
		openConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (OutputConsole.quit)
					new OutputConsole();
				OutputConsole.frame.toFront();
			}
		});
		
		SpinnerModel cRAMsm = new SpinnerModel() {
			
			ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
			int v = 0;
			int uplimit = 1024*25; // Who needs more than 10 GB ram JUST for Minecraft ? Playing Minecraft on a Server PC ?
			int downlimit = 128; // 256 MB needed !
			
			@Override
			public String getValue() {
				return v + "";
			}
			
			@Override
			public void setValue(Object v) {
				if (v instanceof Integer) {
					change((Integer) v);
					return;
				}
				if (v instanceof String) {
					change(Integer.parseInt((String) v));
					return;
				}
				throw new IllegalArgumentException();
			}
			
			private void change(int newv) {
				int oldv = v;
				v = (newv/8)*8;
				if (v > uplimit || v < downlimit) {
					v = oldv;
					return;
				}
				ChangeEvent ce = new ChangeEvent(this);
				for (ChangeListener cl : listeners) {
					cl.stateChanged(ce);
				}
			}

			@Override
			public String getNextValue() {
				return (v+128)+"";
			}
			
			@Override
			public String getPreviousValue() {
				return (v-128)+"";
			}
			
			@Override
			public void addChangeListener(ChangeListener l) {
				listeners.add(l);
			}
			
			@Override
			public void removeChangeListener(ChangeListener l) {
				listeners.remove(l);
			}
			
		};
	    final JSpinner cRAMf = new JSpinner(cRAMsm);
	    cRAMf.setValue(Options.get("ram_amount", "1024"));
	    labelPanel.add(new JLabel("Custom RAM value ( MB ) :", 4));
	    fieldPanel.add(cRAMf);
		
	    //Load module menus
	    for (ModuleBase mb : Modules.getModules()) {
	    	for (final OptionModule o : mb.getOptionModules()) {
	    		JButton b = new JButton(o.getMenuButtonLabel());
	    		b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setMainPanel(o.getMenu());
					}
	    		});
	    		fieldPanel.add(b);
	    		labelPanel.add(new JLabel(o.getMenuLabel(), 4));
	    	}
	    }
	    
	    labelPanel.add(new JLabel("Save: ", 4));
		JButton saveButton = new JButton("Save options");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.set("ram_amount", cRAMf.getValue()+"");
				Options.save();
				MinecraftSL.instance.setNewsPageFromOptions();
			}
		});
	    fieldPanel.add(saveButton);
	    
	    labelPanel.add(new JLabel("Reload: ", 4));
		JButton loadButton = new JButton("Load options");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.load();
				cRAMf.setValue(Options.get("ram_amount", "1024"));
				MinecraftSL.instance.setNewsPageFromOptions();
			}
		});
	    fieldPanel.add(loadButton);
	    
	    labelPanel.add(new JLabel("Reset: ", 4));
		JButton resetButton = new JButton("Reset options");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.clear();
				Options.setup();
				MinecraftSL.instance.setNewsPageFromOptions();
				labelPanel.removeAll();
				fieldPanel.removeAll();
				addOptions(labelPanel, fieldPanel);
				validate();
				repaint();
			}
		});
	    fieldPanel.add(resetButton);
	    
	    //////////////////////////////////////////////////////////////////////////////////
	    //////////////////////////////////////////////////////////////////////////////////
	    //////////////////////////////////////////////////////////////////////////////////
	    
	    labelPanel.add(new JLabel("", 4));
	    fieldPanel.add(new JLabel("", 4));
	    
		labelPanel.add(new JLabel("Update Launcher ( needs Internet Connection ): ", 4));
		TransparentButton updateLauncher = new TransparentButton("Update Launcher");
		fieldPanel.add(updateLauncher);
		updateLauncher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Options.set("mcsl_dir", MinecraftSL.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1).toString());
					File updaterFile = new File(Options.getSystemTempDir(), "MinecraftSLUpdater.jar");
					if (updaterFile.exists()) {
						net.minecraft.utils.BackupUtil.deleteFile(updaterFile);
					}
					System.out.println("Downloading the main updater file ...");
					net.minecraft.utils.BackupUtil.downloadFile("https://sites.google.com/site/minecraftsloader/downloads/MinecraftSLUpdater.jar", updaterFile);
					updaterFile = new File(Options.getSystemTempDir(), "MinecraftSLUpdater.jar");
					System.out.println("Launching the main updater file ...");
					Runtime rt = Runtime.getRuntime();
					Process pr = rt.exec("java -jar \""+updaterFile+"\"");
					InputStream is = pr.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					@SuppressWarnings("unused")
					BufferedReader br = new BufferedReader(isr);
					System.exit(0);
				}catch(Exception updateE) {
					updateE.printStackTrace();
				}
			}
		});
	    
	    labelPanel.add(new JLabel("", 4));
	    fieldPanel.add(new JLabel("", 4));
	    
	    JLabel dirLink = new JLabel(Options.getMCDir().toString());
	    dirLink.setOpaque(false);
	    dirLink.setCursor(Cursor.getPredefinedCursor(12));
	    dirLink.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent arg0) {
	    		try {
	    			HTTPClient.openLink(new URL("file://" + Options.getMCDir().getAbsolutePath()).toURI());
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    });
	    dirLink.setForeground(new Color(2105599));
	    labelPanel.add(new JLabel("Default game location on disk with current user: ", 4));
	    fieldPanel.add(dirLink);
	    
	    labelPanel.add(new JLabel("Build "+MinecraftSL.build+" by : ", 4));
	    labelPanel.add(new JLabel("Minecraft Launcher Release 13", 4));
	    labelPanel.add(new JLabel("Codename Minecraft.Reloaded , info : ", 4));
	    fieldPanel.add(new JLabel("AngelDE98 , AnjoCaido , Mojang * , Zebz213 , Kartracer97 , Smoolak , JBextrem & you all there", 4));
	    fieldPanel.add(new JLabel("This Version is the result of hard work made for the community by the community .", 4));
	    fieldPanel.add(new JLabel("It is an revolution in user experience , modding and playing minecraft .", 4));
	    labelPanel.add(new JLabel("", 4));
	    fieldPanel.add(new JLabel("", 4));
	    labelPanel.add(new JLabel("", 4));
	    fieldPanel.add(new JLabel("This launcher isn't property of Mojang . It's being maintained serperately as open source software .", 4));
	}
	



	@Override
	public void paintComponent(Graphics g2) {
	    int w = getWidth() + 1;
	    int h = getHeight() + 1;
	    if ((this.img == null) || (this.img.getWidth(null) != w) || (this.img.getHeight(null) != h)) {
	    	this.img = (BufferedImage) createImage(w, h);
	    	
	    	Graphics g = this.img.getGraphics();
	    	int ww = raww;
	    	int hh = rawh;
	    	for (int x = 0; x <= w / ww; x++) {
	    		for (int y = 0; y <= h / hh; y++) {
	    			g.drawImage(this.bgImage, x * ww / 2, y * hh / 2, null);
	    		}
	    	}
	    	g.dispose();
	   	}
	   	g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
	}


	
	public static void setMainPanel(TransparentPanel p) {
		lastMainPanel = p;
		lastInstance.remove(lastInstance.mainpanel);
		lastInstance.add(p);
		lastInstance.setBorder(null);
		lastInstance.validate();
		lastInstance.repaint();
	}
	
	public static void back() {
		lastInstance.remove(lastMainPanel);
		lastInstance.add(lastInstance.mainpanel, "Center");
		lastInstance.setBorder(new EmptyBorder(8, 12, 12, 12));
		lastInstance.validate();
		lastInstance.repaint();
	}
	
}
