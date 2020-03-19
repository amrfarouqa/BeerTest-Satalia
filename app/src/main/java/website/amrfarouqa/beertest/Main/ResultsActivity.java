package website.amrfarouqa.beertest.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;

import website.amrfarouqa.beertest.Classes.TripJournalClass;
import website.amrfarouqa.beertest.Classes.TripJournalAdapter;
import website.amrfarouqa.beertest.Classes.VisitedJournalAdapter;
import website.amrfarouqa.beertest.Classes.VisitedJournalClass;
import website.amrfarouqa.beertest.models.BreweryLocation;
import website.amrfarouqa.beertest.models.Location;
import website.amrfarouqa.beertest.models.Logistics;
import website.amrfarouqa.beertest.models.Plane;
import website.amrfarouqa.beertest.models.Results;


import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;
import website.amrfarouqa.beertest.R;
import website.amrfarouqa.beertest.utilities.DatabaseHelper;


import static website.amrfarouqa.beertest.utilities.Haversine.calculateDistance;
import static website.amrfarouqa.beertest.utilities.Parameters.getMagicValue;
import static website.amrfarouqa.beertest.utilities.Parameters.getReachableRadius;
import static website.amrfarouqa.beertest.utilities.Parameters.getStartingFuel;
import static website.amrfarouqa.beertest.utilities.Parameters.getTolerantOffset;
import static website.amrfarouqa.beertest.utilities.Parameters.setMagicValue;


public class ResultsActivity extends AppCompatActivity {
    private static double executionTime;
    private static boolean routeFound;
    private static Plane planeData;
    public Cursor c = null;
    private static Results results;
    private double homeLatitude;
    private double homeLongitude;
    private boolean priority;
    public DatabaseHelper myDbHelper;
    private ProgressDialog pDialog;
    private static HashMap<Integer, HashMap<Integer, Double>> distances = new HashMap<>();
    private static DatabaseHelper database;
    private static Plane winnerPlane;
    private List<TripJournalClass> tripJournalClassList = new ArrayList<>();
    private List<VisitedJournalClass> tripJournalClassListVisited = new ArrayList<>();
    private RecyclerView recyclerView_brewTypes, recyclerView_Visited_brew;
    private TripJournalAdapter mAdapter_brewTypes;
    private VisitedJournalAdapter mAdapter_visited_brews;
    public Stack<Logistics> node;
    private LinearLayout ResultsActivityMainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ResultsActivityMainLayout = findViewById(R.id.activity_main);
        homeLatitude = getIntent().getDoubleExtra("homeLatitude",0);
        homeLongitude = getIntent().getDoubleExtra("homeLongitude",0);
        priority = getIntent().getBooleanExtra("priority",true);
        myDbHelper = new DatabaseHelper(ResultsActivity.this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (android.database.SQLException sqle) {
            throw sqle;
        }
        new LoadData().execute();
    }

    private void hideProgressDialog() {
        pDialog.dismiss();
    }

    private void showProgressDialog() {
        pDialog = new ProgressDialog(ResultsActivity.this, ProgressDialog.THEME_HOLO_DARK);
        pDialog.setMessage("Please Wait..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
            showProgressDialog();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            executeAlgorithm();
            printResults();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            hideProgressDialog();
        }

    }

    public ArrayList<BreweryLocation> initData() throws SQLException {
        database = new DatabaseHelper(ResultsActivity.this);
        return database.getBreweries();
    }

    public  Results FindRoute(boolean priority, Location homeLocation) throws SQLException {
        distances.clear();
        ArrayList<BreweryLocation> locationList = initData();
        HashMap<Integer, BreweryLocation> copyList = new HashMap<>(getReachableBreweries(locationList, homeLocation));

        return FindRoute(priority, copyList, homeLocation);
    }

