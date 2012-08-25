package net.minecraft.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import net.minecraft.MinecraftSL;
import net.minecraft.Options;
import net.minecraft.http.HTTPClient;
import net.minecraft.res.Skin;

public class OutputConsole extends WindowAdapter implements WindowListener,
		ActionListener, Runnable {
	public static JFrame frame;
	private static JEditorPaneX textArea;
	private Thread reader;
	private Thread reader2;
	public static boolean quit = true;
	private final PipedInputStream pin = new PipedInputStream();
	private final PipedInputStream pin2 = new PipedInputStream();
	public static String log;

	public OutputConsole() {
		frame = new JFrame("Output Console");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(screenSize.width / 2,
				screenSize.height / 2);
		int x = frameSize.width / 2;
		int y = frameSize.height / 2;
		frame.setBounds(x, y, frameSize.width, frameSize.height);
		frame.setIconImage(Skin.icon);
		frame.setLocation(0, 0);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new MatteBorder(2, 2, 2, 2, new Color(255, 255,
				255, 1)));

		textArea = new JEditorPaneX();
		log = "";
		textArea.setContentType("text/html");
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		textArea.setText("<html><head></head><body><font style=\"font-family:sans-serif\"></font></body></html>");

		final Action copyRAction = new AbstractAction("Copy") {
			private static final long serialVersionUID = 1L;

			{
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			}

			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(log);
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		};
		final JPopupMenu popupTextArea = new JPopupMenu();

		{
			popupTextArea.add(new JMenuItem(copyRAction));
		}
		MouseAdapter mouseListenerTA = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if (textArea.getSelectedText() == null
							|| textArea.getSelectedText() == "") {
						copyRAction.setEnabled(false);
					} else {
						copyRAction.setEnabled(true);
					}
					popupTextArea.show(textArea, e.getX(), e.getY());
					popupTextArea.repaint();
				}
			}
		};

		textArea.addMouseListener(mouseListenerTA);

		final JButton button = new JButton("Clear");
		button.setOpaque(false);
		button.setFont(new Font("Sans Serif", 0, 12));
		final JButton dumpB = new JButton("Dump Output");
		dumpB.setOpaque(false);
		dumpB.setFont(new Font("Sans Serif", 0, 12));
		final JButton dumpfB = new JButton("Open Dump Folder");
		dumpfB.setOpaque(false);
		dumpfB.setFont(new Font("Sans Serif", 0, 12));
		final JButton copyB = new JButton("Copy to Clipboard");
		copyB.setOpaque(false);
		copyB.setFont(new Font("Sans Serif", 0, 12));

		frame.getContentPane().setLayout(new BorderLayout());
		JScrollPane areaPane = new JScrollPane(OutputConsole.textArea);
		areaPane.setAutoscrolls(true);
		mainPanel.add(areaPane, "Center");
		final JPanel southPanel = new JPanel(new BorderLayout());
		JPanel leftSPanel = new JPanel();
		// leftSPanel.setOpaque(false);
		leftSPanel.setBackground(new Color(255, 255, 255, 0));
		leftSPanel.add(button);
		leftSPanel.add(dumpB);
		leftSPanel.add(dumpfB);
		leftSPanel.add(copyB);
		southPanel.add(leftSPanel, "West");
		final JLabel versionL = new JLabel("v.1.5");
		versionL.setBorder(new MatteBorder(4, 4, 4, 4, new Color(255, 255, 255,
				0)));
		versionL.setFont(new Font("Sans Serif", 0, 12));
		versionL.setOpaque(true);
		southPanel.add(versionL, "East");

		mainPanel.add(southPanel, "South");
		frame.setVisible(true);
		quit = false;

		OutputConsole.frame.toFront();

		frame.getContentPane().add(mainPanel, "Center");

		frame.addWindowListener(this);
		button.addActionListener(this);
		dumpB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dump();
			}
		});
		dumpfB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!new File(Options.getMCDir(), "DUMPS").exists()) {
						try {
							new File(Options.getMCDir(), "DUMPS").mkdirs();
						} catch (Exception e1) {
							e1.printStackTrace();
						} catch (Error e1) {
							e1.printStackTrace();
						}
						try {
							new File(Options.getMCDir(), "DUMPS").mkdir();
						} catch (Exception e1) {
							e1.printStackTrace();
						} catch (Error e1) {
							e1.printStackTrace();
						}
					}
					HTTPClient.openLink(new URL("file://"
							+ new File(Options.getMCDir(), "DUMPS")
									.getAbsolutePath()).toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		copyB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(textArea
						.getTXT());
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});

		try {
			PipedOutputStream pout = new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout, true));
		} catch (IOException io) {
			OutputConsole.textArea.append("Couldn't redirect STDOUT to this console\n"
					+ io.getMessage(), Color.red);
		} catch (SecurityException se) {
			OutputConsole.textArea.append("Couldn't redirect STDOUT to this console\n"
					+ se.getMessage(), Color.red);
		}

		try {
			PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2, true));
		} catch (IOException io) {
			OutputConsole.textArea.append("Couldn't redirect STDERR to this console\n"
					+ io.getMessage(), Color.red);
		} catch (SecurityException se) {
			OutputConsole.textArea.append("Couldn't redirect STDERR to this console\n"
					+ se.getMessage(), Color.red);
		}

		OutputConsole.quit = false;

		this.reader = new Thread(this);
		this.reader.setDaemon(true);
		this.reader.start();

		this.reader2 = new Thread(this);
		this.reader2.setDaemon(true);
		this.reader2.start();

	}

	public static void dump() {
		int number = 0;
		File dumpDir = new File(Options.getMCDir(), "DUMPS");
		if (!dumpDir.exists()) {
			try {
				dumpDir.mkdirs();
			} catch (Exception e1) {
				e1.printStackTrace();
			} catch (Error e1) {
				e1.printStackTrace();
			}
			try {
				dumpDir.mkdir();
			} catch (Exception e1) {
				e1.printStackTrace();
			} catch (Error e1) {
				e1.printStackTrace();
			}
			dumpDir = new File(Options.getMCDir(), "DUMPS");
		}
		String extension = ".htm";
		File myDump = new File(dumpDir, "OUTPUTDUMP_" + number + extension);
		while (myDump.exists()) {
			number = number + 1;
			myDump = new File(dumpDir, "OUTPUTDUMP_" + number + extension);
		}
		dumpDir = new File(Options.getMCDir(), "DUMPS");
		myDump = new File(dumpDir, "OUTPUTDUMP_" + number + extension);
		if (!myDump.exists()) {
			try {
				myDump.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		dumpDir = new File(Options.getMCDir(), "DUMPS");
		myDump = new File(dumpDir, "OUTPUTDUMP_" + number + extension);

		try {
			PrintWriter printwriter = new PrintWriter(new FileWriter(myDump));
			String html = textArea.getText();
			String[] html2 = html.split("<font face=\"sans-serif\">");
			html = html2[0]
					+ "<font face=\"sans-serif\">MinecraftSL build "
					+ MinecraftSL.build
					+ "<br>Made by the MinecraftSL OpenSource- and User Community<br><br>This is an Output Console Log Dump .<br>If problems were happening create a new issue , see the homepage to see how .<br><br><br><div style=\"height: 1px; font-size: 1px; line-height: 1px; background-color: #000000; overflow:hidden;\"></div><br> "
					+ html2[1];
			String[] lines = html.split("\n");
			int done = 0;
			while (done != lines.length) {
				printwriter.println((new StringBuilder()).append(lines[done])
						.toString());
				done = done + 1;
			}
			printwriter.close();
			System.out.println("Dump saved .");
		} catch (Exception exception) {
			System.out.println("Failed to save dump .");
			exception.printStackTrace();
		}
	}

	public synchronized void windowClosed(WindowEvent evt) {
		OutputConsole.quit = true;
		try {
			this.reader.join(1L);
			this.pin.close();
		} catch (Exception localException) {
		}
		try {
			this.reader2.join(1L);
			this.pin2.close();
		} catch (Exception localException1) {
		}
	}

	public synchronized void windowClosing(WindowEvent evt) {
		quit = true;
		frame.setVisible(false);
		frame.dispose();
		windowClosed(evt);
	}

	public synchronized void actionPerformed(ActionEvent evt) {
		OutputConsole.textArea.setText("");
	}

	public synchronized void run() {
		try {
			while (Thread.currentThread() == this.reader) {
				try {
					wait(1L);
				} catch (InterruptedException localInterruptedException) {
				}
				if (this.pin.available() != 0) {
					String input = readLine(this.pin);
					log = log + input;
					OutputConsole.textArea.append(input);
				}
				if (OutputConsole.quit)
					return;
			}

			while (Thread.currentThread() == this.reader2) {
				try {
					wait(1L);
				} catch (InterruptedException localInterruptedException1) {
				}
				if (this.pin2.available() != 0) {
					String input = readLine(this.pin2);
					log = log + input;
					OutputConsole.textArea.append(input, Color.red);
				}
				if (OutputConsole.quit)
					return;
			}
		} catch (Exception e) {
			OutputConsole.textArea.append("\nConsole reports an Internal error.");
			OutputConsole.textArea.append("The error is: " + e);
		}
	}

	public synchronized String readLine(PipedInputStream in) throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte[] b = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while ((!input.endsWith("\n")) && (!input.endsWith("\r\n"))
				&& (!OutputConsole.quit));
		return input;
	}

	public static void main(String[] arg) {
		new OutputConsole();
	}
}