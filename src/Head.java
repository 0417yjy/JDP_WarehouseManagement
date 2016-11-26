
/*
 *filename : Head.java
 *author : team Tic Toc
 *since : 2016.10.10
 *purpose/function : Show Head GUI.
 *
 */
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

class warehouseheadGUI extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableWarehouse;
	private JTable tableStore;
	private JTable tableRequest;
	private DefaultTableModel requestModel;
	public JLabel lbTime;
	private JButton btnWarehouseDetail, btnStoreDetail;
	private JButton btnEachProcess, btnAllProcess;
	private ResultSet rs;
	private final String[] columnNames_request = { "Order_no", "Store_ID", "Ordering_Date" };
	private Object[][] requestData;

	public warehouseheadGUI() throws SQLException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(822, 479);
		setTitle("Head Client");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbTitle = new JLabel("Warehouse Head Management");
		lbTitle.setFont(new Font("Serif", Font.BOLD, 16));
		lbTitle.setBounds(12, 10, 256, 19);
		contentPane.add(lbTitle);

		lbTime = new JLabel("Current time : " + new Date().toString());
		lbTime.setBounds(386, 10, 251, 15);
		contentPane.add(lbTime);

		// make warehouse table
		JLabel lblWarehouseInfo = new JLabel("Warehouse Info");
		lblWarehouseInfo.setFont(new Font("Serif", Font.PLAIN, 13));
		lblWarehouseInfo.setBounds(12, 36, 99, 16);
		contentPane.add(lblWarehouseInfo);

		btnWarehouseDetail = new JButton("Show Detail");
		btnWarehouseDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = tableWarehouse.getSelectedRow();
				String targetID = (String) tableWarehouse.getValueAt(selectedRow, 0);
				try {
					new LineGraph(targetID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		btnWarehouseDetail.setBounds(280, 193, 120, 23);
		contentPane.add(btnWarehouseDetail);

		rs = DataBaseConnect.execute("select row from table_rows where table_name='warehouse'");

		String[] columnNames_warehouse = { "Warehouse_ID", "Latitude", "Longitude", "Address" };
		if (rs.next()) {
			Object[][] data_warehouse = new Object[rs.getInt("row")][];
			rs = DataBaseConnect.execute("select * from warehouse");
			for (int i = 0; i < data_warehouse.length; i++) {
				if (rs.next()) {
					Object[] tmpData = { rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4) };
					data_warehouse[i] = tmpData;
				}
			}
			tableWarehouse = new JTable(data_warehouse, columnNames_warehouse) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}
		JScrollPane scrollWarehouse = new JScrollPane();
		scrollWarehouse.setBounds(12, 62, 388, 121);
		contentPane.add(scrollWarehouse);
		scrollWarehouse.setViewportView(tableWarehouse);
		// end of making warehouse table

		// make store table
		JLabel lblStoreInfo = new JLabel("Store Info");
		lblStoreInfo.setFont(new Font("Serif", Font.PLAIN, 13));
		lblStoreInfo.setBounds(425, 35, 59, 16);
		contentPane.add(lblStoreInfo);

		rs = DataBaseConnect.execute("select row from table_rows where table_name='store'");
		if (rs.next()) {
			String[] columnNames_store = { "Store_ID", "Latitude", "Longitude", "Address" };
			Object[][] data_store = new Object[rs.getInt("row")][];
			rs = DataBaseConnect.execute("select * from store");
			for (int i = 0; i < data_store.length; i++) {
				if (rs.next()) {
					Object[] tmpData = { rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4) };
					data_store[i] = tmpData;
				}
			}
			tableStore = new JTable(data_store, columnNames_store) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}

		JScrollPane scrollStore = new JScrollPane();
		scrollStore.setBounds(425, 62, 343, 121);
		contentPane.add(scrollStore);
		scrollStore.setViewportView(tableStore);

		btnStoreDetail = new JButton("Show Detail");
		btnStoreDetail.setBounds(648, 193, 120, 23);
		btnStoreDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = tableStore.getSelectedRow();
				String targetID = (String) tableStore.getValueAt(selectedRow, 0);
				try {
					new LineGraph(targetID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		contentPane.add(btnStoreDetail);
		// end of making store table

		// make request table
		JLabel lblRequest = new JLabel("Request");
		lblRequest.setFont(new Font("Serif", Font.PLAIN, 14));
		lblRequest.setBounds(12, 222, 54, 17);
		contentPane.add(lblRequest);

		requestData = getRequestingData();
		requestModel = new DefaultTableModel(requestData, columnNames_request);
		tableRequest = new JTable(requestModel);

		JScrollPane scrollRequest = new JScrollPane(tableRequest);
		scrollRequest.setBounds(12, 249, 756, 155);
		contentPane.add(scrollRequest);

		btnEachProcess = new JButton("Process Selected");
		btnEachProcess.setBounds(615, 407, 150, 23);
		contentPane.add(btnEachProcess);

		btnAllProcess = new JButton("Process All");
		btnAllProcess.setBounds(440, 407, 150, 23);
		contentPane.add(btnAllProcess);
		// end of making request table
	}

	public Object[][] getRequestingData() throws SQLException {
		rs = DataBaseConnect.execute("select count(*) from ordering");
		Object[][] requestData = null;
		if (rs.next()) {
			int rows = rs.getInt(1);
			requestData = new Object[rows][];
			rs = DataBaseConnect.execute("select * from ordering");
			for (int i = 0; i < rows; i++) {
				if (rs.next()) {
					// make row data
					Object[] tmpdata = { rs.getString("order_no"), rs.getString("store_id"), rs.getDate("order_date") };
					requestData[i] = tmpdata;
				}
			}
		}
		return requestData;
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) { // update frame
			lbTime.setText("Current time : " + new Date().toString());
		}
	}
}

