package ch.hevs.aislab.paams.connector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.db.DBHelper;
import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.Type;

public class DoubleValueDAO {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DoubleValueDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createDoubleValue(DoubleValue doubleValue) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FIRST_VALUE, doubleValue.getFirstValue());
        values.put(DBHelper.COLUMN_SECOND_VALUE, doubleValue.getSecondValue());
        values.put(DBHelper.COLUMN_TIMESTAMP, doubleValue.getTimestamp());

        long insertId = database.insert(DBHelper.TABLE_BLOOD_PRESURE, null, values);
        Cursor cursor = database.query(DBHelper.TABLE_BLOOD_PRESURE,
                null, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        cursor.close();
        doubleValue.setId(insertId);
    }

    public List<DoubleValue> getAllDoubleValues() {
        List<DoubleValue> items = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_BLOOD_PRESURE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DoubleValue doubleValue = cursorToDoubleValue(cursor);
            items.add(doubleValue);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private DoubleValue cursorToDoubleValue(Cursor cursor) {
        DoubleValue measurement = new DoubleValue();
        measurement.setId(cursor.getLong(0));
        measurement.setFirstValue(cursor.getInt(1));
        measurement.setSecondValue(cursor.getInt(2));
        measurement.setTimestamp(cursor.getLong(3));
        measurement.setType(Type.BLOOD_PRESSURE);
        return measurement;
    }
}
