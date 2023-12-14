package pw.geckonerd.CursedRB.Launcher;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JButton;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1532457610395801457L;
	private JPanel contentPane;
	private JTextPane textPane_1;
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		MainFrame instance = this;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JOptionPane.showMessageDialog(contentPane, "Внимание! Администрация RuBeta не поддерживает сторонние моды (как технически, так и по принципу)!"
				+ "\nИспользуйте лаунчер/загрузчик на свой страх и риск!"
				+ "\n(мы не поддерживаем читы бла бла бла так далее, крч будьте паиньками и не нарушайте правила)", "CursedRB", JOptionPane.WARNING_MESSAGE);
		
		JLabel lblCursedrbLauncher = new JLabel("CursedRB Launcher");
		lblCursedrbLauncher.setBounds(12, 28, 185, 15);
		contentPane.add(lblCursedrbLauncher);
		
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setText("Загрузка...");
		textPane.setBounds(209, 55, 419, 413);
		
		JScrollPane jsp = new JScrollPane(textPane);
		jsp.setBounds(209, 55, 419, 413);
		contentPane.add(jsp);
		
		JLabel label_1 = new JLabel("Вы вошли как:");
		label_1.setBounds(12, 55, 179, 15);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel(Config.cfg.getProperty("username"));
		label_2.setBounds(12, 71, 70, 15);
		contentPane.add(label_2);
		
		JButton button = new JButton("Запуск");
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					// get java
					File java = Config.getJava();
					if(!java.exists())
					{
						JOptionPane.showMessageDialog(contentPane, "Мы не нашли Java! Попробуйте указать корректный путь.", "CursedRB", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// update libraries
					LoaderFrame lf = new LoaderFrame(new Runnable() {
						@Override public void run() {
							try {
								// run game
								CursedRBLauncher.log("Libraries are updated/up-to-date, launching");
								
								// overwrite server info
								try {
									FileOutputStream fos = new FileOutputStream(new File(Config.getWorkDir(), "servers.dat"));
							        byte[] servers = HttpUtil.getServers();
									fos.write(servers, 0, servers.length);
									fos.flush();
									fos.close();
								} catch (Exception e) {
									e.printStackTrace();
									CursedRBLauncher.log("Could not overwrite server.dat!");
								}
								
								// natives
								String natives = Config.getNatives().getAbsolutePath();
								
								// classpath
								String classpath = buildClasspath();
								
								// build cmdline
								ProcessBuilder processBuilder = (new ProcessBuilder(new String[0])).directory(Config.getWorkDir());
							    processBuilder.command(new String[] { 
							          Config.getJava().getAbsolutePath(),
							          "-javaagent:" + new File(Config.getLibraries(), "cursedrbloader.jar"),
							          "-Dorg.lwjgl.librarypath=" + natives,
							          "-Dnet.java.games.input.librarypath=" + natives,
							          "-Dfile.encoding=UTF-8", "-Xmx" + Config.getMaxMemory() + "M",
							          "-cp", classpath,
							          "net.minecraft.client.Minecraft",
							          "--username=" + Config.getUsername(),
							          "--session=" + Config.getSession(), 
							          "--workdir=" + Config.getWorkDir().getAbsolutePath() });
							    // start
							    
						        Process start = processBuilder.inheritIO().start();
						        (new Timer()).schedule(new TimerTask() {
						              public void run() {
						                instance.setVisible(false);
						              }
						            },  1000L);
						        start.waitFor();
							    for (Frame awtFrame : Frame.getFrames())
							    	awtFrame.dispose();
							    CursedRBLauncher.log("finished");
							    System.exit(0);
							} catch(Exception ee3) { CursedRBLauncher.panic(instance, "Не получилось запустить игру!", ee3); }
						}});
					lf.setVisible(true);
				} catch(Exception ee2) { CursedRBLauncher.panic(instance, "Не получилось запустить игру!", ee2); }
			}});
		button.setBounds(12, 98, 185, 25);
		contentPane.add(button);
		
		JButton button_1 = new JButton("Настройки");
		button_1.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				new OptionDialog(instance).setVisible(true);
			}});
		button_1.setBounds(12, 135, 185, 25);
		contentPane.add(button_1);
		
		JButton button_2 = new JButton("Выйти из аккаунта");
		button_2.setBounds(12, 185, 185, 25);
		button_2.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				Config.cfg.remove("token");
				Config.cfg.remove("username");
				try {
					Config.save();
			        File currentJar = new File(CursedRBLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			        String executableName = currentJar.getName().toLowerCase();
			        if (currentJar.isFile() && (executableName.endsWith(".jar") || executableName.endsWith(".exe"))) {
			          Process start = (new ProcessBuilder(new String[] { "java", "-Xmx96M", "-jar", currentJar.getPath(), "restarted" })).inheritIO().start();
			          if (start.isAlive()) {
			            System.exit(0);
			            return;
			          } 
			        } else {
				        CursedRBLauncher.panic(instance, "Не получилось перезапустить лаунчер! (Приложение запущено методом не поддерживающим перезапуск)");
			        }
			      } catch (Exception ee2) {
			        ee2.printStackTrace();
			        CursedRBLauncher.panic(instance, "Не получилось перезапустить лаунчер!", ee2);
			      }
			}});
		contentPane.add(button_2);
		
		textPane_1 = new JTextPane();
		textPane_1.setText("Загрузка...");
		textPane_1.setBounds(12, 353, 185, 115);
		contentPane.add(textPane_1);
		
		JLabel label_3 = new JLabel("Статус сервера");
		label_3.setBounds(12, 326, 179, 15);
		contentPane.add(label_3);
		
		JLabel label = new JLabel("Новости");
		label.setBounds(209, 28, 419, 15);
		contentPane.add(label);
		
		setVisible(true);
		CompletableFuture.supplyAsync(() -> new MineStat("mc.rubeta.net", 25565, 5)).thenAccept(r -> updateServerStats(r.isServerUp(), r.getLatency(), r.getCurrentPlayers(), r.getMaximumPlayers()));
		CompletableFuture.supplyAsync(() -> HttpUtil.getNews()).thenAccept(s -> textPane.setText(s));
	}

	private void updateServerStats(boolean serverUp, long latency, int currentPlayers, int maximumPlayers) {
		if(!serverUp)
		{
			textPane_1.setText("Сервер выключен!");
			return;
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append("Игроков: ");
		sb.append(currentPlayers);
		sb.append("/");
		sb.append(maximumPlayers);
		sb.append("\n");
		
		sb.append("Пинг: ");
		sb.append(latency);
		
		textPane_1.setText(sb.toString());
	}
	
	private String buildClasspath() {
		StringBuilder sb = new StringBuilder();
		for(File lib : Config.getLibraries().listFiles()) {
			sb.append(lib.getAbsolutePath());
			sb.append(File.pathSeparator);
		}
		return sb.toString();
	}
}
