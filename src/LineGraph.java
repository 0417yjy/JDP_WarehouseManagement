import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;

public class LineGraph extends Panel {

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
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4; // radius of point
	private int numberYDivisions = 10; // num of grid lines
	private ArrayList<Integer> values;
	private ArrayList<Date> dates;

	public LineGraph(ArrayList<Integer> values, ArrayList<Date> dates) {
		this.values = values;
		this.dates = dates;
		
		//create a frame
		JFrame frame = new JFrame("Chart");
		frame.getContentPane().add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(width, height);
		
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// distance of x values of each point
		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.size() - 1);
		// distance of y values of each point
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue());

		// ArrayList of points
		ArrayList<Point> graphPoints = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxValue() - values.get(i)) * yScale + padding);
			graphPoints.add(new Point(x1, y1));
		}

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding,
				getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (values.size() > 0) {
				g2.setColor(gridColor);
				//draw grid line
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
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if (i == 0 || i == values.size() - 1) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String xLabel = dateFormat.format(dates.get(i));
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1); //create hatch
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
				getHeight() - padding - labelPadding);

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

		//draw each point
		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
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
	
}
