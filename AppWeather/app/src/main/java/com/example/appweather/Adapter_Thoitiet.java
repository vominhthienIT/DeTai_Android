package com.example.appweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Thoitiet extends BaseAdapter {
    Context context;
    ArrayList<ThoiTiet> arrayList;

    public Adapter_Thoitiet(Context context, ArrayList<ThoiTiet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.listview_custom, null);
        ThoiTiet thoiTiet = arrayList.get(i);
        TextView ngaythang = (TextView) view.findViewById(R.id.tv_ngay);
        TextView trangthai = (TextView) view.findViewById(R.id.tv_tranthai);
        TextView max = (TextView) view.findViewById(R.id.tv_max);
        TextView min = (TextView) view.findViewById(R.id.tv_min);
        ImageView imv_trangthai = (ImageView) view.findViewById(R.id.imv_trangthai);

        ngaythang.setText(thoiTiet.ngay);
        max.setText(thoiTiet.nhietDoMax + "°C");
        min.setText(thoiTiet.nhietDoMin + "°C");
        trangthai.setText(thoiTiet.trangThai);

        Picasso.with(context).load("https://openweathermap.org/img/w/" + thoiTiet.hinhAnh + ".png").into(imv_trangthai);
        return view;
    }
}
