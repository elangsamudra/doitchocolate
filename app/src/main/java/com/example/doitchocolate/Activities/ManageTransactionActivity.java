package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doitchocolate.Adapters.OrderDetailsAdapter;
import com.example.doitchocolate.Adapters.ProductAdapter;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.Models.Order;
import com.example.doitchocolate.Models.OrderDetail;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

public class ManageTransactionActivity extends AppCompatActivity {

    private Button submit, back;
    private TextView nama, total, tanggal, status;
    private ListView lvItem;
    private List<OrderDetail> orderDetailList;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managetransaction);

        mContext = this;

        back = (Button) findViewById(R.id.btnBack);
        submit = (Button) findViewById(R.id.btnConfirm);
        nama = (TextView) findViewById(R.id.txtNama);
        total = (TextView) findViewById(R.id.txtTotal);
        tanggal = (TextView) findViewById(R.id.txtTanggal);
        status = (TextView) findViewById(R.id.txtStatus);
        lvItem = (ListView) findViewById(R.id.lvItem);

        Intent i = getIntent();
        final Order orders = new Order();
        orders.setId(i.getStringExtra("id"));
        orders.setUsername(i.getStringExtra("username"));
        orders.setStatus(i.getIntExtra("status", 0));
        orders.setTotal(i.getIntExtra("total", 0));
        orders.setCreated_on(i.getStringExtra("created_on"));

        nama.setText(orders.getUsername());
        total.setText(macroCollection.formatRupiah(orders.getTotal()));
        tanggal.setText(orders.getCreated_on());
        status.setText(getStatus(orders.getStatus()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(orders.getStatus() != 0) {
            submit.setVisibility(View.INVISIBLE);
        }
        else {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int status = orders.getStatus();
                    updateStatus(orders, status+1);
                }
            });
        }

        setupContent(orders.getId());
    }

    private void updateStatus(Order order, int status) {
        final ProgressDialog dialog = ProgressDialog.show(ManageTransactionActivity.this,"",
                "Loading. Please wait...", true);

        order.setStatus(status);
        db.collection("orders")
                .document(order.getId())
                .set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toasty.success(ManageTransactionActivity.this, "Success updating transcation!", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("[DEBUG]", "Error adding document", e);
                        dialog.dismiss();
                        Toasty.error(ManageTransactionActivity.this, "Failed updating transcation, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    private void setupContent(String id) {
        final ProgressDialog dialog = ProgressDialog.show(ManageTransactionActivity.this,"",
                "Loading. Please wait...", true);

        db.collection("order_detail")
                .whereEqualTo("order_id", id)
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
                                orderDetailList = new ArrayList<OrderDetail>();
                                orderDetailList.clear();
                                Log.d("[DEBUG]", "size-1: " + orderDetailList.size());
                                for (DocumentSnapshot document : task.getResult()) {
                                    OrderDetail orderDetail = document.toObject(OrderDetail.class);
                                    orderDetail.setId(document.getId());
                                    orderDetailList.add(orderDetail);
                                    Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                    Log.d("[DEBUG]", "size-2: " + orderDetailList.size());
                                }

                                Log.d("[DEBUG]", "size-3: " + orderDetailList.size());

                                dialog.dismiss();

                                OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(ManageTransactionActivity.this, orderDetailList);
                                orderDetailsAdapter.notifyDataSetChanged();
                                lvItem.setAdapter(orderDetailsAdapter);
                            }
                        } else {
                            dialog.dismiss();
                            Log.d("[DEBUG]}", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public String getStatus(int status) {
        String strStatus = "";

        switch(status) {
            case 0: {
                strStatus =  "MENUNGGU PEMBAYARAN";
                break;
            }
            case 1: {
                strStatus =  "PEMBAYARAN TERVERIFIKASI";
                break;
            }
        }

        return strStatus;
    }
}
