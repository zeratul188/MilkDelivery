package com.example.mixroidminigames.ui.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mixroidminigames.AlertDialogMethods;
import com.example.mixroidminigames.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MilkEditActivity extends AppCompatActivity {
    private EditText edtPassword;
    private Button btnDelete, btnEdit;
    private CheckBox chkPassword;
    private LinearLayout layoutMilk;

    private FirebaseDatabase mDatabase;
    private DatabaseReference milkRef;
    private ArrayList<MilkEdit> milkEdits;

    private String name = "null", week = "null", path = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtPassword = findViewById(R.id.edtPassword);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        chkPassword = findViewById(R.id.chkPassword);
        layoutMilk = findViewById(R.id.layoutMilk);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        week = intent.getStringExtra("week");
        name = intent.getStringExtra("name");

        mDatabase = FirebaseDatabase.getInstance();
        milkRef = mDatabase.getReference(path);

        milkEdits = new ArrayList<MilkEdit>();

        chkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) edtPassword.setVisibility(View.VISIBLE);
                else edtPassword.setVisibility(View.GONE);
            }
        });

        milkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pwd = "null", find = "null";
                boolean isPwd = false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot data2 : data.getChildren()) {
                        if (data2.getKey().equals("name")) {
                            if (data2.getValue().toString().equals(name)) {
                                find = data.getKey();
                            }
                        }
                    }
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(find)) {
                        for (DataSnapshot data2 : data.getChildren()) {
                            final String house_key = data.getKey();
                            if (data2.getKey().equals("ispwd")) isPwd = Boolean.parseBoolean(data2.getValue().toString());
                            else if (data2.getKey().equals("pwd")) pwd = data2.getValue().toString();
                            else if (data2.getKey().equals("isdelivery") || data2.getKey().equals("order") || data2.getKey().equals("name")) continue;
                            else {
                                final String milk_key = data2.getKey();
                                String type = "null";
                                int count = 0;
                                for (DataSnapshot data3 : data2.getChildren()) {
                                    if (data3.getKey().equals("type")) type = data3.getValue().toString();
                                    else if (data3.getKey().equals("count")) count = Integer.parseInt(data3.getValue().toString());
                                }
                                View view = getLayoutInflater().inflate(R.layout.week_add_layout, null);

                                final EditText edtType = view.findViewById(R.id.edtType);
                                final EditText edtCount = view.findViewById(R.id.edtCount);
                                final ImageButton btnCommit = view.findViewById(R.id.btnCommit);

                                edtType.setText(type);
                                edtCount.setText(Integer.toString(count));

                                btnCommit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (edtType.getText().toString().equals("") || edtCount.getText().toString().equals("")) {
                                            toast("입력값이 비어있습니다.");
                                            return;
                                        }
                                        String type = edtType.getText().toString();
                                        int count = Integer.parseInt(edtCount.getText().toString());
                                        MilkEdit me = new MilkEdit(type, house_key, milk_key, count);
                                        milkEdits.add(me);
                                        edtType.setEnabled(false);
                                        edtCount.setEnabled(false);
                                        btnCommit.setEnabled(false);
                                    }
                                });

                                layoutMilk.addView(view);
                            }
                        }
                    }
                }
                chkPassword.setChecked(isPwd);
                if (isPwd) {
                    edtPassword.setVisibility(View.VISIBLE);
                    edtPassword.setText(pwd);
                } else edtPassword.setVisibility(View.GONE);
                setTitle(name+"("+week+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (milkEdits.isEmpty()) {
                    toast("수정된 값이 없습니다.");
                    return;
                } else if (chkPassword.isChecked() && edtPassword.getText().toString().equals("")) {
                    toast("비밀번호가 비어있습니다.");
                    return;
                }
                milkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean ispwd = chkPassword.isChecked();
                        String pwd = "null";
                        if (ispwd) pwd = edtPassword.getText().toString();
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("ispwd", ispwd);
                        if (ispwd) taskMap.put("pwd", pwd);
                        String house_key = milkEdits.get(0).getHouse_key();
                        milkRef.child(house_key).updateChildren(taskMap);
                        for (MilkEdit milkEdit : milkEdits) {
                            Map<String, Object> taskMap2 = new HashMap<String, Object>();
                            taskMap2.put("type", milkEdit.getType());
                            taskMap2.put("count", milkEdit.getCount());
                            milkRef.child(milkEdit.getHouse_key()).child(milkEdit.getMilk_key()).updateChildren(taskMap2);
                        }
                        toast("정보가 수정되었습니다.");
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialogMethods adm = new AlertDialogMethods(MilkEditActivity.this);
                View view = getLayoutInflater().inflate(R.layout.answerdialog, null);

                TextView txtView = view.findViewById(R.id.txtView);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                Button btnOK = view.findViewById(R.id.btnOK);

                txtView.setText("\""+name+"\"를 삭제하시겠습니까?");
                btnOK.setText("삭제");

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
                                            if (data2.getValue().toString().equals(name)) {
                                                key = data.getKey();
                                                milkRef.child(key).removeValue();
                                                toast("\""+name+"\"를 삭제하였습니다.");
                                                adm.alertDismiss();
                                                finish();
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
}
