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
	// set of each client's printwriters
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	// set of client's ids
	private static HashSet<String> ids = new HashSet<String>();
	private static ResultSet rs;

	// Server Constructor
	public Server() throws Exception {
		ServerSocket ss = new ServerSocket(9001); // port number is 9001
		System.out.println("The server has been hosted.");
		try {
			while (true) {
				// make handler object when client access.
				new Handler(ss.accept()).start();
			}
		} finally {
			ss.close();
		}
	}

	private static class Handler extends Thread { // Handler class
		private String id;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) { // handler constructor
			this.socket = socket;
		}

		public void run() {
			try {
				// add client's id and printwriter
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while (true) {
					out.println("Verifying id..");

					id = in.readLine(); // id reading
					if (id == null)
						return;

					synchronized (ids) { // add id when it's ok
						if (!ids.contains(id)) {
							ids.add(id);
							break;
						}
					}
				}

				out.println("Accepted");
				System.out.println(id + " has logged in");
				writers.add(out); // add printwriter

				while (true) {
					String input = in.readLine(); // when client send message to server
					if (input == null) { 
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
								DataBaseConnect.update("update store_inventory set product_max=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory set product_max=" + commands[3]
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
								DataBaseConnect.update("update store_inventory set product_min=" + commands[3]
										+ " where store_id=" + commands[1] + " and product_id=" + commands[2]);
							} else {
								DataBaseConnect.update("update warehouse_inventory set product_min=" + commands[3]
										+ " where warehouse_id=" + commands[1] + " and product_id=" + commands[2]);
							}
							out.println(input+"has completed");
							break;
							
						case "O": // edit new order
							
							break;
							
						case "CO": // edit CancelOrder
							
							break;
							
						case "C": // edit completed
							// check if this member is store
							
							break;
							
						case "S": // edit shipping
							// check if this member is store
							
							break;
							
							
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// 
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