package com.michael.ebeano;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 1 -> new CartFragment();
            case 2 -> new AccountFragment();
            default -> new ExploreFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
