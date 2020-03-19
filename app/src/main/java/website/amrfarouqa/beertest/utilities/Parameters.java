package website.amrfarouqa.beertest.utilities;

public class Parameters {
    public final static int startingFuel = 2000;
    public final static int earthRadius = 6371000;

    /**
     * Value determining how far can plane sidetrack from current target.
     */
    public final static double tolerantOffset = 1.25;

    /**
     * Value giving boundaries how far should plane look for possible candidates.
     */
    public final static int reachableRadius = startingFuel / 2;

    /**
     * Value determining the radius, which is used for finding most dense areas.
     */
    public static int magicValue = 10;

    public static void setMagicValue(int magicValue) {
        Parameters.magicValue = magicValue;
    }

    public static int getStartingFuel() {
        return startingFuel;
    }

    public static int getEarthRadius() {
        return earthRadius;
    }

    public static double getTolerantOffset() {
        return tolerantOffset;
    }

    public static int getReachableRadius() {
        return reachableRadius;
    }

    public static int getMagicValue() {
        return magicValue;
    }
}
