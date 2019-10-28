package com.example.appweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
        ev_imageViewBack();
        //nhacnen();
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
        imv_back = (ImageView) findViewById(R.id.imv_back);
        array_thoitiet = new ArrayList<ThoiTiet>();
        adapter_thoitiet = new Adapter_Thoitiet(Main2Activity.this, array_thoitiet);
        listview.setAdapter(adapter_thoitiet);
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
                    for (int i = 1; i <= jsonArray_List.length(); i++) {
                        if (i % 8 == 0) {
                            JSONObject jsonObjectList = jsonArray_List.getJSONObject(i-1);
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

}
