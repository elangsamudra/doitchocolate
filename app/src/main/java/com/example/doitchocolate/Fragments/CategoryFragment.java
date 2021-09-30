package com.example.doitchocolate.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doitchocolate.Activities.LoginActivity;
import com.example.doitchocolate.Activities.ManageCategoryCRUDActivity;
import com.example.doitchocolate.Adapters.CategoryAdapter;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;

public class CategoryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;

    public CategoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1) {
        CategoryFragment fragment = new CategoryFragment();
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

    private FloatingActionButton fab;
    private ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        mContext = getActivity();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        listView = (ListView) view.findViewById(R.id.lvCategory);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ManageCategoryCRUDActivity.class);
                i.putExtra("pageMode", "add");
                startActivity(i);
            }
        });

        setupContent();

        return view;
    }

    private void setupContent() {
        final ProgressDialog dialog = ProgressDialog.show(mContext,"",
                "Loading. Please wait...", true);

        db.collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                dialog.dismiss();
                                Log.d("[DEBUG]", "kosong");
                                Toasty.error(mContext, "Category still empty, please add one!", Toast.LENGTH_SHORT, true).show();
                            }
                            else {
                                categoryList = new ArrayList<Category>();
                                categoryList.clear();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Category category = document.toObject(Category.class);
                                    category.setId(document.getId());
                                    categoryList.add(category);
                                    Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                }

                                dialog.dismiss();

                                CategoryAdapter categoriesAdapter = new CategoryAdapter(mContext, categoryList, "category");
                                categoriesAdapter.notifyDataSetChanged();
                                listView.setAdapter(categoriesAdapter);
                            }
                        } else {
                            dialog.dismiss();
                            Log.d("[DEBUG]}", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupContent();
    }
}
