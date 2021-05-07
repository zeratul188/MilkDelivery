package com.example.mixroidminigames;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private final static int END_TIME = 2000;
    private final static int ACT_RESULT = 0;

    private TextView txtID, txtEmail, txtLocation;
    private ImageView imgSetting;
    private Button btnLogin;

    private long backKeyPressedTime = 0;
    private boolean isLogin = false;

    private FirebaseDatabase mDatabase;
    private DatabaseReference memberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mDatabase = FirebaseDatabase.getInstance();
        memberRef = mDatabase.getReference("Members");

        View view = navigationView.getHeaderView(0);

        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        txtID = view.findViewById(R.id.txtID);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtLocation = view.findViewById(R.id.txtLocation);
        imgSetting = view.findViewById(R.id.imgSetting);
        btnLogin = view.findViewById(R.id.btnLogin);

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialogMethods mainADM = new AlertDialogMethods(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.memberdialog, null);

                final TextView txtID2 = view.findViewById(R.id.txtID);
                final TextView txtEmail2 = view.findViewById(R.id.txtEmail);
                final TextView txtLocation2 = view.findViewById(R.id.txtLocation);
                Button btnEdit = view.findViewById(R.id.btnEdit);
                Button btnPasswordChange = view.findViewById(R.id.btnPasswordChange);
                Button btnDeleteData = view.findViewById(R.id.btnDeleteData);
                Button btnLogout = view.findViewById(R.id.btnLogout);
                Button btnDelete = view.findViewById(R.id.btnDelete);

                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String location = "null", email = "null";
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().equals(loadProfile())) {
                                location = data.child("location").getValue().toString();
                                email = data.child("email").getValue().toString();
                            }
                        }
                        txtID2.setText(loadProfile());
                        txtLocation2.setText(location);
                        txtEmail2.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ProfileEditActivity.class);
                        startActivity(intent);
                    }
                });

                btnPasswordChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PasswordEditActivity.class);
                        startActivity(intent);
                    }
                });

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialogMethods adm = new AlertDialogMethods(MainActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.answerdialog, null);

                        TextView txtView = view.findViewById(R.id.txtView);
                        Button btnCancel = view.findViewById(R.id.btnCancel);
                        Button btnOK = view.findViewById(R.id.btnOK);

                        txtView.setText("정말로 로그아웃하시겠습니까?");

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adm.alertDismiss();
                            }
                        });

                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isLogin = false;
                                btnLogin.setVisibility(View.VISIBLE);
                                txtID.setVisibility(View.GONE);
                                imgSetting.setVisibility(View.GONE);
                                txtLocation.setVisibility(View.GONE);
                                txtEmail.setVisibility(View.GONE);
                                saveProfile("null");
                                toast("로그아웃되었습니다.");
                                adm.alertDismiss();
                                mainADM.alertDismiss();
                            }
                        });

                        adm.setView(view);
                        adm.showDialog(false);
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialogMethods adm = new AlertDialogMethods(MainActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.answerdialog, null);

                        TextView txtView = view.findViewById(R.id.txtView);
                        Button btnCancel = view.findViewById(R.id.btnCancel);
                        Button btnOK = view.findViewById(R.id.btnOK);

                        txtView.setText("정말로 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.");

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adm.alertDismiss();
                            }
                        });

                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isLogin = false;
                                btnLogin.setVisibility(View.VISIBLE);
                                txtID.setVisibility(View.GONE);
                                imgSetting.setVisibility(View.GONE);
                                txtLocation.setVisibility(View.GONE);
                                txtEmail.setVisibility(View.GONE);
                                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            if (data.getKey().equals(loadProfile())) {
                                                memberRef.child(loadProfile()).removeValue();
                                                saveProfile("null");
                                                toast("탈퇴되었습니다.");
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                adm.alertDismiss();
                                mainADM.alertDismiss();
                            }
                        });

                        adm.setView(view);
                        adm.showDialog(false);
                    }
                });

                mainADM.setView(view);
                mainADM.showDialog(true);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ACT_RESULT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loadProfile().equals("null")) {
            memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = "null", location = "null";
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.getKey().equals(loadProfile())) {
                            isLogin = true;
                            email = data.child("email").getValue().toString();
                            location = data.child("location").getValue().toString();
                            break;
                        }
                    }
                    if (isLogin) {
                        txtEmail.setText(email);
                        txtLocation.setText(location);
                        txtID.setText(loadProfile());

                        btnLogin.setVisibility(View.GONE);
                        txtID.setVisibility(View.VISIBLE);
                        imgSetting.setVisibility(View.VISIBLE);
                        txtLocation.setVisibility(View.VISIBLE);
                        txtEmail.setVisibility(View.VISIBLE);
                    } else {
                        btnLogin.setVisibility(View.VISIBLE);
                        txtID.setVisibility(View.GONE);
                        imgSetting.setVisibility(View.GONE);
                        txtLocation.setVisibility(View.GONE);
                        txtEmail.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            isLogin = false;
            btnLogin.setVisibility(View.VISIBLE);
            txtID.setVisibility(View.GONE);
            imgSetting.setVisibility(View.GONE);
            txtLocation.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACT_RESULT:
                if (resultCode == RESULT_OK) {
                    Member member = (Member)data.getSerializableExtra("logined_member");
                    isLogin = true;
                    txtID.setText(member.getId());
                    txtLocation.setText(member.getLocation());
                    txtEmail.setText(member.getEmail());

                    btnLogin.setVisibility(View.GONE);
                    txtID.setVisibility(View.VISIBLE);
                    imgSetting.setVisibility(View.VISIBLE);
                    txtLocation.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.VISIBLE);

                    saveProfile(member.getId());
                }
                break;
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + END_TIME) {
            backKeyPressedTime = System.currentTimeMillis();
            toast("\"뒤로\"버튼을 한번 더 누르시면 앱이 종료됩니다.");
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + END_TIME) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void saveProfile(String id) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("id.txt", MODE_PRIVATE);
            fos.write(id.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
