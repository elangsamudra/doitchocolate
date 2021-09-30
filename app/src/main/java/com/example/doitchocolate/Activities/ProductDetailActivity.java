package com.example.doitchocolate.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgBarang;
    private TextView namaBarang, hargaBarang, stockBarang;
    private EditText edtQty;
    private Button btnAdd;

    private String prodIcon;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        prefHelper.init(this);

        imgBarang = (ImageView) findViewById(R.id.imgBarang);
        namaBarang = (TextView) findViewById(R.id.txtNamaBarang);
        hargaBarang = (TextView) findViewById(R.id.txtHargaBarang);
        stockBarang = (TextView) findViewById(R.id.txtStockBarang);
        edtQty = (EditText) findViewById(R.id.edt_qty);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        final Intent i = getIntent();
        String strName = i.getStringExtra("nama_produk") ;
        String strStock = i.getIntExtra("stok", 0) + " unit";
        namaBarang.setText(strName);
        stockBarang.setText(strStock);
        String price = macroCollection.formatRupiah(i.getIntExtra("harga", 0));
        hargaBarang.setText(price);

        prodIcon = i.getStringExtra("foto_produk");

        if(!prodIcon.equals("no_image")) {
            Glide.with(ProductDetailActivity.this)
                    .load(prodIcon)
                    .into(imgBarang);
        }
        id = i.getStringExtra("id");

        if(!prefHelper.getUserdata("level").equals("0")) {
            btnAdd.setVisibility(View.INVISIBLE);
            edtQty.setVisibility(View.INVISIBLE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(ProductDetailActivity.this,"",
                        "Loading. Please wait...", true);

                Product product = new Product();
                product.setId(id);
                product.setNama_produk(i.getStringExtra("nama_produk"));
                product.setHarga(i.getIntExtra("harga", 0));
                product.setStok(i.getIntExtra("stok", 0));

                Log.d("[DEBUG]", "nama: " + product.getNama_produk() + " - id: " + product.getId());

                if(edtQty.getText().toString().trim().length() > 0) {
                    int qty = Integer.parseInt(edtQty.getText().toString());
                    if(qty != 0) {
                        if(qty <= product.getStok()) {
                            prefHelper.cartAction("add", product, qty);
                            String msg = prefHelper.getCartdata("cart_msg", "string");
                            if(msg.equals("OK")) {
                                Toasty.success(ProductDetailActivity.this, "Success adding item to cart!", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }
                            else {
                                Toasty.error(ProductDetailActivity.this, "Failed adding item to cart, you already have this item on your cart and by adding more item will exceed the store stock!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                        else {
                            Toasty.error(ProductDetailActivity.this, "Failed adding item to cart, quantity cannot be larger than stock!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    else {
                        Toasty.error(ProductDetailActivity.this, "Failed adding item to cart, please fill the quantity you willing to add!", Toast.LENGTH_SHORT, true).show();
                    }

                    dialog.dismiss();
                }
                else {
                    Toasty.error(ProductDetailActivity.this, "Failed adding item to cart, please fill the quantity you willing to add!", Toast.LENGTH_SHORT, true).show();
                    dialog.dismiss();
                }
            }
        });
    }
}
