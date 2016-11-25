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
	// 媛� �겢�씪�씠�뼵�듃�뱾�쓽 PrintWriter媛앹껜 愿�由�
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	// 媛� �겢�씪�씠�뼵�듃�뱾�쓽 id 愿�由�
	private static HashSet<String> ids = new HashSet<String>();
	private static ResultSet rs;

	// �꽌踰� �깮�꽦�옄
	public Server() throws Exception {
		ServerSocket ss = new ServerSocket(9001); // �꽌踰꾩냼耳� �룷�듃�뒗 9001
		System.out.println("The server has been hosted.");
		try {
			while (true) {
				// �겢�씪�씠�뼵�듃媛� �뱾�뼱�삱 �븣留덈떎 Handler 媛앹껜 �깮�꽦
				new Handler(ss.accept()).start();
			}
		} finally {
			ss.close();
		}
	}

	private static class Handler extends Thread { // �궡遺� Handler �겢�옒�뒪
		private String id;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) { // �겢�씪�씠�뼵�듃媛� �깮�꽦�릺硫� �엫�쓽�쓽 �냼耳� �븷�떦諛쏆쓬
			this.socket = socket;
		}

		public void run() {
			try {
				// �냼耳볦쓽 in, out �뒪�듃由� 媛앹껜 �깮�꽦
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while (true) {
					out.println("Verifying id..");

					id = in.readLine(); // id �씫�뼱�샂
					if (id == null)
						return;

					synchronized (ids) { // �빐�떦 �븘�씠�뵒媛� �엳�뒗吏� 寃��궗
						if (!ids.contains(id)) {
							ids.add(id);
							break;
						}
					}
				}

				out.println("Accepted");
				System.out.println(id + " has logged in");
				writers.add(out); // �꽌踰꾩뿉 �빐�떦 媛앹껜�쓽 PrintWriter 異붽�

				while (true) {
					String input = in.readLine(); // �뼱�뒓 �븳 �겢�씪�씠�뼵�듃�뿉�꽌 硫붿떆吏�媛� �뱾�뼱�삤硫�
					if (input == null) { // null�씠硫� 由ы꽩
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
							out.println(input+"has completed");
							break;
							
						case "MX": // edit inventory capacity
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("update store_inventory set capacity=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory set capacity=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "MN": // edit inventory quantity
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("update store_inventory set quantity=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory set quantity=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "O": // edit new order
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("update store_inventory amount of new order=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory amount of new order" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "CO": // edit CancelOrder
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("update store_inventory amount of cancelling order=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory amount of cancelling order=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "C": // edit completed
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("updating store_inventory has been completed. Amount=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("updating warehouse_inventory has been completed. Amount=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "S": // edit shipping
							// check if this member is store
							rs = DataBaseConnect
									.execute("select isStore from identification where id='" + commands[1] + "'");
							if (rs.next()) {
								isStore = rs.getBoolean(1);
							}
							if (isStore) {
								DataBaseConnect.update("shipping info. Amount=" + commands[3]
										+ " From.store_id" + commands[1] +" To." + commands[2] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("shipping info. Amount=" + commands[3]
										+ " From.warehouse_id" + commands[1] +" To." + commands[2] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
							
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// �겢�씪�씠�뼵�듃媛� 醫낅즺�릱�쓣 �븣 �빐�떦 �겢�씪�씠�뼵�듃�쓽 PrintWriter 媛앹껜�� id �궘�젣
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