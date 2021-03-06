package com.example.mixroidminigames.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mixroidminigames.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private TextView txtWeek, txtNow, txtNextMilk, txtNextType, txtNextMilkInfo, txtNextTypeInfo;
    private ListView listMilk, listType;

    private SimpleDateFormat format;
    private Date date;
    private Calendar cal;
    private ArrayList<MilkNext> milkNexts;
    private ArrayList<String> milkStrings;
    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;
    private ArrayAdapter adapter;
    private ArrayList<MilkList> milkLists;
    private MilkListAdapter milkListAdapter;

    private String now_date;
    private String[] week_name = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private String week;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        txtWeek = root.findViewById(R.id.txtWeek);
        txtNow = root.findViewById(R.id.txtNow);
        txtNextMilk = root.findViewById(R.id.txtNextMilk);
        txtNextType = root.findViewById(R.id.txtNextType);
        listMilk = root.findViewById(R.id.listMilk);
        listType = root.findViewById(R.id.listType);
        txtNextMilkInfo = root.findViewById(R.id.txtNextMilkInfo);
        txtNextTypeInfo = root.findViewById(R.id.txtNextTypeInfo);

        format = new SimpleDateFormat("yyyy??? MM??? dd???");
        date = new Date();
        cal = Calendar.getInstance();
        milkNexts = new ArrayList<MilkNext>();
        milkStrings = new ArrayList<String>();

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, milkStrings);
        listMilk.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance();
        week = week_name[nextDay(cal.get(Calendar.DAY_OF_WEEK))-1];
        memberRef = mDatabase.getReference("Members/"+loadProfile()+"/"+week);

        milkLists = new ArrayList<MilkList>();
        milkListAdapter = new MilkListAdapter(getActivity(), milkLists);
        listType.setAdapter(milkListAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        now_date = format.format(date);
        txtNow.setText(now_date);
        txtWeek.setText(changeWeek(cal.get(Calendar.DAY_OF_WEEK)));
        txtNextMilk.setText("?????? ?????? ?????? - "+changeWeek((cal.get(Calendar.DAY_OF_WEEK)+1)%7));
        txtNextType.setText("?????? ?????? ?????? ?????? - "+changeWeek((cal.get(Calendar.DAY_OF_WEEK)+1)%7));

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                milkNexts.clear();
                milkStrings.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = "null";
                    int order = 0;
                    for (DataSnapshot data2 : data.getChildren()) {
                        if (data2.getKey().equals("name")) name = data2.getValue().toString();
                        else if (data2.getKey().equals("order")) order = Integer.parseInt(data2.getValue().toString());
                    }
                    MilkNext mn = new MilkNext(name, order);
                    milkNexts.add(mn);
                }
                Collections.sort(milkNexts);
                for (int i = 0; i < milkNexts.size(); i++) milkStrings.add(milkNexts.get(i).getName());
                adapter.notifyDataSetChanged();
                if (milkStrings.isEmpty()) {
                    listMilk.setVisibility(View.GONE);
                    txtNextMilkInfo.setVisibility(View.VISIBLE);
                } else {
                    listMilk.setVisibility(View.VISIBLE);
                    txtNextMilkInfo.setVisibility(View.GONE);
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot data2 : data.getChildren()) {
                        switch (data2.getKey()) {
                            case "isdelivery":
                            case "ispwd":
                            case "name":
                            case "order":
                            case "pwd":
                                break;
                            default:
                                String type = "null";
                                int count = 0;
                                for (DataSnapshot data3 : data2.getChildren()) {
                                    switch (data3.getKey()) {
                                        case "type":
                                            type = data3.getValue().toString();
                                            break;
                                        case "count":
                                            count = Integer.parseInt(data3.getValue().toString());
                                            break;
                                    }
                                }
                                if (!type.equals("null")) {
                                    boolean isFind = false;
                                    for (int i = 0; i < milkLists.size(); i++) {
                                        if (milkLists.get(i).getType().equals(type)) {
                                            int temp_cnt = milkLists.get(i).getCount();
                                            temp_cnt++;
                                            milkLists.get(i).setCount(temp_cnt);
                                            isFind = true;
                                        }
                                    }
                                    if (!isFind) {
                                        MilkList ml = new MilkList(type, count);
                                        milkLists.add(ml);
                                    }
                                }
                        }
                    }
                }
                milkListAdapter.notifyDataSetChanged();
                if (milkLists.isEmpty()) {
                    listType.setVisibility(View.GONE);
                    txtNextTypeInfo.setVisibility(View.VISIBLE);
                } else {
                    listType.setVisibility(View.VISIBLE);
                    txtNextTypeInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int nextDay(int dayOfWeek) {
        dayOfWeek++;
        if (dayOfWeek > 7) dayOfWeek = 1;
        return dayOfWeek;
    }

    private String changeWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "?????????";
            case 2:
                return "?????????";
            case 3:
                return "?????????";
            case 4:
                return "?????????";
            case 5:
                return "?????????";
            case 6:
                return "?????????";
            case 7:
            case 0:
                return "?????????";
            default:
                return "null";
        }
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
