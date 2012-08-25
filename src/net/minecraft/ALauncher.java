package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *	ALauncher implementation , used for various ALauncher versions implementing InGameMinecraftMenu or unbinding notchcode from MinecraftSL . <p>
 *	
 *	Code design inspired by Markus Notch Persson .
 *	@author Maik
 *	@author Notch ( inspired by )
 */
public abstract class ALauncher extends Applet implements Runnable, AppletStub, MouseListener {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isActive() {
		if (LaunchUtil.context == 0) {
			LaunchUtil.context = -1;
			try {
				if (getAppletContext() != null) LaunchUtil.context = 1; 
			}
			catch (Exception localException) {
			}
		}
		if (LaunchUtil.context == -1) return LaunchUtil.active;
		return super.isActive();
	}
	
	public abstract void init(String username, String lastversion, String ticket, String sessionid);
	
	//FIXME it this correct?
	@SuppressWarnings("deprecation")
	public boolean canPlayOffline() {
		return LaunchUtil.getGameUpdater().canForceOffline() && LaunchUtil.getGameUpdater().canPlayOffline();
	}
	
	@Override
	public void init() {
		if (LaunchUtil.applet != null) {
			LaunchUtil.applet.init();
			return;
		}
		init(getParameter("userName"), getParameter("latestVersion"), getParameter("downloadTicket"), getParameter("sessionId"));
	}
	
	@Override
	public void start() {
		if (LaunchUtil.applet != null) {
			LaunchUtil.applet.start();
			return;
		}
		if (LaunchUtil.gameUpdaterStarted) return;
		
		Object localObject = new Thread() {
			public void run() {
				LaunchUtil.getGameUpdater().run();
				try {
					if (!LaunchUtil.fatalError)
						replace(LaunchUtil.getGameUpdater().createApplet());
				}
				catch (ClassNotFoundException localClassNotFoundException)
				{
					localClassNotFoundException.printStackTrace();
				} catch (InstantiationException localInstantiationException) {
					localInstantiationException.printStackTrace();
				} catch (IllegalAccessException localIllegalAccessException) {
					localIllegalAccessException.printStackTrace();
				}
			}
		};
		((Thread)localObject).setDaemon(true);
		((Thread)localObject).start();
		
		localObject = new Thread() {
			public void run() {
				while (LaunchUtil.applet == null) {
					repaint();
					try {
						Thread.sleep(10L);
					} catch (InterruptedException localInterruptedException) {
						localInterruptedException.printStackTrace();
					}
				}
			}
		};
		((Thread)localObject).setDaemon(true);
		((Thread)localObject).start();
		
		LaunchUtil.gameUpdaterStarted = true;
	}
	
	@Override
	public void stop() {
		if (LaunchUtil.applet != null) {
			LaunchUtil.active = false;
			LaunchUtil.applet.stop();
			return;
		}
	}
	
	@Override
	public void destroy() {
		if (LaunchUtil.applet != null) {
			LaunchUtil.applet.destroy();
			return;
		}
	}
	
	public void replace(Applet newApplet) {
		LaunchUtil.applet = newApplet;
		newApplet.setStub(this);
		newApplet.setSize(getWidth(), getHeight());
		
		setLayout(new BorderLayout());
		add(newApplet, "Center");
		
		newApplet.init();
		LaunchUtil.active = true;
		newApplet.start();
		validate();
	}
	
	@Override
	public void update(Graphics paramGraphics) {
		paint(paramGraphics);
	}
	
	@Override
	public abstract void paint(Graphics paramGraphics);
	
	@Override
	public String getParameter(String paramString) {
		String str = (String)LaunchUtil.customParameters.get(paramString);
		if (str != null) return str; try {
			return super.getParameter(paramString);
		} catch (Exception localException) {
			LaunchUtil.customParameters.put(paramString, null);
		}
		return null;
	}
	
	@Override
	public void appletResize(int w, int h) {
	}
	
	@Override
	public URL getDocumentBase() {
		try {
			return new URL("http://www.minecraft.net/game/");
		} catch (MalformedURLException localMalformedURLException) {
			localMalformedURLException.printStackTrace();
		}
		return null;
	}
	
	@Override
	public abstract void mouseClicked(MouseEvent paramMouseEvent);
	
	@Override
	public abstract void mouseEntered(MouseEvent paramMouseEvent);
	
	@Override
	public abstract void mouseExited(MouseEvent paramMouseEvent);
	
	@Override
	public abstract void mousePressed(MouseEvent paramMouseEvent);
	
	@Override
	public abstract void mouseReleased(MouseEvent paramMouseEvent);
	
	public boolean contains(int a, int b, int c, int d, int e, int f) {
		return (a >= c) && (b >= d) && (a < c + e) && (b < d + f);
	}
	
}
