package net.minecraft.texpackinstaller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;

import net.minecraft.Options;
import net.minecraft.res.Skin;

public class MinecraftTexpackInstaller extends JFrame {
	private static final long serialVersionUID = 1L;

	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton5;
	private JLabel jLabel1;
	private JLabel jLabel10;
	private JLabel jLabel12;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JPanel jPanel4;
	private JSeparator jSeparator1;
	private JTabbedPane jTabbedPane1;
	private JButton uninstall;
	@SuppressWarnings("unused")
	private ButtonGroup worldgroup;

	public MinecraftTexpackInstaller() {
		pack();
		setLocationRelativeTo(null);
		setIconImage(Skin.icon);

		setResizable(false);

		initComponents();
		refreshButtons();
	}

	private void initComponents() {
		this.worldgroup = new ButtonGroup();
		this.jTabbedPane1 = new JTabbedPane();
		this.jPanel1 = new JPanel();
		this.jPanel3 = new JPanel();
		this.jPanel4 = new JPanel();
		this.jButton1 = new JButton();
		this.jButton2 = new JButton();
		this.jLabel3 = new JLabel();
		this.jLabel4 = new JLabel();
		this.jLabel5 = new JLabel();
		this.jLabel6 = new JLabel();
		this.jLabel10 = new JLabel();
		this.jPanel2 = new JPanel();
		this.uninstall = new JButton();
		this.jButton5 = new JButton();
		this.jLabel1 = new JLabel();
		this.jLabel2 = new JLabel();
		this.jLabel7 = new JLabel();
		this.jSeparator1 = new JSeparator();
		this.jLabel8 = new JLabel();
		this.jLabel12 = new JLabel();

		setDefaultCloseOperation(2);
		setTitle("Minecraft Texturepack Installer ( by AnjoCaido and AngelDE98 )");

		this.jButton1.setText("Backup Texturepacks");
		this.jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MinecraftTexpackInstaller.this.jButton1ActionPerformed(evt);
			}
		});
		this.jButton2.setText("Restore Texturepacks");
		this.jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MinecraftTexpackInstaller.this.jButton2ActionPerformed(evt);
			}
		});
		GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
		this.jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				jPanel4Layout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								jPanel4Layout
										.createParallelGroup(
												GroupLayout.Alignment.TRAILING)
										.addComponent(this.jButton2,
												GroupLayout.Alignment.LEADING,
												-1, -1, 32767)
										.addComponent(this.jButton1,
												GroupLayout.Alignment.LEADING,
												-1, 118, 32767))
						.addContainerGap()));

		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout.createSequentialGroup().addContainerGap()
								.addComponent(this.jButton1).addGap(18, 18, 18)
								.addComponent(this.jButton2)
								.addContainerGap(-1, 32767)));

		this.jLabel3.setText("Everything will be stored in a Zip format.");

		this.jLabel4
				.setText("But I will use .mctexpacks extension,  cuz that makes me happy.");

		this.jLabel5.setText("This Patch makes you backup all texturepacks.");

		this.jLabel6
				.setText("Ex: If you have 2 texturepacks all of them will be backuped . Otherwise 3 if 3 avaible and so on.");

		GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
		this.jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				this.jPanel3,
																				-2,
																				-1,
																				-2)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addGap(6,
																												6,
																												6)
																										.addComponent(
																												this.jPanel4,
																												-2,
																												-1,
																												-2))
																						.addComponent(
																								this.jLabel10)
																						.addComponent(
																								this.jLabel3)))
														.addComponent(
																this.jLabel4)
														.addComponent(
																this.jLabel5)
														.addComponent(
																this.jLabel6))
										.addContainerGap(109, 32767)));

		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																this.jPanel3,
																-2, -1, -2)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				this.jLabel10)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				this.jPanel4,
																				-2,
																				-1,
																				-2)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED,
																				-1,
																				32767)
																		.addComponent(
																				this.jLabel3)))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel4)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel5)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel6)
										.addContainerGap()));

		this.jTabbedPane1.addTab("Backup / Restore", this.jPanel1);

		this.uninstall.setText("Uninstall ALL PACKS");

		this.uninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MinecraftTexpackInstaller.this.uninstallActionPerformed(evt);
			}
		});
		this.jButton5.setText("Install Texturepack");
		this.jButton5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MinecraftTexpackInstaller.this.jButton5ActionPerformed(evt);
			}
		});
		this.jLabel1
				.setText("You might want to backup your Texturepack-Pack at least once after it's installed.");

		this.jLabel2
				.setText("Never know when there is something deleting everything.");

		this.jLabel7
				.setText("Zip format again, but again .mctexpacks extension. It really makes me happy.");

		GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
		this.jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addGap(6,
																				6,
																				6)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								GroupLayout.Alignment.LEADING)
																						.addComponent(
																								this.jLabel2)
																						.addComponent(
																								this.jLabel7)
																						.addComponent(
																								this.jLabel1)))
														.addComponent(
																this.jSeparator1,
																-1, 551, 32767)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				this.uninstall)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED))
														.addComponent(
																this.jButton5,
																-2, 239, -2))
										.addContainerGap()));

		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																this.uninstall))
										.addGap(3, 3, 3)
										.addComponent(this.jSeparator1, -2, 10,
												-2)
										.addGap(18, 18, 18)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(GroupLayout.Alignment.BASELINE))
										.addGap(18, 18, 18)
										.addComponent(this.jButton5)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel1)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel2)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.jLabel7)
										.addContainerGap(-1, 32767)));

		this.jTabbedPane1.addTab("Install / Uninstall", this.jPanel2);

		this.jLabel8.setText("by AnjoCaido & AngelDE98 - v1.0b1");

		this.jLabel12
				.setText("Backup/Restore take some seconds, wait for \"Done!\".");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.TRAILING)
												.addComponent(
														this.jTabbedPane1,
														GroupLayout.Alignment.LEADING,
														-1, 563, 32767)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		this.jLabel12)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED,
																		137,
																		32767)
																.addComponent(
																		this.jLabel8)))
								.addContainerGap()));

		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(this.jTabbedPane1, -1, 234, 32767)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(this.jLabel8)
												.addComponent(this.jLabel12))
								.addContainerGap()));

		pack();
	}

	private void uninstallActionPerformed(ActionEvent evt) {
		int result = JOptionPane
				.showConfirmDialog(
						this,
						"Are you sure that you want to uninstall Minecrafts Texturepacks?\nCan't Undo It! (unless you have backup, hehe)",
						"Are you sure? (Uninstallation)", 0, 2);

		if ((result == 1) || (result == -1)) {
			return;
		}
		BackupUtil.uninstallPacks();
		JOptionPane.showMessageDialog(this, "Done!", "Uninstallation", -1);
		refreshButtons();
	}

	private void jButton5ActionPerformed(ActionEvent evt) {
		int result = JOptionPane
				.showConfirmDialog(
						this,
						"Are you sure that you want to install another Texturepack?\nIT MIGHT OVERWRITE YOUR OLD ONE!\nMake sure you have your most recent texture packs backed up before this!",
						"Are you sure? (Texturepack Install)", 0, 2);

		if ((result == 1) || (result == -1)) {
			return;
		}
		JFileChooser save = new JFileChooser();
		save.setFileSelectionMode(0);
		save.setFileFilter(new BackupUtil.GameFileFilter());
		save.showOpenDialog(this);
		File f = save.getSelectedFile();
		if ((f == null) || (!f.exists()))
			return;
		try {
			BackupUtil.installPacks(f);
		} catch (IllegalStateException ex) {
			JOptionPane.showMessageDialog(this,
					"Failed!\nInvalid Zip File!\nOr another unknown error !",
					"Texturepack Installation", 0);

			return;
		}
		JOptionPane.showMessageDialog(this, "Done!",
				"Texturepack Installation", -1);
		refreshButtons();
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		int world = getWorldSelected();
		if (!new File(Options.getMCDir(), "texturepacks").exists()) {
			JOptionPane.showMessageDialog(this,
					"Sorry, but you have no texturepacks!",
					"Texturepack Backup", 0);
			return;
		}
		JFileChooser save = new JFileChooser();
		Calendar now = GregorianCalendar.getInstance();
		save.setFileSelectionMode(0);
		save.setSelectedFile(new File(String.format("MCTexpacks" + world + "_"
				+ "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS" + "_Backup."
				+ "mctexpacks", new Object[] { now })));

		save.setFileFilter(new BackupUtil.WorldFileFilter());
		int result = save.showSaveDialog(this);
		if (result != 0) {
			return;
		}
		File f = save.getSelectedFile();
		if (f == null) {
			return;
		}
		BackupUtil.backupPacks(f);
		JOptionPane.showMessageDialog(this, "Done!", "Texturepack Backup", -1);
	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		//int world = getWorldSelected();
		if (new File(Options.getMCDir(), "texturepacks").exists()) {
			int result = JOptionPane
					.showConfirmDialog(
							this,
							"Are you sure that you want to overwrite your Texturepacks?\nCan't Undo It! (unless you have backup, hehe)",
							"Are you sure? (Texturepack Restoration)", 0, 2);

			if ((result == 1) || (result == -1)) {
				return;
			}
		}

		JFileChooser save = new JFileChooser();
		save.setFileSelectionMode(0);
		save.setFileFilter(new BackupUtil.WorldFileFilter());
		save.showOpenDialog(this);
		File f = save.getSelectedFile();
		if (f == null)
			return;
		try {
			BackupUtil.restorePacks(f);
		} catch (IllegalStateException ex) {
			JOptionPane
					.showMessageDialog(
							this,
							"Failed!\nInvalid Zip Contents!\nthe world folder inside must have 'texpacks_backup' as name.",
							"Texturepack Restoration", 0);

			return;
		}
		JOptionPane.showMessageDialog(this, "Done!", "Texturepack Restoration",
				-1);
		refreshButtons();
	}

	public int getWorldSelected() {
		return 1;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MinecraftTexpackInstaller().setVisible(true);
			}
		});
	}

	private void refreshButtons() {
	}
}