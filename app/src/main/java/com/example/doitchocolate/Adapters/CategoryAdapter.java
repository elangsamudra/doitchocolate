package com.example.doitchocolate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Activities.ManageCategoryCRUDActivity;
import com.example.doitchocolate.Activities.ManageProductListActivity;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.R;

import java.util.List;

import androidx.cardview.widget.CardView;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> dataList;
    private String mode;

    public CategoryAdapter(Context context, List<Category> dataList, String mode) {
        this.context = context;
        this.dataList = dataList;
        this.mode = mode;
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
        convertView = layoutInflater.inflate(R.layout.item_categories, null);

        CategoryHolder categoryHolder = new CategoryHolder();
        categoryHolder.name = convertView.findViewById(R.id.txtCategoryName);
        categoryHolder.cardCat = convertView.findViewById(R.id.card);

        final Category category = dataList.get(position);
        categoryHolder.name.setText(category.getCategory_name());

        Log.d("[DEBUG]", "cat index adapter: " + category.getId());
        categoryHolder.cardCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if(mode.equals("product")) {
                    i = new Intent(context, ManageProductListActivity.class);
                    i.putExtra("id", category.getId());
                    i.putExtra("category_name", category.getCategory_name());
                }
                else if(mode.equals("category")) {
                    i = new Intent(context, ManageCategoryCRUDActivity.class);
                    i.putExtra("id", category.getId());
                    i.putExtra("category_name", category.getCategory_name());
                    i.putExtra("pageMode", "edit");
                }
                context.startActivity(i);
            }
        });

        return convertView;
    }

    public class CategoryHolder {
        private TextView name;
        private CardView cardCat;
    }
}
