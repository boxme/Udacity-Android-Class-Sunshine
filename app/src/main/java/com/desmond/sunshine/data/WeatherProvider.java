package com.desmond.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by desmond on 19/7/14.
 */
public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;

    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static WeatherDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    private static final String sLocationSettingSelection;
    private static final String sLocationSettingWithStartDateSelection;
    private static final String sLocationSettingWithDaySelection;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +  // Specify the column for _ID
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + // with the table name to differentiate
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);

        sLocationSettingSelection =
                WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                        " = ? ";

        sLocationSettingWithStartDateSelection =
                WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                        " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

        sLocationSettingWithDaySelection =
                WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                        " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";
    }


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[] {locationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[] {locationSetting, startDate};
        }

        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingwithDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String day = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDaySelection,
                new String[] {locationSetting, day},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            // weather/*/*
            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = getWeatherByLocationSettingwithDate(uri, projection, sortOrder);
                break;

            // weather/*
            case WEATHER_WITH_LOCATION:
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;

            // weather
            case WEATHER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // location/*
            case LOCATION_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // location
            case LOCATION:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Cursor to register a content observer to watch for changes in this uri and its descendants
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    // Return the Mime Type for the given URI
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found. The code passed into the constructor represents the code to return for the root
        // URI. It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code
        // The 5 URIs matched below depend on the helper methods in WeatherContract.buildWeatherLocation & etc
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }
}
