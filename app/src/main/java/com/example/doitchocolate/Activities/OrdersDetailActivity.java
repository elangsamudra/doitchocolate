package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doitchocolate.Adapters.OrderDetailsAdapter;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Order;
import com.example.doitchocolate.Models.OrderDetail;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

public class OrdersDetailActivity extends AppCompatActivity {

    private EditText edtNama, edtStatus, edtTanggal, edtTotal;
    private ListView listView;
    private Context mContext;
    private List<OrderDetail> orderDetailList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);

        mContext = this;

        prefHelper.init(mContext);

        Intent i = getIntent();
        final Order orders = new Order();
        orders.setId(i.getStringExtra("id"));
        orders.setUsername(i.getStringExtra("username"));
        orders.setStatus(i.getIntExtra("status", 0));
        orders.setTotal(i.getIntExtra("total", 0));
        orders.setCreated_on(i.getStringExtra("created_on"));

        edtStatus = (EditText) findViewById(R.id.edt_status);
        listView = (ListView) findViewById(R.id.lvItem);
        edtNama = (EditText) findViewById(R.id.edt_fullname);
        edtTanggal = (EditText)findViewById(R.id.edt_tanggal);
        edtTotal = (EditText) findViewById(R.id.edt_total);

        edtNama.setText(orders.getUsername());
        edtTanggal.setText(orders.getCreated_on());
        edtStatus.setText(getStatus(orders.getStatus()));
        edtTotal.setText(macroCollection.formatRupiah(orders.getTotal()));

        edtTotal.setEnabled(false);
        edtNama.setEnabled(false);
        edtTotal.setEnabled(false);
        edtStatus.setEnabled(false);
        edtTanggal.setEnabled(false);

        setupContent(orders.getId());
    }

    private void setupContent(String id) {
        final ProgressDialog dialog = ProgressDialog.show(OrdersDetailActivity.this,"",
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

                                OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrdersDetailActivity.this, orderDetailList);
                                orderDetailsAdapter.notifyDataSetChanged();
                                listView.setAdapter(orderDetailsAdapter);
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
