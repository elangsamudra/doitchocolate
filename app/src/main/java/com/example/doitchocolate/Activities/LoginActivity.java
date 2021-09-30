package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.User;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private CardView btnLogin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefHelper.init(LoginActivity.this);

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (CardView) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(username.length() > 0 && password.length() >0) {
                    final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,"",
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
                                        if(task.getResult().isEmpty()){
                                            Log.d("[DEBUG]", "kosong");
                                            Toasty.error(LoginActivity.this, "Login failed, please check your username and password!", Toast.LENGTH_SHORT, true).show();
                                        }
                                        else {
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

                                                    Toasty.success(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT, true).show();

                                                    Intent i;
                                                    if(user.getLevel() == 1) {
                                                        i = new Intent(LoginActivity.this, AdminMainActivity.class);
                                                    }
                                                    else {
                                                        i = new Intent(LoginActivity.this, MainActivity.class);
                                                    }
                                                    startActivity(i);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    finish();
                                                }
                                                else {
                                                    Toasty.error(LoginActivity.this, "Login failed, please check your username and password!", Toast.LENGTH_SHORT, true).show();
                                                }
                                            }
                                        }
                                    } else {
                                        Toasty.error(LoginActivity.this, "Login failed due to system error, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                        Log.d("[DEBUG]", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });
    }
}
