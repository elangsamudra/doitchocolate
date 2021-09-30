package com.example.doitchocolate.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.doitchocolate.Fragments.CategoryFragment;
import com.example.doitchocolate.Fragments.HomeFragment;
import com.example.doitchocolate.Fragments.ProductFragment;
import com.example.doitchocolate.Fragments.ProfileFragment;
import com.example.doitchocolate.Fragments.TransactionFragment;
import com.example.doitchocolate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;



public class AdminMainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance(""));
    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_category: {
                            openFragment(CategoryFragment.newInstance(""));
                            return true;
                        }
                        case R.id.navigation_product: {
                            openFragment(ProductFragment.newInstance(""));
                            return true;
                        }
                        case R.id.navigation_transaction: {
                            openFragment(TransactionFragment.newInstance(""));
                            return true;
                        }
                        case R.id.navigation_profile: {
                            openFragment(ProfileFragment.newInstance(""));
                            return true;
                        }
                    }
                    return false;
                }
            };
}