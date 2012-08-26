package net.minecraft;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import net.minecraft.res.Skin;

public class Launcher extends ALauncher {
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
	}

	@Override
	public void init(String username, String lastversion, String ticket,
			String sessionid) {
		LaunchUtil.bgImage = Skin.wool_blue.getScaledInstance(32, 32, 16);
		
		LaunchUtil.customParameters.put("username", username);
		LaunchUtil.customParameters.put("sessionid", sessionid);
		
		LaunchUtil.newGameUpdater(lastversion, "minecraft.jar?user=" + username + "&ticket=" + sessionid);
	}

	@Override
	public void paint(Graphics paramGraphics) {
		if (LaunchUtil.applet != null) return;
		
		int i = getWidth() / 2;
		int j = getHeight() / 2;
		if ((LaunchUtil.img == null) || (LaunchUtil.img.getWidth() != i) || (LaunchUtil.img.getHeight() != j)) {
			LaunchUtil.img = createVolatileImage(i, j);
		}
		
		Graphics localGraphics = LaunchUtil.img.getGraphics();
		for (int k = 0; k <= i / 32; k++)
			for (int m = 0; m <= j / 32; m++)
				localGraphics.drawImage(LaunchUtil.bgImage, k * 32, m * 32, null);
		String str;
		FontMetrics localFontMetrics;
		if (LaunchUtil.pauseAskUpdate) {
			if (!LaunchUtil.hasMouseListener) {
				LaunchUtil.hasMouseListener = true;
				addMouseListener(this);
			}
			//localGraphics.setColor(Color.LIGHT_GRAY);
			localGraphics.setColor(Color.BLACK);
			str = "New update available";
			localGraphics.setFont(new Font(null, 1, 20));
			localFontMetrics = localGraphics.getFontMetrics();
			localGraphics.drawString(str, i / 2 - localFontMetrics.stringWidth(str) / 2, j / 2 - localFontMetrics.getHeight() * 2);
			
			localGraphics.setFont(new Font(null, 0, 12));
			localFontMetrics = localGraphics.getFontMetrics();
			
			localGraphics.setColor(Color.LIGHT_GRAY);
			localGraphics.fill3DRect(i / 2 - 56 - 8, j / 2, 56, 20, true);
			localGraphics.fill3DRect(i / 2 + 8, j / 2, 56, 20, true);
			localGraphics.setColor(Color.BLACK);
			
			str = "Would you like to update?";
			localGraphics.drawString(str, i / 2 - localFontMetrics.stringWidth(str) / 2, j / 2 - 8);
			
			localGraphics.setColor(Color.BLACK);
			str = "Yes";
			localGraphics.drawString(str, i / 2 - 56 - 8 - localFontMetrics.stringWidth(str) / 2 + 28, j / 2 + 14);
			str = "Not now";
			localGraphics.drawString(str, i / 2 + 8 - localFontMetrics.stringWidth(str) / 2 + 28, j / 2 + 14);
		} else {
			//localGraphics.setColor(Color.LIGHT_GRAY);
			localGraphics.setColor(Color.BLACK);
			
			str = "Launching Minecraft";
			if (LaunchUtil.forceUpdate || (LaunchUtil.state == LaunchUtil.STATE_DOWNLOADING || LaunchUtil.state == LaunchUtil.STATE_EXTRACTING_PACKAGES)) {
		    	str = "Updating Minecraft";
		    }
		    if (!LaunchUtil.getGameUpdater().canForceOffline()) {
		    	str = "Installing Minecraft";
		    }
			if (LaunchUtil.fatalError) {
				str = "Failed to launch";
			}
			
			localGraphics.setFont(new Font(null, 1, 20));
			localFontMetrics = localGraphics.getFontMetrics();
			localGraphics.drawString(str, i / 2 - localFontMetrics.stringWidth(str) / 2, j / 2 - localFontMetrics.getHeight() * 2);
			
			localGraphics.setFont(new Font(null, 0, 12));
			localFontMetrics = localGraphics.getFontMetrics();
			str = LaunchUtil.getGameUpdater().getDescriptionForState();
			if (LaunchUtil.fatalError) {
				str = LaunchUtil.fatalErrorDescription;
			}
			
			localGraphics.drawString(str, i / 2 - localFontMetrics.stringWidth(str) / 2, j / 2 + localFontMetrics.getHeight() * 1);
			str = LaunchUtil.subtaskMessage;
			localGraphics.drawString(str, i / 2 - localFontMetrics.stringWidth(str) / 2, j / 2 + localFontMetrics.getHeight() * 2);
			
			if (!LaunchUtil.fatalError) {
				localGraphics.setColor(Color.black);
				localGraphics.fillRect(64, j - 64, i - 128 + 1, 5);
				localGraphics.setColor(new Color(32768));
				localGraphics.fillRect(64, j - 64, LaunchUtil.percentage * (i - 128) / 100, 4);
				localGraphics.setColor(new Color(2138144));
				localGraphics.fillRect(65, j - 64 + 1, LaunchUtil.percentage * (i - 128) / 100 - 2, 1);
			}
		}
		
		localGraphics.dispose();
		
		paramGraphics.drawImage(LaunchUtil.img, 0, 0, i * 2, j * 2, null);
	}

	@Override
	public void mouseClicked(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mouseEntered(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mouseExited(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {
		int i = paramMouseEvent.getX() / 2;
		int j = paramMouseEvent.getY() / 2;
		int k = getWidth() / 2;
		int m = getHeight() / 2;
		
		if (contains(i, j, k / 2 - 56 - 8, m / 2, 56, 20)) {
			removeMouseListener(this);
			LaunchUtil.shouldUpdate = true;
			LaunchUtil.pauseAskUpdate = false;
			LaunchUtil.hasMouseListener = false;
		}
		if (contains(i, j, k / 2 + 8, m / 2, 56, 20)) {
			removeMouseListener(this);
			LaunchUtil.shouldUpdate = false;
			LaunchUtil.pauseAskUpdate = false;
			LaunchUtil.hasMouseListener = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent paramMouseEvent) {
	}

}
