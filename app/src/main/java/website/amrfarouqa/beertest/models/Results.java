package website.amrfarouqa.beertest.models;

public class Results {
    private boolean routeFound;
    private Plane planeData;
    private double executionTime = 0;

    public Results(boolean routeFound, Plane planeData) {
        this.routeFound = routeFound;
        this.planeData = planeData;
    }

    public boolean isRouteFound() {
        return routeFound;
    }

    public Plane getPlaneData() {
        return planeData;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }
}
