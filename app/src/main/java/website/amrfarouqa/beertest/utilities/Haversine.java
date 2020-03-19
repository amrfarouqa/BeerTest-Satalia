package website.amrfarouqa.beertest.utilities;

import website.amrfarouqa.beertest.models.Location;
import static website.amrfarouqa.beertest.utilities.Parameters.getEarthRadius;

public class Haversine {
    public static double calculateDistance(Location loc1, Location loc2) {
        double lat1 = loc1.getLatitude() * Math.PI / 180;
        double long1 = loc1.getLongitude() * Math.PI / 180;
        double lat2 = loc2.getLatitude() * Math.PI / 180;
        double long2 = loc2.getLongitude() * Math.PI / 180;

        double first = Math.pow(Math.sin((lat2 - lat1) / 2), 2);
        double second2 = Math.pow(Math.sin((long2 - long1) / 2), 2);
        double second1 = Math.cos(lat1)*Math.cos(lat2);
        double second = second1 * second2;
        return (2*getEarthRadius()*Math.asin(Math.sqrt(first + second)) / 1000);
    }
}
