package pw.geckonerd.CursedRB.Launcher;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JButton;

public class PanicFrame extends JFrame {

	private static final long serialVersionUID = 6951249155992072654L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public PanicFrame(Component thing, String reason, Exception e) {
		if(thing != null) thing.setVisible(false);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("Упс! Лаунчер крашнулся! Скопируйте это и отправьте на сервер поддержки.");
		label.setBounds(12, 25, 616, 15);
		contentPane.add(label);
		
		JTextPane textPane = new JTextPane();
		textPane.setText(new StringBuilder("*** CursedRBLauncher crashed! ***\n").append(reason).append("\n").append(convertException(e)).append("\n*** no stats here").toString());
		textPane.setBounds(12, 46, 616, 393);
		contentPane.add(textPane);
		
		JButton button = new JButton("Закрыть лаунчер");
		button.setBounds(12, 443, 159, 25);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}});
		contentPane.add(button);
		
		JButton button_1 = new JButton("Скопировать");
		button_1.setBounds(183, 443, 132, 25);
		button_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(textPane.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}});
		contentPane.add(button_1);
		setVisible(true);
	}
	
	public static String convertException(Exception e) {
		if(e == null) return "(no stack trace)";
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		return sw.toString();
	}
}
