package com.example.doitchocolate.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doitchocolate.Activities.LoginActivity;
import com.example.doitchocolate.Activities.MainActivity;
import com.example.doitchocolate.Activities.ManageProductListActivity;
import com.example.doitchocolate.Adapters.CategoryAdapter;
import com.example.doitchocolate.Adapters.HomeCategoriesAdapter;
import com.example.doitchocolate.Adapters.ProductAdapter;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.Category;
import com.example.doitchocolate.Models.Product;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

public class MenuFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;

    public MenuFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1) {
        MenuFragment fragment = new MenuFragment();
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

    private int userLevel;
    private TextView txtMore;
    private RecyclerView rvCategory;
    private GridView listView;
    private HomeCategoriesAdapter homeCategoriesAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Category> categoryList;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mContext = getActivity();

        prefHelper.init(mContext);

        txtMore = (TextView) view.findViewById(R.id.txtMore);
        rvCategory = (RecyclerView) view.findViewById(R.id.recyclerCategory);
        listView = (GridView) view.findViewById(R.id.lvProduct);

        setupCategory();
        setupContent();

        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, ProductFragment.newInstance(""));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void setupCategory() {
        db.collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
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

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                homeCategoriesAdapter = new HomeCategoriesAdapter(mContext, categoryList);
                                rvCategory.setLayoutManager(mLayoutManager);
                                rvCategory.setItemAnimator(new DefaultItemAnimator());
                                rvCategory.setAdapter(homeCategoriesAdapter);
                                homeCategoriesAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("[DEBUG]}", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setupContent() {
        final ProgressDialog dialog = ProgressDialog.show(mContext,"",
                "Loading. Please wait...", true);

        db.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                dialog.dismiss();
                                Log.d("[DEBUG]", "kosong");
                                Toasty.error(mContext, "Product still empty, please add one!", Toast.LENGTH_SHORT, true).show();
                            }
                            else {
                                productList = new ArrayList<Product>();
                                productList.clear();
                                Log.d("[DEBUG]", "size-1: " + productList.size());
                                for (DocumentSnapshot document : task.getResult()) {
                                    Product product = document.toObject(Product.class);
                                    product.setId(document.getId());
                                    productList.add(product);
//                                    Log.d("[DEBUG]", document.getId() + " => " + document.getData());
                                    Log.d("[DEBUG]", "size-2: " + productList.size());
                                }

                                dialog.dismiss();

                                Category category = new Category();
                                category.setId("0");
                                category.setCategory_name("base");
                                Log.d("[DEBUG]", "size-3: " + productList.size());
                                ProductAdapter productAdapter = new ProductAdapter(mContext, productList, userLevel, category);
                                productAdapter.notifyDataSetChanged();
                                listView.setAdapter(productAdapter);
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
        setupCategory();
        setupContent();
    }
}
