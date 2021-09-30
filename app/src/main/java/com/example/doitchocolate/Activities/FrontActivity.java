package com.example.doitchocolate.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.User;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;

public class FrontActivity extends AppCompatActivity {

    private CardView btnLogin, btnRegister;
    private String username, password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String LOG_TAG_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        prefHelper.init(FrontActivity.this);

        btnLogin = (CardView) findViewById(R.id.btnLogin);
        btnRegister = (CardView) findViewById(R.id.btnRegister);

        String sessionCheck = prefHelper.getUserdata("id");
        Log.d("[DEBUG]", "sessioncheck");

        if (sessionCheck != null) {
            if (!sessionCheck.equals("0")) {
                username = prefHelper.getUserdata("username");
                password = prefHelper.getUserdata("password");

                login(username, password);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FrontActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FrontActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(FrontActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(FrontActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

        int readExternalStoragePermission = ContextCompat.checkSelfPermission(FrontActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(readExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(FrontActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }

    private void login(String username, String password) {
        if (username.length() > 0 && password.length() > 0) {
            final ProgressDialog dialog = ProgressDialog.show(FrontActivity.this, "",
                    "Loading. Please wait...", true);

            Log.d("[DEBUG]", "test");

            db.collection("users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                Log.d("[DEBUG]", "sukses");
                                if (task.getResult().isEmpty()) {
                                    Log.d("[DEBUG]", "kosong");
                                    Toasty.error(FrontActivity.this, "Auto login failed, please go to login page to enter your username and password!", Toast.LENGTH_SHORT, true).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                            User user = document.toObject(User.class);
                                            Log.d("[DEBUG]", "exist");

                                            //Setup prefHelper saat baru login, assign semua var user kedalam shared preferences
                                            prefHelper.setUserdata("id", document.getId());
                                            prefHelper.setUserdata("username", user.getUsername());
                                            prefHelper.setUserdata("fullname", user.getNama());
                                            prefHelper.setUserdata("email", user.getEmail());
                                            prefHelper.setUserdata("password", user.getPassword());
                                            prefHelper.setUserdata("phone", user.getNo_tlp());
                                            prefHelper.setUserdata("level", String.valueOf(user.getLevel()));
                                            prefHelper.setUserdata("alamat", user.getAlamat());
                                            prefHelper.setUserdata("profile_picture", user.getProfile_picture());

                                            Toasty.success(FrontActivity.this, "Login success!", Toast.LENGTH_SHORT, true).show();

                                            Intent i;
                                            if (user.getLevel() == 1) {
                                                i = new Intent(FrontActivity.this, AdminMainActivity.class);
                                            } else {
                                                i = new Intent(FrontActivity.this, MainActivity.class);
                                            }
                                            startActivity(i);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                        } else {
                                            Toasty.error(FrontActivity.this, "Auto login failed, please go to login page to enter your username and password!", Toast.LENGTH_SHORT, true).show();
                                        }
                                    }
                                }
                            } else {
                                Toasty.error(FrontActivity.this, "Login failed due to system error, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                Log.d("[DEBUG]", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}