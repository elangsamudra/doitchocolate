package com.example.doitchocolate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.doitchocolate.Activities.CartActivity;
import com.example.doitchocolate.Helpers.macroCollection;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.R;

import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;

    public CartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    private TextView txtStats;
    private Button btnDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mContext = getActivity();

        prefHelper.init(mContext);

        txtStats = (TextView) view.findViewById(R.id.txtData);
        btnDetail = (Button) view.findViewById(R.id.btnDetail);

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CartActivity.class);
                startActivity(i);
            }
        });

        setupContent();

        return view;
    }

    private void setupContent() {
        int index = prefHelper.countCartItem();
        int total_price = prefHelper.countCartTotal();
        String stats = index + " items total of \n" + macroCollection.formatRupiah(total_price);
        txtStats.setText(stats);
    }
}
