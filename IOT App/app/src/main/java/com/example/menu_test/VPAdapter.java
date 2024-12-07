package com.example.menu_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class VPAdapter extends FragmentStateAdapter {

//    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        switch (position) {
//            case 0: return new smoke_fragment();
//            case 1: return new gas_fragment();
//            case 2: return new uv_fragment();
//            default: return new smoke_fragment();
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return 3;
//    }

    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final ArrayList<String> fragmentTitles = new ArrayList<>();

    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new smoke_fragment());
        fragmentTitles.add("Smoke");

        fragments.add(new gas_fragment());
        fragmentTitles.add("Gas");

        fragments.add(new uv_fragment());
        fragmentTitles.add("UV");
    }

    public Fragment createFragment(int position) {
        return fragments.get(position);  // Επιστρέφει το Fragment για τη συγκεκριμένη θέση
    }

    public int getItemCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
        notifyItemInserted(fragments.size() - 1);
    }

    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

}
