package com.example.rajesh.whatstheweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    TextView textView2;
    EditText editText;

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void getWeather(View view) {
        boolean net = isOnline();
        if (net == true) {
            DownloadTask task = new DownloadTask();
            try {
                String encodeCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                task.execute("http://openweathermap.org/data/2.5/weather?q=" + encodeCityName + "&appid=b6907d289e10d714a6e88b30761fae22").get();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :)", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :)", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content:", weatherInfo);
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    message += main + ": " + description + "\n";
                }
                String mainInfo = jsonObject.getString("main");
                JSONObject mainObject = new JSONObject(mainInfo);
                String windInfo = jsonObject.getString("wind");
                JSONObject windObject = new JSONObject(windInfo);
                String sysInfo = jsonObject.getString("sys");
                JSONObject sysObject = new JSONObject(sysInfo);
                String temp = mainObject.getString("temp");
                String humidity = mainObject.getString("humidity");
                String country = sysObject.getString("country");
                int windSpeed = (windObject.getInt("speed") * 18/5);
                message += "Temperature: " + temp + "°C" + "\n"
                        + "Humidity: " + humidity + "%" + "\n" + "Wind: " + Integer.toString(windSpeed) + " km/h" + "\n"
                        + "Country: " + country + "\n";
                textView2.setText(message);
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = findViewById(R.id.textView2);
        editText = findViewById(R.id.editText);
    }
}
