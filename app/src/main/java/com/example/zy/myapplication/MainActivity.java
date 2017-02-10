package com.example.zy.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private List<TabFragment> tabFragments;

    private List<ShadeView> tabIndicators;

    private ViewPager viewPager;

    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initData() {
        tabFragments = new ArrayList<>();
        tabIndicators = new ArrayList<>();
        String[] titles = new String[]{"微信", "通讯录", "发现", "我"};
        for (String title : titles) {
            TabFragment tabFragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Title", title);
            tabFragment.setArguments(bundle);
            tabFragments.add(tabFragment);
        }
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return tabFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return tabFragments.get(arg0);
            }
        };
        initTabIndicator();
    }

    private void initTabIndicator() {
        ShadeView one = (ShadeView) findViewById(R.id.id_indicator_one);
        ShadeView two = (ShadeView) findViewById(R.id.id_indicator_two);
        ShadeView three = (ShadeView) findViewById(R.id.id_indicator_three);
        ShadeView four = (ShadeView) findViewById(R.id.id_indicator_four);
        tabIndicators.add(one);
        tabIndicators.add(two);
        tabIndicators.add(three);
        tabIndicators.add(four);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }

    @Override
    public void onClick(View v) {
        resetTabsStatus();
        switch (v.getId()) {
            case R.id.id_indicator_one:
                tabIndicators.get(0).setIconAlpha(1.0f);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                tabIndicators.get(1).setIconAlpha(1.0f);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                tabIndicators.get(2).setIconAlpha(1.0f);
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                tabIndicators.get(3).setIconAlpha(1.0f);
                viewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置Tab状态
     */
    private void resetTabsStatus() {
        for (int i = 0; i < tabIndicators.size(); i++) {
            tabIndicators.get(i).setIconAlpha(0);
        }
    }

    /**
     * 如果是直接点击图标来跳转页面的话，position值为0到3，positionOffset一直为0.0
     * 如果是通过滑动来跳转页面的话
     * 假如是从第一页滑动到第二页
     * 在这个过程中，positionOffset从接近0逐渐增大到接近1.0，滑动完成后又恢复到0.0，而position只有在滑动完成后才从0变为1
     * 假如是从第二页滑动到第一页
     * 在这个过程中，positionOffset从接近1.0逐渐减小到0.0，而position一直是0
     *
     * @param position             当前页面索引
     * @param positionOffset       偏移量
     * @param positionOffsetPixels 偏移量
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("TAG", "position==" + position);
        Log.e("TAG", "positionOffset==" + positionOffset);
        Log.e("TAG", "positionOffsetPixels==" + positionOffsetPixels);
        if (positionOffset > 0) {
            ShadeView leftTab = tabIndicators.get(position);
            ShadeView rightTab = tabIndicators.get(position + 1);
            leftTab.setIconAlpha(1 - positionOffset);
            rightTab.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2) {
            tabIndicators.get(position).setIconBitmap(this, R.drawable.discover_green);
        } else {
            tabIndicators.get(2).setIconBitmap(this, R.drawable.discover);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
