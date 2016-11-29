/*
 *filename : Login.java
 *author : team Tic Toc
 *since : 2016.10.07
 *purpose/function : 
 *
 */
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7921901733199665301L;

	private JPanel contentPane;
	private JTextField idField;
	private JPasswordField passwordField;
	private JButton btnProceed;
	private JLabel lblUserAuthorisationRequired;
	private String inputId; // id and pw(user inserted)
	private String inputPw;
	private String tmpId; // temporarily save id and pw
	private String tmpPw;
	private BufferedReader in;
	private ResultSet rs;

	/**
	 * Launch the application.
	 * 
	 * @throws SQLException
	 */

	public static void main(String[] args) throws SQLException {
		DataBaseConnect.connect("1234");
		DataBaseConnect.execute("use wms");
		
		//insert distance;
//		try {
//			Head.calculateNewStore("2001");
//			Head.calculateNewStore("2002");
//			Head.calculateNewStore("2003");
//			Head.calculateNewStore("2004");
//			Head.calculateNewStore("2005");
//			Head.calculateNewStore("2006");
//			Head.calculateNewStore("2007");
//			Head.calculateNewStore("2008");
//			Head.calculateNewStore("2009");
//			Head.calculateNewStore("2010");
//			Head.calculateNewStore("2011");
//			Head.calculateNewStore("2012");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		EventQueue.invokeLater(new Runnable() { // create login frame
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		setSize(294, 195);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblId = new JLabel("ID");
		lblId.setBounds(32, 64, 20, 15);
		contentPane.add(lblId);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(32, 95, 69, 15);
		contentPane.add(lblPassword);

		idField = new JTextField();
		idField.setBounds(106, 61, 142, 21);
		contentPane.add(idField);
		idField.setColumns(10);

		btnProceed = new JButton("Proceed");
		btnProceed.setBounds(92, 123, 89, 23);
		btnProceed.addActionListener(this);
		contentPane.add(btnProceed);

		lblUserAuthorisationRequired = new JLabel("User authorisation required");
		lblUserAuthorisationRequired.setFont(new Font("Serif", Font.PLAIN, 16));
		lblUserAuthorisationRequired.setBounds(32, 10, 214, 28);
		contentPane.add(lblUserAuthorisationRequired);

		passwordField = new JPasswordField();
		passwordField.setBounds(106, 92, 142, 21);
		contentPane.add(passwordField);
	}

	@SuppressWarnings("deprecation")
	@Override
	// 'Proceed' Button event handler method
	public void actionPerformed(ActionEvent e) {
		inputId = idField.getText();
		inputPw = passwordField.getText();
		try {
			rs = DataBaseConnect.execute("select * from identification");
			while (rs.next()) {
				tmpId = rs.getString(1);
				tmpPw = rs.getString(2);
				if (inputId.equals(tmpId) && inputPw.equals(tmpPw)) {
					if (inputId.equals("admin")) {
						// create head GUI & close login GUI
						this.dispose();
						try {
							new Head();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else if (inputId.equals("server")) {
						this.dispose();
						try {
							new Server(); // server host
						} catch (Exception e1) {
							e1.printStackTrace();
							System.out.println("Server has already been hosted or has error.");
						}
					} else if (!rs.getBoolean("isStore")) {
						// create warehouse GUI
						try {
							new Warehouse(inputId, inputPw, 1);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						this.dispose();
					} else if (rs.getBoolean("isStore")) {
						// create store GUI
						try {
							new Store(inputId, inputPw, 2);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						this.dispose();
					}
				}
			}
			lblUserAuthorisationRequired.setText("Invalid ID");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
