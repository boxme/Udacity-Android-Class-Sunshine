package com.desmond.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database
 */
public class WeatherContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "com.desmond.sunshine.app";

    // Use CONTENT AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    /**
     * Inner class that defines the table contents of the weather table
     */
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + PATH_WEATHER;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south). Stored as floats
        public static final String COLUMN_DEGREES = "degrees";

        // Access a single row in the Weather table
        public static Uri buildWeatherUri(long id) {
            //  ContentUris contains convenience methods for appending id values to a URI
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {

            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(
                String locationSetting, String date) {

            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }

    /**
     * Inner class that defines the table contents of the the location table
     */
    public static final class LocationEntry implements BaseColumns {

        // Base location to search for the location data with a Content Provider
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        // Return multiple rows
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Return a single row
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        // Location name, stored as Text in more human readable location string
        public static final String COLUMN_CITY_NAME = "city_name";

        // The location setting string is what will be sent to openweathermap
        // as the location query
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // In order to uniquely pinpoint the location on the map when we launch the map
        // intent, we store the latitude and longitude as returned by openweathermap
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
