package com.punitexample.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView resultText;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultText = findViewById(R.id.resultText);
        editText = findViewById(R.id.editText);
    }
    public void getWeather(View view){
        DownloadTask task = new DownloadTask();
        try {
            String encodeCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q="+ encodeCityName +"&appid=b6907d289e10d714a6e88b30761fae22").get();
            InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"weather not found :(",Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void ,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                Log.i("debug","1");
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"weather not found :(",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject obj = new JSONObject(s);
                String weatherInfo = obj.getString("weather");
                String tempInfo = "["+obj.getString("main")+"]";
                JSONArray weatherArray = new JSONArray(weatherInfo);
                JSONArray tempArray = new JSONArray(tempInfo);
                String message = "";
                String main = "";
                String des = "";
                String temp = "";
                String hum="";
                for(int i=0;i<weatherArray.length();i++) {
                    JSONObject part1 = weatherArray.getJSONObject(i);
                    main = part1.getString("main");
                    des = part1.getString("description");
                }

                for(int i=0;i<tempArray.length();i++)
                {
                    JSONObject part2 = tempArray.getJSONObject(i);
                    temp = part2.getString("temp");
                    hum = part2.getString("humidity");

                }
                if(!main.equals("") && !des.equals("") && !temp.equals("")&& !hum.equals("")){
                    message += "Main:"+main+"\n"+"Description:"+des+"\n"+"Temperature:"+temp+"*C"+"\n"+"Humidity:"+hum;
                }else{
                    Toast.makeText(getApplicationContext(),"weather not found :(",Toast.LENGTH_SHORT).show();
                }

                if(!message.equals("")) {
                    resultText.setText(message);
                    Log.i("tag",message);
                }else{
                    Toast.makeText(getApplicationContext(),"weather not found :(",Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"weather not found :(",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
