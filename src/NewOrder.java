
/*
 *filename : NewOrder.java
 *author : team Tic Toc
 *since : 2016.11.26
 *purpose/function : 
 *
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public abstract class NewOrder extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnNewButton, btnCancel;
	private final String[] columnNames = { "Product_Name", "Amount" };
	private Object[][] data;
	private ArrayList<Object[]> commandData = new ArrayList<Object[]>();
	private ResultSet rs;
	private String id;
	private int rows = 0;
	private boolean isStore;
	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */
	public NewOrder(String id, boolean isStore) throws SQLException {
		this.isStore = isStore;
		this.id = id;
		setVisible(true);
		setTitle("New Order");
		setBounds(100, 100, 280, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// make table
		data = getProductData();
		model = new DefaultTableModel(data, columnNames);
		table = new JTable(model);

		// add table to scrollPanel
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(12, 10, 238, 236);
		contentPane.add(scrollPane);

		btnNewButton = new JButton("OK");
		btnNewButton.setBounds(12, 256, 97, 23);
		btnNewButton.addActionListener(this);
		contentPane.add(btnNewButton);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(153, 256, 97, 23);
		btnCancel.addActionListener(this);
		contentPane.add(btnCancel);
	}

	public ArrayList<Object[]> getCommandData() {
		return commandData;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) // cancel button
			this.dispose(); // close the frame
		else { // ok button
			for (int i = 0; i < rows; i++) {
				// make Request array and add to ArrayList
				// only when order amount is not 0
				if (Integer.parseInt(table.getValueAt(i, 1).toString()) != 0) {
					String pdID = null;
					rs = DataBaseConnect.execute("select product_id from product where product_name='" + data[i][0]+"'");
					try {
						if (rs.next()) {
							pdID = rs.getString(1); // get product_id
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					Object[] tmpdata = { pdID, table.getValueAt(i, 1) };
					commandData.add(tmpdata);
				}
			}
			makeCommand();
			this.dispose();
		}
	}

	abstract void makeCommand();

	public Object[][] getProductData() throws SQLException {
		// get inventory info
		if(isStore)
			rs = DataBaseConnect.execute("select count(*) from store_inventory where store_id=" + id);
		else
			rs = DataBaseConnect.execute("select count(*) from warehouse_inventory where warehouse_id="+id);
		
		if (rs.next()) {
			rows = rs.getInt(1);
		}

		Object[][] productData = new Object[rows][];
		rs = DataBaseConnect.execute("select * from product");
		for (int i = 0; i < rows; i++) {
			if (rs.next()) {
				String strProduct_Name = null;
				ResultSet pdNameSet = DataBaseConnect
						.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
				// get name of product using product_id
				if (pdNameSet.next())
					strProduct_Name = pdNameSet.getString("product_name");

				// make row data
				Object[] tmpdata = { strProduct_Name, new Integer(0) };
				productData[i] = tmpdata;
			}
		}
		return productData;
	}
}
