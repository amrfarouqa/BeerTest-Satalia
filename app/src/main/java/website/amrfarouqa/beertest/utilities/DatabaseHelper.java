package website.amrfarouqa.beertest.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import website.amrfarouqa.beertest.models.BreweryLocation;


public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "BeerDB.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having) {
        return myDataBase.query(table, columns, selection, selectionArgs, groupBy, having, having);
    }

    public ArrayList<BreweryLocation> getBreweries() {
        myDataBase= this.getReadableDatabase();
        Cursor c = query("breweries", null, null, null, null, null);
        ArrayList<BreweryLocation> breweries = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                do {
                    int brewId = c.getInt(0);
                    String brewName = c.getString(1);
                    double latitude = c.getDouble(2);
                    double longitude = c.getDouble(3);
                    breweries.add(new BreweryLocation(latitude, longitude, brewId, null, brewName));
                } while (c.moveToNext());
            }
            return breweries;
        } finally {
            c.close();
        }
    }

    public void updateBeerTypes(BreweryLocation brewery) {
        myDataBase = this.getWritableDatabase();
        String whereClause = "brewId="+brewery.getBrew_id();
        Cursor c = query("beerTypes", null, whereClause, null, null, null);
        HashSet<String> beerTypes = new HashSet<>();
        try {
            if (c.moveToFirst()) {
                do {
                    beerTypes.add(c.getString(2));
                } while (c.moveToNext());
            }
            brewery.setBeerTypes(beerTypes);
        } finally {
            c.close();
        }
    }

    public String getBreweryName(int brewId) {
        myDataBase= this.getReadableDatabase();
        String whereClause = "brewId="+brewId;
        Cursor c = query("breweries", null, whereClause, null, null, null);
        try {
            c.moveToNext();
            return c.getString(1);
        } finally {
            c.close();
        }
    }


}
