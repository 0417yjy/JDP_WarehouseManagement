import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public abstract class AddProduct extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private ResultSet rs;
	private String[] productIds;
	private String[] productNames;
	private ArrayList<String> productIdList = new ArrayList<String>();
	private ArrayList<String> productNameList = new ArrayList<String>();
	private JButton btnOk, btnCancel;
	private JLabel lblProductNameContent;
	private JComboBox productBox;

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */
	public AddProduct() throws SQLException {
		setVisible(true);
		setTitle("Add Product");
		setBounds(100, 100, 275, 243);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblProductId = new JLabel("Product ID");
		lblProductId.setBounds(12, 10, 66, 15);
		contentPane.add(lblProductId);

		// get product list from database
		rs = DataBaseConnect.execute("select * from product");
		while (rs.next()) {
			productIdList.add(rs.getString("Product_ID"));
			productNameList.add(rs.getString("product_name"));
		}
		// make string array
		productIds = new String[productIdList.size()];
		productNames = new String[productNameList.size()];

		for (int i = 0; i < productIdList.size(); i++) {
			productIds[i] = productIdList.get(i);
			productNames[i] = productNameList.get(i);
		}

		lblProductNameContent = new JLabel("[ProductName]");
		lblProductNameContent.setBounds(156, 38, 90, 15);
		contentPane.add(lblProductNameContent);

		productBox = new JComboBox(productIds);
		productBox.setBounds(146, 7, 100, 21);
		productBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lblProductNameContent.setText(productNames[productBox.getSelectedIndex()]);
			}
		});
		contentPane.add(productBox);

		JLabel lblProductName = new JLabel("Product Name");
		lblProductName.setBounds(12, 35, 90, 15);
		contentPane.add(lblProductName);

		JLabel lblQuantity = new JLabel("Quantity");
		lblQuantity.setBounds(12, 66, 57, 15);
		contentPane.add(lblQuantity);

		textField = new JTextField();
		textField.setBounds(130, 63, 116, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblMaxCapacity = new JLabel("Max Capacity");
		lblMaxCapacity.setBounds(12, 100, 90, 15);
		contentPane.add(lblMaxCapacity);

		textField_1 = new JTextField();
		textField_1.setBounds(130, 97, 116, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblMinMain = new JLabel("Min Stock Amount");
		lblMinMain.setBounds(12, 131, 116, 15);
		contentPane.add(lblMinMain);

		textField_2 = new JTextField();
		textField_2.setBounds(130, 128, 116, 21);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		btnOk = new JButton("OK");
		btnOk.setBounds(12, 175, 97, 23);
		btnOk.addActionListener(this);
		contentPane.add(btnOk);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(149, 175, 97, 23);
		btnCancel.addActionListener(this);
		contentPane.add(btnCancel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) // if the pressed button is "Cancel"
			this.dispose(); // close the frame
		else if (e.getSource() == btnOk) { // if the pressed button is "OK"
			this.makeCommand(); // make a command string
			this.dispose();
		}
	}
	
	public JTextField getTextField() {
		return textField;
	}

	public JTextField getTextField_1() {
		return textField_1;
	}

	public JTextField getTextField_2() {
		return textField_2;
	}
	
	public JComboBox getProductBox() {
		return productBox;
	}

	abstract void makeCommand();
}
