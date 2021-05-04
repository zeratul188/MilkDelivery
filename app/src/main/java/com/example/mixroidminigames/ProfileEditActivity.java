package com.example.mixroidminigames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends BaseActivity {
    private EditText edtEmail, edtLocation;
    private Button btnEdit;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(loadProfile()+"님의 정보");

        edtEmail = findViewById(R.id.edtEmail);
        edtLocation = findViewById(R.id.edtLocation);
        btnEdit = findViewById(R.id.btnEdit);

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members/"+loadProfile());

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals("email")) edtEmail.setText(data.getValue().toString());
                    else if (data.getKey().equals("location")) edtLocation.setText(data.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().equals("") || edtLocation.getText().toString().equals("")) {
                    toast("입력하지 않은 칸이 있습니다. 입력 후 다시 시도해주시기 바랍니다.");
                    return;
                }
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("email", edtEmail.getText().toString());
                taskMap.put("location", edtLocation.getText().toString());
                memberRef.updateChildren(taskMap);
                toast("정보가 수정되었습니다.");
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