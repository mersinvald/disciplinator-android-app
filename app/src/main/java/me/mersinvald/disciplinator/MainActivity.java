package me.mersinvald.disciplinator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    TextView statusTV;
    TextView activeMinutesTV;
    TextView debtTV;
    ListView dayLogLV;

    DayLogListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        statusTV = findViewById(R.id.statusTV);
        activeMinutesTV = findViewById(R.id.activeMinutesStatusTV);
        debtTV = findViewById(R.id.debtStatusTV);
        dayLogLV = findViewById(R.id.dayLogLV);
        adapter = new DayLogListViewAdapter(this);

        dayLogLV.setAdapter(adapter);

        // Fix refresh-on-scrolling-list-up behavior
        dayLogLV.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int topRowVerticalPosition = (dayLogLV == null || dayLogLV.getChildCount() == 0) ? 0 : dayLogLV.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        new loadDataTask().execute();
    }

    @Override
    public void onRefresh() {
        new loadDataTask().execute();
    }

    protected class loadDataTask extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {
            swipeRefreshLayout.setRefreshing(true);
            String str = "https://api.disciplinator.mersinvald.me/";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "loadDataTask", ex);
                swipeRefreshLayout.setRefreshing(false);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Failed to get an update.",
                                Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            if(response != null)
            {
                try {
                    Log.e("App", "Success: " + response.toString() );
                    CurrentStatus status = new CurrentStatus(response.getJSONObject("state"));
                    DayLog dayLog = new DayLog(response.getJSONArray("dayLog"));

                    // Update status
                    String text;
                    int color;
                    if (status.state.equals("normal")) {
                        text = getString(R.string.state_normal);
                    } else if (status.state.equals("debtCollection")) {
                        text = getString(R.string.state_debt);
                    } else if (status.state.equals("debtCollectionPaused")) {
                        text = getString(R.string.state_debt_paused);
                    } else {
                        text = getString(R.string.state_unknown);
                    }


                    statusTV.setText(text);
                    //statusTV.setTextColor(status.color());

                    activeMinutesTV.setText(getString(R.string.active_minutes, status.activeMinutes));
                    debtTV.setText(getString(R.string.debt, status.debt));

                    // Update day log
                    adapter.clear();
                    for (HourLog hourLog: dayLog.hours) {
                        adapter.add(hourLog);
                    }

                    dayLogLV.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }
}
