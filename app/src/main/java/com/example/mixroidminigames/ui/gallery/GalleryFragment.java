package com.example.mixroidminigames.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mixroidminigames.AlertDialogMethods;
import com.example.mixroidminigames.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private RadioGroup rgWeek;
    private RadioButton[] rdoWeek = new RadioButton[7];
    private TextView txtHouse, txtHouseMax;
    private ListView listView;
    private FloatingActionButton fabAdd;
    private ImageButton btnReset, btnList;

    private MilkAdapter milkAdapter;
    private ArrayList<MilkHouse> milkHouses;
    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    private String[] week_name = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private int position = 0, pick = 0;
    private long max = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        rgWeek = root.findViewById(R.id.rgWeek);
        txtHouse = root.findViewById(R.id.txtHouse);
        txtHouseMax = root.findViewById(R.id.txtHouseMax);
        listView = root.findViewById(R.id.listView);
        fabAdd = root.findViewById(R.id.fabAdd);
        btnList = root.findViewById(R.id.btnList);
        btnReset = root.findViewById(R.id.btnReset);
        for (int i = 0; i < rdoWeek.length; i++) {
            int resource = getActivity().getResources().getIdentifier("rdoWeek"+(i+1), "id", getActivity().getPackageName());
            rdoWeek[i] = root.findViewById(resource);
        }

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members/"+loadProfile());

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MilkAddActivity.class);
                startActivity(intent);
            }
        });

        rgWeek.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onResume();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick = 1;
                final AlertDialogMethods adm = new AlertDialogMethods(getActivity());
                View view = getLayoutInflater().inflate(R.layout.listdialog, null);

                final ArrayList<MilkPosition> milkPositions = new ArrayList<MilkPosition>();

                final TextView txtCount = view.findViewById(R.id.txtCount);
                ListView listMilk = view.findViewById(R.id.listMilk);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                final ArrayList<String> deliverys = new ArrayList<String>();
                for (int i = 0; i < milkHouses.size(); i++) {
                    deliverys.add(milkHouses.get(i).getName());
                }

                txtCount.setText(Integer.toString(pick));

                final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, deliverys);
                listMilk.setAdapter(adapter);

                listMilk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MilkPosition mp = new MilkPosition(deliverys.get(position), pick);
                        milkPositions.add(mp);
                        pick++;
                        deliverys.remove(position);
                        if (!deliverys.isEmpty()) txtCount.setText(Integer.toString(pick));
                        else txtCount.setText("END");
                        adapter.notifyDataSetChanged();
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!deliverys.isEmpty()) {
                            toast("모든 배달지의 순서를 정하지 않았습니다.");
                            return;
                        }
                        final DatabaseReference milkRef = mDatabase.getReference("Members/"+loadProfile()+"/"+getWeek());
                        milkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (int i = 0; i < milkPositions.size(); i++) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        String key = data.getKey();
                                        if (data.child("name").getValue().toString().equals(milkPositions.get(i).getName())) {
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("order", milkPositions.get(i).getLocation());
                                            milkRef.child(key).updateChildren(taskMap);
                                        }
                                    }
                                }
                                toast("순서를 변경하였습니다.");
                                onResume();
                                adm.alertDismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adm.alertDismiss();
                    }
                });

                adm.setView(view);
                adm.showDialog(false);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference milkRef = mDatabase.getReference("Members/"+loadProfile()+"/"+getWeek());
                final AlertDialogMethods adm = new AlertDialogMethods(getActivity());
                View view = getLayoutInflater().inflate(R.layout.answerdialog, null);

                TextView txtView = view.findViewById(R.id.txtView);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                txtView.setText("모두 초기화하시겠습니까?");

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
                                        if (data2.getKey().equals("isdelivery")) {
                                            key = data.getKey();
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("isdelivery", false);
                                            milkRef.child(key).updateChildren(taskMap);
                                        }
                                    }
                                }
                                onResume();
                                toast("모두 초기화하였습니다.");
                                adm.alertDismiss();
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

        milkHouses = new ArrayList<MilkHouse>();
        milkAdapter = new MilkAdapter(getActivity(), milkHouses, getActivity());
        listView.setAdapter(milkAdapter);

        return root;
    }

    private void toast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private String getWeek() {
        for (int i = 0; i < rdoWeek.length; i++) {
            if (rdoWeek[i].isChecked()) {
                return week_name[i];
            }
        }
        return "null";
    }

    private int selectWeek() {
        for (int i = 0; i < rdoWeek.length; i++) {
            if (rdoWeek[i].isChecked()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        milkHouses.clear();
        max = 0;
        milkAdapter.setReference(getWeek(), loadProfile());
        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String week = week_name[selectWeek()];
                int cnt = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(week)) {
                        max = data.getChildrenCount();
                        for (DataSnapshot data2 : data.getChildren()) {
                            String name = "null", password = "null";
                            int order = 0;
                            ArrayList<Milk> milks = new ArrayList<Milk>();
                            boolean isPassword = false, isDelivery = false;
                            for (DataSnapshot data3 : data2.getChildren()) {
                                if (data3.getKey().equals("name")) name = data3.getValue().toString();
                                else if (data3.getKey().equals("order")) order = Integer.parseInt(data3.getValue().toString());
                                else if (data3.getKey().equals("ispwd")) isPassword = Boolean.parseBoolean(data3.getValue().toString());
                                else if (data3.getKey().equals("pwd")) password = data3.getValue().toString();
                                else if (data3.getKey().equals("isdelivery")) isDelivery = Boolean.parseBoolean(data3.getValue().toString());
                                else {
                                    String type = "null";
                                    int count = 0;
                                    for (DataSnapshot data4 : data3.getChildren()) {
                                        if (data4.getKey().equals("type")) type = data4.getValue().toString();
                                        else if (data4.getKey().equals("count")) count = Integer.parseInt(data4.getValue().toString());
                                    }
                                    Milk milk = new Milk(type, count);
                                    milks.add(milk);
                                }
                            }
                            MilkHouse milkHouse = new MilkHouse(name, password, order, milks, isPassword, isDelivery);
                            if (!isDelivery) {
                                cnt++;
                                milkHouses.add(milkHouse);
                            }
                        }
                    }
                }
                Collections.sort(milkHouses);
                milkAdapter.notifyDataSetChanged();
                txtHouse.setText(Long.toString(cnt));
                txtHouseMax.setText(Long.toString(max));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String loadProfile() {
        FileInputStream fis = null;
        try {
            fis = getActivity().openFileInput("id.txt");
            byte[] memoData = new byte[fis.available()];
            while(fis.read(memoData) != -1) {}
            return new String(memoData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "null";
    }
}
