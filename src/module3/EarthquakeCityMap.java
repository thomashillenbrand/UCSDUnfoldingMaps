package module3;

//Java utilities libraries

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

//import java.util.Collections;
//import java.util.Comparator;
//Processing library
//Unfolding libraries
//Parsing library

/**
 * EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 */
public class EarthquakeCityMap extends PApplet {

    // Less than this threshold is a light earthquake
    public static final float THRESHOLD_MODERATE = 5;
    // Less than this threshold is a minor earthquake
    public static final float THRESHOLD_LIGHT = 4;
    // You can ignore this.  It's to keep eclipse from generating a warning.
    private static final long serialVersionUID = 1L;
    // IF YOU ARE WORKING OFFLINE, change the value of this variable to true
    private static final boolean offline = false;
    /**
     * This is where to find the local tiles, for working without an Internet connection
     */
    public static String mbTilesString = "blankLight-1-3.mbtiles";

    // The map
    private UnfoldingMap map;

    //feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";


    public void setup() {
        size(950, 600, OPENGL);

        if (offline) {
            map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
            earthquakesURL = "2.5_week.atom";    // Same feed, saved Aug 7, 2015, for working offline
        } else {
            //map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
            map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.RoadProvider());
            // IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
            //earthquakesURL = "2.5_week.atom";
        }

        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map);

        // The List you will populate with new SimplePointMarkers
        List<Marker> markers = new ArrayList<Marker>();

        //Use parser to collect properties for each earthquake
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);

        for (PointFeature feature : earthquakes) {
            markers.add(createMarker(feature));
        }


        // Add the markers to the map so that they are displayed
        map.addMarkers(markers);
    }

    private SimplePointMarker createMarker(PointFeature feature) {
        // print properties of all locations
        System.out.println(feature.getProperties());

        // Create a new SimplePointMarker at the location given by the PointFeature
        SimplePointMarker marker = new SimplePointMarker(feature.getLocation());

        // set up color ints
        int blue = color(0, 0, 255);
        int yellow = color(255, 255, 0);
        int red = color(255, 0, 0);


        // set marker size and color based on earthquake magnitude
        Object magObj = feature.getProperty("magnitude");
        float mag = Float.parseFloat(magObj.toString());
        if (mag < THRESHOLD_LIGHT) {
            marker.setColor(blue);
            marker.setRadius(5.0f);
        } else if (mag >= THRESHOLD_LIGHT && mag <= THRESHOLD_MODERATE) {
            marker.setColor(yellow);
            marker.setRadius(10.0f);
        } else {
            marker.setColor(red);
            marker.setRadius(15.0f);

        }

        // Finally return the marker
        return marker;
    }

    public void draw() {
        background(10);
        map.draw();
        addKey();
    }


    // helper method to draw key in GUI
    private void addKey() {
        // draw rectangle
        this.fill(255, 255, 255);
        this.rect(25, 50, 150, 150);

        // title
        String title = "Earthquake Key";
        this.fill(0, 0, 0);
        this.text(title, 50, 65);

        // large earthquakes
        this.fill(255, 0, 0);
        this.ellipse(50, 100, 15, 15);
        String largeQuakes = "5.0+ Magnitude";
        this.text(largeQuakes, 67, 105);

        // moderate earthquakes
        this.fill(255, 255, 0);
        this.ellipse(50, 135, 10, 10);
        String moderateQuakes = "4.0+ Magnitude";
        this.text(moderateQuakes, 67, 140);

        // light earthquakes
        this.fill(0, 0, 255);
        this.ellipse(50, 163, 5, 5);
        String lightQuakes = "< 4.0 Magnitude";
        this.text(lightQuakes, 64, 168);

    }
}
