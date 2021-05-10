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

public class SignupActivity extends BaseActivity {

    private EditText edtID, edtPassword, edtPasswordRetry, edtEmail, edtLocation;
    private Button btnDouble, btnSignup;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    private boolean isDouble = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("회원가입");

        edtID = findViewById(R.id.edtID);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordRetry = findViewById(R.id.edtPasswordRetry);
        edtEmail = findViewById(R.id.edtEmail);
        edtLocation = findViewById(R.id.edtLocation);
        btnDouble = findViewById(R.id.btnDouble);
        btnSignup = findViewById(R.id.btnSignup);

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members");

        btnDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtID.getText().toString().equals("")) {
                    toast("아이디를 입력해주세요.");
                    return;
                }
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().equals(edtID.getText().toString())) {
                                toast("동일한 아이디가 존재합니다. 다른 아이디를 입력해주세요.");
                                return;
                            }
                        }
                        btnDouble.setEnabled(false);
                        edtID.setEnabled(false);
                        isDouble = false;
                        toast(edtID.getText().toString()+"는 사용가능한 아이디입니다.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDouble) {
                    toast("중복 체크를 하세요.");
                    return;
                } else if (edtPassword.getText().equals("") || edtPasswordRetry.getText().equals("") || edtEmail.getText().equals("") || edtLocation.getText().equals("")) {
                    toast("비어있는 입력란이 있습니다. 확인 후 다시 시도해주세요.");
                    return;
                } else if (!edtPassword.getText().toString().equals(edtPasswordRetry.getText().toString())) {
                    toast("비밀번호가 동일하지 않습니다.");
                    return;
                }
                Member member = new Member(edtID.getText().toString(), edtPassword.getText().toString(), edtLocation.getText().toString(), edtEmail.getText().toString(), 0);
                memberRef.child(edtID.getText().toString()).setValue(member);
                toast(edtID.getText().toString()+"님, 회원가입에 성공하였습니다.");
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
}
