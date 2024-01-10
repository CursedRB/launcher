package pw.geckonerd.CursedRB.Launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;

public class OptionDialog extends JDialog {

	private static final long serialVersionUID = -4290448232276256312L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Create the dialog.
	 */
	public OptionDialog(JFrame parent) {
		super(parent, true);
		OptionDialog instance = this;
		setResizable(false);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel label = new JLabel("Путь к папке bin вашей JRE");
			label.setBounds(12, 12, 426, 15);
			contentPanel.add(label);
		}
		
		textField = new JTextField();
		textField.setBounds(12, 39, 426, 19);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel label = new JLabel("Количество выделенной памяти игре");
		label.setBounds(12, 70, 426, 15);
		contentPanel.add(label);
		
		JLabel label_1 = new JLabel("мегабайт");
		label_1.setBounds(99, 99, 305, 15);
		contentPanel.add(label_1);
		
		JSpinner spinner = new JSpinner();
		spinner.setBounds(12, 97, 79, 20);
		contentPanel.add(spinner);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Применить");
				okButton.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) {
						int memory = (int) spinner.getValue();
					    if (memory > 1536)
					      memory = 1536; 
					    if (memory < 96)
					      memory = 96;
					    File java = new File(textField.getText());
						if(!java.exists())
						{
							JOptionPane.showMessageDialog(getContentPane(), "Мы не нашли Java! Попробуйте указать корректный путь.", "CursedRB", JOptionPane.ERROR_MESSAGE);
							return;
						}
						Config.cfg.put("memory", String.valueOf(memory));
						Config.cfg.put("javabin", textField.getText());
						try { Config.save(); } catch(Exception ee) { CursedRBLauncher.panic(instance, "Не удалось сохранить параметры!", ee);}
						setVisible(false);
						dispose();
					}});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Отмена");
				cancelButton.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}});
				buttonPane.add(cancelButton);
			}
		}
		
		spinner.setValue(Integer.parseInt(Config.cfg.getProperty("memory", "512")));
		textField.setText(Config.cfg.getProperty("javabin", "-"));
		
		JLabel lblNewLabel = new JLabel("Версия лаунчера: " + CursedRBLauncher.versionCRB);
		lblNewLabel.setBounds(12, 128, 426, 15);
		contentPanel.add(lblNewLabel);
		
		JLabel lblRubeta = new JLabel("Совместиамя версия RuBeta лаунчера: " + CursedRBLauncher.versionRBT);
		lblRubeta.setBounds(12, 155, 426, 15);
		contentPanel.add(lblRubeta);
	}
}
