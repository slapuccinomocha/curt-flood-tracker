package curt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * @author wellsantos@wellsantos.com
 * @created 18/09/2014
 */

// Pin that will drop to note locations
public class MapPin extends MapMarkerCircle implements MapMarker {

	private Image image;
	private int id;

	public MapPin(Coordinate coord, Image image, int ID) {
		this(coord, 1, image);
		this.id = ID;
	}

	public MapPin(Coordinate coord, double radius, Image image) {
		super(coord, radius);
		this.image = image;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void paint(Graphics g, Point position, int radio) {
		double r = this.getRadius();
		int width = (int) (this.image.getWidth(null) * r);
		int height = (int) (this.image.getHeight(null) * r);
		int w2 = width / 2;
		int h2 = height / 2;
		g.drawImage(this.image, position.x - w2, position.y - h2, width, height, null);
		this.paintText(g, position);
	}

	public Image getImage() {
		return this.image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}