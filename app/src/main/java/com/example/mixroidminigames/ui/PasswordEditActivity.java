package com.example.mixroidminigames.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mixroidminigames.BaseActivity;
import com.example.mixroidminigames.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PasswordEditActivity extends BaseActivity {
    private EditText edtUndoPassword, edtPassword, edtPasswordRetry;
    private Button btnEdit;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("비밀번호 변경");

        edtUndoPassword = findViewById(R.id.edtUndoPassword);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordRetry = findViewById(R.id.edtPasswordRetry);
        btnEdit = findViewById(R.id.btnEdit);

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members/"+loadProfile());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getText().toString().equals("") || edtPasswordRetry.getText().toString().equals("") || edtUndoPassword.getText().toString().equals("")) {
                    toast("입력하지 않은 칸이 있습니다. 입력 후 다시 시도해주시기 바랍니다.");
                    return;
                }
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String password = "null";
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().equals("pwd")) {
                                password = data.getValue().toString();
                            }
                        }
                        if (!edtUndoPassword.getText().toString().equals(password)) {
                            toast("기존 비밀번호가 일치하지 않습니다.");
                            return;
                        } else if (!edtPassword.getText().toString().equals(edtPasswordRetry.getText().toString())) {
                            toast("비밀번호가 서로 동일하지 않습니다.");
                            return;
                        }
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("pwd", edtPassword.getText().toString());
                        memberRef.updateChildren(taskMap);
                        toast("비밀번호가 변경되었습니다.");
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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