package ch.hevs.aislab.paams.connector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.paams.db.DBHelper;
import ch.hevs.aislab.paams.model.Alert;

public class AlertDAO {

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
        String alertName = alert.getArguments().get(0);
        alertName = alertName.substring(1, alertName.length() - 1); // Remove the wrapping ''
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

    public void deleteAlert(Alert alert) {
        database.delete(DBHelper.TABLE_ALERT, DBHelper.COLUMN_ID + " = " + alert.getId(), null);
    }

    public List<Alert> getAllAlerts() {
        List<Alert> items = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_ALERT, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alert alert = cursorToAlert(cursor);
            items.add(alert);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private Alert cursorToAlert(Cursor cursor) {
        Alert alert = new Alert();
        alert.setId(cursor.getLong(0));
        alert.setName(cursor.getString(1));
        alert.setTimestamp(cursor.getLong(2));
        alert.setDummy((cursor.getInt(3) == 1));
        return alert;
    }
}
