package website.amrfarouqa.beertest.models;

import java.util.HashSet;

public class BreweryLocation extends Location {
    private int brew_id;
    private String breweryName;
    private HashSet<String> beerTypes = new HashSet<>();

    public BreweryLocation(double latitude, double longitude, int brew_id, HashSet<String> beerTypes, String breweryName) {
        super(latitude, longitude);
        this.brew_id = brew_id;
        if (beerTypes != null)
            this.beerTypes.addAll(beerTypes);
        this.breweryName = breweryName;
    }

    public String getBreweryName() {
        return breweryName;
    }

    private void setBreweryName(String breweryName) {
        this.breweryName = breweryName;
    }

    public HashSet<String> getBeerTypes() {
        return beerTypes;
    }

    public void setBeerTypes(HashSet<String> beerTypes) {
        this.beerTypes.clear();
        this.beerTypes.addAll(beerTypes);
    }

    public int getBrew_id() {
        return brew_id;
    }

    private void setBrew_id(int brew_id) {
        this.brew_id = brew_id;
    }

    public void changeLocation(BreweryLocation targetLocation) {
        this.setLatitude(targetLocation.getLatitude());
        this.setLongitude(targetLocation.getLongitude());
        this.setBrew_id(targetLocation.getBrew_id());
        this.setBeerTypes(targetLocation.getBeerTypes());
        this.setBreweryName(targetLocation.getBreweryName());
    }
}
