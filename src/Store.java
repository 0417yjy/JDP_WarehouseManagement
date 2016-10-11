import java.util.ArrayList;///////////////////

public class Store { //창고, 가게의 공통 상위클래스
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
	
	//내부 주문 클래스
	private class Order {
		private String warehouseName; //창고 이름
		private String name; //물품 이름
		private int quantity; //운송량
		private int cost; //운송비
		private boolean isSent; //발송여부 
		//private boolean isReceived; //수령여부
		/* 필드 종료 */
		
		public void setSent(boolean isSent) { //발송여부 수정
			this.isSent = isSent;
		}

		public Order(String warename, String name, int quantity, int cost, boolean issent) { //Order 생성자
			this.warehouseName = warename;
			this.name = name;
			this.quantity = quantity;
			this.cost = cost;
			this.isSent = false;
			//this.isReceived = false;
		}
	}
	
	private double x, y; // 좌표
	private String id; // 고유 넘버
	private String password;
	private String name;
	private ArrayList<Stock> stocks = new ArrayList<Stock>(); //재고 어레이리스트
	private ArrayList<Order> orders = new ArrayList<Order>(); //주문 어레이리스트
	
	public Store(double x, double y, String id, String password, String name) { //Store 생성자
		this.x = x;
		this.y = y;
		this.id = id;
		this.password = password;
		this.name = name;
	}
	
}
