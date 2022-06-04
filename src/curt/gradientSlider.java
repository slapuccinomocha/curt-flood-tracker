package curt;

/***************************************************************************************
*    
*    Title: MySliderUI
*    Author: trashgod   (https://stackoverflow.com/users/230513/trashgod)
*    Date: 24/05/2022
*    Code version: 1.0.0
*    Availability: https://stackoverflow.com/a/6996263
*
***************************************************************************************/

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;

import java.awt.*;
import java.awt.geom.Point2D;

public class gradientSlider extends BasicSliderUI {

	private static float[] fracs = { 0.0f, 1.0f };
	private LinearGradientPaint p;

	public gradientSlider(JSlider slider) {
		super(slider);
	}

	public void paintTrack(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle t = trackRect;
		Point2D start = new Point2D.Float(t.x, t.y);
		Point2D end = new Point2D.Float(t.width, t.height);
		Color[] colors = { Color.decode("#8cce7f"), Color.decode("#f6504f") };
		p = new LinearGradientPaint(start, end, fracs, colors);
		g2d.setPaint(p);
		g2d.fillRect(t.x, t.y, t.width, t.height);
	}

	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Rectangle t = thumbRect;
		g2d.setColor(Color.black);
		int tw2 = t.width / 2;
		g2d.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
		g2d.drawLine(t.x, t.y, t.x + tw2, t.y + t.height);
		g2d.drawLine(t.x + t.width - 1, t.y, t.x + tw2, t.y + t.height);
	}
}