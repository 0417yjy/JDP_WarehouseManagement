/*
 *filename : OrderDetail.java
 *author : team Tic Toc
 *since : 2016.11.26
 *purpose/function : This class shows orders in detail, only for store managers.
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

public class OrderDetail extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnNewButton;
	private final String[] columnNames = { "Product_Name", "Amount" };
	private Object[][] data;
	private ArrayList<Object[]> commandData = new ArrayList<Object[]>();
	private ResultSet rs;
	private String order_no;
	private int rows = 0;

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */
	public OrderDetail(String order_no) throws SQLException {
		this.order_no = order_no;
		setVisible(true);
		setTitle("New Order");
		setBounds(100, 100, 280, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// make table
		data = getOrderData();
		model = new DefaultTableModel(data, columnNames);
		table = new JTable(model);

		// add table to scrollPanel
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(12, 10, 238, 236);
		contentPane.add(scrollPane);

		btnNewButton = new JButton("OK");
		btnNewButton.setBounds(71, 256, 97, 23);
		btnNewButton.addActionListener(this);
		contentPane.add(btnNewButton);
	}

	public ArrayList<Object[]> getCommandData() {
		return commandData;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

	public Object[][] getOrderData() throws SQLException {
		// get inventory info
		rs = DataBaseConnect.execute("select count(*) from ordering_list where order_no=" + order_no);
		if (rs.next()) {
			rows = rs.getInt(1);
		}

		Object[][] productData = new Object[rows][];
		rs = DataBaseConnect.execute("select * from ordering_list where order_no="+order_no);
		for (int i = 0; i < rows; i++) {
			if (rs.next()) {
				String strProduct_Name = null;
				ResultSet pdNameSet = DataBaseConnect
						.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
				// get name of product using product_id
				if (pdNameSet.next())
					strProduct_Name = pdNameSet.getString("product_name");

				// make row data
				Object[] tmpdata = { strProduct_Name, rs.getInt("amount") };
				productData[i] = tmpdata;
			}
		}
		return productData;
	}
}
