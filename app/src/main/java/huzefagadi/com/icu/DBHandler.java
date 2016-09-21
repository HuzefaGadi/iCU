package huzefagadi.com.icu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "locationsForUsers";

    // Contacts table name
    private static final String TABLE_LOCATION = "locations";
    private static final String TABLE_LOCATION_LATEST = "locationsLates";
    private static final String USERNAME = "username";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String LOCATION_TIME = "locTime";
    // Contacts Table Columns names
    private static final String LOCATION_JSON = "json";
    private static final String ID = "id";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "(" + USERNAME + " TEXT," + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT," + LOCATION_TIME + " DATETIME" + ")";
        String CREATE_LOCATION_TABLE_LATEST = "CREATE TABLE " + TABLE_LOCATION_LATEST + "(" + USERNAME + " TEXT PRIMARY KEY," + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT," + LOCATION_TIME + " DATETIME" + ")";

        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE_LATEST);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_LATEST);

        this.onCreate(db);
    }

    public void deleteDeviceId() {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            // int count = db.delete(TABLE_DEVICE_ID, null, null);
            db.close();
        } catch (Exception e) {

        }
    }


    public void addLocation(SingleLocation location) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USERNAME, location.getUserName());
            values.put(LATITUDE, location.getLatitude());
            values.put(LONGITUDE, location.getLongitude());
            values.put(LOCATION_TIME, location.getLocTime());
         //   db.insert(TABLE_LOCATION, null, values);
            db.replace(TABLE_LOCATION_LATEST, null, values);
            db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public List<SingleLocation> getAllLocations(String username) {
        List<SingleLocation> locationList = new ArrayList<SingleLocation>();
        // Select All Query
        try {
            String selectQuery = null;
            if (username != null) {
                selectQuery = "SELECT  * FROM " + TABLE_LOCATION
                        + "WHERE " + USERNAME + " = " + username
                        + " ORDER BY " + LOCATION_TIME
                        + " LIMIT=10";
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_LOCATION;
            }
            SQLiteDatabase db = this.getWritableDatabase();
            //  db.query(TABLE_LOCATION,null,USERNAME+"=?",new String[]{username},null,null,LOCATION_TIME,"10");
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        SingleLocation location = new SingleLocation();
                        location.setUserName(cursor.getString(0));
                        location.setLatitude(cursor.getString(1));
                        location.setLongitude(cursor.getString(2));
                        location.setLocTime(cursor.getString(3));
                        locationList.add(location);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationList;
    }

    public List<SingleLocation> getAllLatestLocations() {
        List<SingleLocation> locationList = new ArrayList<SingleLocation>();
        // Select All Query
        try {
            String selectQuery = null;

            selectQuery = "SELECT  * FROM " + TABLE_LOCATION_LATEST;

            SQLiteDatabase db = this.getWritableDatabase();
            //  db.query(TABLE_LOCATION,null,USERNAME+"=?",new String[]{username},null,null,LOCATION_TIME,"10");
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        SingleLocation location = new SingleLocation();
                        String username = cursor.getString(0);
                        location.setUserName(cursor.getString(0));
                        location.setLatitude(cursor.getString(1));
                        location.setLongitude(cursor.getString(2));
                        location.setLocTime(cursor.getString(3));
                        locationList.add(location);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationList;
    }

    public List<String> getAllUsers() {
        List<String> userList = new ArrayList<String>();
        // Select All Query
        try {
            String selectQuery = "SELECT " + USERNAME + " FROM " + TABLE_LOCATION
                    + "DISTINCT " + USERNAME;
            SQLiteDatabase db = this.getWritableDatabase();
            //  db.query(TABLE_LOCATION,null,USERNAME+"=?",new String[]{username},null,null,LOCATION_TIME,"10");
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        userList.add(cursor.getString(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public int deleteAllLocations() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int count = db.delete(TABLE_LOCATION, null, null);
            db.close();
            //    fileUtils.writeLogs("All locations deleted from Local DB");
            return count;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return 0;
        }
    }


}
