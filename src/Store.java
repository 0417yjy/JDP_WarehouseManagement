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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

class storeGUI extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultTableModel stockModel, transModel;
	private JTable stockTable, transTable;
	private JScrollPane stockScroll, transScroll;
	private JPanel stockPanel, transPanel;
	private JLabel timeLabel;
	private String id;
	private Store form;
	private ResultSet rs;
	private final String[] stockColumnNames = { "Product_ID", "Product_Name", "Quantity", "Maximum capacity",
			"Maintaining minimum quantity" };
	private final String[] transColumnNames = { "Warehouse name", "goods name", "amount of trasportation",
			"cost of trasportation", "shipping(Y/N)" };
	private Object[][] stockData, transData;
	private int stockRows, transRows;

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 */

	public storeGUI(Store form, String id) throws SQLException {
		this.form = form;
		this.id = id;
		setTitle("Store Management");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		timeLabel = new JLabel("Current time : " + new Date().toString());

		timeLabel.setBounds(386, 10, 251, 15);
		contentPane.add(timeLabel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 10, 625, 359);
		contentPane.add(tabbedPane);

		// manage inventory tab panel
		stockPanel = new JPanel();
		tabbedPane.addTab("Manage inventory", null, stockPanel, null);
		stockPanel.setLayout(null);

		rs = DataBaseConnect.execute("select count(*) from store_inventory where store_id='" + id + "'");
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
		stockScroll.setBounds(0, 0, 620, 265);

		stockPanel.add(stockScroll);

		JButton btnModifyStock = new JButton("Edit inventory");
		btnModifyStock.setBounds(80, 275, 116, 23);
		btnModifyStock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("Edit Inventory", "Product ID", "Quantity") {
					private static final long serialVersionUID = 1L;

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
					private static final long serialVersionUID = 1L;

					@Override
					public void makeCommand() {
						String command = "MX;";
						command += id + ";";
						command += this.textField.getText() + ";";
						command += this.textField_1.getText() + ";";
						form.getOut().println(command);
						stockTable.repaint();
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
					private static final long serialVersionUID = 1L;

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

		// order managing tab panel
		transPanel = new JPanel();
		tabbedPane.addTab("Order Managing", null, transPanel, null);

		transPanel.setLayout(null);

		Object[][] transData = { { "A Warehouse", "A", new Integer(50), new Integer(30000), new Boolean(false) } };
		transModel = new DefaultTableModel(transData, transColumnNames);
		transTable = new JTable(transModel) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		transScroll = new JScrollPane(transTable);
		transScroll.setBounds(12, 46, 596, 238);
		transPanel.add(transScroll);

		JButton btnReceived = new JButton("Receipt of Completed");
		btnReceived.setBounds(486, 294, 122, 23);
		transPanel.add(btnReceived);

		JButton btnNew = new JButton("New Order");
		btnNew.setBounds(12, 10, 140, 23);
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("New Order", "Product ID", "Product Quantity") {
					private static final long serialVersionUID = 1L;

					@Override
					public void makeCommand() {
						String command = "O;";
						command += id + ";";
						command += this.textField.getText() + ";";
						command += this.textField_1.getText() + ";";
						form.getOut().println(command);
					}

				};
			}
		});
		transPanel.add(btnNew);

		// SHOULD ADD EVENT HANDLER!!
		JButton btnCancle = new JButton("Cancel Order");
		btnCancle.setBounds(164, 10, 114, 23);
		transPanel.add(btnCancle);
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
		rs = DataBaseConnect.execute("select * from store_inventory where store_id='" + id + "'");
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
	
	//getter and setter for field
	public String[] getStockColumnNames() {
		return stockColumnNames;
	}

	public String[] getTransColumnNames() {
		return transColumnNames;
	}

	public Object[][] getStockData() {
		return stockData;
	}

	public void setStockData(Object[][] stockData) {
		this.stockData = stockData;
	}

	public void setTransData(Object[][] transData) {
		this.transData = transData;
	}

	public Object[][] getTransData() {
		return transData;
	}
	
	public int getStockRows() {
		return stockRows;
	}

	public DefaultTableModel getStockModel() {
		return stockModel;
	}

	public DefaultTableModel getTransModel() {
		return transModel;
	}
}

public class Store extends Thread { // super class for warehouse and store
	// class for stock
	private class Stock {
		private String name;
		private int remain; // stock(remain)
		private int max; // max capacity
		private int min; // min quantity
		/* field ends */

		public Stock(String name, int remain, int max, int min) { // Stock constructor
			this.name = name;
			this.remain = remain;
			this.max = max;
			this.min = min;
		}

		public void setRemain(int remain) { // edit stock
			this.remain = remain;
		}

		public void setMax(int max) { // edit max capacity
			this.max = max;
		}

		public void setMin(int min) { // edit min quantity
			this.min = min;
		}
	}

	// inner order class
	private class Order {
		private String warehouseName; // warehouse name
		private String name; // product name
		private int quantity; // quantity
		private int cost; // cost
		private boolean isSent; //state of sending
		// private boolean isReceived;

		public void setSent(boolean isSent) { // edit state of sending
			this.isSent = isSent;
		}

		public Order(String warename, String name, int quantity, int cost, boolean issent) { // Order
																								
			this.warehouseName = warename;
			this.name = name;
			this.quantity = quantity;
			this.cost = cost;
			this.isSent = false;
			// this.isReceived = false;
		}
	}

	/* field starts */
	private double x, y; // coordinate
	private String id; // inherent number
	private String password;
	private String name;
	private storeGUI frame;
	private ArrayList<Stock> stocks = new ArrayList<Stock>(); // stock ArrayList
	private ArrayList<Order> orders = new ArrayList<Order>(); // order ArrayList
	private Socket socket; // socket for connecting server
	private BufferedReader in; // in stream for communicating with server
	private PrintWriter out; // out stream
	private storeGUI storeForm;
	private warehouseGUI warehouseForm;
	private int kind;
	/* field ends */

	/* Store constructor */
	public Store(String id, String password, int kind) throws Exception {
		this.kind = kind;
		this.id = id;
		this.password = password;
		socket = new Socket("localhost", 9001); // set socket(localhost, port 9001)
		// create stream at set socket
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		if (kind == 2) {
			storeForm = new storeGUI(this, id);
			Thread gui = new Thread(storeForm);
			this.start();
			gui.start();
		} else {
			warehouseForm = new warehouseGUI((Warehouse) this, id);
			Thread gui = new Thread(warehouseForm);
			this.start();
			gui.start();
		}
	}

	public PrintWriter getOut() {
		return out;
	}

	@Override
	public void run() {
		String command;
		while (true) {
			try {
				command = in.readLine(); // ream command from server
				System.out.println(command);
				if (command.startsWith("E") || command.startsWith("MX") || command.startsWith("MN"))
					if (kind == 2){
						storeForm.setStockData(storeForm.getInventoryData(storeForm.getStockRows()));
						storeForm.getStockModel().setDataVector(storeForm.getStockData(), storeForm.getStockColumnNames());
					}
					else{
						warehouseForm.setStockData(warehouseForm.getInventoryData(warehouseForm.getStockRows()));
						warehouseForm.getStockModel().setDataVector(warehouseForm.getStockData(), warehouseForm.getStockColumnNames());
					}
				if (command.startsWith("Verifying"))
					out.println(this.id);
				if (command.startsWith("Accepted")) {
					// popup for login success
					JOptionPane.showMessageDialog(frame, "You are connected to server.");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
