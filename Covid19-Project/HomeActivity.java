package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    AnyChartView anyChartView;
    TextView mTextViewFetchApiData;
    Button mButtonSearch;
    TextView mTextEnterCountry;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        anyChartView = findViewById(R.id.any_chart_view);
        mTextEnterCountry = (EditText)findViewById(R.id.edittext_enterCountry);
        mTextViewFetchApiData = (TextView)findViewById(R.id.fetch_api_data);
        mButtonSearch = (Button)findViewById(R.id.button_search);

        mQueue = Volley.newRequestQueue(this);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence country = mTextEnterCountry.getText();
                String url = "https://api.covid19api.com/live/country/"+country+"?from=2020-04-28T00:00:00Z&to=2020-04-29T01:00:00Z";



                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    System.out.println(response.getJSONObject(0).get("Country"));

                                    int position = 0;
                                    JSONObject stats = response.getJSONObject(position);

                                    String countryName = stats.getString("Country");
                                    Integer confirmed = stats.getInt("Confirmed");
                                    Integer deaths = stats.getInt("Deaths");
                                    Integer recovered = stats.getInt("Recovered");
                                    Integer active = stats.getInt("Active");

                                    DecimalFormat formatter = new DecimalFormat("#,###,###");

                                    mTextViewFetchApiData.setText("Country: " + countryName);
                                    mTextViewFetchApiData.append("\nConfirmed: "+formatter.format(confirmed));
                                    mTextViewFetchApiData.append("\nActive: "+formatter.format(active));
                                    mTextViewFetchApiData.append("\nRecovered: "+formatter.format(recovered));
                                    mTextViewFetchApiData.append("\nDeaths: "+formatter.format(deaths));

                                    Pie pie = AnyChart.pie();
                                    List<DataEntry> dataEntries = new ArrayList<>();

                                    dataEntries.add(new ValueDataEntry("Active",active));
                                    dataEntries.add(new ValueDataEntry("Recovered",recovered));
                                    dataEntries.add(new ValueDataEntry("Deaths",deaths));

                                    pie.data(dataEntries);
                                    pie.title("Total: "+confirmed);
                                    anyChartView.setChart(pie);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                mQueue.add(request);
            }


        });
    }
}
