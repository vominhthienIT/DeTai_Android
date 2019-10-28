package com.example.appweather;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/*https://stackoverflow.com/questions/22741632/get-current-location-using-gps-in-android*/


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    EditText edit_thanhpho;
    TextView tv_tenthanhpho, tv_quocgia, tv_ngayhientai, tv_nhietdo, tv_doam, tv_may, tv_gio, tv_trangthai
    ,tv_nhietngaymai,tv_doamngaymai,tv_mayngaymai,tv_giongaymai,tv_trangthaingaymai,tv_datemai;
    ImageView imv_timkiem, imv_tranthai;
    String vitriht;
    LinearLayout main;
    private Location location;
    private boolean bo =false;
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;
    GestureDetector gestureDetector;
    // Đối tượng tương tác với Google API
    private GoogleApiClient gac;
    // Đối tượng tương tác với Google API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ketnoi();
        //nhacnen();
        //getLocation();
        //loadnextdate(Double.toString(location.getLatitude()),Double.toHexString(location.getLongitude()));
        if (checkPlayServices()) {
           buildGoogleApiClient();
        }
        ev_clicktim();
        ev_chuyentrang();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Đã kết nối với google api, lấy vị trí
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        gac.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Lỗi kết nối: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }
    public void ev_chuyentrang(){
        gestureDetector = new GestureDetector(this,new Mygesture());
        main.setOnTouchListener(new View.OnTouchListener() {
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
            if(e1.getX()-e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) >  SWIPE_VELOCITY_THRESHOLD ){
               chuyentrang();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
    private void chuyentrang() {
        if(bo==false||edit_thanhpho.getText().toString().isEmpty()) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            intent.putExtra("name", edit_thanhpho.getText().toString());
            intent.putExtra("vitriht", vitriht);
            startActivity(intent);
        }else
            Toast.makeText(MainActivity.this,"Không tìm thấy vị trí",Toast.LENGTH_LONG).show();

    }

    public void loadnextdate(String lat,String lon) {
        String url;

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String tp = edit_thanhpho.getText().toString();
        if(tp.isEmpty()) {
            url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&appid=59952d62b2db0ba8108e2e11126cde00&lang=vi";
        }
        else{
            url = "https://api.openweathermap.org/data/2.5/forecast?q=" + tp + "&lang=vi&appid=59952d62b2db0ba8108e2e11126cde00";
        }
         StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray objList = object.getJSONArray("list");
                    JSONObject objnd = objList.getJSONObject(7);

                    String day = objnd.getString("dt");
                    Long d = Long.valueOf(day);
                    Locale vn = new Locale("vi", "VN");
                    Date date = new Date(d * 1000L);
                    DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, vn);
                    String Da = format.format(date);
                    tv_datemai.setText(Da);

                    JSONArray objW = objnd.getJSONArray("weather");
                    JSONObject objwt = objW.getJSONObject(0);
                    String des = objwt.getString("description");
                    tv_trangthaingaymai.setText(des);

                    JSONObject objtemp = objnd.getJSONObject("main");
                    String temp = objtemp.getString("temp");
                    Double nd = Double.valueOf(temp);
                   // nd = nd / 10;
                    NumberFormat fm = new DecimalFormat("#0");
                    tv_nhietngaymai.setText(fm.format(nd) + "°C");

                    String hum = objtemp.getString("humidity");
                    Double da = Double.valueOf(hum);
                    tv_doamngaymai.setText(fm.format(da) + "%");

                    JSONObject objWind = objnd.getJSONObject("wind");
                    String wind = objWind.getString("speed");
                    tv_giongaymai.setText(wind + "m/s");

                    JSONObject objCloud = objnd.getJSONObject("clouds");
                    String clouds = objCloud.getString("all");
                    tv_mayngaymai.setText(clouds + "%");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Không tìm thấy vị trí",Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void ev_clicktim() {
        imv_timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tp = edit_thanhpho.getText().toString();
                if(tp.isEmpty()){
                    Toast.makeText(MainActivity.this,"Hãy nhập tên thành phố",Toast.LENGTH_LONG).show();
                }else {
                    load(tp);
                    loadnextdate(Double.toString(location.getLatitude()),Double.toHexString(location.getLongitude()));
                }
            }
        });
    }

    public void loadlocal(String lat, String lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=59952d62b2db0ba8108e2e11126cde00&lang=vi";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("kq", response);
                    JSONObject object = new JSONObject(response);

                    String name = object.getString("name");
                    tv_tenthanhpho.setText(name);
                    vitriht = name;
                    JSONObject objSys = object.getJSONObject("sys");
                    String country = objSys.getString("country");
                    tv_quocgia.setText(country);

                    String day = object.getString("dt");
                    Long d = Long.valueOf(day);
                    Locale vn = new Locale("vi", "VN");
                    Date date = new Date(d * 1000L);
                    DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, vn);
                    String Da = format.format(date);
                    tv_ngayhientai.setText(Da);

                    JSONArray jsonarray = object.getJSONArray("weather");
                    JSONObject jow = jsonarray.getJSONObject(0);
                    String cloud = jow.getString("description");
                    tv_trangthai.setText(cloud);

                    String icon = jow.getString("icon");
                    Picasso.with(MainActivity.this).load("https://openweathermap.org/img/w/" + icon + ".png").into(imv_tranthai);

                    JSONObject objMain = object.getJSONObject("main");
                    String temp = objMain.getString("temp");
                    Double nd = Double.valueOf(temp);

                    NumberFormat fm = new DecimalFormat("#0");
                    tv_nhietdo.setText(fm.format(nd) + "°C");

                    String hum = objMain.getString("humidity");
                    Double da = Double.valueOf(hum);
                    tv_doam.setText(fm.format(da) + "%");

                    JSONObject objWind = object.getJSONObject("wind");
                    String wind = objWind.getString("speed");
                    tv_gio.setText(wind + "m/s");

                    JSONObject objCloud = object.getJSONObject("clouds");
                    String clouds = objCloud.getString("all");
                    tv_may.setText(clouds + "%");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Không tìm thấy vị trí",Toast.LENGTH_LONG).show();
                    }

                });
        requestQueue.add(stringRequest);
    }

    public void load(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=59952d62b2db0ba8108e2e11126cde00&lang=vi";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("kq", response);
                    JSONObject object = new JSONObject(response);

                    String name = object.getString("name");
                    tv_tenthanhpho.setText(name);

                    JSONObject objSys = object.getJSONObject("sys");
                    String country = objSys.getString("country");
                    tv_quocgia.setText(country);

                    String day = object.getString("dt");
                    Long d = Long.valueOf(day);
                    Locale vn = new Locale("vi", "VN");
                    Date date = new Date(d * 1000L);
                    DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, vn);
                    String Da = format.format(date);
                    tv_ngayhientai.setText(Da);

                    JSONArray jsonarray = object.getJSONArray("weather");
                    JSONObject jow = jsonarray.getJSONObject(0);
                    String cloud = jow.getString("description");
                    tv_trangthai.setText(cloud);

                    String icon = jow.getString("icon");
                    Picasso.with(MainActivity.this).load("https://openweathermap.org/img/w/" + icon + ".png").into(imv_tranthai);

                    JSONObject objMain = object.getJSONObject("main");
                    String temp = objMain.getString("temp");
                    Double nd = Double.valueOf(temp);

                    NumberFormat fm = new DecimalFormat("#0");
                    tv_nhietdo.setText(fm.format(nd) + "°C");

                    String hum = objMain.getString("humidity");
                    Double da = Double.valueOf(hum);
                    tv_doam.setText(fm.format(da) + "%");

                    JSONObject objWind = object.getJSONObject("wind");
                    String wind = objWind.getString("speed");
                    tv_gio.setText(wind + "m/s");

                    JSONObject objCloud = object.getJSONObject("clouds");
                    String clouds = objCloud.getString("all");
                    tv_may.setText(clouds + "%");

                    bo=false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(MainActivity.this,"Không tìm thấy vị trí",Toast.LENGTH_LONG).show();
                        bo=true;
                    }

                });
        requestQueue.add(stringRequest);
    }

    public void ketnoi() {
        edit_thanhpho = (EditText) findViewById(R.id.edit_thanhpho);
        tv_quocgia = (TextView) findViewById(R.id.tv_quocgia);
        tv_ngayhientai = (TextView) findViewById(R.id.tv_ngayhientai);
        tv_tenthanhpho = (TextView) findViewById(R.id.tv_tenthanhpho);
        tv_nhietdo = (TextView) findViewById(R.id.tv_nhietdo);
        tv_doam = (TextView) findViewById(R.id.tv_doam);
        tv_may = (TextView) findViewById(R.id.tv_may);
        tv_gio = (TextView) findViewById(R.id.tv_gio);
        imv_timkiem = (ImageView) findViewById(R.id.imv_timkiem);
        imv_tranthai = (ImageView) findViewById(R.id.imv_trangthai);
        tv_trangthai = (TextView) findViewById(R.id.tv_trangthai);
        tv_nhietngaymai = (TextView)findViewById(R.id.tv_nhietngaymai);
        tv_doamngaymai = (TextView)findViewById(R.id.tv_doamngaymai);
        tv_mayngaymai = (TextView)findViewById(R.id.tv_mayngaymai);
        tv_trangthaingaymai = (TextView)findViewById(R.id.tv_trangthaingaymai);
        tv_datemai = (TextView)findViewById(R.id.tv_datemai);
        tv_giongaymai = (TextView)findViewById(R.id.tv_giongaymai);
        main = (LinearLayout)findViewById(R.id.main);
    }

    public void nhacnen() {
        MediaPlayer media = MediaPlayer.create(MainActivity.this, R.raw.dubao);
        media.setLooping(true);
        media.start();

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Kiểm tra quyền hạn
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            location = LocationServices.FusedLocationApi.getLastLocation(gac);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                loadlocal(Double.toString(latitude), Double.toHexString(longitude));
                loadnextdate(Double.toString(latitude),Double.toHexString(longitude));

            }
        }
    }

    /**
     * Tạo đối tượng google api client
     */
    protected synchronized void buildGoogleApiClient() {
        if (gac == null) {
            gac = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    /**
     * Phương thức kiểm chứng google play services trên thiết bị
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1000).show();
            } else {
                Toast.makeText(this, "Thiết bị này không hỗ trợ.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
}