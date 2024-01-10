package pw.geckonerd.CursedRB.Launcher;

import java.awt.Component;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class CursedRBLauncher {
	public static final String versionRBT = "20230821";
	public static final String versionCRB = "0.2";
	
	public static void main(String[] args) {
		try {
			// setup logging
			logfile = new PrintWriter("cursedrblauncher.log", "UTF-8");
			
			// load config
			Config.load();
			
			// login
			login();
			
			// start
			log("Starting MainFrame");
			
			new MainFrame();
			while(true) {
				
			}
		} catch (Exception e) {
			panic(null, new StringBuilder().append("Причина не установлена!").toString(), e);
		}
	}
	
	public static void login() {
		try {
		// check if token is there and that token is working
		boolean shouldRelog = false;
		if(Config.cfg.containsKey("token") && Config.cfg.containsKey("username")) {
			// verify token
			log("Session exists, verifying token");
			AuthResponse ar = HttpUtil.refreshToken(Config.cfg.getProperty("username"), Config.cfg.getProperty("token"));
			if(ar.getError())
			{
				log("Should relog");
				shouldRelog = true;
				if(ar.getErrorType() == ErrorType.SERVER)
				{
					JOptionPane.showMessageDialog(null, "Токен входа больше не актуален! Необходимо снова войти. (Ошибка сервера - " + ar.getErrorS() + ")", "CursedRB", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Произошла ошибка лаунчера при проверке входа токена!. Ошибка: " + PanicFrame.convertException(ar.getErrorE()), "CursedRB", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				log("Token is OK");
				Config.cfg.put("token", ar.getToken());
				Config.save();
			}
		} else {
			shouldRelog = true;
		}
		
		if(shouldRelog) {
			log("Logging in");
			LoginFrame f = new LoginFrame();
			f.setVisible(true);
			while(f.isVisible()) {
				Thread.sleep(20);
			}
		}
		log("Logged in!");
		} catch(Exception ee) { CursedRBLauncher.panic(null, "Другая ошибка входа!", ee);}
	}
	
	static PrintWriter logfile;
	public static void log(String text) {
		System.out.println("[" + java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss").format(java.time.LocalDateTime.now()) + "] " + text);
	}
	public static void panic(Component thing, String reason, Exception e) {
		new PanicFrame(thing, reason, e);
		
		if(thing == null)
			while(true) {}
	}
	public static void panic(Component thing, String reason) {
		panic(thing, reason, null);
	}
}