public class Head extends Thread {
	// internal request class
	private class Request {
		private String storeName;
		private String stockName;
		private int amount;
		private boolean confirmed; // check order receiving
		private String confirmTime; // Save time log at confirmed

		public Request(String storeName, String stockName, int amount, boolean confirmed) {
			this.storeName = storeName;
			this.stockName = stockName;
			this.amount = amount;
			this.confirmed = confirmed;
		}
	}

	/* Field start */
	private String id = "admin";
	private String password;
	private warehouseheadGUI frame;
	private Object[][] warehouses; // array of array which is used to show the
									// list of warehouses.
	private Object[][] stores; // array of array which is used to show the list
								// of stores.
	private ArrayList<Request> requests = new ArrayList<Request>(); // order
																	// Arraylist
	private Socket socket; // socket for connecting server
	private BufferedReader in; // in stream for communicate with server
	private PrintWriter out; // out stream
	private BufferedReader fin; // File input stream
	/* End of field */

	/* head constructor */
	public Head() throws Exception {
		socket = new Socket("localhost", 9001); // setting socket(localhost,
												// port 9001)
		// create stream at set socket
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		Thread gui = new Thread(new warehouseheadGUI());
		this.start();
		gui.start();
	}

	public static void calculateNewStore(String storeID) throws IOException, SQLException {
		ResultSet rs;
		double storeLa = 0, storeLo = 0, wareLa, wareLo;
		String[] queries;
		int warehouses = 0;
		rs = DataBaseConnect.execute("select count(*) from warehouse");
		if (rs.next())
			warehouses = rs.getInt(1);

		rs = DataBaseConnect.execute("select * from store where store_id=" + storeID);
		if (rs.next()) {
			storeLa = rs.getDouble("latitude");
			storeLo = rs.getDouble("longitude");
		}

		rs = DataBaseConnect.execute("select * from warehouse");
		queries = new String[warehouses];
		for (int i = 0; i < warehouses; i++) {
			try {
				if (rs.next()) {
					wareLa = rs.getDouble("latitude");
					wareLo = rs.getDouble("longitude");
					String warehouseID = rs.getString("warehouse_id");
					String resultStr = new GoogMatrixRequest(wareLa, wareLo, storeLa, storeLo).calculate();
					String[] results = resultStr.split("\n|:|\\{|\\}");
					double distance = 0;
					if (results[20].contains("km")) {
						String[] tmp = results[20].split(" ");
						distance = Double.parseDouble(tmp[0].substring(1)) * 1000;
					} else
						distance = Double.parseDouble(results[20]);
					queries[i] = "insert into distance values ('" + storeID + "','" + warehouseID + "','" + distance
							+ "')";
				}
			} catch (NumberFormatException e) {
				queries[i] = null;
				e.printStackTrace();
			} catch (StringIndexOutOfBoundsException e) {
				queries[i] = null;
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < warehouses; i++)
			if (queries[i] != null)
				DataBaseConnect.update(queries[i]);
	}
	
	public static void calculateNewWarehouse(String warehouseID) throws IOException, SQLException {
		ResultSet rs;
		double storeLa = 0, storeLo = 0, wareLa = 0, wareLo = 0;
		String[] queries;
		int stores = 0;
		rs = DataBaseConnect.execute("select count(*) from store");
		if (rs.next())
			stores = rs.getInt(1);

		rs = DataBaseConnect.execute("select * from warehouse where warehouse_id=" + warehouseID);
		if (rs.next()) {
			wareLa = rs.getDouble("latitude");
			wareLo = rs.getDouble("longitude");
		}

		rs = DataBaseConnect.execute("select * from warehouse");
		queries = new String[stores];
		for (int i = 0; i < stores; i++) {
			try {
				if (rs.next()) {
					storeLa = rs.getDouble("latitude");
					storeLo = rs.getDouble("longitude");
					String storeID = rs.getString("warehouse_id");
					String resultStr = new GoogMatrixRequest(wareLa, wareLo, storeLa, storeLo).calculate();
					String[] results = resultStr.split("\n|:|\\{|\\}");
					double distance = 0;
					if (results[20].contains("km")) {
						String[] tmp = results[20].split(" ");
						distance = Double.parseDouble(tmp[0].substring(1)) * 1000;
					} else
						distance = Double.parseDouble(results[20]);
					queries[i] = "insert into distance values ('" + storeID + "','" + warehouseID + "','" + distance
							+ "')";
				}
			} catch (NumberFormatException e) {
				queries[i] = null;
				e.printStackTrace();
			} catch (StringIndexOutOfBoundsException e) {
				queries[i] = null;
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < stores; i++)
			if (queries[i] != null)
				DataBaseConnect.update(queries[i]);
	}

	// head thread work
	@Override
	public void run() {
		String command;
		while (true) {
			try {
				command = in.readLine(); // read command from server
				System.out.println(command);
				if (command.startsWith("Verifying"))
					out.println(this.id);
				if (command.startsWith("Accepted")) {
					// popup of login success
					JOptionPane.showMessageDialog(frame, "You are connected to server.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
