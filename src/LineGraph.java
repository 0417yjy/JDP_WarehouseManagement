
/*
 *filename : LineGraph.java
 *author : team Tic Toc
 *since : 2016.11.16
 *purpose/function : Linegraph can be seen in head. Users can see graphs of store or warehouse. The graph shows fluctuation of 1 month's stock.
 *In graph, when mouse cursor moves to a point of graph, details will be shown.
 *
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class LineGraph extends Panel implements ActionListener, MouseMotionListener {

	/*****************************************************************
	 * Class Name : LineGraph Description : Make a frame which contains a
	 * graphic line chart. Parameters : String id
	 *****************************************************************/

	private static final long serialVersionUID = 1L;
	private int width = 800; // width of the frame
	private int height = 600; // height of the frame
	private int padding = 40; // padding of whole panel
	private int labelPadding = 25; // padding of each label
	// Array of pre-determined colors
	private Color[] lineColor = { Color.red, Color.blue, Color.cyan, Color.darkGray, Color.green, Color.magenta,
			Color.ORANGE, Color.pink };
	private Color pointColor = Color.black; // Color of points
	// Color of grid lines
	private Color gridColor = Color.lightGray;
	private Point mousePoint; // Point that indicate the hovering point
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4; // radius of point
	private int numberYDivisions = 10; // num of grid lines
	private int productRows = 0;
	private boolean hoveringPoint = false;
	private ArrayList<ArrayList<Integer>> values; // ArrayList of values
	private ArrayList<Date> dates; // ArrayList of dates
	private ArrayList<ArrayList<Point>> graphPoints; // ArrayList of each point
	private ArrayList<String> product_ids; // ArrayList of each product's id

	private ResultSet rs;
	private Font font = new Font("Serif", Font.BOLD, 20);
	private int pointIndex, arrayIndex;
	private JButton exportToXLS;
	Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	// Data of each store / warehouse
	private String id = "[id]";
	private double cash;
	private String address = "[address]";
	private double latitude, longitude;
	private double totalValue;
	private String owner = "[owner]";
	private String contact = "[contact]";
	private boolean isStore;

	@SuppressWarnings("deprecation")
	public LineGraph(String id) throws SQLException {
		this.id = id;
		values = new ArrayList<ArrayList<Integer>>();
		dates = new ArrayList<Date>();
		product_ids = new ArrayList<String>();
		rs = DataBaseConnect.execute("select * from identification where Id='" + id + "'");
		// get this id is store or warehouse
		if (rs.next())
			isStore = rs.getBoolean("isStore");

		if (isStore) {
			rs = DataBaseConnect.execute("select * from store where store_id='" + id + "'");
			// get information of this store
			if (rs.next()) {
				this.latitude = rs.getDouble("latitude");
				this.longitude = rs.getDouble("longitude");
				this.cash = rs.getDouble("cash");
				this.address = rs.getString("address");
				this.owner = rs.getString("owner");
				this.contact = rs.getString("contact");
			}

			// add each product's id into list
			rs = DataBaseConnect.execute("select * from store_inventory where store_id=" + id);
			while (rs.next())
				product_ids.add(rs.getString("product_id"));

			// get inventory's total value
			for (int i = 0; i < product_ids.size(); i++) {
				double unit_price = 0;
				rs = DataBaseConnect.execute("select * from product where product_id=" + product_ids.get(i));
				if (rs.next())
					unit_price = rs.getDouble("unit_price");
				rs = DataBaseConnect.execute(
						"select * from store_inventory where store_id=" + id + " and product_id=" + product_ids.get(i));
				if (rs.next())
					totalValue += unit_price * rs.getInt("amount");
			}

			// get number of kinds of products in inventory
			productRows = product_ids.size();

			// make ArrayList of each products
			for (int i = 0; i < productRows; i++)
				values.add(new ArrayList<Integer>());

			// add dates into list(1st day to Today)
			for (int i = 1; i <= new Date().getDate(); i++)
				dates.add(new Date(new Date().getYear(), new Date().getMonth(), i));

			// add values into each product's list
			for (int i = 0; i < productRows; i++) {
				rs = DataBaseConnect.execute("select * from log where member_id=" + id + " and product_id="
						+ product_ids.get(i) + " order by change_date");
				int date = 1;
				// get value list with log
				while (rs.next()) {
					int value = 0;
					Date logDateObj = rs.getDate("change_date");
					String dateStr = dateFormat.format(logDateObj);
					ResultSet tmpRs = DataBaseConnect.execute("select * from log where member_id=" + id
							+ " and product_id=" + product_ids.get(i) + " and change_date='" + dateStr + "'");
					// get this date's recent log
					while (tmpRs.next())
						value = tmpRs.getInt("changed_result");

					int logDate = logDateObj.getDate();
					for (; date <= logDate; date++) {
						// add values into list
						values.get(i).add(value);
					}
				}
				// if there is no log on today, set to quantity of now
				if (date <= new Date().getDate()) {
					rs = DataBaseConnect.execute("select * from store_inventory where store_id=" + id
							+ " and product_id=" + product_ids.get(i));
					if (rs.next())
						for (; date <= new Date().getDate(); date++) {
							values.get(i).add(rs.getInt("amount"));
						}
				}
			}
		}
		// get information of this warehouse
		else {
			rs = DataBaseConnect.execute("select * from warehouse where warehouse_id='" + id + "'");
			if (rs.next()) {
				this.latitude = rs.getDouble("latitude");
				this.longitude = rs.getDouble("longitude");
				this.address = rs.getString("address");
				this.owner = rs.getString("owner");
				this.contact = rs.getString("contact");
			}

			// add each product's id into list
			rs = DataBaseConnect.execute("select * from warehouse_inventory where warehouse_id=" + id);
			while (rs.next())
				product_ids.add(rs.getString("product_id"));

			// get inventory's total value
			for (int i = 0; i < product_ids.size(); i++) {
				double unit_price = 0;
				rs = DataBaseConnect.execute("select * from product where product_id=" + product_ids.get(i));
				if (rs.next())
					unit_price = rs.getDouble("unit_price");
				rs = DataBaseConnect.execute("select * from warehouse_inventory where warehouse_id=" + id
						+ " and product_id=" + product_ids.get(i));
				if (rs.next())
					totalValue += unit_price * rs.getInt("amount");
			}

			// get number of kinds of products in inventory
			productRows = product_ids.size();

			// make ArrayList of each products
			for (int i = 0; i < productRows; i++)
				values.add(new ArrayList<Integer>());

			// add dates into list(1st day to Today)
			for (int i = 1; i <= new Date().getDate(); i++)
				dates.add(new Date(new Date().getYear(), new Date().getMonth(), i));

			// add values into each product's list
			for (int i = 0; i < productRows; i++) {
				rs = DataBaseConnect.execute("select * from log where member_id=" + id + " and product_id="
						+ product_ids.get(i) + " order by change_date");
				int date = 1;
				// get value list with log
				while (rs.next()) {
					int value = 0;
					Date logDateObj = rs.getDate("change_date");
					String dateStr = dateFormat.format(logDateObj);
					ResultSet tmpRs = DataBaseConnect
							.execute("select * from log where member_id=" + id + " and product_id=" + product_ids.get(i)
									+ " and change_date='" + dateStr + "' order by log_no");
					// get this date's recent log
					while (tmpRs.next())
						value = tmpRs.getInt("changed_result");

					int logDate = logDateObj.getDate();
					for (; date <= logDate; date++) {
						// add values into list
						values.get(i).add(value);
					}
				}
				// if there is no log on today, set to quantity of now
				if (date <= new Date().getDate()) {
					rs = DataBaseConnect.execute("select * from warehouse_inventory where warehouse_id=" + id
							+ " and product_id=" + product_ids.get(i));
					if (rs.next())
						for (; date <= new Date().getDate(); date++) {
							values.get(i).add(rs.getInt("amount"));
						}
				}
			}
		}

		// create a frame
		JFrame frame = new JFrame("Chart");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.getContentPane().add(this);
		//frame.setLocationRelativeTo(null);
		frame.setLocation((dim.width / 2) - (this.width / 2), (dim.height / 2) - (this.height / 2));
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();

		this.setLayout(null);
		JLabel idLabel = new JLabel("ID : " + id);
		idLabel.setFont(font);
		idLabel.setBounds(padding, padding, getWidth(), labelPadding);
		this.add(idLabel);
		this.addMouseMotionListener(this);

		if (isStore) {
			JLabel currentCashLabel = new JLabel("Current Cash : " + cash);
			currentCashLabel.setFont(font);
			currentCashLabel.setBounds(padding, padding + labelPadding, getWidth(), labelPadding);
			this.add(currentCashLabel);
		}

		JLabel addressLabel = new JLabel("Address : " + address);
		addressLabel.setFont(font);
		addressLabel.setBounds(padding, padding + labelPadding * 2, getWidth(), labelPadding);
		this.add(addressLabel);

		JLabel pointLabel = new JLabel("Latitude : " + latitude + ", Longitude : " + longitude);
		pointLabel.setFont(font);
		pointLabel.setBounds(padding, padding + labelPadding * 3, getWidth(), labelPadding);
		this.add(pointLabel);

		// sum of each item's (stock * unit price)
		JLabel valueLabel = new JLabel("Total Inventory Value : " + totalValue);
		valueLabel.setFont(font);
		valueLabel.setBounds(padding, padding + labelPadding * 4, getWidth(), labelPadding);
		this.add(valueLabel);

		// num of kinds of items
		JLabel itemsLabel = new JLabel("Inventory Items : " + values.size());
		itemsLabel.setFont(font);
		itemsLabel.setBounds(padding, padding + labelPadding * 5, getWidth(), labelPadding);
		this.add(itemsLabel);

		JLabel nameLabel = new JLabel("Owner : " + owner);
		nameLabel.setFont(font);
		nameLabel.setBounds(padding, padding + labelPadding * 6, getWidth(), labelPadding);
		this.add(nameLabel);

		JLabel contactLabel = new JLabel("Contact Number : " + contact);
		contactLabel.setFont(font);
		contactLabel.setBounds(padding, padding + labelPadding * 7, getWidth(), labelPadding);
		this.add(contactLabel);

		exportToXLS = new JButton("Export to .xls file");
		exportToXLS.setBounds(getWidth() - 4 * padding - 20, (height + labelPadding) / 2 - padding, 140, 20);
		exportToXLS.addActionListener(this);
		this.add(exportToXLS);

		// distance of x values of each point
		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.get(0).size() - 1);
		// distance of y values of each point
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue()) / 2;

		// ArrayList of points
		graphPoints = new ArrayList<ArrayList<Point>>();
		for (int i = 0; i < values.size(); i++) {
			graphPoints.add(new ArrayList<Point>());
			for (int j = 0; j < values.get(i).size(); j++) {
				int x1 = (int) (j * xScale + padding + labelPadding);
				int y1 = (int) ((getMaxValue() - values.get(i).get(j)) * yScale + padding)
						+ (getHeight() - padding - labelPadding / 2) / 2;
				graphPoints.get(i).add(new Point(x1, y1));
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, (height + labelPadding) / 2, getWidth() - (2 * padding) - labelPadding,
				(getHeight() - 2 * padding - labelPadding) / 2);
		g2.setColor(Color.BLACK);

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = (getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding))
					/ 2 + (getHeight() - padding + labelPadding) / 2;
			int y1 = y0;
			if (values.size() > 0) {
				g2.setColor(gridColor);
				// draw grid line
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinValue() // value of each hatch
														// mark
						+ (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1); // create hatch
		}

		// and for x axis
		for (int i = 0; i < values.get(0).size(); i++) {
			if (values.get(0).size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (values.get(0).size() - 1) + padding
						+ labelPadding;
				int x1 = x0;
				int y0 = (getHeight() - ((0 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2;
				;
				int y1 = y0 - pointWidth;
				if (i == 0 || i == values.get(0).size() - 1) {
					g2.setColor(Color.BLACK);
					String xLabel = dateFormat.format(dates.get(i));
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1); // create hatch
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, // y axis
				(getHeight() - ((10 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2,
				padding + labelPadding,
				(getHeight() - ((0 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2);
		g2.drawLine(padding + labelPadding, // x axis
				(getHeight() - ((0 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2,
				getWidth() - padding,
				(getHeight() - ((0 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2);

		// make line chart
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(GRAPH_STROKE);
		for (int j = 0; j < graphPoints.size(); j++) {
			for (int i = 0; i < graphPoints.get(j).size() - 1; i++) {
				if (j + 1 >= lineColor.length) {
					// if number of products are more than pre-determined color,
					// make a random color to use.
					Random generator = new Random();
					int R = generator.nextInt(255);
					int G = generator.nextInt(255);
					int B = generator.nextInt(255);
					g2.setColor(new Color(R, G, B));
				} else
					g2.setColor(lineColor[j]);
				int x1 = graphPoints.get(j).get(i).x;
				int y1 = graphPoints.get(j).get(i).y;
				int x2 = graphPoints.get(j).get(i + 1).x;
				int y2 = graphPoints.get(j).get(i + 1).y;
				g2.drawLine(x1, y1, x2, y2);
			}
		}

		// draw each point
		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int j = 0; j < graphPoints.size(); j++) {
			for (int i = 0; i < graphPoints.get(j).size(); i++) {
				int x = graphPoints.get(j).get(i).x - pointWidth / 2;
				int y = graphPoints.get(j).get(i).y - pointWidth / 2;
				int ovalW = pointWidth;
				int ovalH = pointWidth;
				g2.fillOval(x, y, ovalW, ovalH);
			}
		}

		// draw tooltip when mouse hover the points
		if (hoveringPoint) {
			g2.setColor(Color.YELLOW);

			g2.fillRect(mousePoint.x - 40, mousePoint.y - 32, 80, 30);
			g2.setFont(new Font("Serif", Font.PLAIN, 10));
			g2.setColor(Color.black);
			g2.drawString("Product_ID : " + product_ids.get(arrayIndex), mousePoint.x - 40, mousePoint.y - 23);
			g2.drawString("Value : " + values.get(arrayIndex).get(pointIndex), mousePoint.x - 40, mousePoint.y - 13);
			g2.drawString("Date : " + dateFormat.format(dates.get(pointIndex)), mousePoint.x - 40, mousePoint.y - 3);
		}
	}

	private int getMinValue() {
		int minValue = Integer.MAX_VALUE;
		for (int i = 0; i < values.size(); i++) {
			for (Integer value : values.get(i)) {
				minValue = Math.min(minValue, value);
			}
		}
		return minValue;
	}

	private int getMaxValue() {
		int maxValue = Integer.MIN_VALUE;
		for (int i = 0; i < values.size(); i++) {
			for (Integer value : values.get(i)) {
				maxValue = Math.max(maxValue, value);
			}
		}
		return maxValue;
	}

	public void setValues(ArrayList<ArrayList<Integer>> values) {
		this.values = values;
		invalidate();
		this.repaint();
	}

	public ArrayList<ArrayList<Integer>> getValues() {
		return values;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (int j = 0; j < graphPoints.size(); j++) {
			for (int i = 0; i < graphPoints.get(j).size(); i++) {
				if ((e.getPoint().getX() >= graphPoints.get(j).get(i).getX() - pointWidth / 2
						&& e.getPoint().getX() <= graphPoints.get(j).get(i).getX() + pointWidth / 2)
						&& (e.getPoint().getY() >= graphPoints.get(j).get(i).getY() - pointWidth / 2
								&& e.getPoint().getY() <= graphPoints.get(j).get(i).getY() + pointWidth / 2)) {
					arrayIndex = j;
					pointIndex = i;
					if (!hoveringPoint)
						repaint();
					hoveringPoint = true;
					mousePoint = e.getPoint();
					return;
				}
			}
		}
		if (hoveringPoint)
			repaint();
		hoveringPoint = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == exportToXLS) {
			generateSheet();
		}
	}

	@SuppressWarnings("deprecation")
	private void generateSheet() { // generate .xls file
		// Excel file object
		WritableWorkbook workbook = null;

		// Sheet object
		WritableSheet sheet = null;

		// Cell object
		Label label = null;
		Number numLabel = null;

		// File to be saved
		Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String filename = dateFormat.format(dates.get(dates.size() - 1)) + "_" + id;
		File file = new File(filename + ".xls"); // file name is
													// Today(yyyy-MM-dd.xls)

		WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 16);
		WritableFont infoFont = new WritableFont(WritableFont.ARIAL, 12);
		WritableFont tableFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		WritableFont tableDataFont = new WritableFont(WritableFont.ARIAL, 10);
		WritableCellFormat titleFormat = new WritableCellFormat(titleFont);
		WritableCellFormat infoTitle = new WritableCellFormat(infoFont);
		WritableCellFormat infoData = new WritableCellFormat(infoFont);
		WritableCellFormat tableColumn = new WritableCellFormat(tableFont);
		WritableCellFormat tableData = new WritableCellFormat(tableDataFont);

		try {
			// generate file
			workbook = Workbook.createWorkbook(file);

			// generate sheet
			workbook.createSheet("Sheet", 0);
			sheet = workbook.getSheet(0);
			sheet.mergeCells(0, 0, 8, 0);

			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 25);
			for (int i = 3; i <= 8; i++)
				sheet.setColumnView(i, 13);
			sheet.setColumnView(5, 20);

			// set format
			titleFormat.setAlignment(Alignment.CENTRE);
			titleFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
			infoTitle.setShrinkToFit(true);
			infoTitle.setBorder(Border.ALL, BorderLineStyle.THIN);
			infoTitle.setBackground(Colour.YELLOW);
			infoData.setShrinkToFit(true);
			infoData.setBorder(Border.ALL, BorderLineStyle.THIN);
			infoData.setBackground(Colour.IVORY);
			tableColumn.setAlignment(Alignment.CENTRE);
			tableColumn.setBorder(Border.ALL, BorderLineStyle.THIN);
			tableColumn.setBackground(Colour.SEA_GREEN);
			tableData.setBorder(Border.ALL, BorderLineStyle.THIN);

			// Write into cell - Info about this
			label = new Label(0, 0, "Detail", titleFormat);
			sheet.addCell(label);

			label = new Label(0, 1, "ID", infoTitle);
			sheet.addCell(label);

			label = new Label(1, 1, id, infoData);
			sheet.addCell(label);

			if (isStore) {
				label = new Label(0, 2, "Current Cash", infoTitle);
				sheet.addCell(label);

				numLabel = new Number(1, 2, cash, infoData);
				sheet.addCell(numLabel);
			}

			label = new Label(0, 3, "Address", infoTitle);
			sheet.addCell(label);

			label = new Label(1, 3, address, infoData);
			sheet.addCell(label);

			label = new Label(0, 4, "Latitude", infoTitle);
			sheet.addCell(label);

			numLabel = new Number(1, 4, latitude, infoData);
			sheet.addCell(numLabel);

			label = new Label(0, 5, "Longitude", infoTitle);
			sheet.addCell(label);

			numLabel = new Number(1, 5, longitude, infoData);
			sheet.addCell(numLabel);

			label = new Label(0, 7, "Total Inventory Value", infoTitle);
			sheet.addCell(label);

			numLabel = new Number(1, 7, totalValue, infoData);
			sheet.addCell(numLabel);

			label = new Label(0, 8, "Inventory Items", infoTitle);
			sheet.addCell(label);

			numLabel = new Number(1, 8, values.size(), infoData);
			sheet.addCell(numLabel);

			label = new Label(0, 10, "Owner", infoTitle);
			sheet.addCell(label);

			label = new Label(1, 10, owner, infoData);
			sheet.addCell(label);

			label = new Label(0, 11, "Contact Number", infoTitle);
			sheet.addCell(label);

			label = new Label(1, 11, contact, infoData);
			sheet.addCell(label);

			// Write into cell - make data table columns
			label = new Label(3, 1, "Product_ID", tableColumn);
			sheet.addCell(label);

			label = new Label(4, 1, "Product_Name", tableColumn);
			sheet.addCell(label);

			label = new Label(5, 1, "Description", tableColumn);
			sheet.addCell(label);

			label = new Label(6, 1, "Unit_Price", tableColumn);
			sheet.addCell(label);

			label = new Label(7, 1, "Quantity", tableColumn);
			sheet.addCell(label);

			label = new Label(8, 1, "Value", tableColumn);
			sheet.addCell(label);

			// Write into cell - make data table
			if (isStore) {
				rs = DataBaseConnect.execute("select * from store_inventory where store_id=" + id);
				int rowIndex = 2;
				while (rs.next()) {
					String strProduct_Name = null;
					String strProduct_desc = null;
					double unit_price = 0;
					ResultSet pdNameSet = DataBaseConnect
							.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
					if (pdNameSet.next()) {
						strProduct_Name = pdNameSet.getString("product_name");
						strProduct_desc = pdNameSet.getString("description");
						unit_price = pdNameSet.getDouble("unit_price");
					}

					label = new Label(3, rowIndex, rs.getString("Product_ID"), tableData);
					sheet.addCell(label);

					label = new Label(4, rowIndex, strProduct_Name, tableData);
					sheet.addCell(label);

					label = new Label(5, rowIndex, strProduct_desc, tableData);
					sheet.addCell(label);

					numLabel = new Number(6, rowIndex, unit_price, tableData);
					sheet.addCell(numLabel);

					numLabel = new Number(7, rowIndex, rs.getInt("amount"), tableData);
					sheet.addCell(numLabel);

					numLabel = new Number(8, rowIndex, (double) rs.getInt("amount") * unit_price, tableData);
					sheet.addCell(numLabel);

					++rowIndex;
				}
			} else {
				rs = DataBaseConnect.execute("select * from warehouse_inventory where warehouse_id=" + id);
				int rowIndex = 2;
				while (rs.next()) {
					String strProduct_Name = null;
					String strProduct_desc = null;
					double unit_price = 0;
					ResultSet pdNameSet = DataBaseConnect
							.execute("select * from product where product_id='" + rs.getString("product_id") + "'");
					if (pdNameSet.next()) {
						strProduct_Name = pdNameSet.getString("product_name");
						strProduct_desc = pdNameSet.getString("description");
						unit_price = pdNameSet.getDouble("unit_price");
					}

					label = new Label(3, rowIndex, rs.getString("Product_ID"), tableData);
					sheet.addCell(label);

					label = new Label(4, rowIndex, strProduct_Name, tableData);
					sheet.addCell(label);

					label = new Label(5, rowIndex, strProduct_desc, tableData);
					sheet.addCell(label);

					numLabel = new Number(6, rowIndex, unit_price, tableData);
					sheet.addCell(numLabel);

					numLabel = new Number(7, rowIndex, rs.getInt("amount"), tableData);
					sheet.addCell(numLabel);

					numLabel = new Number(8, rowIndex, (double) rs.getInt("amount") * unit_price, tableData);
					sheet.addCell(numLabel);

					++rowIndex;
				}
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
