package website.amrfarouqa.beertest.Classes;

public class TripJournalClass {
    private String BreweryName, distance,from, to;

    public TripJournalClass() {
    }

    public TripJournalClass(String from, String to, String distance, String BreweryName) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.BreweryName = BreweryName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBreweryName() {
        return BreweryName;
    }

    public void setBreweryName(String breweryName) {
        BreweryName = breweryName;
    }
}
