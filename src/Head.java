import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
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

class warehouseheadGUI extends JFrame implements Runnable, ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableWarehouse;
	private JTable tableStore;
	private JTable tableRequest;
	public JLabel lbTime;
	private JButton btnWarehouseDetail, btnStoreDetail;
	private JButton btnEachProcess, btnAllProcess;
	private ResultSet rs;

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

		String[] columnNames_request = { "Store", "article", "amount", "accept" };
		Object[][] data_request = { { "A", "A", new Integer(150), "Pending" },
				{ "B", new Integer(70), new Integer(200), "Confirmed" } };
		tableRequest = new JTable(data_request, columnNames_request);

		JScrollPane scrollRequest = new JScrollPane(tableRequest);
		scrollRequest.setBounds(12, 249, 756, 155);
		contentPane.add(scrollRequest);

		btnEachProcess = new JButton("Individual treatment");
		btnEachProcess.setBounds(615, 407, 150, 23);
		contentPane.add(btnEachProcess);

		btnAllProcess = new JButton("Batch processing");
		btnAllProcess.setBounds(440, 407, 150, 23);
		contentPane.add(btnAllProcess);
		// end of making request table
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) { // update frame
			lbTime.setText("Current time : " + new Date().toString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnWarehouseDetail) {

		} else if (e.getSource() == btnStoreDetail) {

		} else if (e.getSource() == btnEachProcess) {

		} else if (e.getSource() == btnAllProcess) {

		}
	}

}

public class Head extends Thread {
	// internal request class
	private class Request {
		private String storeName;
		private String stockName;
		private int amount;
		private boolean confirmed; // 주문 접수 여부
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
	private ArrayList<Request> requests = new ArrayList<Request>(); // 주문 어레이리스트
	private Socket socket; // 서버에 연결하기 위한 소켓
	private BufferedReader in; // 서버와 통신하기위한 in 스트림
	private PrintWriter out; // out 스트림
	private BufferedReader fin; // File input stream
	/* End of field */

	public void showDetail(Warehouse obj) {

	}

	public void showDetail(Store obj) {

	}

	public void calculate(Request r) {

		/* calculating ... */

		r.confirmed = true;
	}

	/* 헤드 생성자 */
	public Head() throws Exception {
		socket = new Socket("localhost", 9001); // 소켓 설정(로컬호스트, 포트 9001)
		// 설정한 소켓에서 스트림 생성
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		// fin = new BufferedReader(new FileReader("data/admin.txt"));
		// while (fin.ready()) {
		// String command = fin.readLine();
		// String[] tokens = command.split(";");
		// switch (tokens[0]) {
		// case "W":
		// String[] warehouseInfo = { tokens[1], tokens[2], tokens[3], tokens[4]
		// };
		// break;
		// case "S":
		// break;
		// case "R": // make Request object and add to 'requests' ArrayList.
		// Request r = new Request(tokens[1], tokens[2],
		// Integer.parseInt(tokens[3]),
		// tokens[4] == "C" ? true : false);
		// requests.add(r);
		// break;
		// }
		// }

		Thread gui = new Thread(new warehouseheadGUI());
		this.start();
		gui.start();
	}

	// 헤드 스레드 작업
	@Override
	public void run() {
		String command;
		while (true) {
			try {
				command = in.readLine(); // 서버에서 커맨드 읽어옴
				System.out.println(command);
				if (command.startsWith("Verifying"))
					out.println(this.id);
				if (command.startsWith("Accepted")) {
					// 로그인 성공 메시지 팝업
					JOptionPane.showMessageDialog(frame, "You are connected to server.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
