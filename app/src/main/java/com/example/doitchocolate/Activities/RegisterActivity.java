package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Models.User;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.RequestParams;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtAddress, edtPhone, edtEmail, edtFullname;
    private CardView btnRegister, btnBrowse;
    private ImageView imageView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://chocolate-cca3e.appspot.com/images/");

    private String fileName = "no_image", pPath, fullUrl;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        edtPhone = (EditText) findViewById(R.id.edt_phone);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtFullname = (EditText) findViewById(R.id.edt_fullname);
        btnRegister = (CardView) findViewById(R.id.btnRegister);
        btnBrowse = (CardView) findViewById(R.id.btnBrowse);
        imageView = (ImageView) findViewById(R.id.imgView);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String fullname = edtFullname.getText().toString().trim();

                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setAlamat(address);
                user.setNo_tlp(phone);
                user.setNama(fullname);
                user.setProfile_picture(fullUrl);

                if(username.length() > 0 && password.length() > 0 && address.length() > 0 && phone.length() > 0 && email.length() > 0 && fullname.length() > 0) {
                    if(!fileName.equals("no_image")) {
                        final ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "",
                                "Loading. Please wait...", true);

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        dialog.dismiss();
                                        Toasty.success(RegisterActivity.this, "Registration success, you can login now!", Toast.LENGTH_SHORT, true).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("[DEBUG]", "Error adding document", e);
                                        dialog.dismiss();
                                        Toasty.error(RegisterActivity.this, "Registration failed, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                    }
                    else {
                        Toasty.error(RegisterActivity.this, "Please select image to upload!", Toast.LENGTH_SHORT, true).show();
                    }
                }
                else {
                    Toasty.error(RegisterActivity.this, "Please fill all empty fields!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pPath = cursor.getString(columnIndex);
            cursor.close();

            String splittedFileName[] = pPath.split("/");
            fileName = splittedFileName[splittedFileName.length - 1];

            Glide.with(this)
                    .load(new File(pPath))
                    .into(imageView);
            Log.d("[DEBUG]", fileName);

            final StorageReference childRef = storageRef.child(fileName);
            Uri file = Uri.fromFile(new File(pPath));

            final ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "",
                    "Loading image. Please wait...", true);

            childRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("[DEBUG]", uri.toString());
                                    fullUrl = uri.toString();
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            dialog.dismiss();
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
    }
}
