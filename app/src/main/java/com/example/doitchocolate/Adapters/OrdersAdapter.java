package com.example.doitchocolate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doitchocolate.Activities.ManageTransactionActivity;
import com.example.doitchocolate.Activities.OrdersDetailActivity;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Models.Order;
import com.example.doitchocolate.R;

import java.util.List;

import androidx.cardview.widget.CardView;

public class OrdersAdapter extends BaseAdapter {
    private Context context;
    private List<Order> dataList;
    private String mode;

    public OrdersAdapter(Context context, List<Order> dataList, String mode) {
        this.context = context;
        this.dataList = dataList;
        this.mode = mode;
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
        convertView = layoutInflater.inflate(R.layout.template_list_history, null);

        OrdersHolder ordersHolder = new OrdersHolder();
        ordersHolder.txtHarga = (TextView) convertView.findViewById(R.id.txtHargaTotal);
        ordersHolder.txtTanggal = (TextView) convertView.findViewById(R.id.txtTanggal);
        ordersHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
        ordersHolder.baseLayout = (CardView) convertView.findViewById(R.id.layoutPembelian);

        final Order order = dataList.get(position);
        ordersHolder.txtHarga.setText(macroCollection.formatRupiah(order.getTotal()));
        ordersHolder.txtTanggal.setText(order.getCreated_on());
        ordersHolder.txtStatus.setText(getStatus(order.getStatus()));
        ordersHolder.baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if(mode.equals("history")) {
                    i = new Intent(context, OrdersDetailActivity.class);
                }
                else if(mode.equals("manage")) {
                    i = new Intent(context, ManageTransactionActivity.class);
                }
                i.putExtra("id", order.getId());
                i.putExtra("username", order.getUsername());
                i.putExtra("total", order.getTotal());
                i.putExtra("status", order.getStatus());
                i.putExtra("created_on", order.getCreated_on());
                context.startActivity(i);
            }
        });

        return convertView;
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

    public class OrdersHolder {
        private TextView txtTanggal, txtKode, txtHarga, txtStatus;
        private CardView baseLayout;
    }
}
