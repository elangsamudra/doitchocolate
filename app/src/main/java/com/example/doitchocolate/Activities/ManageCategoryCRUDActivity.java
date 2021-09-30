package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ManageCategoryCRUDActivity extends AppCompatActivity {

    private CardView btnSubmit, btnDelete, btnBack;
    private EditText edtName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String id, category_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managecategory);

        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnDelete = (CardView) findViewById(R.id.btnDelete);
        btnBack = (CardView) findViewById(R.id.btnBack);
        edtName = (EditText) findViewById(R.id.edt_category_name);

        Intent i = getIntent();
        final String pageMode = i.getStringExtra("pageMode");
        switch (pageMode) {
            case "add": {
                btnDelete.setVisibility(View.GONE);
                break;
            }
            case "edit":{
                edtName.setText(i.getStringExtra("category_name"));
                id = i.getStringExtra("id");
                break;
            }
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCategory(pageMode);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(id);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitCategory(String pageMode) {
        category_name = edtName.getText().toString().trim();

        if(category_name.length() > 0) {
            Category category = new Category();
            category.setCategory_name(category_name);

            final ProgressDialog dialog = ProgressDialog.show(ManageCategoryCRUDActivity.this, "",
                    "Loading. Please wait...", true);

            if(pageMode.equals("add")) {
                db.collection("category")
                        .add(category)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                dialog.dismiss();
                                Toasty.success(ManageCategoryCRUDActivity.this, "Success adding category!", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[DEBUG]", "Error adding document", e);
                                dialog.dismiss();
                                Toasty.error(ManageCategoryCRUDActivity.this, "Failed adding category, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                            }
                        });
            }
            else if(pageMode.equals("edit")) {
                db.collection("category")
                        .document(String.valueOf(id))
                        .set(category)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toasty.success(ManageCategoryCRUDActivity.this, "Success updating category!", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[DEBUG]", "Error adding document", e);
                                dialog.dismiss();
                                Toasty.error(ManageCategoryCRUDActivity.this, "Failed updating category, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                            }
                        });
            }
        }
        else {
            Toasty.error(ManageCategoryCRUDActivity.this, "Please fill all empty field!", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void deleteCategory(String id) {
        final ProgressDialog dialog = ProgressDialog.show(ManageCategoryCRUDActivity.this, "",
                "Loading. Please wait...", true);

        db.collection("category")
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toasty.success(ManageCategoryCRUDActivity.this, "Success deleting category!", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("[DEBUG]", "Error adding document", e);
                        dialog.dismiss();
                        Toasty.error(ManageCategoryCRUDActivity.this, "Failed deleting category, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
}
