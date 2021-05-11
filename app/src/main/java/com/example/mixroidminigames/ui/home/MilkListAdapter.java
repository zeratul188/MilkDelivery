package com.example.mixroidminigames.ui.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mixroidminigames.R;

import java.util.ArrayList;

public class MilkListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MilkList> milkLists;

    public MilkListAdapter(Context context, ArrayList<MilkList> milkLists) {
        this.context = context;
        this.milkLists = milkLists;
    }

    @Override
    public int getCount() {
        return milkLists.size();
    }

    @Override
    public Object getItem(int position) {
        return milkLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.milk_list_list, null);

        TextView txtType = convertView.findViewById(R.id.txtType);
        TextView txtCount = convertView.findViewById(R.id.txtCount);

        txtType.setText(milkLists.get(position).getType());
        txtCount.setText(Integer.toString(milkLists.get(position).getCount()));

        return convertView;
    }
}
