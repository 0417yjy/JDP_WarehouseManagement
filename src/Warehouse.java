import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.EventQueue;
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					warehouseGUI frame = new warehouseGUI();
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
	public warehouseGUI() {
		setTitle("Warehouse Management");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		timeLabel = new JLabel("현재시간 : " + new Date().toString());
		timeLabel.setBounds(386, 10, 251, 15);
		contentPane.add(timeLabel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 10, 625, 359);
		contentPane.add(tabbedPane);

		// 재고관리 탭 패널
		stockPanel = new JPanel();
		tabbedPane.addTab("재고관리", null, stockPanel, null);
		stockPanel.setLayout(null);
		String[] stockColumnNames = { "물품명", "재고량", "최대 수용가능수량", "최소 유지재고수량" };
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
		stockScroll.setBounds(0, 0, 620, 265);

		stockPanel.add(stockScroll);

		JButton btnModifyStock = new JButton("재고량 수정");
		btnModifyStock.setBounds(170, 275, 116, 23);
		stockPanel.add(btnModifyStock);

		JButton btnModifyMaxMin = new JButton("최대/최소 수량 편집");
		btnModifyMaxMin.setBounds(333, 275, 173, 23);
		stockPanel.add(btnModifyMaxMin);

		// 주문관리 탭 패널
		transPanel = new JPanel();
		tabbedPane.addTab("주문관리", null, transPanel, null);
		transPanel.setLayout(null);

		String[] transColumnNames = { "창고명", "물품명", "운송량", "운송비", "발송여부" };
		Object[][] transData = { { "A창고", "A", new Integer(50), new Integer(30000), new Boolean(false) } };
		transTable = new JTable(transData, transColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		transScroll = new JScrollPane(transTable);
		transScroll.setBounds(12, 42, 596, 241);
		transPanel.add(transScroll);

		JButton btnReceived = new JButton("수령완료");
		btnReceived.setBounds(486, 294, 122, 23);
		transPanel.add(btnReceived);
		
		JButton btnNew_w = new JButton("새로 주문하기");
		btnNew_w.setSize(140, 23);
		btnNew_w.setLocation(12, 10);
		transPanel.add(btnNew_w);

		JButton btnCancle = new JButton("주문취소");
		btnCancle.setBounds(164, 10, 114, 23);
		transPanel.add(btnCancle);

		// 운송관리 탭 패널
		sendPanel = new JPanel();
		tabbedPane.addTab("운송관리", null, sendPanel, null);
		sendPanel.setLayout(null);

		String[] sendColumnNames = { "가게명", "x", "y", "물품명", "운송량" };
		Object[][] sendData = { { "A가게", "92.5", "45.0", "A", new Integer(50)} };
		sendTable = new JTable(sendData, sendColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		sendScroll = new JScrollPane(sendTable);
		sendScroll.setBounds(12, 10, 596, 274);
		sendPanel.add(sendScroll);
		btnReceived.setBounds(486, 294, 122, 23);

		

		JButton btnSended = new JButton("발송완료");
		btnSended.setSize(114, 23);
		btnSended.setLocation(494, 294);
		btnCancle.setBounds(360, 294, 114, 23);
		sendPanel.add(btnSended);
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) {
			timeLabel.setText("현재시간 : " + new Date().toString());
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
