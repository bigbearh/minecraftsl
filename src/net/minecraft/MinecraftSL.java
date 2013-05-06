package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.minecraft.http.HTTPClient;
import net.minecraft.modules.Modules;
import net.minecraft.res.Skin;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/*
 * Minecraft Smart Launcher ( MinecraftSL , in-code MCSL but Minecraft ServerList already take MCSL ... )  Project info : 
 * - Started 2011 as joke about Minecraft for free
 * - Free , legal Minecraft due to just downloading resources from original host
 * - Can be build up with various options or utilities , some of them are given already
 * - Parent project of the IGM2 project ( which ended as IGM2E , AngelDE98's game engine ) , IGM2 is still referenced to InGameMenu2
 * -- ( Sidenote ) -- IGM2E and IGM2 aren't trademarks or whatever of IGM(.com?) .
 * - Since 2012 Open-Source and uses the JPlayground which is created by AngelDE98 and some other external references .
 */

/*
 * TODO @todo ( TODOs ) : 
 * - Make it force updating 
 * - Make it check for updates 
 */


/**
 * MinecraftSL main class . Hence " Maik " is the real name of AngelDE98 , but it's just a sidenote . <p>
 * 
 * The field {@link instance} holds the only given MinecraftSL instance .
 * 	Fields responsible for username or password are private and transient . They should not be accessed .
 * 
 * @author Maik
 *	
 */
