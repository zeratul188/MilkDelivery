package com.example.mixroidminigames.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mixroidminigames.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MilkAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MilkHouse> milkHouses;
    private Activity activity;

    public MilkAdapter(Context context, ArrayList<MilkHouse> milkHouses, Activity activity) {
        this.context = context;
        this.milkHouses = milkHouses;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return milkHouses.size();
    }

    @Override
    public Object getItem(int position) {
        return milkHouses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.delivery_list, null);

        TextView txtName = convertView.findViewById(R.id.txtName);
        LinearLayout layoutMilk = convertView.findViewById(R.id.layoutMilk);
        LinearLayout layoutPassword = convertView.findViewById(R.id.layoutPassword);
        TextView txtPassword = convertView.findViewById(R.id.txtPassword);

        txtName.setText(milkHouses.get(position).getName());

        if (milkHouses.get(position).isPassword()) {
            layoutPassword.setVisibility(View.VISIBLE);
            txtPassword.setText(milkHouses.get(position).getPassword());
        } else layoutPassword.setVisibility(View.GONE);

        layoutMilk.removeAllViews();
        for (int i = 0; i < milkHouses.get(position).getMilks().size(); i++) {
            View view = activity.getLayoutInflater().inflate(R.layout.milk_layout, null);

            TextView txtType = view.findViewById(R.id.txtType);
            TextView txtCount = view.findViewById(R.id.txtCount);

            txtType.setText(milkHouses.get(position).getMilks().get(i).getType());
            txtCount.setText(Integer.toString(milkHouses.get(position).getMilks().get(i).getCount()));

            layoutMilk.addView(view);
        }

        return convertView;
    }
}
