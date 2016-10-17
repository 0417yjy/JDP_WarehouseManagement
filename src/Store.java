import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

class storeGUI extends JFrame implements Runnable {
	private JPanel contentPane;
	private JTable stockTable, transTable;
	private JScrollPane stockScroll, transScroll;
	private JPanel stockPanel, transPanel;
	private JLabel timeLabel;

	/**
	 * Create the frame.
	 */
	public storeGUI() {
		setTitle("Store Management");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		timeLabel = new JLabel("접속시간 : " + new Date().toString());
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
		transScroll.setBounds(12, 46, 596, 238);
		transPanel.add(transScroll);

		JButton btnReceived = new JButton("수령완료");
		btnReceived.setBounds(486, 294, 122, 23);
		transPanel.add(btnReceived);

		JButton btnNew = new JButton("새로 주문하기");
		btnNew.setBounds(12, 10, 140, 23);
		transPanel.add(btnNew);

		JButton btnCancle = new JButton("주문취소");
		btnCancle.setBounds(164, 10, 114, 23);
		transPanel.add(btnCancle);
	}

	@Override
	public void run() {
		setVisible(true);
		while (true) {
			timeLabel.setText("현재시간 : " + new Date().toString());
		}
	}
}

public class Store extends Thread { // 창고, 가게의 공통 상위클래스
	// 내부 재고 클래스
	private class Stock {
		private String name;
		private int remain; // 재고량
		private int max; // 최대 수용가능량
		private int min; // 최소 유지재고량
		/* 필드 종료 */

		public Stock(String name, int remain, int max, int min) { // Stock 생성자
			this.name = name;
			this.remain = remain;
			this.max = max;
			this.min = min;
		}

		public void setRemain(int remain) { // 재고량 수정
			this.remain = remain;
		}

		public void setMax(int max) { // 최대 수량 수정
			this.max = max;
		}

		public void setMin(int min) { // 최소 수량 수정
			this.min = min;
		}
	}

	// 내부 주문 클래스
	private class Order {
		private String warehouseName; // 창고 이름
		private String name; // 물품 이름
		private int quantity; // 운송량
		private int cost; // 운송비
		private boolean isSent; // 발송여부
		// private boolean isReceived; //수령여부

		public void setSent(boolean isSent) { // 발송여부 수정
			this.isSent = isSent;
		}

		public Order(String warename, String name, int quantity, int cost, boolean issent) { // Order
																								// 생성자
			this.warehouseName = warename;
			this.name = name;
			this.quantity = quantity;
			this.cost = cost;
			this.isSent = false;
			// this.isReceived = false;
		}
	}

	/* 필드 시작 */
	private double x, y; // 좌표
	private String id; // 고유 넘버
	private String password;
	private String name;
	private storeGUI frame;
	private ArrayList<Stock> stocks = new ArrayList<Stock>(); // 재고 어레이리스트
	private ArrayList<Order> orders = new ArrayList<Order>(); // 주문 어레이리스트
	private Socket socket; // 서버에 연결하기 위한 소켓
	private BufferedReader in; // 서버와 통신하기위한 in 스트림
	private PrintWriter out; // out 스트림
	/* 필드 종료 */

	/* Store 생성자 */
	public Store(String id, String password, int kind) throws Exception { // Store
																			// 생성자
		this.id = id;
		this.password = password;
		socket = new Socket("localhost", 9001); // 소켓 설정(로컬호스트, 포트 9001)
		// 설정한 소켓에서 스트림 생성
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		if (kind == 2) {
			Thread gui = new Thread(new storeGUI());
			this.start();
			gui.start();
		}
		else {
			Thread gui = new Thread(new warehouseGUI());
			this.start();
			gui.start();
		}
	}

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
