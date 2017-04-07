package com.znvoid.demo1.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.znvoid.demo1.MainActivity;
import com.znvoid.demo1.R;

import java.util.ArrayList;
import java.util.List;

import static com.znvoid.demo1.R.id.bottomNavigationView;

/**
 * Created by zn on 2017/3/23.
 */

public class ViewPagerFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private BottomNavigationView bnView;
    private List<android.support.v4.app.Fragment> list=new ArrayList<>();


    private FragmentManager v4fragmentManager;
    private FloatingActionButton fab;
    private MenuItem prevMenuItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.viewpage_fragment,null);

        initFragmentLiat();


        findView(view);


        return view;
    }

    private void initFragmentLiat() {
        ContactsFragment contactsFragment=new ContactsFragment();
        LinkFrangemt linkFrangemt=new LinkFrangemt();
        NetBackupFragment netBackupFragment=new NetBackupFragment();
//        v4fragmentManager=contactsFragment.getFragmentManager();
        if (v4fragmentManager==null)
            System.out.println("kong------");
        list.add(contactsFragment);
        list.add(linkFrangemt);
        list.add(netBackupFragment);
    }

    private void findView(View view) {
        viewPager= (ViewPager) view.findViewById(R.id.viewpager);
        bnView= (BottomNavigationView) view.findViewById(bottomNavigationView);
        bnView.setOnNavigationItemSelectedListener(this);
        v4fragmentManager=((MainActivity)getActivity()).getSupportFragmentManager();
        viewPager.setAdapter(new TabPagerAdapter(v4fragmentManager));
        fab=(FloatingActionButton) view.findViewById(R.id.contacts_fab);
        fab.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_messages:
                viewPager.setCurrentItem(0,false);
                break;

            case R.id.menu_found:
                viewPager.setCurrentItem(1,false);
                break;
            case R.id.menu_nothing:
                viewPager.setCurrentItem(2,false);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Animation animation= AnimationUtils.loadAnimation(getActivity(), R.anim.wifirefreshrotate);
        fab.setAnimation(animation);
        final LinkFrangemt linkFrangemt= (LinkFrangemt) list.get(1);

        fab.post(new Runnable() {
            @Override
            public void run() {
                linkFrangemt.onRefresh();
            }
        });
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position == 0||position==2) {
            fab.setAlpha(positionOffset);
            if (fab.isClickable()) {
                fab.setClickable(false);
            }

            return;
        }
        if (position == 1) {
            if (!fab.isClickable()) {
                fab.setClickable(true);
            }
            fab.setAlpha(1 - positionOffset);
            return;
        }

    }

    @Override
    public void onPageSelected(int position) {
        if (prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        } else {
            bnView.getMenu().getItem(0).setChecked(false);
        }
        bnView.getMenu().getItem(position).setChecked(true);
        prevMenuItem = bnView.getMenu().getItem(position);


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class TabPagerAdapter extends FragmentPagerAdapter{

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
        ContactsFragment contactsFragment= (ContactsFragment) list.get(0);
            contactsFragment.refleshDate();
        }
    }
}
