package com.example.doitchocolate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.doitchocolate.Activities.ReportActivity;
import com.example.doitchocolate.Activities.TransactionActivity;
import com.example.doitchocolate.R;

import androidx.fragment.app.Fragment;

public class TransactionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;

    public TransactionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1) {
        TransactionFragment fragment = new TransactionFragment();
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

    private LinearLayout menuReport, menuTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        mContext = getActivity();

        menuReport = (LinearLayout) view.findViewById(R.id.menuReport);
        menuTransaction = (LinearLayout) view.findViewById(R.id.menuTransaksi);

        menuTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, TransactionActivity.class);
                startActivity(i);
            }
        });

        menuReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ReportActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}
