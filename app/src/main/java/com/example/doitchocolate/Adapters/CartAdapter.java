package com.example.doitchocolate.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doitchocolate.Activities.LoginActivity;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Keranjang;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Keranjang> dataList;

    public CartAdapter(Context context, List<Keranjang> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.template_list_cart, null);

        prefHelper.init(context);

        final CartHolder cartHolder = new CartHolder();
        cartHolder.txtNamaBarang = (TextView) convertView.findViewById(R.id.txtNamaBarang);
        cartHolder.txtHarga = (TextView) convertView.findViewById(R.id.txtHargaBarang);
        cartHolder.add = (ImageView) convertView.findViewById(R.id.btnIncreaseCart);
        cartHolder.decrease = (ImageView) convertView.findViewById(R.id.btnDecreaseCart);
        cartHolder.edtQty = (EditText) convertView.findViewById(R.id.edt_qty);

        final Keranjang keranjang = dataList.get(position);
        final Product product = new Product();
        product.setStok(keranjang.getStock_barang());
        product.setId(keranjang.getId_barang());
        Log.d("[DEBUG]", keranjang.getNama_barang());
        Log.d("[DEBUG]", String.valueOf(keranjang.getHarga_satuan()));
        Log.d("[DEBUG]", String.valueOf(keranjang.getJumlah_barang()));
        cartHolder.txtNamaBarang.setText(keranjang.getNama_barang());
        int calc = (keranjang.getHarga_satuan() * keranjang.getJumlah_barang());
        String harga = macroCollection.formatRupiah(calc);
        cartHolder.txtHarga.setText(harga);
        cartHolder.edtQty.setText(String.valueOf(keranjang.getJumlah_barang()));

        cartHolder.edtQty.setEnabled(false);

        cartHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefHelper.cartAction("increase", product, 1);
                String msg = prefHelper.getCartdata("cart_msg", "string");
                if(msg.equals("OK")) {
                    int old_qty = Integer.parseInt(cartHolder.edtQty.getText().toString());
                    old_qty += 1;
                    cartHolder.edtQty.setText(String.valueOf(old_qty));
                    keranjang.setJumlah_barang(old_qty);

                    int new_harga = keranjang.getJumlah_barang() * keranjang.getHarga_satuan();
                    cartHolder.txtHarga.setText(macroCollection.formatRupiah(new_harga));
                    Toasty.success(context, "Quantity successfully increased!", Toast.LENGTH_SHORT, true).show();
                }
                else {
                    Toasty.error(context, "Quantity exceed product stock!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        cartHolder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int old_qty = Integer.parseInt(cartHolder.edtQty.getText().toString());
                if(old_qty != 0) {
                    old_qty -= 1;
                    cartHolder.edtQty.setText(String.valueOf(old_qty));
                    keranjang.setJumlah_barang(old_qty);
                    prefHelper.cartAction("decrease", product, 1);
                    int new_harga = keranjang.getJumlah_barang() * keranjang.getHarga_satuan();
                    cartHolder.txtHarga.setText(macroCollection.formatRupiah(new_harga));
                }
                else {
                    prefHelper.cartAction("delete", product, 1);
                    dataList.remove(position);
                }
                Toasty.success(context, "Quantity successfully decreased!", Toast.LENGTH_SHORT, true).show();
            }
        });

        return convertView;
    }

    public class CartHolder {
        private TextView txtNamaBarang, txtHarga;
        private EditText edtQty;
        private ImageView add, decrease;
    }
}
