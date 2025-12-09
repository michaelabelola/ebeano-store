package com.michael.ebeano;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);

        viewPager.setAdapter(new MainPagerAdapter(this));
        viewPager.setUserInputEnabled(true);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_explore) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (item.getItemId() == R.id.nav_search) {
                viewPager.setCurrentItem(1, false);
                return true;
            } else if (item.getItemId() == R.id.nav_cart) {
                viewPager.setCurrentItem(2, false);
                return true;
            } else if (item.getItemId() == R.id.nav_account) {
                viewPager.setCurrentItem(3, false);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.nav_explore);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.nav_search);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.nav_cart);
                        break;
                    case 3:
                        bottomNav.setSelectedItemId(R.id.nav_account);
                        break;
                }
            }
        });

        // Handle deep-link like navigation from ProductDetailActivity to open Cart tab
        boolean openCart = getIntent().getBooleanExtra("open_cart", false);
        if (openCart) {
            viewPager.setCurrentItem(2, false);
            bottomNav.setSelectedItemId(R.id.nav_cart);
        } else {
            // Default to Explore
            bottomNav.setSelectedItemId(R.id.nav_explore);
        }
    }

    public void openCartTab() {
        viewPager.setCurrentItem(2, false);
        bottomNav.setSelectedItemId(R.id.nav_cart);
    }

    public void openExploreTab() {
        viewPager.setCurrentItem(0, false);
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }

    public void openAccountTab() {
        viewPager.setCurrentItem(3, false);
        bottomNav.setSelectedItemId(R.id.nav_account);
    }
}