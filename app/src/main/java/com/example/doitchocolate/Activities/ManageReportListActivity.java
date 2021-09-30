package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doitchocolate.Adapters.OrderDetailsAdapter;
import com.example.doitchocolate.Adapters.OrdersAdapter;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ManageReportListActivity extends AppCompatActivity {

    private Context mContext;
    private Button btnBack;
    private ListView listView;
    private EditText edtTotal;
    private int total = 0;
    private List<Order> orderList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managereportlist);

        mContext = this;

        prefHelper.init(mContext);

        Intent i = getIntent();
        from = i.getStringExtra("from");
        to = i.getStringExtra("to");

        listView = (ListView) findViewById(R.id.lvHistory);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        btnBack = (Button) findViewById(R.id.btnKembali);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupContent();
    }

    private void setupContent() {
        final ProgressDialog dialog = ProgressDialog.show(ManageReportListActivity.this,"",
                "Loading. Please wait...", true);

        db.collection("orders")
                .whereGreaterThanOrEqualTo("created_on", from)
                .whereLessThanOrEqualTo("created_on", to)
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
                                orderList = new ArrayList<Order>();
                                orderList.clear();
                                Log.d("[DEBUG]", "size-1: " + orderList.size());
                                for (DocumentSnapshot document : task.getResult()) {
                                    Order order = document.toObject(Order.class);
                                    order.setId(document.getId());
                                    orderList.add(order);

                                    Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                    Log.d("[DEBUG]", "size-2: " + orderList.size());
                                }
                                Log.d("[DEBUG]", "size-3: " + orderList.size());

                                dialog.dismiss();

                                OrdersAdapter ordersAdapter = new OrdersAdapter(mContext, orderList, "history");
                                ordersAdapter.notifyDataSetChanged();
                                listView.setAdapter(ordersAdapter);

                                countTotal(orderList);
                            }
                        } else {
                            dialog.dismiss();
                            Log.d("[DEBUG]}", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void countTotal(List<Order> orderList) {
        total = 0;
        edtTotal.setText(macroCollection.formatRupiah(0));

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            total = total + order.getTotal();
            Log.d("[DEBUG]", "Total: " + total + " - " + i);
        }

        edtTotal.setText(macroCollection.formatRupiah(total));
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        setupContent();
    }
}
