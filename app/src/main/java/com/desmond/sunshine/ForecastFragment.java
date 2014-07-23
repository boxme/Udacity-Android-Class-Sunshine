package com.desmond.sunshine;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.desmond.sunshine.data.WeatherContract;
import com.desmond.sunshine.data.WeatherContract.LocationEntry;
import com.desmond.sunshine.data.WeatherContract.WeatherEntry;

import java.util.Date;

/**
 * Created by desmond on 16/7/14.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ForecastFragment.class.getSimpleName();
    private ForecastAdapter mForecastAdapter;

    private static final int FORECAST_LOADER = 0;
    private static final String LOCATION_KEY = "location";
    private String mLocation;

    private static final String POSITION_KEY = "position";
    private int mPosition;
    private ListView mListView;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have _id columns)
            // On the one hand, that's annoying. On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_CONDITION_ID = 5;
    public static final int COL_LOCATION_SETTING = 6;

    private Callback mListener;

    private boolean mUseTodayLayout;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }

    @Override
    public void onAttach(Activity activity) {
        mListener = (Callback) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(LOCATION_KEY)) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapter = new ForecastAdapter(
                getActivity(),
                null,
                0
        );

        // Activity#onCreate might called before fragment#onCreateView
        // when adapter is null.
        // So we set the mUseTodayLayout value to the adapter here too
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        // Check out the source code of SimpleCursorAdapter
        // The code below is for use with a SimpleCursorAdapter
//        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                boolean isMetric = Utility.isMetric(getActivity());
//                switch (columnIndex) {
//                    case COL_WEATHER_MAX_TEMP:
//                    case COL_WEATHER_MIN_TEMP:
//                        // we have to do some formatting and possibly a conversion
//                        ((TextView) view).setText(Utility.formatTemperature(
//                                cursor.getDouble(columnIndex), isMetric));
//                        return true;
//                    case COL_WEATHER_DATE:
//                        String dateString = cursor.getString(columnIndex);
//                        TextView dateView = (TextView) view;
//                        dateView.setText(Utility.formatDate(dateString));
//                        return true;
//                }
//
//                // If return false, 2 types of binding will occur
//                // 1: view is a TextView, SimpleCursorAdapter#setViewText(TextView, String) is called
//                // 2: view is a ImageView, SimpleCursorAdapter#setViewImage(ImageView v, String value) is called
//                return false;
//            }
//        });


        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mListener.onItemSelected(cursor.getString(COL_WEATHER_DATE));
                }
                mPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {;
        new FetchWeatherTask(getActivity())
                .execute(Utility.getPreferredLocation(getActivity()));
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(useTodayLayout);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new loader needs to be created. This
        // fragment only uses one loader, so we don't care about checking the id

        // To only show current and future dates, get the String representation for today
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order: Ascending, by date
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(mLocation, startDate);

        // Now create and return a CursorLoader that will take care of creating a Cursor
        // for the data being displayed
        return new CursorLoader(
                getActivity(),
                weatherLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Use the data from the cursor that the loader just loaded
        mForecastAdapter.swapCursor(data);

        if (!mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        } else if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to
            // restore to, do so now
//            mListView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Put in null to clear the data
        mForecastAdapter.swapCursor(null);
    }
}
