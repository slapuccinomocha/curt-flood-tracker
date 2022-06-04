package curt;

/***************************************************************************************
*    
*    Title: RoundedBorderTest
*    Author: 843805   (https://community.oracle.com/tech/developers/profile/843805)
*    Date: 13/05/2022
*    Code version: 1.0.0
*    Availability: https://community.oracle.com/tech/developers/discussion/1371231/jpanel-border-with-rounded-corners
*    
*    Code has been modified
*
***************************************************************************************/

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

class RoundedBorder implements Border {

	private int radius;
	private int borderCurve;
	private String hexCol;

	RoundedBorder(int radius, int curve, String color) {
		this.radius = radius;
		this.borderCurve = curve;
		this.hexCol = color;

	}

	public Insets getBorderInsets(Component c) {
		return new Insets(this.radius + 1, this.radius + 2, this.radius + 1, this.radius);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		g2.setColor(Color.decode(hexCol));
		g2.fillRoundRect(x, y, width - 1, height - 1, borderCurve, borderCurve);
	}
}
