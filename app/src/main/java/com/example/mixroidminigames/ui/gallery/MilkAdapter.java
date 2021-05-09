package com.example.mixroidminigames.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mixroidminigames.AlertDialogMethods;
import com.example.mixroidminigames.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MilkAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MilkHouse> milkHouses;
    private Activity activity;
    private FirebaseDatabase mDatabase;
    private DatabaseReference milkRef;
    private TextView txtHouse = null;

    private String week = "null", path = "null";

    public MilkAdapter(Context context, ArrayList<MilkHouse> milkHouses, Activity activity) {
        this.context = context;
        this.milkHouses = milkHouses;
        this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void setReference(String week, String profile) {
        milkRef = mDatabase.getReference("Members/"+profile+"/"+week);
        this.week = week;
        path = "Members/"+profile+"/"+week;
    }

    public void setTxtHouse(TextView txtHouse) {
        this.txtHouse = txtHouse;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.delivery_list, null);

        final TextView txtName = convertView.findViewById(R.id.txtName);
        LinearLayout layoutMilk = convertView.findViewById(R.id.layoutMilk);
        LinearLayout layoutPassword = convertView.findViewById(R.id.layoutPassword);
        TextView txtPassword = convertView.findViewById(R.id.txtPassword);
        ImageButton btnCommit = convertView.findViewById(R.id.btnCommit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

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

        final int index = position;
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialogMethods adm = new AlertDialogMethods(context);
                View view = activity.getLayoutInflater().inflate(R.layout.answerdialog, null);

                TextView txtView = view.findViewById(R.id.txtView);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                txtView.setText("\""+milkHouses.get(index).getName()+"\" 배달 완료하시겠습니까?");

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adm.alertDismiss();
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        milkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String key = "null";
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    for (DataSnapshot data2 : data.getChildren()) {
                                        if (data2.getKey().equals("name")) {
                                            if (data2.getValue().toString().equals(milkHouses.get(index).getName())) {
                                                key = data.getKey();
                                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                                taskMap.put("isdelivery", true);
                                                milkRef.child(key).updateChildren(taskMap);
                                                toast("\""+milkHouses.get(index).getName()+"\" 배달 완료하였습니다.");
                                                milkHouses.remove(index);
                                                int house = Integer.parseInt(txtHouse.getText().toString());
                                                if (house > 0) house--;
                                                if (txtHouse != null) txtHouse.setText(Integer.toString(house));
                                                notifyDataSetChanged();
                                                adm.alertDismiss();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                adm.setView(view);
                adm.showDialog(false);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MilkEditActivity.class);
                intent.putExtra("name", milkHouses.get(position).getName());
                intent.putExtra("path", path);
                intent.putExtra("week", week);
                context.startActivity(intent);

                /*final AlertDialogMethods adm = new AlertDialogMethods(context);
                View view = activity.getLayoutInflater().inflate(R.layout.answerdialog, null);

                TextView txtView = view.findViewById(R.id.txtView);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                txtView.setText("\""+milkHouses.get(index).getName()+"\"를 삭제하시겠습니까?");

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adm.alertDismiss();
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        milkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String key = "null";
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    for (DataSnapshot data2 : data.getChildren()) {
                                        if (data2.getKey().equals("name")) {
                                            if (data2.getValue().toString().equals(milkHouses.get(index).getName())) {
                                                key = data.getKey();
                                                milkRef.child(key).removeValue();
                                                toast("\""+milkHouses.get(index).getName()+"\"를 삭제하였습니다.");
                                                milkHouses.remove(index);
                                                int house = Integer.parseInt(txtHouse.getText().toString());
                                                if (house > 0) house--;
                                                if (txtHouse != null) txtHouse.setText(Integer.toString(house));
                                                notifyDataSetChanged();
                                                adm.alertDismiss();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                adm.setView(view);
                adm.showDialog(false);*/
            }
        });

        return convertView;
    }

    private void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
