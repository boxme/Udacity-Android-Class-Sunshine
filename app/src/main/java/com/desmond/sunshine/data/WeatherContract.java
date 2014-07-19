package com.desmond.sunshine.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database
 */
public class WeatherContract {

    /**
     * Inner class that defines the table contents of the weather table
     */
    public static final class WeatherEntry implements BaseColumns {

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
    }

    /**
     * Inner class that defines the table contents of the the location table
     */
    public static final class LocationEntry implements BaseColumns {

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
    }
}