    public Results FindRoute(boolean priority, HashMap<Integer, BreweryLocation> copyList, Location homeLocation) {
        HashMap<Integer, BreweryLocation> reachableArea;
        Plane planeData;
        Location currentLocation;

        if (copyList == null || copyList.size() == 0)
            return new Results(false, null);

        PriorityQueue<Logistics> areaNodes;
        boolean needForBeer;
        int maxBreweries = 0;
        int maxTypes = 0;

        for (int i = 2; i <= 10; i++) {
            setMagicValue(i);
            currentLocation = homeLocation;
            reachableArea = new HashMap<>(copyList);
            planeData = new Plane(getStartingFuel(), currentLocation, reachableArea, homeLocation);
            areaNodes = new PriorityQueue<>();
            needForBeer = true;
            while (needForBeer) {
                if (planeData.getReachableArea().isEmpty())
                    break;
                Plane.Mode currentMode = planeData.getPlaneMode();
                switch (currentMode) {
                    case LOOKING_FOR_AREA:
                        FindDenseArea(areaNodes, planeData);
                        planeData.setPlaneMode(Plane.Mode.TRAVELLING_TO_AREA);
                        if (planeData.getTargetNode().peek() == null)
                            planeData.setPlaneMode(Plane.Mode.LAST_BREATH);
                        break;
                    case TRAVELLING_TO_AREA:
                        FindNearByNode(planeData);
                        if (planeData.getTargetNode().size() == 1)
                            planeData.setPlaneMode(Plane.Mode.SCAVENGING);

                        if (!FlyToTheTarget(planeData))
                            planeData.setPlaneMode(Plane.Mode.LAST_BREATH);
                        break;
                    case SCAVENGING:
                        if (areaNodes.isEmpty()) {
                            planeData.setPlaneMode(Plane.Mode.LOOKING_FOR_AREA);
                            break;
                        }
                        FindClosestFromArea(areaNodes, planeData);
                        if (!FlyToTheTarget(planeData))
                            planeData.setPlaneMode(Plane.Mode.LAST_BREATH);
                        break;
                    case LAST_BREATH:
                        FindClosest(planeData);
                        if (!FlyToTheTarget(planeData))
                            needForBeer = false;
                        break;

                }
            }
            if (priority) {
                if (planeData.getVisitedBreweries().size() > maxBreweries) {
                    winnerPlane = planeData;
                    maxBreweries = planeData.getVisitedBreweries().size();
                    maxTypes = planeData.getCollectedBeerTypes().size();
                }
                else if (planeData.getVisitedBreweries().size() == maxBreweries) {
                    if (planeData.getCollectedBeerTypes().size() > maxTypes) {
                        winnerPlane = planeData;
                        maxTypes = planeData.getCollectedBeerTypes().size();
                    }
                }
            }
            else {
                if (planeData.getCollectedBeerTypes().size() > maxTypes) {
                    winnerPlane = planeData;
                    maxBreweries = planeData.getVisitedBreweries().size();
                    maxTypes = planeData.getCollectedBeerTypes().size();
                }
                else if (planeData.getCollectedBeerTypes().size() == maxTypes) {
                    if (planeData.getVisitedBreweries().size() > maxBreweries) {
                        winnerPlane = planeData;
                        maxBreweries = planeData.getVisitedBreweries().size();
                    }
                }
            }
        }
        return new Results(true, winnerPlane);
    }

    private  HashMap<Integer, BreweryLocation> getReachableBreweries(ArrayList<BreweryLocation> locationList, Location homeLocation) throws SQLException {
        HashMap<Integer, BreweryLocation> reachableBrews = new HashMap<>();

        for (BreweryLocation a : locationList) {
            double distance = getDistance(homeLocation, a);
            if (distance <= getReachableRadius()) {
                reachableBrews.put(a.getBrew_id(), a);
            }
            database.updateBeerTypes(a);
        }

        return reachableBrews;
    }

    private  void FindClosest(Plane planeData) {
        PriorityQueue<Logistics> closestBrewery = new PriorityQueue<>();
        HashMap<Integer, BreweryLocation> reachableArea = planeData.getReachableArea();
        Location currentLocation = planeData.getCurrentLocation();

        for(Map.Entry<Integer, BreweryLocation> breweries : reachableArea.entrySet()) {
            int brewId = breweries.getKey();
            BreweryLocation breweryLocation = breweries.getValue();
            double dist_currentToBrewery = getDistance(currentLocation, breweryLocation);
            closestBrewery.add(new Logistics(brewId, dist_currentToBrewery));
        }

        planeData.setTargetNode(closestBrewery.poll());
    }

