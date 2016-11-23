import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

class warehouseGUI extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTable stockTable, transTable, sendTable;
	private JScrollPane stockScroll, transScroll, sendScroll;
	private JPanel stockPanel, transPanel, sendPanel;
	private JLabel timeLabel;
	private String id;
	private Warehouse form;

	/**
	 * Create the frame.
	 */
	public warehouseGUI(Warehouse form, String id) {
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

		// 재고관리 탭 패널
		stockPanel = new JPanel();
		tabbedPane.addTab("Manage inventory", null, stockPanel, null);
		stockPanel.setLayout(null);
		String[] stockColumnNames = { "name", "amount", "Maximum capacity", "Maintaining minimum quantity" };
		Object[][] stockData = { { "A", new Integer(50), new Integer(100), new Integer(20) },
				{ "B", new Integer(70), new Integer(150), new Integer(50) } };
		stockTable = new JTable(stockData, stockColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
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

					@Override
					public void makeCommand() {
						String command = "E;";
						command+=id+";";
						command+=this.textField.getText()+";";
						command+=this.textField_1.getText()+";"; 
						form.getOut().println(command);
						//make a Command String, but not send it yet.
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

					@Override
					public void makeCommand() {
						String command = "MX;";
						command+=id+";";
						command+=this.textField.getText()+";";
						command+=this.textField_1.getText()+";"; 
						form.getOut().println(command);
						//make a Command String, but not send it yet.
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

					@Override
					public void makeCommand() {
						String command = "MN;";
						command+=id+";";
						command+=this.textField.getText()+";";
						command+=this.textField_1.getText()+";"; 
						form.getOut().println(command);
						//make a Command String, but not send it yet.
					}
					
				};
			}
		});
		stockPanel.add(btnModifyMin);

		// 주문관리 탭 패널
		transPanel = new JPanel();
		tabbedPane.addTab("Order Management", null, transPanel, null);
		transPanel.setLayout(null);
		String[] transColumnNames = { "Warehouse name", "goods name", "amount of trasportation", "cost of trasportation", "shipping(Y/N)" };
		Object[][] transData = { { "B warehouse", "A", new Integer(50), new Integer(30000), new Boolean(false) } };
		transTable = new JTable(transData, transColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		transScroll = new JScrollPane(transTable);
		transScroll.setBounds(12, 42, 696, 241);
		transPanel.add(transScroll);

		JButton btnReceived = new JButton("Received");
		btnReceived.setSize(232, 23);
		btnReceived.setLocation(636, 294);
		transPanel.add(btnReceived);
		
		JButton btnNew_w = new JButton("New Order");
		btnNew_w.setSize(140, 23);
		btnNew_w.setLocation(12, 10);
		btnNew_w.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Add_popup("New Order", "Product ID", "Product Quantity") {

					@Override
					public void makeCommand() {
						String command = "O;";
						command+=id+";";
						command+=this.textField.getText()+";";
						command+=this.textField_1.getText()+";"; 
						form.getOut().println(command);
						//make a Command String, but not send it yet.
					}
					
				};
			}
		});
		transPanel.add(btnNew_w);
		JButton btnCancle = new JButton("Cancel Order");
		btnCancle.setBounds(164, 10, 114, 23);
		transPanel.add(btnCancle);

		// 운송관리 탭 패널
		sendPanel = new JPanel();
		tabbedPane.addTab("Transprotation Management", null, sendPanel, null);
		sendPanel.setLayout(null);

		String[] sendColumnNames = { "store name", "x", "y", "name of goods", "amount of transportation" };
		Object[][] sendData = { { "A가게", "92.5", "45.0", "A", new Integer(50)} };
		sendTable = new JTable(sendData, sendColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		sendScroll = new JScrollPane(sendTable);
		sendScroll.setBounds(12, 10, 696, 274);
		sendPanel.add(sendScroll);
		btnReceived.setBounds(486, 294, 122, 23);

		

		JButton btnSended = new JButton("Shipped");//발생완료
		btnSended.setSize(114, 23);
		btnSended.setLocation(494, 294);
		btnCancle.setBounds(360, 294, 114, 23);
		sendPanel.add(btnSended);
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) {
			timeLabel.setText("Current time : " + new Date().toString());
		}
	}

}

public class Warehouse extends Store {
	//내부 운송 클래스
	private class Transport {
		private String storeName; //가게명
		private String stockName; //재고명
		private double x, y; //목표지점 좌표
		private int amount; //운송량
		
		public Transport(String storeName, String stockName, double x, double y, int amount) {
			this.stockName = storeName;
			this.stockName = stockName;
			this.x = x;
			this.y = y;
			this.amount = amount;
		}
		
	}
	
	public Warehouse(String id, String password, int kind) throws Exception { //Warehouse 생성자
		super(id, password, kind);
	}
	
	private ArrayList<Transport> transports = new ArrayList<Transport>();
}

