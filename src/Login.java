import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	// private class Account { // 
	// private String id;
	// private String pw;
	//
	// public Account(String id, String pw) {
	// this.id = id;
	// this.pw = pw;
	// }
	//
	// public String getId() {
	// return id;
	// }
	//
	// public String getPw() {
	// return pw;
	// }
	// }

	private JPanel contentPane;
	private JTextField idField;
	private JPasswordField passwordField;
	private JButton btnProceed;
	private JLabel lblUserAuthorisationRequired;
	// private boolean isFileExist; //
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
		// ArrayList<ArrayList<Integer>> tmpintarr = new
		// ArrayList<ArrayList<Integer>>();
		// ArrayList<ArrayList<Date>> tmpdate = new
		// ArrayList<ArrayList<Date>>();
		// Random random = new Random();
		// int maxDataPoints = 30;
		// int maxScore = 100;
		// for (int j = 0; j < 1; j++) {
		// ArrayList<Integer> intarr = new ArrayList<Integer>();
		// ArrayList<Date> datearr = new ArrayList<Date>();
		// for (int i = 0; i < maxDataPoints; i++) {
		// intarr.add((int) (random.nextDouble() * maxScore));
		// datearr.add(new Date());
		// }
		// tmpintarr.add(intarr);
		// tmpdate.add(datearr);
		// }
		// new LineGraph(tmpintarr, tmpdate);
		DataBaseConnect.connect("1234");
		DataBaseConnect.execute("use wms");

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
