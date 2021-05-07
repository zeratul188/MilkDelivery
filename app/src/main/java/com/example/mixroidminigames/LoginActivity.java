package com.example.mixroidminigames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends BaseActivity {

    private EditText edtID, edtPassword;
    private Button btnSignup, btnLogin;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("로그인");

        edtID = findViewById(R.id.edtID);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtID.getText().toString().equals("")) {
                    toast("아이디를 입력해주세요.");
                    return;
                } else if (edtPassword.getText().toString().equals("")) {
                    toast("비밀번호를 입력해주세요.");
                    return;
                }
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String message = "아이디가 존재하지 않습니다.";
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().equals(edtID.getText().toString())) {
                                if (data.child("pwd").getValue().toString().equals(edtPassword.getText().toString())) {
                                    String id = data.child("id").getValue().toString();
                                    String pwd = data.child("pwd").getValue().toString();
                                    String location = data.child("location").getValue().toString();
                                    String email = data.child("email").getValue().toString();
                                    int count = Integer.parseInt(data.child("count").getValue().toString());
                                    Member member = new Member(id, pwd, location, email, count);
                                    message = id+"님 환영합니다.";
                                    Intent intent = new Intent();
                                    intent.putExtra("logined_member", member);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    break;
                                } else {
                                    message = "비밀번호가 일치하지 않습니다.";
                                    break;
                                }
                            }
                        }
                        toast(message);
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
}
