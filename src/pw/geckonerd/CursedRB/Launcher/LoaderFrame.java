package pw.geckonerd.CursedRB.Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class LoaderFrame extends JFrame {

	private static final long serialVersionUID = -7907077800798135507L;
	private JPanel contentPane;
	private JLabel label;
	private JLabel lblNull;
	private JProgressBar progressBar;
	private List<LoaderTask> tasks;
	private JProgressBar progressBar_1;

	/**
	 * Create the frame.
	 */
	public LoaderFrame(Runnable r) {
		LoaderFrame instance = this;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		label = new JLabel("Загрузка игры... ");
		label.setBounds(12, 28, 426, 15);
		contentPane.add(label);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 55, 426, 14);
		contentPane.add(progressBar);
		
		lblNull = new JLabel("Задание: (нет)");
		lblNull.setBounds(12, 81, 426, 15);
		contentPane.add(lblNull);
		
		progressBar_1 = new JProgressBar();
		progressBar_1.setBounds(12, 108, 426, 14);
		contentPane.add(progressBar_1);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					tasks = getTasks();
					int ctask = 0;
					for(LoaderTask task : tasks) {
						ctask += 1;
						setTaskMeter(ctask, tasks.size());
						setTask("Сверяю " + task.f.getName() + "...");
						CursedRBLauncher.log("looking at " + task.f.getName());
						// check local version
						if(task.needsUpdate()) {
							CursedRBLauncher.log("update required!");
							setTask("Скачиваю " + task.f.getName() + "...");
							task.download(instance);
						}
					}

					setTaskMeter(1, 1);
					label.setText("Готово!");
					setTask("(нет)");

					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(contentPane, "Не получилось обновить файлы! Игра будет запущена без обновления, что может привести к багам и ошибкам.\nЕсли известно, что существует критический баг, игру лучше не запускать.\nПричина:\n" + PanicFrame.convertException(e), "CursedRB", JOptionPane.ERROR_MESSAGE);
					//System.exit(0);
				}
				try {
					Timer timer = new Timer(2500, new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) {
							setVisible(false);
							r.run();
						}});
					timer.setRepeats(false);
				    timer.start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(contentPane, "Произошла ошибка при запуске игры! Причина:\n" + PanicFrame.convertException(e), "CursedRB", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}).start();	
	}
	
	public void setTaskMeter(int cur, int max) {
		progressBar.setMinimum(0);
		progressBar.setMaximum(max);
		progressBar.setValue(cur);
		label.setText("Загрузка игры... " + cur + "/" + max + " заданий выполнено");
	}
	public void setTaskMeter2(int cur, int max) {
		progressBar_1.setMinimum(0);
		progressBar_1.setMaximum(max);
		progressBar_1.setValue(cur);
	}
	public void setTask(String task) {
		lblNull.setText("Задание: " + task);
	}

	private List<LoaderTask> getTasks() throws Exception {
		List<LoaderTask> tasks = new ArrayList<LoaderTask>();
		for(TaskHolder t : getLibraries())
		{
			CursedRBLauncher.log("Server sent lib " + t.libpath + " ver " + t.ver);
			tasks.add(new LoaderTask(new File(Config.getLibraries(), t.libpath), HttpUtil.cursedrbAPI + "download/" + t.libpath, Integer.parseInt(t.ver), t.folder));
		}
		for(TaskHolder t : getNatives())
		{
			CursedRBLauncher.log("Server sent native " + t.libpath + " ver " + t.ver);
			tasks.add(new LoaderTask(new File(Config.getNatives(), t.libpath), HttpUtil.cursedrbAPI + "download/" + t.libpath, Integer.parseInt(t.ver), t.folder));
		}
		return tasks;
	}
	private List<TaskHolder> getLibraries() throws Exception {
		List<TaskHolder> t = new ArrayList<TaskHolder>();
		HttpURLConnection con = HttpUtil.cookConnection(HttpUtil.cursedrbAPI + "getlibs");
		// read data
		String jdata = HttpUtil.readData(con);
		// convert to tasks
		// data is stored as "lib1name/lib1ver;lib2name/lib2ver..."
		for(String libdata : jdata.split(";", 0)) {
			if(!jdata.contains("/")) break;
			String[] libdata2 = libdata.split("/", 0);
			t.add(new TaskHolder(libdata2[0], libdata2[1], Config.getLibraries()));
		}
		
		return t;
	}
	private List<TaskHolder> getNatives() throws Exception {
		List<TaskHolder> t = new ArrayList<TaskHolder>();
		HttpURLConnection con = HttpUtil.cookConnection(HttpUtil.cursedrbAPI + "getnatives_" + EnumOS.getOS().toString());
		// read data
		String jdata = HttpUtil.readData(con);
		// convert to tasks
		// data is stored as "lib1name/lib1ver;lib2name/lib2ver..."
		for(String libdata : jdata.split(";", 0)) {
			if(!jdata.contains("/")) break;
			String[] libdata2 = libdata.split("/", 0);
			t.add(new TaskHolder(libdata2[0], libdata2[1], Config.getNatives()));
		}
		
		return t;
	}
}

class TaskHolder {
	public String libpath;
	public String ver;
	public File folder;
	public TaskHolder(String lp, String v, File f) {
		libpath = lp;
		ver = v;
		folder = f;
	}
}