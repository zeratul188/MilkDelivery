package com.example.mixroidminigames.ui.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mixroidminigames.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MilkAddActivity extends AppCompatActivity {
    private final int WEEK_COUNT = 7;

    private EditText edtName;
    private CheckBox[] chkWeek = new CheckBox[WEEK_COUNT];
    private EditText[] edtWeek = new EditText[WEEK_COUNT];
    private ImageButton[] btnWeek = new ImageButton[WEEK_COUNT];
    private LinearLayout[] layoutWeek = new LinearLayout[WEEK_COUNT];
    private Button btnAdd;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;
    private ArrayList<Milk>[] arrayMilk = new ArrayList[WEEK_COUNT];

    private String[] week_name = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private int last_number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("배달지 추가");

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members/"+loadProfile());
        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals("count")) {
                        last_number = Integer.parseInt(data.getValue().toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edtName = findViewById(R.id.edtName);
        btnAdd = findViewById(R.id.btnAdd);

        for (int i = 0; i < WEEK_COUNT; i++) {
            int resource = getResources().getIdentifier("chkWeek"+(i+1), "id", getPackageName());
            chkWeek[i] = findViewById(resource);
            resource = getResources().getIdentifier("edtWeek"+(i+1), "id", getPackageName());
            edtWeek[i] = findViewById(resource);
            resource = getResources().getIdentifier("btnWeek"+(i+1), "id", getPackageName());
            btnWeek[i] = findViewById(resource);
            resource = getResources().getIdentifier("layoutWeek"+(i+1), "id", getPackageName());
            layoutWeek[i] = findViewById(resource);
            arrayMilk[i] = new ArrayList<Milk>();

            final int index = i;
            btnWeek[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edtWeek[index].getText().toString().equals("")) {
                        toast("입력이 비어있습니다.");
                        return;
                    }
                    int count = Integer.parseInt(edtWeek[index].getText().toString());

                    for (int i = 0; i < count; i++) {
                        View view = getLayoutInflater().inflate(R.layout.week_add_layout, null);

                        final EditText edtType = view.findViewById(R.id.edtType);
                        final EditText edtCount = view.findViewById(R.id.edtCount);
                        final ImageButton btnCommit = view.findViewById(R.id.btnCommit);

                        btnCommit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String type = edtType.getText().toString();
                                int count = Integer.parseInt(edtCount.getText().toString());
                                Milk milk = new Milk(type, count);
                                arrayMilk[index].add(milk);
                                edtType.setEnabled(false);
                                edtCount.setEnabled(false);
                                btnCommit.setEnabled(false);
                            }
                        });

                        layoutWeek[index].addView(view);
                    }
                }
            });
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().toString().equals("")) {
                    toast("주소가 비었습니다. 입력 후 다시 시도해주시기 바랍니다.");
                    return;
                }
                String name = edtName.getText().toString();
                for (int i = 0; i < WEEK_COUNT; i++) {
                    if (chkWeek[i].isChecked()) {
                        memberRef.child(week_name[i]).child("house"+(last_number+1)).child("name").setValue(name);
                        for (int j = 0; j < arrayMilk[i].size(); j++) {
                            memberRef.child(week_name[i]).child("house"+(last_number+1)).child("milk"+(j+1)).setValue(arrayMilk[i].get(j));
                        }
                    }
                }
                toast("\""+name+"\" 배달지가 추가되었습니다.");
                Map<String, Object> taskMap = new HashMap<String, Object>();
                last_number++;
                taskMap.put("count", last_number);
                memberRef.updateChildren(taskMap);
                finish();
            }
        });

    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String loadProfile() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("id.txt");
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