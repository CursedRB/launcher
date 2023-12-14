package pw.geckonerd.CursedRB.Launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	static Properties cfg = new Properties();
	static final String path = "cursedrblauncher.properties";
	
	public static void load() throws FileNotFoundException, IOException {
		File f = new File(path);
		f.createNewFile();
		cfg.load(new FileInputStream(f));
	}
	public static void save() throws FileNotFoundException, IOException {
		// save is never called before load
		cfg.store(new FileOutputStream(new File("cursedrblauncher.properties")), null);
	}
	
	public static File getJava() {
		return new File(cfg.getProperty("javabin"), "java");
	}
	
	public static File getNatives() {
		return new File(getWorkDir(), "natives_cursed");
	}
	public static File getLibraries() {
		return new File(getWorkDir(), "libraries_cursed");
	}
	
	public static File getWorkDir() {
		switch(EnumOS.getOS())
		{
			case WINDOWS:
				return new File(System.getProperty("APPDATA"), "rubeta");
			case MACOS:
				return new File(System.getProperty("user.home"), "Library/Application/rubeta");
			case OTHER:
				return new File(System.getProperty("user.home"), "rubeta");
		}
		// should never happen
		return null;
	}
	
	public static String getUsername() {
		return cfg.getProperty("username");
	}

	public static String getSession() {
		return cfg.getProperty("token");
	}

	public static String getMaxMemory() {
		return cfg.getProperty("memory", "512");
	}
}
