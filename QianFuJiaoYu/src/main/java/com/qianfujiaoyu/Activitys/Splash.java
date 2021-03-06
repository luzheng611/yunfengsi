package com.qianfujiaoyu.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.qianfujiaoyu.R;
import com.qianfujiaoyu.Utils.ACache;
import com.qianfujiaoyu.Utils.AnalyticalJSON;
import com.qianfujiaoyu.Utils.ApisSeUtil;
import com.qianfujiaoyu.Utils.Constants;
import com.qianfujiaoyu.Utils.ImageUtil;
import com.qianfujiaoyu.Utils.LoginUtil;
import com.qianfujiaoyu.Utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;


/**
 * Created by Administrator on 2016/10/31.
 */
public class Splash extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Splash";

    /*
       启动页图像控件
     */
    private ImageView image;
    /*
      跳过
    */
    private TextView skip;
    /*
     倒数计时器
    */
    private CountDownTimer cdt;

    //获取到的广告页地址
    private String imageUrl;
    private ACache acache;
    private Bitmap mAD;
    private int screenHeight;

    private TextView type;//类型提示
    private boolean isFirstIn = true;
    /**
     * 轮播引导页
     */
    private ViewPager pager;
    /**
     * 引导页适配器
     */
    private PagerAdapter pagerAdapter;
    /**
     * 广告页URL
     */
    private URL y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        initView();



    }

    /**
     * 获取广告图
     */
    private void getAD() {
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkGo.post(Constants.getAD).tag(TAG)
                .params("key",m.K())
                .params("msg",m.M()).execute(new AbsCallback<HashMap<String, String>>() {
            @Override
            public HashMap<String, String> convertSuccess(okhttp3.Response response) throws Exception {
                HashMap<String, String> map = AnalyticalJSON.getHashMap(response.body().string());
                return map;
            }

            @Override
            public void onSuccess(HashMap<String, String> map, Call call, okhttp3.Response response) {
                String url = map.get("image1");
                String detail = map.get("url");
                getBitmapForAD(url, detail);
            }
        });
    }

    //new AbsCallback<Object>() {
//
//        @Override
//        public Object parseNetworkResponse(Response response) throws Exception {
//            return null;
//        }
//
//        @Override
//        public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
//            if (response != null) {
//                try {
//                    String data = response.body().string();
//                    HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
//                    Log.w(TAG, "onResponse: 广告页地址" + map);
//                    if (map != null) {
//                        String url = map.get("image1");
//                        String detail = map.get("url");
//                        getBitmapForAD(url, detail);
//                    }
//
//                } catch (IOException e) {
//                    Log.w(TAG, "onResponse:广告页 错误" + e.toString());
//                    e.printStackTrace();
//                }
//            } else {
//                Log.w(TAG, "onResponse:广告页 错误");
//            }
//        }
//    }
    private void getBitmapForAD(final String url, final String detail) {
        try {
            y = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            e.printStackTrace();
            Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
        }
        if (!(url).equals(acache.getAsString("ad_str"))) {//新广告页
            final BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inPreferredConfig = Bitmap.Config.RGB_565;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream in = null;
                    try {
                        in = y.openStream();
                    } catch (IOException e) {
                        Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
                        e.printStackTrace();
                    }
                    final Bitmap b = BitmapFactory.decodeStream(in, null, bfo);
                    if (b != null) {
                        Log.w(TAG, "onResourceReady: 广告页bitmap大小————》" + b.getRowBytes() * b.getHeight());
                        acache.put(("ad_str"), url, ACache.TIME_DAY);
                        acache.put(("ad_bmp"), b, ACache.TIME_HOUR);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (image != null) {
                                    image.setImageBitmap(b);
                                    type.setVisibility(View.VISIBLE);
                                    image.setOnClickListener(Splash.this);
                                    image.setTag(detail);
                                }
                            }
                        });
                    }
                    try {
                        if (in != null)
                            in.close();
                    } catch (IOException e) {
                        Log.w(TAG, "getBitmapForAD:广告页 错误" + e.toString());
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {//缓存广告页
            Bitmap b = acache.getAsBitmap("ad_bmp");
            if (b != null && image != null) {
                image.setImageBitmap(b);
                image.setOnClickListener(Splash.this);
                image.setTag(detail);
                type.setVisibility(View.VISIBLE);
            } else {
                acache.put("ad_str", "");
                getBitmapForAD(url, detail);//重新加载图片并缓存
            }
        }


    }


    /**
     * 初始化控件
     */
    private void initView() {
        type = (TextView) findViewById(R.id.splash_type);
        skip = (TextView) findViewById(R.id.splash_skip);
        skip.setOnClickListener(this);
        cdt = new CountDownTimer(5200, 1000) {///倒计时
            @Override
            public void onTick(long millisUntilFinished) {
                skip.setText("跳过" + (millisUntilFinished / 1000) + "s");
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
        acache = ACache.get(getApplicationContext());
//        if (PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true)) {
//            ViewStub viewStubfirst = (ViewStub) findViewById(R.id.view_stub_first);
//            View view = viewStubfirst.inflate();
//            pager = (ViewPager) view.findViewById(R.id.view_stub_viewpager);
//            final int[] images = new int[]{R.drawable.loading1, R.drawable.loading2};
//            pagerAdapter = new PagerAdapter() {
//                @Override
//                public int getCount() {
//                    return images.length;
//                }
//
//                @Override
//                public boolean isViewFromObject(View view, Object object) {
//                    return view == object;
//                }
//
//                @Override
//                public void destroyItem(ViewGroup container, int position, Object object) {
//                    container.removeView((View) object);
//                }
//
//                @Override
//                public Object instantiateItem(ViewGroup container, int position) {
//                    ImageView img = new ImageView(Splash.this);
//                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    img.setImageBitmap(ImageUtil.readBitMap(Splash.this, images[position]));
//                    container.addView(img);
//                    return img;
//                }
//
//            };
//            pager.setOffscreenPageLimit(images.length);
//            pager.setAdapter(pagerAdapter);
//            skip.setText("跳过");
//            skip.setVisibility(View.VISIBLE);
//        } else {
            ViewStub viewStubstart = (ViewStub) findViewById(R.id.view_stub_start);
            View view = viewStubstart.inflate();
            image = (ImageView) view.findViewById(R.id.splash_image);
            image.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.start));
            image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAD();
                    skip.setText("跳过5s");
                    skip.setVisibility(View.VISIBLE);
                    cdt.start();
                }
            }, 1200);


//        }


    }


    /**
     * 手动释放
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        image = null;
        if (mAD != null && !mAD.isRecycled()) {
            mAD = null;
        }
        pagerAdapter = null;
        pager = null;
        OkGo.getInstance().cancelTag(TAG);
        Log.w(TAG, "onDestroy: 销毁页面");
        Log.w(TAG, "onDestroy: " + PreferenceUtil.getUserIncetance(this).getBoolean("isFirstIn", true));
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
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.splash_skip://跳过
                v.setEnabled(false);
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                if(new LoginUtil().checkLogin(this)){
                    intent.setClass(this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
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
                    intent.setClass(this, AD.class);
                    intent.putExtra("url", v.getTag().toString());
                    startActivity(intent);
                    finish();
                }

                break;
        }
    }

}
