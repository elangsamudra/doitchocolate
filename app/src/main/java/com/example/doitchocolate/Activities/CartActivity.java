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

import com.example.doitchocolate.Adapters.CartAdapter;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Keranjang;
import com.example.doitchocolate.Models.Order;
import com.example.doitchocolate.Models.OrderDetail;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    public EditText edtTotal;
    private Button btnBatal, btnPesan;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat dateFormatter;
    private Date date;
    private List<Keranjang> keranjangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        context = this;

        prefHelper.init(context);

        listView = (ListView) findViewById(R.id.lvPurchase);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        btnBatal = (Button) findViewById(R.id.btnBatal);
        btnPesan = (Button) findViewById(R.id.btnPesan);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        date = new Date();

        setupList();

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keranjangList.size() > 0) {
                    int total_price = prefHelper.countCartTotal();

                    Order order = new Order();
                    order.setStatus(0);
                    order.setTotal(total_price);
                    order.setUsername(prefHelper.getUserdata("username"));
                    order.setCreated_on(dateFormatter.format(date));

                    final ProgressDialog dialog = ProgressDialog.show(CartActivity.this, "",
                            "Loading. Please wait...", true);

                    db.collection("orders")
                            .add(order)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String orderCode = documentReference.getId();

                                    final int index = Integer.parseInt(prefHelper.getCartdata("cart_index", "int"));
                                    for(int i = 1; i <= index; i++) {
                                        int deleted = Integer.parseInt(prefHelper.getCartdata("cart_deleted_" + i, "int"));
                                        Log.d("[DEBUG]", "index: " + index + " - i: " + i);
                                        if(deleted == 0) {
                                            Keranjang keranjang = keranjangList.get(i - 1);
                                            final OrderDetail orderDetail = new OrderDetail();
                                            orderDetail.setOrder_id(orderCode);
                                            orderDetail.setProduct_id(keranjang.getId_barang());
                                            orderDetail.setProduct_name(keranjang.getNama_barang());
                                            orderDetail.setProduct_price(keranjang.getHarga_satuan());
                                            orderDetail.setQty(keranjang.getJumlah_barang());

                                            db.collection("order_detail")
                                                    .add(orderDetail)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            db.collection("product")
                                                                    .document(orderDetail.getProduct_id())
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            Product product = documentSnapshot.toObject(Product.class);
                                                                            product.setId(documentSnapshot.getId());

                                                                            int newStock = product.getStok() - orderDetail.getQty();
                                                                            product.setStok(newStock);

                                                                            db.collection("product")
                                                                                    .document(product.getId())
                                                                                    .set(product)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("[DEBUG]", "Error adding document", e);
                                                            dialog.dismiss();
                                                            Toasty.error(CartActivity.this, "Failed ordering product, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                                        }
                                                    });

                                            Log.d("[DEBUG]", "done!!");
                                            Log.d("[DEBUG]", "compare index: " + index + " - i: " + i);
                                        }

                                        if(i == index) {
                                            dialog.dismiss(); // DISMSI
                                            Toasty.success(CartActivity.this, "Success ordering product!", Toast.LENGTH_SHORT, true).show();
                                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            prefHelper.flushCart();
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("[DEBUG]", "Error adding document", e);
                                    dialog.dismiss();
                                    Toasty.error(CartActivity.this, "Failed ordering product, please contact system administrator!", Toast.LENGTH_SHORT, true).show();
                                }
                            });
                }
                else {
                    Toasty.error(CartActivity.this, "Failed to order product, you can't checkout an empty cart!!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    private void setupList() {
        keranjangList = new ArrayList<Keranjang>();
        keranjangList.clear();
        int index = Integer.parseInt(prefHelper.getCartdata("cart_index", "int"));
        for(int i = 1; i <= index; i++) {
            int deleted = Integer.parseInt(prefHelper.getCartdata("cart_deleted_" + i, "int"));
            if(deleted == 0) {
                Keranjang keranjang = new Keranjang();
                keranjang.setId_barang(prefHelper.getCartdata("cart_id_barang_" + i, "string"));
                keranjang.setNama_barang(prefHelper.getCartdata("cart_nama_barang_" + i, "string"));
                keranjang.setStock_barang(Integer.parseInt(prefHelper.getCartdata("cart_stock_barang_" + i, "int")));
                keranjang.setHarga_satuan(Integer.parseInt(prefHelper.getCartdata("cart_harga_barang_" + i, "int")));
                keranjang.setJumlah_barang(Integer.parseInt(prefHelper.getCartdata("cart_jumlah_barang_" + i, "int")));
                keranjangList.add(keranjang);
            }
        }

        if(keranjangList.size() > 0) {
            CartAdapter cartAdapter = new CartAdapter(CartActivity.this, keranjangList);
            cartAdapter.notifyDataSetChanged();
            listView.setAdapter(cartAdapter);

            int total_price = prefHelper.countCartTotal();
            edtTotal.setText(macroCollection.formatRupiah(total_price));
        }
        else {
            Toast.makeText(CartActivity.this, "Data kosong!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupList();
    }
}
