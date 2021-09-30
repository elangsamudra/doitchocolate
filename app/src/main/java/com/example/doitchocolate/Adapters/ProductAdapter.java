package com.example.doitchocolate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Activities.ManageProductCRUDActivity;
import com.example.doitchocolate.Activities.ProductDetailActivity;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;

import java.util.List;

import androidx.cardview.widget.CardView;



public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> dataList;
    private int userLevel;
    private Category category;

    public ProductAdapter(Context context, List<Product> dataList, int userLevel, Category category) {
        this.context = context;
        this.dataList = dataList;
        this.userLevel = userLevel;
        this.category = category;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.item_products, null);

        ProductHolder productHolder = new ProductHolder();
        productHolder.name = convertView.findViewById(R.id.txtProductName);
        productHolder.price = convertView.findViewById(R.id.txtProductPrice);
        productHolder.image = convertView.findViewById(R.id.imgProduct);
        productHolder.cardProduct = convertView.findViewById(R.id.card);

        final Product product = dataList.get(position);
        productHolder.name.setText(product.getNama_produk());
        String price = macroCollection.formatRupiah(product.getHarga());
        productHolder.price.setText(price);
        Glide.with(context)
                .load(product.getFoto_produk())
                .into(productHolder.image);
        productHolder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if(userLevel == 0) {
                    i = new Intent(context, ProductDetailActivity.class);
                }
                else if(userLevel == 1) {
                    i = new Intent(context, ManageProductCRUDActivity.class);
                }

                i.putExtra("id", product.getId());
                i.putExtra("kategori", product.getKategori());
                i.putExtra("nama_produk", product.getNama_produk());
                i.putExtra("harga", product.getHarga());
                i.putExtra("stok", product.getStok());
                i.putExtra("foto_produk", product.getFoto_produk());
                i.putExtra("pageMode", "edit");
                if(userLevel == 0) {
                    i.putExtra("category_name", category.getCategory_name());
                    i.putExtra("category_id", category.getId());
                }
                context.startActivity(i);
            }
        });

        return convertView;
    }

    public class ProductHolder {
        private TextView name, price;
        private ImageView image;
        private CardView cardProduct;
    }
}
