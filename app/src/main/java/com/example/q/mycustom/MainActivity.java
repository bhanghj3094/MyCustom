package com.example.q.mycustom;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("errtest", "begin onCreate");
        super.onCreate(savedInstanceState);
        Log.d("errtest", "super.onCreate");
        setContentView(R.layout.activity_main);

        Log.d("errtest", "on Create setContentView done");
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("errtest", "made toolbar");

        TabLayout tabLayout = findViewById(R.id.tabs);

        Log.d("errtest", "made tablayout");
        viewPager = findViewById(R.id.container);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        Log.d("errtest", "viewpager and set adapter finished");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        Log.d("errtest", "done onCreate");
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs = 3;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Tab1 tab1 = new Tab1();
                    return tab1;
                case 1:
                    Tab2 tab2 = new Tab2();
                    return tab2;
                case 2:
                    Tab3 tab3 = new Tab3();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    
}
