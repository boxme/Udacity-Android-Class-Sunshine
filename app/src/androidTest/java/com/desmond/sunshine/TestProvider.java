package com.desmond.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.desmond.sunshine.data.WeatherContract.LocationEntry;
import com.desmond.sunshine.data.WeatherContract.WeatherEntry;
import com.desmond.sunshine.data.WeatherDbHelper;

/**
 * Test runner will execute all the functions in this class that start with "test"
 * and in the order by which they are declared here.
 * Each function should have at least one assert function
 */
public class TestProvider extends AndroidTestCase {

    public static final String TAG = TestProvider.class.getSimpleName();

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet())
            destination.put(key, source.getAsString(key));
    }

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    public void testInsertReadProvider() {
        // If there's an error in those massive SQL table creation Strings,
        // errors will be throw here when you try to get a writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Create a new map of values, where columns names are the keys
        ContentValues testLocationValues = TestDb.createLocationValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testLocationValues);

        // Verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        // A cursor is your primary interface to the query results.
        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(locationCursor, testLocationValues);
        locationCursor.close();

        // Now see if we can successfully query if we include the row id
        locationCursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(locationCursor, testLocationValues);
        locationCursor.close();

        ContentValues testWeatherValues = createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null,  testWeatherValues);

        assertTrue(weatherRowId != -1);
        Log.d(TAG, "Weather Row Id is " + weatherRowId);

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,       // leaving "columns" null just returns all the columns
                null,       // columns for "where" clause
                null,       // values for "where" clause
                null        // columns to group by
        );

        TestDb.validateCursor(weatherCursor, testWeatherValues);
        weatherCursor.close();

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(testWeatherValues, testLocationValues);

        // Get the joined Weather and Location data
        Cursor weatherAndLocationCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestDb.TEST_LOCATION),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(weatherAndLocationCursor, testWeatherValues);
        weatherAndLocationCursor.close();

        // Get the joined Weather and Location data with a start date
        weatherAndLocationCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(weatherAndLocationCursor, testWeatherValues);
        weatherAndLocationCursor.close();

        weatherAndLocationCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(
                        TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(weatherAndLocationCursor, testLocationValues);
        weatherAndLocationCursor.close();

        dbHelper.close();
    }

    public void testGetType() {
        // content://com.desmond.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.desmond.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "97074";
        // content://com.desmond.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.desmond.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.desmond.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.desmond.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.desmond.sunshine.app/location
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.desmond.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.desmond.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.desmond.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}
