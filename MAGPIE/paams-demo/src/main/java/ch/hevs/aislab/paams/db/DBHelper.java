package ch.hevs.aislab.paams.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_GLUCOSE = "glucose";
    public static final String TABLE_BLOOD_PRESURE = "blood_pressure";
    public static final String TABLE_WEIGHT = "weight";
    public static final String TABLE_ALERT = "alert";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_VALUE = "first_value";
    public static final String COLUMN_SECOND_VALUE = "second_value";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_NAME = "magpie.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create the table for the glucose
    private final static String CREATE_TABLE_GLUCOSE =
            "CREATE TABLE " + TABLE_GLUCOSE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " DOUBLE NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

    private final static String CREATE_TABLE_BLOOD_PRESSURE =
            "CREATE TABLE " + TABLE_BLOOD_PRESURE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " INTEGER NOT NULL,"
                    + COLUMN_SECOND_VALUE + " INTEGER NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

    private final static String CREATE_TABLE_WEIGHT =
            "CREATE TABLE " + TABLE_WEIGHT + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FIRST_VALUE + " DOUBLE NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

    private final static String CREATE_TABLE_ALERT =
            "CREATE TABLE " + TABLE_ALERT + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " STRING NOT NULL,"
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_GLUCOSE);
        database.execSQL(CREATE_TABLE_BLOOD_PRESSURE);
        database.execSQL(CREATE_TABLE_WEIGHT);
        database.execSQL(CREATE_TABLE_ALERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GLUCOSE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOOD_PRESURE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERT);
        onCreate(database);
    }
}