    private  void FindClosestFromArea(PriorityQueue<Logistics> areaNodes, Plane planeData) {
        PriorityQueue<Logistics> tempNodes = new PriorityQueue<>();
        HashMap<Integer, BreweryLocation> reachableArea = planeData.getReachableArea();
        Location currentLocation = planeData.getCurrentLocation();
        for (Logistics areaNode : areaNodes) {
            BreweryLocation areaNodeLocation = reachableArea.get(areaNode.getDestinationId());
            if (areaNodeLocation == null)
                continue;
            tempNodes.add(new Logistics(areaNode.getDestinationId(), getDistance(currentLocation, areaNodeLocation)));
        }
        areaNodes.clear();
        areaNodes.addAll(tempNodes);
        planeData.setTargetNode(areaNodes.poll());
    }

    private  boolean FlyToTheTarget(Plane planeData) {
        HashMap<Integer, BreweryLocation> reachableArea = planeData.getReachableArea();
        Logistics targetLocation = planeData.getTargetNode().pop();
        Location currentLocation = planeData.getCurrentLocation();
        BreweryLocation targetBrewery = reachableArea.get(targetLocation.getDestinationId());

        double dist_currentToTarget = Math.ceil(getDistance(currentLocation, targetBrewery));
        double dist_targetToHome = Math.ceil(getDistance(targetBrewery, planeData.getHomeLocation()));
        int fuel = planeData.getFuelLeft();

        if (fuel < Math.ceil(dist_currentToTarget) || fuel < dist_currentToTarget + dist_targetToHome)
            return false;

        planeData.setFuelLeft((int) (fuel - dist_currentToTarget));
        planeData.addBear(targetBrewery.getBeerTypes());
        targetLocation.setDistance(dist_currentToTarget);
        planeData.addVisitedBrewery(targetLocation);

        if (currentLocation instanceof BreweryLocation)
            ((BreweryLocation) currentLocation).changeLocation(targetBrewery);
        else
            planeData.setCurrentLocation(new BreweryLocation(
                    targetBrewery.getLatitude(),
                    targetBrewery.getLongitude(),
                    targetBrewery.getBrew_id(),
                    targetBrewery.getBeerTypes(),
                    targetBrewery.getBreweryName())
            );

        reachableArea.remove(targetBrewery.getBrew_id());
        return true;
    }

    private  void FindNearByNode(Plane planeData) {
        PriorityQueue<Logistics> alongTheWayNodes = new PriorityQueue<>();
        Location currentLocation = planeData.getCurrentLocation();
        HashMap<Integer, BreweryLocation> reachableArea = planeData.getReachableArea();
        int targetBrewId = planeData.getTargetNode().peek().getDestinationId();
        double dist_currentToTarget = getDistance(currentLocation, reachableArea.get(targetBrewId));

        for(Map.Entry<Integer, BreweryLocation> nearByBreweries : reachableArea.entrySet()) {
            int brewId = nearByBreweries.getKey();
            BreweryLocation nearByBrewery = nearByBreweries.getValue();

            double dist_currentToNearby = getDistance(currentLocation, nearByBrewery);
            double dist_nearbyToTarget = getDistance(nearByBrewery, reachableArea.get(targetBrewId));
            double newPathDistance = dist_currentToNearby + dist_nearbyToTarget;
            double tolerantPath = newPathDistance / getTolerantOffset();
            if (dist_currentToNearby < dist_currentToTarget && tolerantPath < dist_currentToTarget)
                alongTheWayNodes.add(new Logistics(brewId, dist_currentToNearby));
        }

        if (!alongTheWayNodes.isEmpty())
            planeData.setTargetNode(alongTheWayNodes.poll());
    }

