package website.amrfarouqa.beertest.Classes;

public class VisitedJournalClass {
    private String VisitedBreweryName;

    public VisitedJournalClass() {
    }

    public VisitedJournalClass(String VisitedBreweryName) {
        this.VisitedBreweryName = VisitedBreweryName;

    }

    public String getVisitedBreweryName() {
        return VisitedBreweryName;
    }

    public void setVisitedBreweryName(String visitedBreweryName) {
        VisitedBreweryName = visitedBreweryName;
    }
}
