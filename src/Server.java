import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class Server {
	// 각 클라이언트들의 PrintWriter객체 관리
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	// 각 클라이언트들의 id 관리
	private static HashSet<String> ids = new HashSet<String>();
	private static ResultSet rs;

	// 서버 생성자
	public Server() throws Exception {
		ServerSocket ss = new ServerSocket(9001); // 서버소켓 포트는 9001
		System.out.println("The server has been hosted.");
		try {
			while (true) {
				// 클라이언트가 들어올 때마다 Handler 객체 생성
				new Handler(ss.accept()).start();
			}
		} finally {
			ss.close();
		}
	}

	private static class Handler extends Thread { // 내부 Handler 클래스
		private String id;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) { // 클라이언트가 생성되면 임의의 소켓 할당받음
			this.socket = socket;
		}

		public void run() {
			try {
				// 소켓의 in, out 스트림 객체 생성
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while (true) {
					out.println("Verifying id..");

					id = in.readLine(); // id 읽어옴
					if (id == null)
						return;

					synchronized (ids) { // 해당 아이디가 있는지 검사
						if (!ids.contains(id)) {
							ids.add(id);
							break;
						}
					}
				}

				out.println("Accepted");
				System.out.println(id + " has logged in");
				writers.add(out); // 서버에 해당 객체의 PrintWriter 추가

				while (true) {
					String input = in.readLine(); // 어느 한 클라이언트에서 메시지가 들어오면
					if (input == null) { // null이면 리턴
						return;
					}
					for (PrintWriter writer : writers) { // do jobs
						String[] commands = input.split(";");
						boolean isStore = false;
						switch (commands[0]) {
						case "E": // edit inventory
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("update store_inventory set amount=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory set amount=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// 클라이언트가 종료됐을 때 해당 클라이언트의 PrintWriter 객체와 id 삭제
				if (id != null)
					ids.remove(id);
				if (out != null) {
					writers.remove(out);
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}