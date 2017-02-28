package ch.hevs.aislab.paams.connector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.paams.db.DBHelper;

public class AlertDAO {

    private final String TAG = getClass().getName();

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public AlertDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public void createAlert(LogicTupleEvent alert) {
        String alertName = alert.getName();
        long timestamp = alert.getTimestamp();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, alertName);
        values.put(DBHelper.COLUMN_TIMESTAMP, timestamp);

        long insertId = database.insert(DBHelper.TABLE_ALERT, null, values);
        Cursor cursor = database.query(DBHelper.TABLE_ALERT,
                null, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }
}
