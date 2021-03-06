package com.qianfujiaoyu.Model_Order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.StatusBarCompat;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Mine_GYQD extends AppCompatActivity  implements View.OnClickListener {
    private ArrayList<Fragment> list;
    private SharedPreferences sp;
    private static final String TAG = "Mine_GYQD";
    private ViewPager viewPager;
    private DDPagerAdapter adapter;
//    private TextView pintuan,fahuo,tuikuan;
    //    private DecimalFormat df;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.mine_dindan);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        list=new ArrayList<Fragment>();
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.back));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        if(sp.getString("role","").equals("3")){//管理员
//            findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//           for(int i=0;i<3;i++){
//               DingDanFragment d=new DingDanFragment();
//               Bundle b=new Bundle();
//               b.putString("tag",String .valueOf(i+1));
//               d.setArguments(b);
//               list.add(d);
//           }
//        }else{//个人
            findViewById(R.id.title).setVisibility(View.VISIBLE);
            DingDanFragment d=new DingDanFragment();
            list.add(d);
//        }
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        adapter =new DDPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.setAdapter(adapter);
//        pintuan.performClick();
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position){
//                    case 0:
//                        pintuan.performClick();
//                        break;
//                    case 1:
//                        fahuo.performClick();
//                        break;
//                    case 2:
//                        tuikuan.performClick();
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.pintuan:
//                view.setSelected(true);
//                pintuan.setTextColor(Color.BLACK);
//                fahuo.setSelected(false);
//                fahuo.setTextColor(Color.WHITE);
//
//                tuikuan.setSelected(false);
//                tuikuan.setTextColor(Color.WHITE);
//                viewPager.setCurrentItem(Integer.valueOf(view.getTag().toString()));
//                break;
//            case R.id.fahuo:
//                view.setSelected(true);
//                fahuo.setTextColor(Color.BLACK);
//                pintuan.setSelected(false);
//                pintuan.setTextColor(Color.WHITE);
//
//                tuikuan.setSelected(false);
//                tuikuan.setTextColor(Color.WHITE);
//                viewPager.setCurrentItem(Integer.valueOf(view.getTag().toString()));
//                break;
//            case R.id.tuikuan:
//                view.setSelected(true);
//                tuikuan.setTextColor(Color.BLACK);
//                fahuo.setSelected(false);
//                fahuo.setTextColor(Color.WHITE);
//
//                pintuan.setSelected(false);
//                pintuan.setTextColor(Color.WHITE);
//                viewPager.setCurrentItem(Integer.valueOf(view.getTag().toString()));
//                break;
            case R.id.back:
                finish();
                break;
        }
    }


    private static  class DDPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;
        public DDPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
            super(fm);
            this.list=list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list==null?0:list.size();
        }
    }







}
