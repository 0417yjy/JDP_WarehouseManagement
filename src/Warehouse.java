import java.util.ArrayList;

public class Warehouse extends Store {
	//내부 운송 클래스
	private class Transport {
		private String storeName; //가게명
		private String stockName; //재고명
		private double x, y; //목표지점 좌표
		private double address;
		private int amount; //운송량
		
		public Transport(String storeName, String stockName, double x, double y, int amount) {
			this.stockName = storeName;
			this.stockName = stockName;
			this.x = x;
			this.y = y;
			this.amount = amount;
		}
		
	}
	
	public Warehouse(double x, double y, String id, String password, String name) { //Warehouse 생성자
		super(x, y, id, password, name);
	}
	
	private ArrayList<Transport> transports = new ArrayList<Transport>();
}
