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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LineGraph extends Panel implements MouseMotionListener {

	/*****************************************************************
	 * Class Name : LineGraph Description : Make a frame which contains a
	 * graphic line chart. Parameters : int array, Date array
	 *****************************************************************/

	private static final long serialVersionUID = 1L;
	private int width = 800; // width of the frame
	private int height = 600; // height of the frame
	private int padding = 40; // padding of whole panel
	private int labelPadding = 25; // padding of each label
	private Color lineColor = Color.red; // Color of lines
	private Color pointColor = Color.black; // Color of points
	private Color gridColor = Color.lightGray; // Color of grid
												// lines
	private Point mousePoint; // Point that indicate the hovering point
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4; // radius of point
	private int numberYDivisions = 10; // num of grid lines
	private boolean hoveringPoint = false;
	private ArrayList<Integer> values; // ArrayList of values
	private ArrayList<Date> dates; // ArrayList of dates
	private ArrayList<Point> graphPoints; // ArrayList of each point
	private Font font = new Font("Serif", Font.BOLD, 20);
	private int pointIndex;

	//Data of each store / warehouse
	private String id = "[id]";
	private String cash = "[cash]";
	private String address = "[address]";
	private double latitude, longitude;

	public LineGraph(ArrayList<Integer> values, ArrayList<Date> dates) {
		this.values = values;
		this.dates = dates;

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		// create a frame
		JFrame frame = new JFrame("Chart");
		frame.getContentPane().add(this);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();

		this.setLayout(null);
		JLabel idLabel = new JLabel("ID : " + id);
		idLabel.setFont(font);
		idLabel.setBounds(padding, padding, getWidth(), labelPadding);
		this.add(idLabel);
		this.addMouseMotionListener(this);

		JLabel currentCashLabel = new JLabel("Current Cash : " + cash);
		currentCashLabel.setFont(font);
		currentCashLabel.setBounds(padding, padding + labelPadding, getWidth(), labelPadding);
		this.add(currentCashLabel);

		JLabel addressLabel = new JLabel("Address : " + address);
		addressLabel.setFont(font);
		addressLabel.setBounds(padding, padding + labelPadding * 2, getWidth(), labelPadding);
		this.add(addressLabel);

		JLabel pointLabel = new JLabel("Latitude : " + latitude + ", Longitude : " + longitude);
		pointLabel.setFont(font);
		pointLabel.setBounds(padding, padding + labelPadding * 3, getWidth(), labelPadding);
		this.add(pointLabel);
		
		JButton exportToXLS = new JButton("Export to .xls file");
		exportToXLS.setBounds(getWidth() - 4*padding - 20, (height+labelPadding) / 2 - padding, 140, 20);
		this.add(exportToXLS);

		// distance of x values of each point
		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.size() - 1);
		// distance of y values of each point
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue()) / 2;

		// ArrayList of points
		graphPoints = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxValue() - values.get(i)) * yScale + padding)
					+ (getHeight() - padding - labelPadding / 2) / 2;
			graphPoints.add(new Point(x1, y1));
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
		for (int i = 0; i < values.size(); i++) {
			if (values.size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (values.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = (getHeight() - ((0 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding
						+ labelPadding)) / 2 + (getHeight() - padding + labelPadding) / 2;
				;
				int y1 = y0 - pointWidth;
				if (i == 0 || i == values.size() - 1) {
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
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
		}

		// draw each point
		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
		}

		// draw tooltip when mouse hover the points
		if (hoveringPoint) {
			g2.setColor(Color.YELLOW);
			g2.fillRect(mousePoint.x, mousePoint.y - 20, 80, 20);
			g2.setFont(new Font("Serif", Font.PLAIN, 10));
			g2.setColor(Color.black);
			g2.drawString("Value : " + values.get(pointIndex), mousePoint.x, mousePoint.y - 11);
			g2.drawString("Date : " + dateFormat.format(dates.get(pointIndex)), mousePoint.x, mousePoint.y - 1);
		}
	}

	private int getMinValue() {
		int minValue = Integer.MAX_VALUE;
		for (Integer value : values) {
			minValue = Math.min(minValue, value);
		}
		return minValue;
	}

	private int getMaxValue() {
		int maxValue = Integer.MIN_VALUE;
		for (Integer value : values) {
			maxValue = Math.max(maxValue, value);
		}
		return maxValue;
	}

	public void setValues(ArrayList<Integer> values) {
		this.values = values;
		invalidate();
		this.repaint();
	}

	public ArrayList<Integer> getValues() {
		return values;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (int i = 0; i < graphPoints.size(); i++) {
			if ((e.getPoint().getX() >= graphPoints.get(i).getX() - pointWidth / 2
					&& e.getPoint().getX() <= graphPoints.get(i).getX() + pointWidth / 2)
					&& (e.getPoint().getY() >= graphPoints.get(i).getY() - pointWidth / 2
							&& e.getPoint().getY() <= graphPoints.get(i).getY() + pointWidth / 2)) {
				pointIndex = i;
				if (!hoveringPoint)
					repaint();
				hoveringPoint = true;
				mousePoint = e.getPoint();
				return;
			}
		}
		if (hoveringPoint)
			repaint();
		hoveringPoint = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

}
