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

    public void createValue(Value value) {
        ContentValues values = new ContentValues();
        switch (value.getType()) {
            case GLUCOSE:
            case WEIGHT:
                values.put(DBHelper.COLUMN_FIRST_VALUE, ((SingleValue) value).getValue());
                break;
            case BLOOD_PRESSURE:
                values.put(DBHelper.COLUMN_FIRST_VALUE, ((DoubleValue) value).getFirstValue());
                values.put(DBHelper.COLUMN_SECOND_VALUE, ((DoubleValue) value).getSecondValue());
                break;
        }
        values.put(DBHelper.COLUMN_TIMESTAMP, value.getTimestamp());

        long insertId = 0;
        Cursor cursor = null;
        switch (value.getType()) {
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
            case BLOOD_PRESSURE:
                insertId = database.insert(DBHelper.TABLE_BLOOD_PRESURE, null, values);
                cursor = database.query(DBHelper.TABLE_BLOOD_PRESURE,
                        null, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
                break;
        }
        cursor.moveToFirst();
        cursor.close();
        value.setId(insertId);
    }

    public void deleteValue(Value value) {
        switch (value.getType()) {
            case GLUCOSE:
                database.delete(DBHelper.TABLE_GLUCOSE, DBHelper.COLUMN_ID + " = " + value.getId(), null);
                break;
            case WEIGHT:
                database.delete(DBHelper.TABLE_WEIGHT, DBHelper.COLUMN_ID + " = " + value.getId(), null);
                break;
            case BLOOD_PRESSURE:
                database.delete(DBHelper.TABLE_BLOOD_PRESURE, DBHelper.COLUMN_ID + " = " + value.getId(), null);
                break;
        }
    }

    public List<Value> getAllValues(Type type) {
        List<Value> items = new ArrayList<>();
        Cursor cursor = null;
        switch (type) {
            case GLUCOSE:
                cursor = database.query(DBHelper.TABLE_GLUCOSE, null, null, null, null, null, null);
                break;
            case WEIGHT:
                cursor = database.query(DBHelper.TABLE_WEIGHT, null, null, null, null, null, null);
                break;
            case BLOOD_PRESSURE:
                cursor = database.query(DBHelper.TABLE_BLOOD_PRESURE, null, null, null, null, null, null);
                break;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            switch (type) {
                case GLUCOSE:
                case WEIGHT:
                    SingleValue singleValue = cursorToSingleValue(cursor, type);
                    items.add(singleValue);
                    break;
                case BLOOD_PRESSURE:
                    DoubleValue doubleValue = cursorToDoubleValue(cursor, type);
                    items.add(doubleValue);
            }
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

    private DoubleValue cursorToDoubleValue(Cursor cursor, Type type) {
        DoubleValue measurement = new DoubleValue();
        measurement.setId(cursor.getLong(0));
        measurement.setFirstValue(cursor.getInt(1));
        measurement.setSecondValue(cursor.getInt(2));
        measurement.setTimestamp(cursor.getLong(3));
        measurement.setType(type);
        return measurement;
    }
}
