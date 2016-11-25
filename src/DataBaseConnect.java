import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnect {
	private static Connection con = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	private static String password;

	public static void connect(String password) {
		DataBaseConnect.password = password;
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost", "root", password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet execute(String query) {
		rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (st.execute(query)) {
				rs = st.getResultSet();
			}
			return rs;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static boolean getBoolValue(int tinyint) {
		return tinyint == 0 ? false : true;
	}
}
