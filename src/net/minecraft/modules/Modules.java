package net.minecraft.modules;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jplayground.loaders.ModuleClassLoader;
import net.minecraft.MinecraftSL;
import net.minecraft.Options;

/**
 * Modules ( ModuleUtil ) , Utility for loading external classes ( Mods ,
 * Plugins , Addons ) .
 * 
 * @author Maik
 */
public class Modules {

	private static ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

	public static ArrayList<ModuleBase> getModules() {
		return modules;
	}

	public static void injectModules() {
		injectModules(Options.getModulesDir());
	}

	public static void injectModules(File dir) {
		for (File f : dir.listFiles()) {
			String name = f.getName();
			if (name.toLowerCase().endsWith(".zip")
					|| name.toLowerCase().endsWith(".jar")) {
				injectModule(f);
			}
		}
	}

	public static void injectModule(File f) {
		try {
			System.out.println("Processing zip: " + f.getName());

			String name = "";
			FileInputStream fis = new FileInputStream(f);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			try {
				while ((entry = zis.getNextEntry()) != null && name.equals("")) {
					if (entry.getName().contains("slmod_")
							&& !entry.getName().contains("$")
							&& !entry.isDirectory())
						name = entry.getName();
				}
			} finally {
				zis.close();
			}
			String className = name.replaceAll("/", ".")
					.replaceAll("\\\\", ".").replaceAll(".class", "");

			final ModuleClassLoader loader = new ModuleClassLoader(
					new URL[] { f.toURI().toURL() });
			System.out.println("Starting injecting class " + className + " in "
					+ f);
			try {
				Object oc = loader.loadClass(className);
				Class<?> cl = (Class<?>) oc;
				Object i = cl.newInstance();
				// Class<? extends Object> c = i.getClass();
				if (!(i instanceof ModuleBase)) {
					System.err.println("Class must extend ModuleBase !");
					return;
				}
				ModuleBase iexc = (ModuleBase) i;
				name = iexc.name();
				String version = iexc.version();
				String canrun = iexc.canRun(MinecraftSL.build, modules);
				System.out.println(name + " " + version
						+ "\nCan this Module run ?");
				System.out.println(canrun);
				if (canrun.startsWith("No")) {
					return;
				}
				inject(iexc);
			} finally {
				loader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void inject(ModuleBase c) {
		modules.add(c);
		c.setupOptions();
		c.init();
	}

	public static void stopAll() {
		for (ModuleBase c : modules) {
			c.stop();
			modules.remove(c);
		}
	}

	public static void eject(int i) {
		modules.get(i).stop();
		modules.remove(i);
	}

	public static void eject(ModuleBase c) {
		modules.get(modules.indexOf(c)).stop();
		modules.remove(c);
	}

}
