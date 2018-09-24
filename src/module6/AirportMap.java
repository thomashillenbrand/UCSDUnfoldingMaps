package module6;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An applet that shows airports (and routes)
 * on a world map.
 *
 * @author Thomas Hillenbrand - 9/23/2018
 */
public class AirportMap extends PApplet {

    UnfoldingMap map;
    private List<Marker> airportList;
    List<Marker> routeList;

    private CommonMarker lastSelected;
    private List<CommonMarker> lastClicked = new ArrayList<>();

    /**
     * Setup method to create map and parse data
     */
    public void setup() {
        // setting up PApplet
        size(900, 700, OPENGL);

        // setting up map and default events
        map = new UnfoldingMap(this, 200, 50, 675, 600, new Microsoft.RoadProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        // get features from airport data
        List<PointFeature> features = ParseFeed.parseAirports(this, "/Users/thomashillenbrand/IdeaProjects/UCSDUnfoldingMaps/data/airports.dat");

        // list for markers, hashmap for quicker access when matching with routes
        airportList = new ArrayList<Marker>();
        HashMap<Integer, Location> airports = new HashMap<Integer, Location>();

        // create markers from features
        for (PointFeature feature : features) {
            AirportMarker m = new AirportMarker(feature);

            m.setRadius(5);
            airportList.add(m);

            // put airport in hashmap with OpenFlights unique id for key
            airports.put(Integer.parseInt(feature.getId()), feature.getLocation());

        }


        // parse route data
        List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "/Users/thomashillenbrand/IdeaProjects/UCSDUnfoldingMaps/data/routes.dat");
        routeList = new ArrayList<Marker>();
        for (ShapeFeature route : routes) {

            // get source and destination airportIds
            int source = Integer.parseInt((String) route.getProperty("source"));
            int dest = Integer.parseInt((String) route.getProperty("destination"));

            // get locations for airports on route
            if (airports.containsKey(source) && airports.containsKey(dest)) {
                route.addLocation(airports.get(source));
                route.addLocation(airports.get(dest));
            }

            SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());

            // set routes hidden by deafult to avoid clutter
            sl.setHidden(true);
            routeList.add(sl);
        }

        // add routes and airports
        map.addMarkers(routeList);
        map.addMarkers(airportList);

    }

    /**
     * Generic draw method to draw map, legend, and selected airports box
     */
    public void draw() {
        background(210, 210, 210);
        map.draw();
        addKey();
        drawSelectedAirports();

    }

    /**
     * Helper method to draw legend
     */
    private void addKey() {
        // create Key box
        fill(255, 250, 240);
        int xbase = 25;
        int ybase = 50;
        rect(xbase, ybase, 150, 125);

        // legend title
        fill(0);
        textAlign(LEFT, CENTER);
        textSize(12);
        text("Legend", xbase + 52, ybase + 25);

        // unselected airport symbol
        fill(0, 0, 255);
        int unsel_rect_xbase = xbase + 20;
        int unsel_rect_ybase = ybase + 50;
        rect(unsel_rect_xbase, unsel_rect_ybase, 12, 12);

        // unselected airport text
        fill(0, 0, 0);
        textAlign(LEFT, CENTER);
        text("Unselected Airport", unsel_rect_xbase + 20, unsel_rect_ybase + 4);

        // selected airport symbol
        fill(255, 0, 0);
        int sel_rect_xbase = xbase + 20;
        int sel_rect_ybase = unsel_rect_ybase + 27;
        rect(sel_rect_xbase, sel_rect_ybase, 12, 12);

        // selected airport text
        fill(0, 0, 0);
        textAlign(LEFT, CENTER);
        text("Selected Airport", sel_rect_xbase + 20, sel_rect_ybase + 4);

        // route symbol
        int lineX1 = xbase + 20;
        int lineY1 = sel_rect_ybase + 27;
        fill(0);
        line(lineX1, lineY1, lineX1 + 12, lineY1);

        // route text
        textAlign(LEFT, CENTER);
        text("Route", lineX1 + 20, lineY1 - 2);

    }

    /**
     * Helper method to draw the box where the selected airports will be displayed.
     * Also draws the airport name text once airport is selected.
     */
    private void drawSelectedAirports() {

        // create display box
        fill(255, 250, 240);
        int xbase = 10;
        int ybase = 185;
        rect(xbase, ybase, 180, 460);

        // draw title
        String title = "Selected Airport(s)";
        int titleWidth = (int) textWidth(title);
        fill(0);
        textAlign(LEFT, CENTER);
        textSize(12);
        text(title, (192 - titleWidth) / 2, ybase + 25);

        // list clicked airports:
        if (!lastClicked.isEmpty()) {
            textSize(10);
            AirportMarker currentMarker;
            String name;
            int count = 0;
            int nameYBase = ybase + 50;

            for (CommonMarker marker : lastClicked) {
                currentMarker = (AirportMarker) marker;
                name = String.format("* %s (%s)", currentMarker.getName(), currentMarker.getCode());
                text(name, xbase + 10, (nameYBase) + 15 * count++);

            }

        } else return;


    }

    /**
     * Event handler that gets called automatically when the
     * mouse moves.
     */
    @Override
    public void mouseMoved() {
        // clear the last selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;

        }
        selectMarkerIfHover(airportList);

    }

    /**
     * Method for action when the mouse is hovered over a selectable marker.
     * @param markers
     */
    private void selectMarkerIfHover(List<Marker> markers) {
        // Abort if there's already a marker selected
        if (lastSelected != null) {
            return;
        }

        for (Marker m : markers) {
            CommonMarker marker = (CommonMarker) m;
            if (marker.isInside(map, mouseX, mouseY)) {
                lastSelected = marker;
                marker.setSelected(true);
                return;
            }
        }
    }

    /**
     * Event handler for when the mouse is clicked. If an airport is clicked,
     * it will be added to the lastClicked list and picked up in the
     * drawSelectedAirports() method.
     */
    @Override
    public void mouseClicked() {

        // loop thru all airports, if one is selected, set as clicked
        for (Marker marker : airportList) {
            if (marker.isInside(map, mouseX, mouseY)) {
                if (!lastClicked.contains(marker)) lastClicked.add((CommonMarker) marker);
                ((CommonMarker) marker).setClicked(true);
            }

        }

    }

    /**
     * Event handler for when a key on the keyboard is pressed
     */
    @Override
    public void keyPressed() {
        switch (key) {
            // clears selected airports and resets the map
            case 'c':
                lastClicked.forEach(marker -> marker.setClicked(false));
                lastClicked.clear();
                revealAllAirports();
                break;

            // hides unselected airports
            case 'h':
                for (Marker m : airportList) {
                    if (!lastClicked.contains(m)) m.setHidden(true);
                }
                break;

            // reveals all airport markers
            case 'u':
                revealAllAirports();
                break;
        }
    }

    /**
     * Helper method to reveal all airports on the map
     */
    private void revealAllAirports() {
        airportList.forEach(a -> a.setHidden(false));
    }


}
