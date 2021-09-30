package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doitchocolate.Adapters.CategoryAdapter;
import com.example.doitchocolate.Adapters.ProductAdapter;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

public class ManageProductListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private GridView listView;
    private TextView txtTagline;
    private Context mContext;
    private String id, category_name;
    private int userLevel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageproduct);

        mContext = this;

        prefHelper.init(mContext);

        userLevel = Integer.parseInt(prefHelper.getUserdata("level"));

        Intent i = getIntent();
        id = i.getStringExtra("id");
        category_name = i.getStringExtra("category_name");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtTagline = (TextView) findViewById(R.id.txtTagline);
        listView = (GridView) findViewById(R.id.lvProduct);

        String strTagline = "Result from category [" + category_name + "]";
        txtTagline.setText(strTagline);

        if(userLevel == 0) {
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManageProductListActivity.this, ManageProductCRUDActivity.class);
                i.putExtra("pageMode", "add");
                i.putExtra("category_name", category_name);
                i.putExtra("category_id", id);
                startActivityForResult(i, 2);
            }
        });

        setupContent(id);
    }

    private void setupContent(final String id) {
        final ProgressDialog dialog = ProgressDialog.show(ManageProductListActivity.this,"",
                "Loading. Please wait...", true);

        db.collection("product")
                .whereEqualTo("kategori", category_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                dialog.dismiss();
                                Log.d("[DEBUG]", "kosong");
                                Toasty.error(mContext, "Product still empty, please add one!", Toast.LENGTH_SHORT, true).show();
                            }
                            else {
                                productList = new ArrayList<Product>();
                                Log.d("[DEBUG]", "size-1: " + productList.size());
                                for (DocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    product.setId(document.getId());
                                    productList.add(product);
//                                    Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                    Log.d("[DEBUG]", "size-2: " + productList.size());
                                }

                                dialog.dismiss();

                                Category category = new Category();
                                category.setId(id);
                                category.setCategory_name(category_name);
                                Log.d("[DEBUG]", "size-3: " + productList.size());
                                ProductAdapter productAdapter = new ProductAdapter(ManageProductListActivity.this, productList, userLevel, category);
                                productAdapter.notifyDataSetChanged();
                                listView.setAdapter(productAdapter);
                            }
                        } else {
                            dialog.dismiss();
                            Log.d("[DEBUG]}", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupContent(id);
    }


}