public final class MinecraftSL extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final String build = "1 alpha 3";
	public static Random rand;
	public static final Color clear = new Color(255, 255, 255, 0);
	public static final Color black = new Color(0, 0, 0, 255);
	public static final Color white = new Color(255, 255, 255, 255);
	
	/**
	 * The only created MinecraftSL instance in this VM .
	 */
	public static MinecraftSL instance;
	private transient JPasswordField passField;
	private transient JTextField userField;
	
	private transient static String username;
	private transient static String password;
	
	//private TexturedPanel mainpanel = new TexturedPanel();
	private JPanel mainpanel = new TexturedPanel();
	private TexturedEditorPane newsPane = new TexturedEditorPane();
	private JScrollPane newsScrollPane;
	private JLabel errorLabel = new JLabel("");
	private JCheckBox forceupdate = new TexturedCheckbox("Force update");
	private LogoButton logo;
	
	@SuppressWarnings("unused")
	private boolean loggedIn = false;
	public static boolean launched = false;
	private static boolean offlinemode = false;
	private Thread newsThread;
	
	public static LauncherPanel launcherpanel;
	public static ALauncher launcher;
	
	private static long currentram = 0;
	public static String[] savedargs;
	
	private MinecraftSL() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Modules.injectModules();
		
		setTitle("MinecraftSL build "+build);
		
		rand = new Random();
		rand = new Random(rand.nextLong());
		rand = new Random(rand.nextLong());
		
		mainpanel.setPreferredSize(new Dimension(854, 480));
		setContentPane(mainpanel);
		mainpanel.setLayout(new BorderLayout());
		mainpanel.setBackground(clear);
		
		TexturedPanel subMainPane = new TexturedGradientPanel();
		//subMainPane.setBorder(new MatteBorder(1, 2, 3, 4, clear));
		subMainPane.setBorder(new MatteBorder(1, 6, 1, 4, clear));
		mainpanel.add(subMainPane, BorderLayout.EAST);
		FormLayout fl_subMainPane = new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("128px:grow"),},
			new RowSpec[] {
				RowSpec.decode("120px"),
				new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("0px", false), Sizes.constant("32px", false)), 0),
				new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("16px", false), Sizes.constant("32px", false)), 0),
				RowSpec.decode("max(24px;default)"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(24px;default)"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(32px;default)"),
				RowSpec.decode("max(32px;default)"),});
		subMainPane.setLayout(fl_subMainPane);
		
		EdgeButton loginButton = new EdgeButton("Login", Skin.button);
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = userField.getText();
				password = new String(passField.getPassword());
				handleLogin(username, password);
			}
		});
		
		final EdgeButton optionsButton = new EdgeButton("Options", Skin.button);
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Component curr = newsScrollPane.getViewport().getView();
				if (curr instanceof OptionsMenu) {
					newsScrollPane.setViewportView(newsPane);
					optionsButton.setText("Options");
				} else {
					OptionsMenu menu = new OptionsMenu();
					menu.setPreferredSize(new Dimension(512+128, menu.getPreferredSize().height));
					newsScrollPane.setViewportView(menu);
					optionsButton.setText("News");
				}
			}
		});
		
		logo = new LogoButton();
		subMainPane.add(logo, "1, 1");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setForeground(Color.RED);
		
		subMainPane.add(errorLabel, "1, 3, fill, default");
		
		userField = new JTextField();
		JLabel userLabel = new JLabel(" Username :");
		userLabel.setForeground(white);
		userLabel.setOpaque(false);
		subMainPane.add(userLabel, "1, 4, fill, fill");
		subMainPane.add(userField, "1, 5, fill, default");
		userField.setColumns(15);
		
		passField = new JPasswordField();
		JLabel passLabel = new JLabel(" Password :");
		passLabel.setForeground(white);
		passLabel.setOpaque(false);
		subMainPane.add(passLabel, "1, 6, fill, fill");
		subMainPane.add(passField, "1, 7, fill, default");
		passField.setColumns(15);
		
		subMainPane.add(forceupdate, "1, 8");
		
		subMainPane.add(loginButton, "1, 9, fill, fill");
		subMainPane.add(optionsButton, "1, 10, fill, fill");
		
		newsPane.setBackground(new Color(0x222222));
		newsPane.setForeground(new Color(0xffffff));
		newsPane.setContentType("text/html");
		//newsPane.setContentType("text/plain");
		newsPane.setEditable(false);
		newsPane.setBorder(null);
		newsPane.setOpaque(false);
		
		newsPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent he) {
				if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						if (he.getURL().toURI().toString().contains("launcher")) {
							newsPane.setPage(he.getURL());
						} else {
							HTTPClient.openLink(he.getURL().toURI());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
		});
		
		newsThread = new Thread("launcher.newsthread") {
			public void run() {
				
				newsPane.setText(htmlify("Loading news ... <br>( "+Options.get("startpage")+" )"));
  				
		    	try {
	  				setNewsPanePage(Options.get("startpage"));
	  				
		  		} catch (Throwable e) {
		  			if (e instanceof ThreadDeath) {
		  				newsPane.setText(htmlify("This newsPane is property of MinecraftSL . <br> It got \"killed\" because of loading Minecraft itself . <br> I'm sorry when you see this and Minecraft won't load ."));
		  				return;
		  			}
		   			
		   			if (!(e instanceof UnknownHostException)) {
			   			newsPane.setText(htmlify("Failed to update news : <br>"+e));
			   			e.printStackTrace();
		   			} else {
		   				setOfflineMode();
		   				newsPane.setText(htmlify("Couldn't connect to news server . <br> Disabled forcing updates and online functions for this session . <br>( "+Options.get("startpage")+" )"));
		   			}
		  		}
			}
		};
		newsThread.setDaemon(true);
		newsThread.start();
		
		newsScrollPane = new JScrollPane(newsPane);
		newsScrollPane.setBorder(new MatteBorder(0, 0, 0, 2, black));
		newsScrollPane.setBackground(clear);
		mainpanel.add(newsScrollPane, BorderLayout.CENTER);
		
		pack();
		requestFocus();
		setResizable(true);
		
		setIconImage(Skin.icon);
		setLocationRelativeTo(null);
	}
	
	public void setNewsPanePage(String url) throws IOException {
		newsPane.setPage(url);
	}
	
	public void setNewsPageFromOptions() {
		try {
			setNewsPanePage(Options.get("startpage"));
		} catch (Exception e) {
		}
	}

	public String htmlify(String s) {
		return "<html><body><font face=\"sans-serif\" color=\"#"+Integer.toHexString(newsPane.getForeground().getRGB())+"\"><br><br><br><br><br><br><br><center>"+s+"</center></font></body></html>";
	}
	
	public static UncaughtExceptionHandler getUEH() {
		return new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				
				int filei = 0;
				String filename = "fatal_"+filei+".txt";
				File errfile = new File(Options.getDir(), filename);
				while (errfile.exists()) {
					filei++;
					filename = "fatal_"+filei+".txt";
					errfile = new File(Options.getDir(), filename);
				}
				try {
					errfile.createNewFile();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				String err = "\nPlease create a new issue on the github project page and give us the content of :\n"+errfile+"\nThanks ! Just by the way : You should restart MinecraftSL . Something went wrong ...";
				String err2 = "Exception: "+e+"\r\n";
				for (int i = 0; i < e.getStackTrace().length; i++) {
					err2 += i+": "+e.getStackTrace()[i]+"\r\n";
				}
				Throwable caused = e;
				while ((caused = caused.getCause()) != null) {
					err2 += "\r\nCaused by: "+e+"\r\n";
					for (int i = 0; i < e.getStackTrace().length; i++) {
						err2 += i+": "+e.getStackTrace()[i]+"\r\n";
					}
				}
				if (err2.isEmpty()) {
					err2 = "Unknown";
				}
				try {
					FileOutputStream fos = new FileOutputStream(errfile);
					fos.write(err2.getBytes());
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(instance, err, e+"", JOptionPane.ERROR_MESSAGE);
			}
		};
	}
	
	public static void main(String[] args) {
		Options.load();
		
		Thread.setDefaultUncaughtExceptionHandler(getUEH());
		Thread.currentThread().setUncaughtExceptionHandler(getUEH());
		
		boolean debug = false;
		for (String s : args) {
			if (s.equalsIgnoreCase("debugide") || s.equalsIgnoreCase("-debugide")) {
				debug = true;
				break;
			}
		}
		
		savedargs = args;
		currentram = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		System.out.println("Started with "+currentram+"mb ram , wanted "+Options.get("ram_amount"));
		try {
			System.out.println("Restarting with "+Options.get("ram_amount")+" mb ram ...");
			String pathToJar = MinecraftSL.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			
			ArrayList<String> params = new ArrayList<String>();
			
			if(System.getProperty("os.name").startsWith("Windows")) {
				params.add("javaw");
			} else { // os x and linux
				params.add("java");
			}
			int newram = Options.getAsInteger("ram_amount");
			newram += newram/28.44444444444; // Every 28.444444444444444444...th MB 1 MB gets hidden because of the VM ...
			newram -= 2; // For CPU , 1 is 0 , 2 is 1 , 3 is 2 , 4 is 3 , ... and also there is a +- 1 radius of not being perfect 
			params.add("-Xmx"+newram+"m");
			for (int i = 0; i < args.length; i++) {
				String s = args[i];
				if (s.equalsIgnoreCase("debugide") || s.equalsIgnoreCase("-debugide")) {
					continue;
				}
				params.add(s);
			}
			
			params.add("-classpath");
			params.add(pathToJar);
			params.add("net.minecraft.RamLauncher");
			ProcessBuilder pb = new ProcessBuilder(params);
			pb.redirectErrorStream(true);
			final Process process = pb.start();
			if (process == null) throw new Exception("!");
			
			if (debug) {
				//Debug mode , this javaw process stays activated because of System.out/err/in tunnels .
				//Usually useful when in eclipse or any other IDE .
				final InputStream outs = process.getInputStream();
				final OutputStream ins = process.getOutputStream();
				final InputStream errs = process.getErrorStream();
				
				Thread updateThread = new Thread() {
					public void run() {
						boolean killed = false;
						while (!killed) {
							try {
								process.exitValue();
								killed = true;
								return;
							} catch (IllegalThreadStateException e) {
								killed = false;
							}
							
							try {
								byte[] b = new byte[outs.available()];
								outs.read(b);
								System.out.write(b);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							try {
								byte[] b = new byte[errs.available()];
								errs.read(b);
								System.err.write(b);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							try {
								byte[] b = new byte[System.in.available()];
								System.in.read(b);
								ins.write(b);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
				//Priority of 3 , otherwise it would slow down either the tunnelling and shutting down this process or the new process which results in lag .
				updateThread.setPriority(3);
				updateThread.start();
				
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						process.destroy();
					}
				});
			} else {
				//The normal user may be confused why there are 2 javaw processes ...
				System.out.println("Closing this unneeded javaw process ...\n    If you expect anything being logged in this process' System.out/err/in , forgett it .\n    If you NEED it , add \"debugide\" as argument .");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			RamLauncher.main(args);
		}
	}

	
	public static void main2(String[] args) {
		Options.load();
		
		Thread.setDefaultUncaughtExceptionHandler(getUEH());
		Thread.currentThread().setUncaughtExceptionHandler(getUEH());
		
		savedargs = args;
		currentram = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		System.out.println("Started with "+currentram+"mb ram , argument gave "+Options.get("ram_amount"));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception localException) {
		}
		instance = new MinecraftSL();
		instance.setVisible(true);
	}
	
	private void handleLogin(String user, String pass) { 
		EnumError error = validateAcc(user, pass);
		handleLogin(user, pass, error);
	}
	
	private void handleLogin(String user, String pass, EnumError error) { 
		//String[] args = {};
		if (error != EnumError.valid) {
			error.handleError();
		} else {
			newsThread.interrupt();
			/*
			 * Stopping a thread with Thread.stop causes it to unlock all of the monitors that it has locked , but the ThreadDeath error is being catched -.-
			 */
			loggedIn = true;
			launched = true;
			LaunchUtil.forceUpdate = forceupdate.isSelected();
			launcher = LaunchUtil.newLauncher();
			launcherpanel = new LauncherPanel();
			launcherpanel.setSize(mainpanel.getPreferredSize());
			launcherpanel.add(launcher);
			JPanel p = new JPanel(new BorderLayout());
			p.setPreferredSize(mainpanel.getPreferredSize());
			p.add(launcherpanel, "Center");
			setContentPane(p);
			launcherpanel.setBackground(new Color(0, 0, 0));
			
			String sessionid = "-1";
			String lastversion = "-1";
			String ticket = "-1";
			launcher.init(username, lastversion, ticket, sessionid);
			validate();
			repaint();
			launcher.start();
		}
	}
	
	private EnumError validateAcc(final String username, final String password) {
		final EnumError[] error = new EnumError[1];
		error[0] = EnumError.loading;
		// TODO : Implement checking on servers ... 
		
		Thread t = new Thread("CheckThread") {
			@Override
			public void run() {
				try {
					String parameters = "user=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + 13;
					String result = HTTPClient.executeSecurePost("https://login.minecraft.net/", parameters, null);
					System.out.println(result);
					if (result == null) {
						error[0] = EnumError.other;
						error[0].data = "Can't connect to minecraft.net";
						setOfflineMode();
						return;
					}
					if (result.trim().isEmpty()) {
						error[0] = EnumError.other;
						error[0].data = "Can't connect to minecraft.net";
						setOfflineMode();
						return;
					}
					if (!result.contains(":")) {
						if (result.trim().equals("Bad login")) {
							error[0] = EnumError.other;
							error[0].data = "Login Failed";
						} else if (result.trim().equals("Old version")) {
							error[0] = EnumError.other;
							error[0].data = "Outdated launcher core";
						} else {
							error[0] = EnumError.other;
							error[0].data = result;
						}
						return;
					}
					String[] values = result.split(":");
					
					error[0] = EnumError.valid;
					//userName latestVersion downloadTicket sessionId
					error[0].data = values[2].trim()+":"+values[0].trim()+":"+values[1].trim()+":"+values[3].trim();
					
				} catch (Exception e) {
					e.printStackTrace();
					error[0] = EnumError.other;
					error[0].data = e+"";
				}
			}
		};
		t.setDaemon(true);
		t.setPriority(4); // Slightly lower than default
		t.start();
		
		error[0].handleError();
		while (error[0].equals(EnumError.loading)) {
		}
		
		return error[0];
	}

	public static enum EnumError { 
		valid, failed, notregi, invalid, loading, other;
		
		private String name = getFullName();
		/**
		 * Login data , like Username , SessionID and such things .
		 */
		public String data = "";
		
		public static String getFullName(EnumError error) {
			String name = "Invalid enum";
			if (error == valid) name = "OK";
			if (error == failed) name = "Failed Check";
			if (error == notregi) name = "Account not found";
			if (error == invalid) name = "Wrong Password";
			if (error == loading) name = "Loading ...";
			if (error == other) name = error.data;
			// No " user not premium "
			return name; 
		}
		
		public String getFullName() {
			name = getFullName(this);
			return name; 
		}
		
		public void handleError() {
			String error = getFullName();
			instance.errorLabel.setText(error);
			instance.errorLabel.validate();
			instance.errorLabel.repaint();
		}
	};
	
	public void setOfflineMode() {
		if (offlinemode) {
			return;
		}
		
		offlinemode = true;
		forceupdate.setEnabled(false);
		forceupdate.setSelected(false);
	}
	
	public static boolean isOfflineMode() {
		return offlinemode;
	}
	
	public static long getCurrentRam() {
		return currentram;
	}
	
	private boolean forceexit = true;
	
	@Override
	public void dispose() {
		Options.save();
        new Thread("launcher.kill") {
        	public void run() {
        		for (int i = 0; i < 1000 && forceexit; i++) {
        			try {
        				Thread.sleep(30L);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        				System.exit(0);
        			}
        		}
        		if (forceexit) {
        			System.out.println("FORCING EXIT!");
        			System.exit(0);
        		}
        	}
        }.start();
        if (launcher != null) {
        	launcher.stop();
        	launcher.destroy();
        }
		super.dispose();
		forceexit = false;
        System.exit(0);
	}
	
}