    private  void FindDenseArea(PriorityQueue<Logistics> areaNodes, Plane planeData) {
        Location currentLocation = planeData.getCurrentLocation();
        HashMap<Integer, BreweryLocation> reachableArea = planeData.getReachableArea();
        Logistics targetArea = new Logistics();
        int fuel = planeData.getFuelLeft();

        int nodesInTheArea;
        int maxNodesInTheArea = 0;
        ArrayList<Logistics> tempAreaNodes = new ArrayList<>();

        for (Map.Entry<Integer, BreweryLocation> reachableNode : reachableArea.entrySet()) {
            int brewId = reachableNode.getKey();
            BreweryLocation possibleCenter = reachableNode.getValue();

            double radius = (fuel - getDistance(currentLocation, possibleCenter)) / getMagicValue();
            nodesInTheArea = 0;
            tempAreaNodes.clear();
            for (Map.Entry<Integer, BreweryLocation> areaNode : reachableArea.entrySet()) {
                int areaNodeId = areaNode.getKey();
                BreweryLocation areaNodeLoc = areaNode.getValue();

                if (brewId != areaNodeId) {
                    double dist_currentToAreaNode = getDistance(currentLocation, areaNodeLoc);
                    if (getDistance(possibleCenter, areaNodeLoc) <= radius) {
                        nodesInTheArea++;
                        tempAreaNodes.add(new Logistics(areaNodeId, dist_currentToAreaNode));
                    }
                }
            }

            if (nodesInTheArea >= maxNodesInTheArea) {
                double dist_currentToTarget = getDistance(currentLocation, possibleCenter);
                if (nodesInTheArea == maxNodesInTheArea) {
                    if (dist_currentToTarget > targetArea.getDistance())
                        continue;
                }
                maxNodesInTheArea = nodesInTheArea;
                targetArea.setDistance(dist_currentToTarget);
                areaNodes.clear();
                areaNodes.add(new Logistics(brewId, dist_currentToTarget));
                areaNodes.addAll(tempAreaNodes);
            }
        }
        planeData.setTargetNode(areaNodes.poll());
    }

    public  double getDistance(Location loc1, Location loc2) {
        if (loc1 instanceof BreweryLocation && loc2 instanceof BreweryLocation) {
            int brewId1 = ((BreweryLocation) loc1).getBrew_id();
            int brewId2 = ((BreweryLocation) loc2).getBrew_id();
            if (!distances.containsKey(brewId1)) {
                HashMap<Integer, Double> tempDist = new HashMap<>();
                tempDist.put(brewId2, calculateDistance(loc1, loc2));
                distances.put(brewId1, tempDist);
            }
            else {
                if (!distances.get(brewId1).containsKey(brewId2)) {
                    HashMap<Integer, Double> tempDist = distances.get(brewId1);
                    tempDist.put(brewId2, calculateDistance(loc1, loc2));
                }
            }
            return distances.get(brewId1).get(brewId2);
        }
        return calculateDistance(loc1, loc2);
    }

