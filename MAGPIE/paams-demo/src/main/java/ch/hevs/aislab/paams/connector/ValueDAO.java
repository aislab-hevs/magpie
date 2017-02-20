package ch.hevs.aislab.paams.connector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.db.DBHelper;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;

public class ValueDAO {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public ValueDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createSingleValue(SingleValue singleValue) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FIRST_VALUE, singleValue.getValue());
        values.put(DBHelper.COLUMN_TIMESTAMP, singleValue.getTimestamp());

        long insertId = 0;
        Cursor cursor = null;
        switch (singleValue.getType()) {
            case GLUCOSE:
                insertId = database.insert(DBHelper.TABLE_GLUCOSE, null, values);
                cursor = database.query(DBHelper.TABLE_GLUCOSE,
                        null, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
                break;
            case WEIGHT:
                insertId = database.insert(DBHelper.TABLE_WEIGHT, null, values);
                cursor = database.query(DBHelper.TABLE_WEIGHT,
                        null, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
                break;
        }
        cursor.moveToFirst();
        cursor.close();
        singleValue.setId(insertId);
    }

    public void deleteSingleValue(SingleValue singleValue) {
        switch (singleValue.getType()) {
            case GLUCOSE:
                database.delete(DBHelper.TABLE_GLUCOSE, DBHelper.COLUMN_ID + " = " + singleValue.getId(), null);
                break;
            case WEIGHT:
                database.delete(DBHelper.TABLE_WEIGHT, DBHelper.COLUMN_ID + " = " + singleValue.getId(), null);
        }
    }

    public List<Value> getAllSingleValues(Type type) {
        List<Value> items = new ArrayList<>();
        Cursor cursor = null;
        switch (type) {
            case GLUCOSE:
                cursor = database.query(DBHelper.TABLE_GLUCOSE, null, null, null, null, null, null);
                break;
            case WEIGHT:
                cursor = database.query(DBHelper.TABLE_WEIGHT, null, null, null, null, null, null);
                break;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SingleValue measurement = cursorToSingleValue(cursor, type);
            items.add(measurement);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private SingleValue cursorToSingleValue(Cursor cursor, Type type) {
        SingleValue measurement = new SingleValue();
        measurement.setId(cursor.getLong(0));
        measurement.setValue(cursor.getDouble(1));
        measurement.setTimestamp(cursor.getLong(2));
        measurement.setType(type);
        return measurement;
    }
}
