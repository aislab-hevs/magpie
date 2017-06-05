package ch.hevs.aislab.paams.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final String TABLE_GLUCOSE = "glucose";
    public static final String TABLE_BLOOD_PRESURE = "blood_pressure";
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
            "CREATE TABLE " + TABLE_BLOOD_PRESURE + "("
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

    private final static String INSERT_DUMMY_DATA_WEIGHT1 =
            "INSERT INTO " + TABLE_WEIGHT + " ("
                    + COLUMN_FIRST_VALUE + "," + COLUMN_TIMESTAMP + "," + COLUMN_DUMMY
                    + ") VALUES ("
                    + "100," + "1496304180000," + "1);";

    private final static String INSERT_DUMMY_DATA_WEIGHT2 =
            "INSERT INTO " + TABLE_WEIGHT + " ("
                    + COLUMN_FIRST_VALUE + "," + COLUMN_TIMESTAMP + "," + COLUMN_DUMMY
                    + ") VALUES ("
                    + "102," + "1496649780000," + "1);";

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
        database.execSQL(INSERT_DUMMY_DATA_WEIGHT1);
        database.execSQL(INSERT_DUMMY_DATA_WEIGHT2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade()");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GLUCOSE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOOD_PRESURE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERT);
        onCreate(database);
    }
}