package pw.geckonerd.CursedRB.Launcher;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = -8606152556860494840L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	public static LoginFrame instance;
	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		LoginFrame.instance = this;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 377, 242);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("Войти в игру");
		label.setBounds(12, 25, 114, 15);
		contentPane.add(label);
		
		textField = new JTextField();
		textField.setBounds(12, 53, 114, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(12, 78, 114, 19);
		contentPane.add(passwordField);
		
		JLabel label_1 = new JLabel("Логин");
		label_1.setBounds(128, 55, 54, 15);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel("Пароль");
		label_2.setBounds(128, 80, 54, 15);
		contentPane.add(label_2);
		
		JButton button = new JButton("Войти");
		button.setBounds(12, 109, 170, 25);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AuthResponse ar = HttpUtil.login(textField.getText().trim(), String.valueOf(passwordField.getPassword()));
				if(ar.getError())
				{
					switch(ar.getErrorType())
					{
						case SERVER:
							JOptionPane.showMessageDialog(contentPane, "Вход не удался! Сервер вернул ошибку - " + ar.getErrorS(), "CursedRB", JOptionPane.ERROR_MESSAGE);
							break;
						case EXCEPTION:
							JOptionPane.showMessageDialog(contentPane, "Произошла ошибка лаунчера во время входа:\n " + PanicFrame.convertException(ar.getErrorE()), "CursedRB", JOptionPane.ERROR_MESSAGE);
							break;
					}
					return;
				}
				System.out.println("Login success!");
				Config.cfg.put("token", ar.getToken());
				Config.cfg.put("username", textField.getText().trim());
				try { Config.save(); } catch(Exception ee) { CursedRBLauncher.panic(instance, "Не удалось сохранить параметры!", ee);}
				setVisible(false);
			}});
		contentPane.add(button);
	}
}
