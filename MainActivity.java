package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    double absoluteZeroInCelcius  = -273.15;
    TextView resultView;
    EditText cityEdit;


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {  //var args

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) // кінець.   'e'  // ASCII  UTF-8
                {
                  char current = (char)data;
                  result += current;
                  data = reader.read();
                }


                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
             }catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("Website content", result);

            try {
                JSONObject root = new JSONObject(result);
                String weatherStr = root.getString("weather");
                JSONArray weatherArray = new JSONArray(weatherStr);

                for (int i = 0; i < weatherArray.length(); i++) // clouds, rain,
                {
                    JSONObject weatherFeature =  weatherArray.getJSONObject(i);
                    String weatherMain = weatherFeature.getString("main");
                    Log.i("Main", weatherMain );
                    resultView.setText(weatherMain);
                }

                String mainTempStr = root.getString("main");
                JSONObject mainTempObj = new JSONObject(mainTempStr);
                double tempKelvin = mainTempObj.getDouble("temp");
                double tempCelcius = tempKelvin + absoluteZeroInCelcius;
                String weatherResult = resultView.getText().toString();
                double roundDbl = Math.round(tempCelcius*100.0)/100.0;
                resultView.setText(weatherResult + "\n temp:" + Double.toString(roundDbl) + "\u00B0" +  " C");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = (TextView) findViewById(R.id.resultView);

        //  https://samples.openweathermap.org/data/2.5/forecast?id=7350&appid=05dced0947a702f8a950e8146cca353a
        //Szombathely  - сомбатхей

        EditText cityEdit = (EditText)findViewById(R.id.cityEdit);


        cityEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press


                    Button button = (Button)findViewById(R.id.button);
                    button.performClick();

                    //Toast.makeText(MainActivity.this, cityEdit.getText(), Toast.LENGTH_SHORT).show();


                    return true;
                }
                return false;
            }
        });
    }


    public void getWeather(View view)
    {

        EditText cityEdit = (EditText)findViewById(R.id.cityEdit);

        String cityName = cityEdit.getText().toString();
        String urlRequest = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=05dced0947a702f8a950e8146cca353a";

        DownloadTask task = new DownloadTask();
        task.execute(urlRequest);
    }



}