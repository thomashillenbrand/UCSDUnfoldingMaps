package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Thomas Hillenbrand - 9/23/2018
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		// For starter code just drawMaker(...)
		if (!hidden) {
			drawMarker(pg, x, y);
			if (selected) {
				showTitle(pg, x, y);
			}
		}
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		if(this.isSelected() || this.getClicked()) pg.fill(255, 0, 0);
		else pg.fill(0, 0, 255);
		pg.rect(x-4, y-4, 8, 8);
		
		
	}

	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		String name = String.format("%s (%s)", getName(), getCode());
		String loc = String.format("%s, %s", getCity(), getCountry());

		pg.pushStyle();

		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-6, Math.max(pg.textWidth(name), pg.textWidth(loc)) + 6, -35);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(name, x+3, y-37);
		pg.text(loc, x+3, y-22);

		pg.popStyle();
	}

//	public void showRoutes(PGraphics pg, float x, float y){
//
//	}

	/**
	 * Helper method to get the name of an AirportMarker
	 *
	 * @return String value of the airport name
	 */
	public String getName(){
		return this.getStringProperty("name").replaceAll("\"", "");

	}

	/**
	 * Helper method to get the country of an AirportMarker
	 *
	 * @return String value of the airport's country
	 */
	public String getCountry(){
		return this.getStringProperty("country").replaceAll("\"", "");

	}

	/**
	 * Helper method to get the city of an AirportMarker
	 *
	 * @return String value of the airport's city
	 */
	public String getCity(){
		return this.getStringProperty("city").replaceAll("\"", "");

	}

	/**
	 * Helper method to get the airprt code of an AirportMarker
	 *
	 * @return String value of the airport's code
	 */
	public String getCode() {
		String code = this.getStringProperty("code").replaceAll("\"", "");
		if(code == null || code.isEmpty()) code = "none";
		return code;

	}
	
}
