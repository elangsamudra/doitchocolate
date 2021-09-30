package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import es.dmoral.toasty.Toasty;

public class ManageProductCRUDActivity extends AppCompatActivity {

    private CardView btnSubmit, btnDelete, btnBack, btnImage;
    private EditText edtCatName, edtProdName, edtStock, edtPrice;
    private ImageView imgIcon;
    private TextView txtFormated;

    private String fileName = "no_image", pPath, imageName, fileExt;
    private String id, prodIcon, category_name, prod_name, fullUrl;
    private int prod_stock, prod_price, category_id;
    private static int RESULT_LOAD_IMAGE = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://chocolate-cca3e.appspot.com/images/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageproductform);

        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnDelete = (CardView) findViewById(R.id.btnDelete);
        btnBack = (CardView) findViewById(R.id.btnBack);
        btnImage = (CardView) findViewById(R.id.btnImage);
        txtFormated = (TextView) findViewById(R.id.txtFormatedPrice);
        edtCatName = (EditText) findViewById(R.id.edt_category_name);
        edtProdName = (EditText) findViewById(R.id.edt_product_name);
        edtStock = (EditText) findViewById(R.id.edt_product_stock);
        edtPrice = (EditText) findViewById(R.id.edt_product_price);
        imgIcon = (ImageView) findViewById(R.id.imgProductIcon);

        edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = "";
                if(edtPrice.getText().toString().trim().length() > 0) {
                    str = "Price: " + macroCollection.formatRupiah(Integer.parseInt(edtPrice.getText().toString().trim()));
                }
                else {
                    str = "Price: Rp0";
                }
                txtFormated.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Intent i = getIntent();
        final String pageMode = i.getStringExtra("pageMode");
        category_id = i.getIntExtra("category_id", 0);
        switch (pageMode) {
            case "add": {
                edtCatName.setText(i.getStringExtra("category_name"));
                btnDelete.setVisibility(View.GONE);
                break;
            }
            case "edit":{
                edtCatName.setText(i.getStringExtra("kategori"));
                edtProdName.setText(i.getStringExtra("nama_produk"));
                edtStock.setText(String.valueOf(i.getIntExtra("stok", 0)));
                edtPrice.setText(String.valueOf(i.getIntExtra("harga", 0)));

                prodIcon = i.getStringExtra("foto_produk");

                if(!prodIcon.equals("no_image")) {
                    Glide.with(ManageProductCRUDActivity.this)
                            .load(prodIcon)
                            .into(imgIcon);

                    fullUrl = prodIcon;
                }
                id = i.getStringExtra("id");
                break;
            }
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProduct(pageMode);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(id);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void submitProduct(String pageMode) {
        category_name = edtCatName.getText().toString().trim();
        prod_name = edtProdName.getText().toString().trim();
        prod_stock = Integer.parseInt(edtStock.getText().toString().trim());
        prod_price = Integer.parseInt(edtPrice.getText().toString().trim());

        if(category_name.length() > 0 && prod_name.length() > 0 && prod_stock > 0 && prod_price > 0) {
            Product product = new Product();
            product.setKategori(category_name);
            product.setHarga(prod_price);
            product.setNama_produk(prod_name);
            product.setStok(prod_stock);
            product.setFoto_produk(fullUrl);

            final ProgressDialog dialog = ProgressDialog.show(ManageProductCRUDActivity.this, "",
                    "Loading. Please wait...", true);

            if (pageMode.equals("add")) {
                if (!fileName.equals("no_image")) {
                    db.collection("product")
                            .add(product)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    dialog.dismiss();
                                    Toasty.success(ManageProductCRUDActivity.this, "Success adding product!", Toast.LENGTH_SHORT, true).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("[DEBUG]", "Error adding document", e);
                                    dialog.dismiss();
                                    Toasty.error(ManageProductCRUDActivity.this, "Failed adding product, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                }
                            });
                } else {
                    dialog.dismiss();
                    Toasty.error(ManageProductCRUDActivity.this, "Please select image to upload!", Toast.LENGTH_SHORT, true).show();
                }
            } else if (pageMode.equals("edit")) {
                db.collection("product")
                        .document(String.valueOf(id))
                        .set(product)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toasty.success(ManageProductCRUDActivity.this, "Success updating product!", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[DEBUG]", "Error adding document", e);
                                dialog.dismiss();
                                Toasty.error(ManageProductCRUDActivity.this, "Failed updating product, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                            }
                        });
            }
        }
    }

    private void deleteProduct(String id) {
        final ProgressDialog dialog = ProgressDialog.show(ManageProductCRUDActivity.this, "",
                "Loading. Please wait...", true);

        db.collection("product")
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toasty.success(ManageProductCRUDActivity.this, "Success deleting product!", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("[DEBUG]", "Error adding document", e);
                        dialog.dismiss();
                        Toasty.error(ManageProductCRUDActivity.this, "Failed deleting product, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
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
                    .into(imgIcon);
            Log.d("[DEBUG]", fileName);

            final StorageReference childRef = storageRef.child(fileName);
            Uri file = Uri.fromFile(new File(pPath));

            final ProgressDialog dialog = ProgressDialog.show(ManageProductCRUDActivity.this, "",
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
