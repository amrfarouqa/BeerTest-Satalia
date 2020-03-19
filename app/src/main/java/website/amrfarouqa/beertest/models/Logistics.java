package website.amrfarouqa.beertest.models;

public class Logistics implements Comparable<Logistics> {
    private int destinationId;
    private double distance;

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Logistics() {

    }

    public Logistics(int destinationId, double distance) {
        this.destinationId = destinationId;
        this.distance = distance;
    }

    @Override
    public int compareTo(Logistics o) {
        if (this.distance < o.getDistance())
            return -1;
        else
            return 1;
    }
}