    private void executeAlgorithm()  {
        try{
            results = Start(new Location(homeLatitude, homeLongitude), priority);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void printResults() {
        try{
            this.setPrinterData(results);
            this.print();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPrinterData(Results results) {
        ResultsActivity.executionTime = results.getExecutionTime();
        ResultsActivity.routeFound = results.isRouteFound();
        ResultsActivity.planeData = results.getPlaneData();
    }

    private Results Start(Location home, boolean priority) throws SQLException {
        long startTime = System.nanoTime();
        Results results = this.FindRoute(priority, home);
        long endTime = System.nanoTime();
        double executionTime = (double) (endTime - startTime) / 1000000000;
        results.setExecutionTime(executionTime);

        return results;
    }

    private void prepareTripJournalData() {
        for (int i = 0; i < node.size(); i++){
            Logistics init = node.get(i);
            Logistics first = node.get(0);
            Logistics last = node.get(node.size()-1);
            if(i == 0){
                int brewIdFirst = first.getDestinationId();
                TripJournalClass tripJournalClass = new TripJournalClass("Home",String.valueOf(brewIdFirst),(int)init.getDistance() + "km.",database.getBreweryName(brewIdFirst));
                tripJournalClassList.add(tripJournalClass);
            }else if(i == node.size()-1){
                Logistics prev = node.get(i-1);
                int brewIdFin = last.getDestinationId();
                int brewIdPrevFin = prev.getDestinationId();
                double lastTripHome = Math.ceil(this.getDistance(planeData.getCurrentLocation(), planeData.getHomeLocation()));
                planeData.setFuelLeft((int) (planeData.getFuelLeft() - lastTripHome));
                TripJournalClass tripJournalClass = new TripJournalClass(String.valueOf(brewIdPrevFin),String.valueOf(brewIdFin),(int)last.getDistance() + "km.",database.getBreweryName(brewIdFin));
                tripJournalClassList.add(tripJournalClass);
                TripJournalClass tripJournalClassFin = new TripJournalClass(String.valueOf(brewIdFin),"Home",(int)lastTripHome + "km.","Home");
                tripJournalClassList.add(tripJournalClassFin);
            }else{
                Logistics prev = node.get(i-1);
                int brewIdPrev = prev.getDestinationId();
                int brewIdInit = init.getDestinationId();
                TripJournalClass tripJournalClass = new TripJournalClass(String.valueOf(brewIdPrev),String.valueOf(brewIdInit),(int)init.getDistance() + "km.",database.getBreweryName(brewIdInit));
                tripJournalClassList.add(tripJournalClass);
            }
        }
        mAdapter_brewTypes.notifyDataSetChanged();
    }

    private void prepareVisitedData() {
        HashSet<String> beer = planeData.getCollectedBeerTypes();
        for (String a : beer){
            VisitedJournalClass VisitedJournal = new VisitedJournalClass(a);
            tripJournalClassListVisited.add(VisitedJournal);
        }
        mAdapter_visited_brews.notifyDataSetChanged();
    }

    public void print() {
        if (!routeFound) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    AlertDialog alertDialog = new AlertDialog.Builder(ResultsActivity.this).create();
                    alertDialog.setTitle("BeerTest");
                    alertDialog.setMessage("No reachable Breweries were found! Try different starting location...");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    alertDialog.show();
                }

            });
        } else {
            final DatabaseHelper database = new DatabaseHelper(ResultsActivity.this);
            node = planeData.getVisitedBreweries();
            final TextView BreweriesVisited = findViewById(R.id.breweriesvisited);
            final TextView BeerTypesCollected = findViewById(R.id.differentTypesCollected);
            final TextView fuelLeft = findViewById(R.id.fuelLeft);
            final TextView calculationTime = findViewById(R.id.calculationTime);
            recyclerView_brewTypes = findViewById(R.id.recycler_view_brewTypes);
            mAdapter_brewTypes = new TripJournalAdapter(tripJournalClassList);
            final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView_Visited_brew = findViewById(R.id.recycler_view_brewVisited);
            mAdapter_visited_brews = new VisitedJournalAdapter(tripJournalClassListVisited);
            final RecyclerView.LayoutManager mLayoutManager_Visited = new LinearLayoutManager(getApplicationContext());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ResultsActivityMainLayout.setVisibility(View.VISIBLE);
                    recyclerView_brewTypes.setLayoutManager(mLayoutManager);
                    recyclerView_brewTypes.setItemAnimator(new DefaultItemAnimator());
                    recyclerView_brewTypes.addItemDecoration(new DividerItemDecoration(ResultsActivity.this, LinearLayoutManager.VERTICAL));
                    recyclerView_brewTypes.setAdapter(mAdapter_brewTypes);
                    recyclerView_brewTypes.setNestedScrollingEnabled(false);
                    prepareTripJournalData();
                    recyclerView_Visited_brew.setLayoutManager(mLayoutManager_Visited);
                    recyclerView_Visited_brew.setItemAnimator(new DefaultItemAnimator());
                    recyclerView_Visited_brew.addItemDecoration(new DividerItemDecoration(ResultsActivity.this, LinearLayoutManager.VERTICAL));
                    recyclerView_Visited_brew.setAdapter(mAdapter_visited_brews);
                    recyclerView_Visited_brew.setNestedScrollingEnabled(false);
                    prepareVisitedData();
                    BreweriesVisited.setText("Breweries visited: " + planeData.getVisitedBreweries().size());
                    BeerTypesCollected.setText("Different beer types collected: " + planeData.getCollectedBeerTypes().size());
                    fuelLeft.setText("Fuel left: " + planeData.getFuelLeft());
                    calculationTime.setText("Calculation time: " + executionTime);
                }
            });
        }
    }
}
