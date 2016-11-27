
/*
 *filename : Warehouse.java
 *author : team Tic Toc
 *since : 2016.10.05
 *purpose/function : 
 *
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

class warehouseGUI extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8574013422604253111L;
	private JPanel contentPane;
	private DefaultTableModel stockModel, sendModel;
	private JTable stockTable, sendTable;
	private JScrollPane stockScroll, sendScroll;
	private JPanel stockPanel, sendPanel;
	private JLabel timeLabel;
	private String id;
	private Warehouse form;
	private ResultSet rs;
	private final String[] stockColumnNames = { "Product_ID", "Product_Name", "Quantity", "Maximum capacity",
			"Maintaining minimum quantity" };
	private final String[] sendColumnNames = { "Store_ID", "Address", "Product_ID", "Product_Name", "Amount" };
	private Object[][] stockData, sendData;
	private int stockRows, sendRows;

	public Object[][] getSendData() {
		return sendData;
	}

	public void setSendData(Object[][] sendData) {
		this.sendData = sendData;
	}

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */
	public warehouseGUI(Warehouse form, String id) throws SQLException {
		this.form = form;
		this.id = id;
		setTitle("Warehouse Management");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 755, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		timeLabel = new JLabel("Current Time : " + new Date().toString());
		timeLabel.setBounds(466, 10, 251, 15);
		contentPane.add(timeLabel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 10, 725, 359);
		contentPane.add(tabbedPane);

		// manage inventory tab panel
		stockPanel = new JPanel();
		tabbedPane.addTab("Manage inventory", null, stockPanel, null);
		stockPanel.setLayout(null);

		rs = DataBaseConnect.execute("select count(*) from warehouse_inventory where warehouse_id='" + id + "'");
		if (rs.next()) {
			stockRows = rs.getInt(1);
			stockData = getInventoryData(stockRows);
			stockModel = new DefaultTableModel(stockData, stockColumnNames);
			stockTable = new JTable(stockModel) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}
		stockTable.setFocusable(false);
		stockTable.setRowSelectionAllowed(true);
		stockScroll = new JScrollPane(stockTable);
		stockScroll.setBounds(0, 0, 720, 265);

		stockPanel.add(stockScroll);

		JButton btnModifyStock = new JButton("Edit inventory");
		btnModifyStock.setBounds(80, 275, 116, 23);
		btnModifyStock.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("Edit Inventory", "Product ID", "Quantity") {
					private static final long serialVersionUID = -5900886092579849605L;

					@Override
					public void makeCommand() {
						String command = "E;";
						command += id + ";";
						command += this.textField.getText() + ";";
						command += this.textField_1.getText() + ";";
						form.getOut().println(command);
					}
				};
			}
		});
		stockPanel.add(btnModifyStock);

		JButton btnModifyMax = new JButton("Edit Max Capacity");
		btnModifyMax.setBounds(230, 275, 173, 23);
		btnModifyMax.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("Edit Max Capacity", "Product ID", "Max Capacity") {
					private static final long serialVersionUID = 2787892723973887959L;

					@Override
					public void makeCommand() {
						String command = "MX;";
						command += id + ";";
						command += this.textField.getText() + ";";
						command += this.textField_1.getText() + ";";
						form.getOut().println(command);
					}
				};
			}
		});
		stockPanel.add(btnModifyMax);

		JButton btnModifyMin = new JButton("Edit Min Stock Amount");
		btnModifyMin.setBounds(410, 275, 173, 23);
		btnModifyMin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("Edit Min Quantity", "Product ID", "Min Quantity") {
					private static final long serialVersionUID = -6873839333911506692L;

					@Override
					public void makeCommand() {
						String command = "MN;";
						command += id + ";";
						command += this.textField.getText() + ";";
						command += this.textField_1.getText() + ";";
						form.getOut().println(command);
					}

				};
			}
		});
		stockPanel.add(btnModifyMin);

		// transportation management tab panel
		sendPanel = new JPanel();
		tabbedPane.addTab("Transprotation Management", null, sendPanel, null);
		sendPanel.setLayout(null);
		sendData = getSendingData();
		sendModel = new DefaultTableModel(sendData, sendColumnNames);
		sendTable = new JTable(sendModel) {
			private static final long serialVersionUID = -970427320898634808L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		sendScroll = new JScrollPane(sendTable);
		sendScroll.setBounds(12, 10, 696, 274);
		sendPanel.add(sendScroll);

		JButton btnSended = new JButton("Shipped");// departure success
		btnSended.setSize(114, 23);
		btnSended.setLocation(594, 294);
		btnSended.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rows[] = sendTable.getSelectedRows();
				for (int i = 0; i < rows.length; i++) {
					String command = "S;";
					command += id + ";"; // warehouse_id
					command += sendTable.getValueAt(rows[i], 0) + ";"; // store_id
					command += sendTable.getValueAt(rows[i], 2) + ";"; // product_id
					command += sendTable.getValueAt(rows[i], 4) + ";"; // amount
					form.getOut().println(command);
				}
			}
		});
		sendPanel.add(btnSended);
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) {
			timeLabel.setText("Current time : " + new Date().toString());
		}
	}

	public Object[][] getInventoryData(int columns) throws SQLException {
		Object[][] stockData = new Object[columns][];

		rs = DataBaseConnect.execute("select * from warehouse_inventory where warehouse_id='" + id + "'");
		for (int i = 0; i < stockData.length; i++) {
			if (rs.next()) {
				String strProduct_Name = null;
				ResultSet pdNameSet = DataBaseConnect
						.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
				if (pdNameSet.next())
					strProduct_Name = pdNameSet.getString("product_name");
				Object[] tmpdata = { rs.getString("product_id"), strProduct_Name, rs.getInt("amount"),
						rs.getInt("product_max"), rs.getInt("product_min") };
				stockData[i] = tmpdata;
			}
		}
		return stockData;
	}

	public Object[][] getSendingData() throws SQLException {
		rs = DataBaseConnect.execute("select count(*) from shipping where starting_=" + id + " and shipped=0");
		Object[][] sendData = null;
		if (rs.next()) {
			sendData = new Object[rs.getInt(1)][];
			rs = DataBaseConnect.execute("select * from shipping where starting_=" + id + " and shipped=0");
			for (int i = 0; i < sendData.length; i++) {
				if (rs.next()) {
					String strProduct_Name = null;
					ResultSet pdNameSet = DataBaseConnect
							.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
					// get name of product using product_id
					if (pdNameSet.next())
						strProduct_Name = pdNameSet.getString("product_name");

					String storeAddress = null;
					ResultSet stAddSet = DataBaseConnect
							.execute("select * from store where store_id=" + rs.getString("arrival_"));
					// get store's address
					if (stAddSet.next())
						storeAddress = stAddSet.getString("Address");

					// make row data {Store_ID, Store's Address, Product_ID,
					// Product_Name, Amount}
					Object[] tmpdata = { rs.getString("arrival_"), storeAddress, rs.getString("product_id"),
							strProduct_Name, rs.getInt("amount") };
					sendData[i] = tmpdata;
				}
			}
		}
		return sendData;
	}

	public DefaultTableModel getStockModel() {
		return stockModel;
	}

	public DefaultTableModel getSendModel() {
		return sendModel;
	}

	public Object[][] getStockData() {
		return stockData;
	}

	public void setStockData(Object[][] stockData) {
		this.stockData = stockData;
	}

	public String[] getStockColumnNames() {
		return stockColumnNames;
	}

	public String[] getSendColumnNames() {
		return sendColumnNames;
	}

	public int getStockRows() {
		return stockRows;
	}
}

public class Warehouse extends Store {
	public Warehouse(String id, String password, int kind) throws Exception { // Warehouse Constructor
		super(id, password, kind);
	}
}
