package ch.hevs.aislab.paams.db;


import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.hevs.aislab.paamsdemo.R;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final String TABLE_GLUCOSE = "glucose";
    public static final String TABLE_BLOOD_PRESSURE = "blood_pressure";
    public static final String TABLE_WEIGHT = "weight";
    public static final String TABLE_ALERT = "alert";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_VALUE = "first_value";
    public static final String COLUMN_SECOND_VALUE = "second_value";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DUMMY = "dummy_data";

    private static final String DATABASE_NAME = "magpie.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create the table for the glucose
    private final static String CREATE_TABLE_GLUCOSE =
            "CREATE TABLE " + TABLE_GLUCOSE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " DOUBLE NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                    + COLUMN_DUMMY + " INTEGER DEFAULT 0);";

    private final static String CREATE_TABLE_BLOOD_PRESSURE =
            "CREATE TABLE " + TABLE_BLOOD_PRESSURE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " INTEGER NOT NULL,"
                    + COLUMN_SECOND_VALUE + " INTEGER NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                    + COLUMN_DUMMY + " INTEGER DEFAULT 0);";

    private final static String CREATE_TABLE_WEIGHT =
            "CREATE TABLE " + TABLE_WEIGHT + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " DOUBLE NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                    + COLUMN_DUMMY + " INTEGER DEFAULT 0);";

    private final static String CREATE_TABLE_ALERT =
            "CREATE TABLE " + TABLE_ALERT + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                    + COLUMN_DUMMY + " INTEGER DEFAULT 0);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(TAG, "onCreate()");
        database.execSQL(CREATE_TABLE_GLUCOSE);
        database.execSQL(CREATE_TABLE_BLOOD_PRESSURE);
        database.execSQL(CREATE_TABLE_WEIGHT);
        database.execSQL(CREATE_TABLE_ALERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade()");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GLUCOSE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOOD_PRESSURE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERT);
        onCreate(database);
    }

    public void initializeDummyData(Context context) {
        try {
            insertFromFile(context, R.raw.dummy_weight);
            //insertFromFile(context, R.raw.dummy_glucose);
            insertFromFile(context, R.raw.dummy_blood_pressure);
            insertFromFile(context, R.raw.dummy_alerts);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void insertFromFile(Context context, int resourceId) throws IOException {
        Log.i(TAG, "insertFromFile() from " + context.getClass());
        int rows = 0;

        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        while (insertReader.ready()) {
            String insertStatement = insertReader.readLine();
            getWritableDatabase().execSQL(insertStatement);
            rows++;
        }
        insertReader.close();
        Log.i(TAG, "Inserted " + rows + " rows.");
    }

    public boolean containsData() {
        Log.i(TAG, "containsData() " + DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_WEIGHT, null, null) + " rows.");
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_WEIGHT, null, null) > 0 ? true : false;
    }
}
