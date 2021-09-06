package com.example.wather;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.logging.type.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String CITY= "New York";
    String API = "46094af256cb816d2e74c7b991f2f1a9";
    ImageView search;
    EditText etCity;
    String listDate[] = new String[40];
    String listTemp[] = new String[40];
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private Spinner spCity;
    private String[] sCity = {
            "Gdansk",
            "Warszawa",
            "Krakow",
            "Wroclaw",
            "Lodz"
    };

    TextView city,country,time,temp,forecast,humidity,min_temp,max_temp,sunrises,sunsets;
    TextView day1,day2,day3,day4,day5,day6,day7;
    TextView tem1,tem2,tem3,tem4,tem5,tem6,sText;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        {
// CALL ALL ANSWERS :
            city = (TextView) findViewById(R.id.city);
            country = (TextView) findViewById(R.id.country);
            time = (TextView) findViewById(R.id.time);
            temp = (TextView) findViewById(R.id.temp);
            forecast = (TextView) findViewById(R.id.forecast);
            humidity = (TextView) findViewById(R.id.humidity);
            day1 = (TextView) findViewById(R.id.day1);
            day2 = (TextView) findViewById(R.id.day2);
            day3 = (TextView) findViewById(R.id.day3);
            day4 = (TextView) findViewById(R.id.day4);
            day5 = (TextView) findViewById(R.id.day5);
            day6 = (TextView) findViewById(R.id.day6);

            tem1 = (TextView) findViewById(R.id.tem1);
            tem2 = (TextView) findViewById(R.id.tem2);
            tem3 = (TextView) findViewById(R.id.tem3);
            tem4 = (TextView) findViewById(R.id.tem4);
            tem5 = (TextView) findViewById(R.id.tem5);
            tem6 = (TextView) findViewById(R.id.tem6);
            spCity = (Spinner) findViewById(R.id.spinCity);

            // inisialiasi Array Adapter dengan memasukkan string array di atas
            adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_list, sCity);

            // mengeset Array Adapter tersebut ke Spinner
            spCity.setAdapter(adapter);

            // mengeset listener untuk mengetahui saat item dipilih
            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                    CITY = adapter.getItem(i);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            makeJSONObjectRequest();
                        }
                    });

                    thread.start();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void makeJSONObjectRequest() {

        AndroidNetworking.get("https://api.openweathermap.org/data/2.5/forecast?q=" + CITY + "&units=metric&appid=" + API)

                .setTag(this)
                .addPathParameter("userId", "1")
                .setPriority(Priority.HIGH)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        try {
                            String humi_dity = "";
                            String temperature = "";
                            String description = "";
                            Long updatedAt= 0L;
                            String updatedAtText = "";
                            Boolean mFirst = true;
                            String mToday = "";
                            int x=0;

                            JSONArray date = jsonObj.getJSONArray("list");
                            for (int i = 0; i < date.length(); i++) {
                                JSONObject c = date.getJSONObject(i);
                                JSONObject main = c.getJSONObject("main");

//                                Date dt = dateFormatter.parse(c.getString("dt_txt"));
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Date dt = format.parse(c.getString("dt_txt"));
                                String mdt = new SimpleDateFormat("yyy/MM/dd", Locale.ENGLISH).format(dt);

                                if (!mdt.equals(mToday)) {

                                    listDate[x] = c.getString("dt_txt");
                                    listTemp[x] = main.getString("temp");
                                    x+=1;
                                    if (mFirst.equals(true)) {
                                        updatedAtText = "Day of week on " + c.getString("dt_txt") + " : " + new SimpleDateFormat("EEEE").format(dt);

                                        humi_dity = main.getString("humidity");
                                        temperature = main.getString("temp");
                                        JSONArray weather = c.getJSONArray("weather");
                                        JSONObject w = weather.getJSONObject(0);
                                        description = w.getString("description");
                                        mFirst = false;

                                    }

                                }

                                mToday = mdt;
                            }

//// CALL VALUE IN API :
                            JSONObject towm = jsonObj.getJSONObject("city");
                            String city_name = towm.getString("name");
                            city.setText(city_name);
                            country.setText(description);
                            time.setText(updatedAtText);
                            temp.setText(temperature + "°C");
//                            forecast.setText(cast);
                            humidity.setText(humi_dity);

                            day1.setText(mDay(0));
                            day2.setText(mDay(1));
                            day3.setText(mDay(2));
                            day4.setText(mDay(3));
                            day5.setText(mDay(4));
                            day6.setText(mDay(5));
//                            day7.setText(mDay(6));

                            tem1.setText(listTemp[0]);
                            tem2.setText(listTemp[1]);
                            tem3.setText(listTemp[2]);
                            tem4.setText(listTemp[3]);
                            tem5.setText(listTemp[4]);
                            tem6.setText(listTemp[5]);

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
//                        Log.d(TAG, "onResponse object : " + response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    private String mDay(int i) throws ParseException {

        String x = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = format.parse(listDate[i]);
        x = new SimpleDateFormat("EEE").format(dt);
        return x;
    }


    public void sendTown(View view) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.town, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        sText = (EditText) mView.findViewById(R.id.town);


        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                       try {


                       } catch (Exception e) {
                           Log.d(TAG, "onError errorDetail : " + e.toString());

                       }
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

    }
}