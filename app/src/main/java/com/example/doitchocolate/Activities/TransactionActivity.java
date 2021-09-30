package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doitchocolate.Adapters.OrdersAdapter;
import com.example.doitchocolate.Adapters.ProductAdapter;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.Models.Order;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class TransactionActivity extends AppCompatActivity {

    private Context mContext;
    private ListView listView;
    private List<Order> orderList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mContext = this;

        prefHelper.init(mContext);

        listView = (ListView) findViewById(R.id.lvHistory);

        setupContent();
    }

    private void setupContent() {
        final ProgressDialog dialog = ProgressDialog.show(TransactionActivity.this,"",
                "Loading. Please wait...", true);

        db.collection("orders")
                .whereEqualTo("status", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                dialog.dismiss();
                                Log.d("[DEBUG]", "kosong");
                                Toasty.error(mContext, "Transactions still empty!", Toast.LENGTH_SHORT, true).show();
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

                                OrdersAdapter ordersAdapter = new OrdersAdapter(mContext, orderList, "manage");
                                ordersAdapter.notifyDataSetChanged();
                                listView.setAdapter(ordersAdapter);
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
        setupContent();
    }
}
