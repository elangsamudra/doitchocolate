package com.example.doitchocolate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.OrderDetail;
import com.example.doitchocolate.R;

import java.util.List;

public class OrderDetailsAdapter extends BaseAdapter {
    private Context context;
    private List<OrderDetail> dataList;

    public OrderDetailsAdapter(Context context, List<OrderDetail> dataList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.template_list_detail, null);

        prefHelper.init(context);

        final OrderDetailsHolder cartHolder = new OrderDetailsHolder();
        cartHolder.txtNamaBarang = (TextView) convertView.findViewById(R.id.txtNamaBarang);
        cartHolder.txtHarga = (TextView) convertView.findViewById(R.id.txtHargaBarang);
        cartHolder.edtQty = (EditText) convertView.findViewById(R.id.edt_qty);

        final OrderDetail orderDetails = dataList.get(position);
        cartHolder.txtNamaBarang.setText(orderDetails.getProduct_name());
        int calc = (orderDetails.getProduct_price() * orderDetails.getQty());
        String harga = macroCollection.formatRupiah(calc);
        cartHolder.txtHarga.setText(harga);
        cartHolder.edtQty.setText(String.valueOf(orderDetails.getQty()));

        return convertView;
    }

    public class OrderDetailsHolder {
        private TextView txtNamaBarang, txtHarga;
        private EditText edtQty;
    }
}
