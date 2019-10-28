package com.example.appweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {
    TextView tv_thanhpho;
    ListView listview;
    ImageView imv_back;
    Adapter_Thoitiet adapter_thoitiet;
    ArrayList<ThoiTiet> array_thoitiet;
    String tenthanhpho;
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;
    GestureDetector gestureDetector;
    LinearLayout main2;
    MainActivity mainActivity;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ketnoi();
        Intent intent = getIntent();
        String thanhpho = intent.getStringExtra("name");

        if (thanhpho.equalsIgnoreCase("")) {
            tenthanhpho = intent.getStringExtra("vitriht");
            loadFiveDay(tenthanhpho);
        } else {
            tenthanhpho = thanhpho;
            loadFiveDay(tenthanhpho);
        }
        ev_chuyentrang();
    }

    private void nhacnen() {
        MediaPlayer media = MediaPlayer.create(Main2Activity.this, R.raw.dubao);
        media.setLooping(true);
        media.start();
    }

    private void ev_imageViewBack() {
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ketnoi() {
        tv_thanhpho = (TextView) findViewById(R.id.tv_thanhpho);
        listview = (ListView) findViewById(R.id.listview);
        array_thoitiet = new ArrayList<ThoiTiet>();
        adapter_thoitiet = new Adapter_Thoitiet(Main2Activity.this, array_thoitiet);
        listview.setAdapter(adapter_thoitiet);
        main2 = (LinearLayout)findViewById(R.id.main2);
    }

    public void loadFiveDay(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(Main2Activity.this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&lang=vi&appid=59952d62b2db0ba8108e2e11126cde00";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                    String name = jsonObjectCity.getString("name");
                    tv_thanhpho.setText(name);

                    JSONArray jsonArray_List = jsonObject.getJSONArray("list");
                    for (int i = 8; i <= jsonArray_List.length(); i++) {
                        if (i % 7 == 0) {
                            JSONObject jsonObjectList = jsonArray_List.getJSONObject(i);
                            String ngay = jsonObjectList.getString("dt");
                            Long d = Long.valueOf(ngay);
                            Locale vn = new Locale("vi", "VN");
                            Date date = new Date(d * 1000L);
                            DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, vn);
                            String Da = format.format(date);

                            JSONObject jsonObject_nhietdo = jsonObjectList.getJSONObject("main");
                            String max = jsonObject_nhietdo.getString("temp_max");
                            String min = jsonObject_nhietdo.getString("temp_min");
                            Double a = Double.valueOf(max);
                            a = a / 10;
                            Double b = Double.valueOf(min);
                            b = b / 10;
                            String max_parse = String.valueOf(a.intValue());
                            String min_parse = String.valueOf(b.intValue());

                            JSONArray jsonArray_weather = jsonObjectList.getJSONArray("weather");
                            JSONObject jsonObject_weather = jsonArray_weather.getJSONObject(0);
                            String trangthai = jsonObject_weather.getString("description");
                            String icon = jsonObject_weather.getString("icon");

                            array_thoitiet.add(new ThoiTiet(Da, trangthai, icon, max_parse, min_parse));
                        }
                    }
                    adapter_thoitiet.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("loicmnr", error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }
    public void ev_chuyentrang(){
        gestureDetector = new GestureDetector(this,new Main2Activity.Mygesture());
        main2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }
    class Mygesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX()-e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) >  SWIPE_VELOCITY_THRESHOLD ){
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
