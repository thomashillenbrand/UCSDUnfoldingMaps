package module6;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;

import java.util.HashMap;
import java.util.List;

public class RouteMarker extends SimpleLinesMarker {

    public RouteMarker(List<Location> locs, HashMap<String, Object> props) {
        super(locs, props);
    }

    public String getDest() {
        return this.getStringProperty("destination");
    }

    public String getSource() {
        return this.getStringProperty("source");
    }

}
