package com.yunfengsi.Models.Model_activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.ruffian.library.RTextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.yunfengsi.Adapter.PL_List_Adapter;
import com.yunfengsi.Audio_BD.WakeUp.Recognizelmpl.IBDRcognizeImpl;
import com.yunfengsi.BuildConfig;
import com.yunfengsi.Managers.Base.BasePayParams;
import com.yunfengsi.Managers.CollectManager;
import com.yunfengsi.Models.YaoYue.Activity_YaoYue;
import com.yunfengsi.Models.YunDou.YunDouAwardDialog;
import com.yunfengsi.R;
import com.yunfengsi.Setting.Login;
import com.yunfengsi.Setting.Mine_gerenziliao;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.CheckNumUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.ImageUtil;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.MD5Utls;
import com.yunfengsi.Utils.Network;
import com.yunfengsi.Utils.PointMoveHelper;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ScaleImageUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.TimeUtils;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.ErWeiMa.QRActivity;
import com.yunfengsi.View.mPLlistview;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/10/7.
 */
public class ActivityDetail extends AndroidPopupActivity implements View.OnClickListener, PL_List_Adapter.onHuifuListener {
    private ImageView rightImg;
    private ImageView shoucang;
    private ImageView dianzanImg;
    private TextView  dianzanText, title, fabuTime, faqidanwei, huodongdidian, huodongTime, peopleNum, Tobaoming, content, FaSong;
    private EditText     PLText;
    private LinearLayout dianzan;
    //    private LinearLayout PointLayou;//轮播图圆点layout
    private mPLlistview  PlListVIew;
    private String page    = "1";
    private String endPage = "";
    private Thread thread;//线程
    private static final String TAG = "Activityd";
    private String             Id;//活动id
    private ArrayList<String>  imageList;
    //    private ViewPager viewPager;//轮播
    private Banner             banner;
    private PL_List_Adapter    adapter;
    private SharedPreferences  sp;
    private InputMethodManager imm;
    private TextView           tv;//评论的头部

    private ShareAction action;
    private boolean isPLing = false;
    private LinearLayout currentLayout;
    private int          currentPosition;
    private String       currentId;
    /*
       活动按钮
        */
    private TextView     tv_activity;

    private FrameLayout     overlay;
    private ImageView       toggle;
    private TextView        audio;
    private IBDRcognizeImpl ibdRcognize;
    private UMWeb           umWeb;
    private String act_prol = "";//活动协议Html

    private ImageView tip;
    //第一次加载的评论数量
    private int firstNum = 0;
    private HashMap<String, String> map;

    private boolean isNeedToChooseTime = false;
    private String  startTime          = "", endTime = "";
    private TextView yue;


    private TextView quickChannel;
    private boolean hasShowedQuick = false;
    private ArrayList<HashMap<String, String>> quickInfoMap;
    private boolean hasShowActivityProtocol = false;
    private boolean shouldShowQuickChannel=false;
    public static class RefreshEvent {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        LogUtil.e("重新加载活动详情");
        LoadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        EventBus.getDefault().register(this);
        initView();
        LoadData();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        tip = findViewById(R.id.tip);
        tip.setOnClickListener(this);
        /**
         * 快速通道
         */
        quickChannel = findViewById(R.id.quickChannel);
        quickChannel.setText(mApplication.ST("直录通道"));
        quickChannel.setOnClickListener(this);
        /**
         * 快速通道
         */

        yue = findViewById(R.id.activity_detail_yue);
        yue.setText("约");
        yue.setOnClickListener(this);
        PLText = findViewById(R.id.activity_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        Glide.with(this).load(R.drawable.pinglun).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
                .into((ImageView) findViewById(R.id.pinglun_image));
        Glide.with(this).load(R.drawable.fenxiangb).skipMemoryCache(true).override(DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25))
                .into((ImageView) findViewById(R.id.fenxiang_image));
        toggle = findViewById(R.id.toggle_audio_word);
        audio = findViewById(R.id.audio_button);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle.setSelected(!view.isSelected());
                if (toggle.isSelected()) {
                    audio.setVisibility(View.VISIBLE);
                    PLText.setVisibility(View.GONE);
                } else {
                    audio.setVisibility(View.GONE);
                    PLText.setVisibility(View.VISIBLE);
                }
            }
        });
        audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (ibdRcognize == null) {
                            ibdRcognize = new IBDRcognizeImpl(ActivityDetail.this);
                            ibdRcognize.attachView(PLText, audio, toggle);
                        }
                        view.setSelected(true);
                        audio.setText("松开完成识别");
                        ibdRcognize.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setSelected(false);
                        audio.setText("按住 说话");
                        ibdRcognize.stop();
                        break;
                }
                return true;
            }
        });
        overlay = findViewById(R.id.frame);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                if(shouldShowQuickChannel){
                    quickChannel.setVisibility(View.VISIBLE);
                }
                isPLing = false;
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        LinearLayout pinglun = findViewById(R.id.pinglun);
        pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                if(shouldShowQuickChannel){
                    quickChannel.setVisibility(View.GONE);
                }
                PLText.requestFocus();
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                PLText.requestFocus();

            }
        });
        LinearLayout fenxiangb = findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, ActivityDetail.this);
                }
            }
        });

        ((TextView) findViewById(R.id.p1)).setText(mApplication.ST("最新评论"));
        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));
        findViewById(R.id.title_back).setOnClickListener(this);
        dianzanImg = findViewById(R.id.activity_detail_dianzan_img);
        dianzanText = findViewById(R.id.activity_detail_dianzan_text);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        int screeWidth = getResources().getDisplayMetrics().widthPixels;
        int dp10       = DimenUtils.dip2px(this, 10);
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        int dp180 = DimenUtils.dip2px(this, 180);
        int dp7   = DimenUtils.dip2px(this, 7);
        //图片地址数组
        imageList = new ArrayList<>();

        //活动Id
        Id = getIntent().getStringExtra("id");
        //返回按钮
        ImageView back = findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        //页面标题
        ((TextView) findViewById(R.id.title_title)).setText(mApplication.ST("活动详情"));

        //分享按钮
        rightImg = findViewById(R.id.shareRight);
//        rightImg.setOnClickListener(this);
        //标题，发布时间
        title = findViewById(R.id.activity_detail_title);
        fabuTime = findViewById(R.id.activity_detail_time);
        //轮播
//        viewPager = (ViewPager) findViewById(R.id.activity_detail_viewPager);
        banner = findViewById(R.id.banner);

        banner.setDelayTime(3000);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setBackgroundColor(Color.parseColor("#e6e6e6"));
                Glide.with(ActivityDetail.this)
                        .load(path).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(ActivityDetail.this, 200))
                        .into(imageView);
            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (imageList != null) {
                    ScaleImageUtil.openBigIagmeMode(ActivityDetail.this, imageList, position, true);
                    LogUtil.e("大图浏览:::" + imageList);
                }
            }
        });

        //发起单位，活动地点，活动时间，已报名人数
        faqidanwei = findViewById(R.id.activity_detail_faxidanwei);
        huodongdidian = findViewById(R.id.activity_detail_huodongdidian);
        huodongTime = findViewById(R.id.activity_detail_huodongshijian);
        peopleNum = findViewById(R.id.activity_detail_peopleNum);
        //报名入口
        Tobaoming = findViewById(R.id.activity_detail_baoming);
        Tobaoming.setText(mApplication.ST("立即报名"));
//        Tobaoming.setOnClickListener(this);
        //点赞
        dianzan = findViewById(R.id.activity_detail_dianzan);
//        dianzan.setOnClickListener(this);
        //活动详情
        content = findViewById(R.id.activity_detail_info);
        //评论列表
        PlListVIew = findViewById(R.id.activity_detail_listview);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLData();
            }
        });
        //评论框底部

        FaSong = findViewById(R.id.activity_detail_fasong);
        FaSong.setText(mApplication.ST("发送"));
        shoucang = findViewById(R.id.title_image2);
        shoucang.setVisibility(View.VISIBLE);
        shoucang.setImageResource(R.drawable.shoucang_selector);


        if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {
            findViewById(R.id.qr_saoyisao).setVisibility(View.VISIBLE);
            new PointMoveHelper(this, findViewById(R.id.qr_saoyisao))
                    .setMargins(50, 10, 10, 55);
            findViewById(R.id.qr_saoyisao).setOnClickListener(this);
        } else {
            findViewById(R.id.qr_saoyisao).setVisibility(View.GONE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        LogUtil.e("报名返回：：：" + requestCode + "       " + resultCode);
        if (requestCode == 666 && resultCode == 66) {//第一次完善资料返回报名
            BaoMing();
        }
    }

    /**
     * 退出页面的设置
     */
    @Override
    protected void onDestroy() {
        if (thread != null) {
            if (thread.isAlive()) thread.interrupt();
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
        UMShareAPI.get(this).release();
        EventBus.getDefault().unregister(this);
//        ShareManager.release();
    }

    /**
     * 获取评论数据
     */
    private void getPLData() {
        new Thread(new Runnable() {
            ArrayList<HashMap<String, String>> Pllist;

            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("act_id", Id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.Activity_pinglun_IP)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run:      PLdata------>" + data);
                        Pllist = AnalyticalJSON.getList(data, "comment");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if ((Pllist != null)) {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(ActivityDetail.this);
                                        tv.setText(mApplication.ST("还没有评论嘞"));
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (Pllist.size() < 20) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;
                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (Pllist.size() < 20) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    }
                                } else {
                                    PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                    PlListVIew.footer.setEnabled(false);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 加载详情页数据（不包括评论）
     */
    private void LoadData() {

        if (!Network.HttpTest(this)) {
            tip.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.load_neterror));
            tip.setVisibility(View.VISIBLE);
            return;
        }
        if (Id == null || Id.equals("")) {
            return;
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("act_id", Id);
                        js.put("m_id", Constants.M_id);
                        if (!PreferenceUtil.getUserId(ActivityDetail.this).equals("")) {
                            js.put("user_id", PreferenceUtil.getUserId(ActivityDetail.this));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkGo.post(Constants.Activity_detail_IP).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    if (!TextUtils.isEmpty(data) && !"null".equals(data)) {
                        Log.e(TAG, "run: " + data + "   id-=-=>" + Id);
                        final HashMap<String, String> totalMap = AnalyticalJSON.getHashMap(data);
                        if (totalMap != null) {
                            map = AnalyticalJSON.getHashMap(totalMap.get("activity"));

                            if (map != null) {//加载数据成功

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tip.setVisibility(View.GONE);

                                        isNeedToChooseTime = map.get("term").equals("2") ? true : false;
                                        if (TimeUtils.dataOne(map.get("act_time")) < System.currentTimeMillis()) {
                                            yue.setVisibility(View.GONE);
                                        }

                                        if (map.get("prol") != null && !map.get("prol").equals("")) {
                                            act_prol = map.get("prol");
                                            LogUtil.e("活动协议：：" + act_prol);
                                        } else {
                                            LogUtil.e("没有活动协议");
                                        }


                                        dianzan.setOnClickListener(ActivityDetail.this);
                                        Tobaoming.setOnClickListener(ActivityDetail.this);
                                        rightImg.setOnClickListener(ActivityDetail.this);
                                        FaSong.setOnClickListener(ActivityDetail.this);
                                        shoucang.setOnClickListener(ActivityDetail.this);


                                        title.setText(mApplication.ST(map.get("title")));//标题
                                        fabuTime.setText(mApplication.ST("发布时间: " + TimeUtils.getTrueTimeStr(map.get("time"))));//发布时间
                                        setUrlToImage(map);//保存图片地址并加载,显示小圆点
                                        faqidanwei.append(mApplication.ST("发起单位:" + map.get("author")));//发布单位
                                        huodongdidian.append(mApplication.ST("活动地点:" + map.get("address")));//活动地点
//                                        String acttime = map.get("act_time");
                                        String endTime   = map.get("end_time");
                                        String startTime = map.get("start_time");
//                                        if ((TimeUtils.dataOne(acttime) - System.currentTimeMillis()) <= 0) {
//                                            huodongTime.append(mApplication.ST("活动时间:活动已开始"));
//                                        } else {
                                        huodongTime.append(mApplication.ST("报名开始时间:" + startTime + "\n报名结束时间:" + endTime
                                        ));//活动时间
//                                        }
                                        if ((TimeUtils.dataOne(endTime) - System.currentTimeMillis()) <= 0) {
                                            Tobaoming.setText(mApplication.ST("已结束"));
                                            Tobaoming.setEnabled(false);
                                            if (getIntent().getStringExtra("wel_id") != null) {
                                                ToastUtil.showToastShort("该活动已结束");
                                            }
                                        } else {
                                            if (!PreferenceUtil.getUserId(ActivityDetail.this).equals("")) {
                                                if ("000".equals(totalMap.get("code"))) {
                                                    //已报名，但是正在审核，显示快速通道
                                                    LogUtil.e("已报名，但是正在审核，显示快速通道");
                                                    quickChannel.setVisibility(View.VISIBLE);
                                                    shouldShowQuickChannel=true;
                                                    Tobaoming.setText(mApplication.ST("已报名"));
                                                    Tobaoming.setEnabled(false);
                                                    if (getIntent().getStringExtra("wel_id") != null) {
                                                        //绑定福利券
                                                        BaoMing();
                                                    }
                                                } else {
                                                    if ("002".equals(totalMap.get("code"))) {
                                                        //已报名并且审核通过
                                                        LogUtil.e("已报名并且审核通过");
                                                        Tobaoming.setText(mApplication.ST("已通过"));
                                                        Tobaoming.setEnabled(false);
                                                        quickChannel.setVisibility(View.GONE);
                                                        shouldShowQuickChannel=false;
                                                    } else if ("005".equals(totalMap.get("code"))) {
                                                        //未报名的情况,暂时先显示快速通道
                                                        shouldShowQuickChannel=true;
                                                        quickChannel.setVisibility(View.VISIBLE);
                                                        // TODO: 2018/4/23 使用券进入详情页  直接模拟点击报名
                                                        if (getIntent().getStringExtra("wel_id") != null) {
                                                            Tobaoming.performClick();
                                                        }
                                                    }

                                                }
                                            }

                                        }
                                        LogUtil.e("code::::" + totalMap.get("code"));
                                        if ("2".equals(map.get("actquick"))) {
                                            //该活动拥有显示快速通道的权利，最后一次确定是否显示
                                            //如果有权利显示，则根据前面判断确定
                                            shouldShowQuickChannel=true;
                                        } else {
//                                        如果没有权利显示，则直接隐藏
                                            shouldShowQuickChannel=false;
                                            quickChannel.setVisibility(View.GONE);
                                        }
                                        peopleNum.append(mApplication.ST("已报名人数:" + map.get("enrollment")));//已报名人数
                                        dianzanText.setText(map.get("likes"));
                                        ((TextView) findViewById(R.id.p1)).setText(mApplication.ST("评论 " + map.get("act_comment")));
                                        firstNum = Integer.valueOf(map.get("act_comment"));
                                        content.setText(mApplication.ST(map.get("abstract")));//简介
                                        findViewById(R.id.scroll).setVisibility(View.VISIBLE);
                                        title.setFocusable(true);
                                        title.setFocusableInTouchMode(true);
                                        title.requestFocus();
                                        String md5 = MD5Utls.stringToMD5(Constants.safeKey);
                                        String m1  = md5.substring(0, 16);
                                        String m2  = md5.substring(16, md5.length());
                                        umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + m1 + Id + m2 + "/st/" + (mApplication.isChina ? "s" : "t"));
                                        LogUtil.e("链接：：：：" + Constants.FX_host_Ip + TAG + "/id/" + m1 + Id + m2);
                                        umWeb.setTitle(mApplication.ST(map.get("title")));
                                        umWeb.setDescription(mApplication.ST(map.get("abstract")));
                                        umWeb.setThumb(new UMImage(ActivityDetail.this, map.get("image1")));

                                        getPLData();
                                        ProgressUtil.dismiss();
                                    }
                                });
                            } else {
                                tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                                tip.setVisibility(View.VISIBLE);
                                ProgressUtil.dismiss();
                            }
                        } else {//加载失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                                    tip.setVisibility(View.VISIBLE);
                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                                tip.setVisibility(View.VISIBLE);
                                ProgressUtil.dismiss();
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tip.setImageBitmap(ImageUtil.readBitMap(mApplication.getInstance(), R.drawable.load_nothing));
                                    tip.setVisibility(View.VISIBLE);
                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    });
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressUtil.dismiss();
                        }
                    });
                }
            }
        });
        thread.start();
    }


    /**
     * 添加评论
     *
     * @param v 发送按钮
     */
    private void addPL(final View v) {
        if (PLText.getText().toString().trim().equals("")) {
            Toast.makeText(this, mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (sp.getString("pet_name", "").trim().equals("")) {
            Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Mine_gerenziliao.class);
            startActivity(intent);
            return;
        }
        v.setEnabled(false);
        ProgressUtil.show(this, "", mApplication.ST("正在提交"));
        if (!isPLing) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String content = PLText.getText().toString();
                        JSONObject   js      = new JSONObject();
                        try {
                            js.put("user_id", sp.getString("user_id", ""));
                            js.put("ct_contents", content);
                            js.put("act_id", Id);
                            js.put("m_id", Constants.M_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String data = OkGo.post(Constants.Activity_pinglun_add_IP)
                                .params("key", ApisSeUtil.getKey())
                                .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                        if (!data.equals("")) {
                            Log.i(TAG, "run:      data------>" + data);
                            final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                            if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!"0".equals(hashMap.get("yundousum"))) {
                                            YunDouAwardDialog.show(ActivityDetail.this, "每日评论", hashMap.get("yundousum"));
                                        }
                                        ToastUtil.showToastShort(mApplication.ST(getString(R.string.commitCommentSuccess)));
                                        v.setEnabled(true);
                                        PLText.setText("");
                                        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                        ((TextView) findViewById(R.id.p1)).setText(mApplication.ST("评论 " + (firstNum + 1)));
                                        overlay.setVisibility(View.GONE);
                                        ProgressUtil.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ActivityDetail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                        v.setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityDetail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                    v.setEnabled(true);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String content = PLText.getText().toString();
                        JSONObject   js      = new JSONObject();
                        try {
                            js.put("user_id", sp.getString("user_id", ""));
                            js.put("ct_contents", content);
                            js.put("ct_id", currentId);
                            js.put("m_id", Constants.M_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String data = OkGo.post(Constants.little_zixun_pl_add_IP)
                                .params("key", ApisSeUtil.getKey())
                                .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                        if (!data.equals("")) {
                            final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                            if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!"0".equals(hashMap.get("yundousum"))) {
                                            YunDouAwardDialog.show(ActivityDetail.this, "每日评论", hashMap.get("yundousum"));
                                        }
                                        ToastUtil.showToastShort(mApplication.ST(getString(R.string.commitCommentSuccess)));

                                        PLText.setText("");
                                        PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                        overlay.setVisibility(View.GONE);
                                        PlListVIew.setSelection(currentPosition);
                                        FaSong.setEnabled(true);
                                        isPLing = false;

                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(true);
                                    Toast.makeText(ActivityDetail.this, mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 加载轮播图片并加载小圆点
     *
     * @param map
     */
    private void setUrlToImage(final HashMap<String, String> map) {
        String image1 = map.get("image1");
        String image2 = map.get("image2");
        String image3 = map.get("image3");
        if (!TextUtils.isEmpty(image1)) {
            imageList.add(image1);
        }
        if (!TextUtils.isEmpty(image2)) {
            imageList.add(image2);
        }
        if (!TextUtils.isEmpty(image3)) {
            imageList.add(image3);
        }
        banner.setImages(imageList);
        banner.start();
    }


    private void BaoMing() {

        if (sp.getString("perfect", "1").equals("1") && !act_prol.equals("")) {
            Intent intent = new Intent(ActivityDetail.this, IdCardCheck.class);
            startActivityForResult(intent, 666);
            Toast.makeText(ActivityDetail.this, mApplication.ST("您还未完善资料，快完善资料吧"), Toast.LENGTH_SHORT).show();
        } else {
            if (hasShowedQuick) {
                //快速报名通道
                getInfos();
            } else {
                //普通报名通道
                JSONObject js = new JSONObject();
                try {
                    js.put("m_id", Constants.M_id);
                    js.put("act_id", Id);
                    js.put("user_id", sp.getString("user_id", ""));
                    if (getIntent().getStringExtra("wel_id") != null) {
                        js.put("wel", getIntent().getStringExtra("wel_id"));
                    }
                    if (isNeedToChooseTime && !startTime.equals("") && !endTime.equals("")) {
                        js.put("start_time", startTime);
                        js.put("end_time", endTime);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("提交报名信息：：；" + js);
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkGo.post(Constants.Activity_BaoMing).tag(TAG)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new AbsCallback<HashMap<String, String>>() {
                            @Override
                            public HashMap<String, String> convertSuccess(Response response) throws Exception {
                                return AnalyticalJSON.getHashMap(response.body().string());
                            }


                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(ActivityDetail.this, "", mApplication.ST("正在报名，请稍等"));
                            }

                            @Override
                            public void onSuccess(final HashMap<String, String> map, Call call, Response response) {
                                if (map != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            View view = LayoutInflater.from(ActivityDetail.this).inflate(R.layout.baoming_alert, null);
                                            AlertDialog.Builder b = new AlertDialog.Builder(ActivityDetail.this).
                                                    setView(view);
                                            final AlertDialog d = b.create();
                                            d.getWindow().setDimAmount(0.2f);
                                            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (sp.getString("perfect", "1").equals("1") && !act_prol.equals("")) {
                                                        Intent intent = new Intent(ActivityDetail.this, user_Info_First.class);
                                                        startActivity(intent);
                                                        Toast.makeText(ActivityDetail.this, mApplication.ST("您还未完善资料，快去完善资料吧"), Toast.LENGTH_SHORT).show();
                                                    }
                                                    d.dismiss();
                                                }
                                            });
                                            if ("000".equals(map.get("code"))) {
                                                ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已成功报名"));
                                                view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                                ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动：\n[我的]->[活动]"));
                                                Tobaoming.setText(mApplication.ST("已报名"));
                                                Tobaoming.setEnabled(false);
                                                d.show();
                                                if (getIntent().getStringExtra("wel_id") != null) {
                                                    d.dismiss();
                                                    ToastUtil.showToastShort("该福利券已使用成功");
                                                    setResult(666);
                                                    finish();
                                                }

                                            } else if ("003".equals(map.get("code"))) {
                                                if (getIntent().getStringExtra("wel_id") != null && map.get("type") != null) {//绑定兑换券
                                                    if (map.get("type").equals("000")) {
                                                        ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("福利券使用成功"));
                                                    } else if (map.get("type").equals("001")) {
                                                        ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("福利券使用失败"));
                                                    }
                                                    ((TextView) view.findViewById(R.id.phone)).setText("");
                                                } else {
                                                    ((TextView) view.findViewById(R.id.phone)).setText(mApplication.ST("审核结果请及时关注App我的活动：\n[我的]->[活动]"));
                                                    ((TextView) view.findViewById(R.id.result_msg)).setText(mApplication.ST("您已经报名过了哟~"));
                                                }
                                                view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                                Tobaoming.setText(mApplication.ST("已报名"));
                                                Tobaoming.setEnabled(false);

                                            } else if ("005".equals(map.get("code"))) {
                                                ToastUtil.showToastShort("使用兑换券失败，兑换券已使用或已转赠");
                                                setResult(666);
                                                finish();
                                            }
                                            ProgressUtil.dismiss();


                                        }
                                    });

                                }
                            }

                            @Override
                            public void onAfter(HashMap<String, String> map, Exception e) {
                                super.onAfter(map, e);
                                ProgressUtil.dismiss();
                            }


                        });
            }

        }
    }

    /**
     * 获取快速通道的信息
     */
    private void getInfos() {
        if (!Network.HttpTest(this)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("act_id", Id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            if (isNeedToChooseTime) {
                js.put("start_time", startTime);
                js.put("end_time", endTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("获取快速通道权限并显示快速通道协议：：" + js);
        OkGo.post(Constants.CheckQuickChannelPermission)
                .tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            boolean havePermisson = false;
                            if ("000".equals(map.get("code"))) {
                                //有直录通道权限
                                havePermisson = true;
                            } else if ("002".equals(map.get("code"))) {
                                //没有直录通道权限
                                havePermisson = false;
                            }
                            //显示直录通道协议
                            View            view    = LayoutInflater.from(ActivityDetail.this).inflate(R.layout.activity_confirm_dialog, null);
                            final WebView   web     = view.findViewById(R.id.web);
                            TextView        cancle  = view.findViewById(R.id.cancle);
                            final RTextView baoming = view.findViewById(R.id.baoming);
                            //判断按钮状态
                            if (havePermisson) {
                                cancle.setText("取消");
                                baoming.setText("确定报名");
                                baoming.setTextColorNormal(Color.WHITE);
                                baoming.setBackgroundColorNormal(ContextCompat.getColor(ActivityDetail.this, R.color.main_color));
                            } else {
                                cancle.setVisibility(View.GONE);
                                baoming.setText("暂无权限");
                                baoming.setTextColorNormal(Color.LTGRAY);
                                baoming.setBackgroundColorNormal(Color.GRAY);
                            }

                            web.loadDataWithBaseURL("", map.get("contents")
                                    , "text/html", "UTF-8", null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    web.destroy();
                                    dialog.dismiss();
                                }
                            });
                            baoming.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    postQuickChannel();

                                }
                            });
                            builder.setCancelable(false);
                            web.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    dialog.show();
                                    web.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(ActivityDetail.this, "", "请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private void postQuickChannel() {
        if (!Network.HttpTest(this)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("act_id", Id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            if (isNeedToChooseTime) {
                js.put("start_time", startTime);
                js.put("end_time", endTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("快速通道报名：：" + js);
        OkGo.post(Constants.ActivityQuick)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String > map=AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if ("000".equals(map.get("code"))) {
                                //有权限
                                LoadData();

                            }else if("003".equals(map.get("code"))){
                                ToastUtil.showToastShort("您还没有直录通道的权限");
                            }
                        }else{
                            ToastUtil.showToastShort("报名失败，请稍后尝试");
                        }

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(ActivityDetail.this,"","正在报名");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    //支付提示窗口
    private void showPayCommitDialog(final HashMap<String, String> map) {
        View           view  = LayoutInflater.from(this).inflate(R.layout.dialog_title_msg_edt_cancel_commit, null);
        TextView       title = view.findViewById(R.id.title);
        TextView       msg   = view.findViewById(R.id.msg);
        final EditText edit  = view.findViewById(R.id.edit);
        edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView commit = view.findViewById(R.id.commit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        title.setText(mApplication.ST("支付提示"));
        msg.setText(mApplication.ST("支付金额：" + map.get("money") + "起"));
        cancel.setText(mApplication.ST("取消"));
        commit.setText(mApplication.ST("支付"));
        edit.setHint("支付金额：" + map.get("money") + "起");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editable = edit.getText().toString().trim();
                if (editable.equals("")) {
                    ToastUtil.showToastShort("请根据提示输入金额");
                    return;
                }
                if (!CheckNumUtil.checkNum(editable)) {
                    ToastUtil.showToastShort("请根据提示输入正确的金额");
                    return;
                }
                if (editable.contains(".") && (editable.substring(editable.lastIndexOf(".")).length() - 1) > 2) {
                    LogUtil.e("小数点后位数：：：" + (editable.substring(editable.lastIndexOf(".")).length() - 1));
                    ToastUtil.showToastShort("金额只支持小数点后两位，请重新输入");
                    return;
                }
                double money = Double.valueOf(map.get("money"));
                double input = Double.valueOf(editable);
                if (money > input) {
                    ToastUtil.showToastShort("支付金额：" + map.get("money") + "起");
                    return;
                }
                dialog.dismiss();
                BasePayParams basePayParams = new BasePayParams();
                JSONObject    jsonInfo      = new JSONObject();
                if (isNeedToChooseTime) {
                    try {
                        jsonInfo.put("act_type", map.get("id"));
                        jsonInfo.put("start_time", startTime);
                        jsonInfo.put("end_time", endTime);
                        basePayParams.jsonInfo = jsonInfo.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                basePayParams.allMoney = editable;
                basePayParams.num = "1";
                basePayParams.payType = "14";
                basePayParams.payId = Id;//活动id
                basePayParams.title = ActivityDetail.this.map.get("title");
                mApplication.openPayLayout(ActivityDetail.this, basePayParams);

            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        dialog.show();

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.quickChannel:
                if (quickInfoMap == null) quickInfoMap = new ArrayList<>();
                hasShowedQuick = true;//确定点击快速通道
                prepareBaoMing();
                break;
            case R.id.qr_saoyisao:
                if (Build.VERSION.SDK_INT >= 23) {
                    int ca = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (ca != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 000);
                        return;
                    }
                }
                Intent intent1 = new Intent(this, QRActivity.class);
                intent1.putExtra("id", Id);
                startActivity(intent1);
                break;
            case R.id.activity_detail_yue:
                if (new LoginUtil().checkLogin(this)) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserId(this));
                        js.put("act_id", Id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    LogUtil.e("判断用户是否报名审核成功" + js);
                    OkGo.post(Constants.IsEnrolled).params("key", m.K())
                            .params("msg", m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            LogUtil.e("已通过审核");
                                            Intent intent = new Intent(ActivityDetail.this, Activity_YaoYue.class);
                                            intent.putExtra("id", Id);
                                            startActivity(intent);
                                        } else if ("001".equals(map.get("code"))) {
                                            ToastUtil.showToastShort("您的报名还没通过审核，请留意活动中的审核结果");
                                        } else if ("002".equals(map.get("code"))) {
                                            ToastUtil.showToastShort("您还没有报名该活动，快去报名参加吧");
                                        }
                                    }
                                }
                            });
                }

                break;
            case R.id.tip:
                LoadData();
                break;
            case R.id.activity_detail_fasong:
                addPL(v);//添加评论
                break;
            case R.id.activity_detail_dianzan://点赞
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(ActivityDetail.this, mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("m_id", Constants.M_id);
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("act_id", Id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String data = OkGo.post(Constants.Activity_DZ_IP).params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js))
                                        .execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!"0".equals(map.get("yundousum"))) {
                                                    YunDouAwardDialog.show(ActivityDetail.this, "每日点赞", map.get("yundousum"));
                                                }
                                                dianzanText.setText((Integer.valueOf(dianzanText.getText().toString()) + 1) + "");
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                                Toast.makeText(ActivityDetail.this, mApplication.ST("已点过赞啦"), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                break;
            case R.id.activity_detail_baoming://报名页面
                hasShowedQuick = false;//未点击快速通道
                prepareBaoMing();


                break;
            case R.id.title_back://返回
                finish();
                break;
            case R.id.shareRight://分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, ActivityDetail.this);
                }
                break;
            case R.id.title_image2:
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(ActivityDetail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                CollectManager.doCollect(ActivityDetail.this, Id, "2", shoucang);

                break;
        }
    }

    private void prepareBaoMing() {
        if (new LoginUtil().checkLogin(ActivityDetail.this)) {
            if (act_prol != null && !act_prol.equals("") && !hasShowActivityProtocol) {
                View          view   = LayoutInflater.from(this).inflate(R.layout.activity_confirm_dialog, null);
                final WebView web    = view.findViewById(R.id.web);
                TextView      cancle = view.findViewById(R.id.cancle);
                cancle.setText(mApplication.ST("不同意"));
                final TextView baoming = view.findViewById(R.id.baoming);
                baoming.setEnabled(false);

                final CountDownTimer cdt = new CountDownTimer(BuildConfig.DEBUG ? 1000 : 10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        baoming.setText(mApplication.ST("请阅读报名须知(" + millisUntilFinished / 1000 + "秒)"));
                    }

                    @Override
                    public void onFinish() {
                        baoming.setText(mApplication.ST("同意"));
                        baoming.setEnabled(true);
                    }
                };
                web.loadDataWithBaseURL("", act_prol
                        , "text/html", "UTF-8", null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        web.destroy();
                        cdt.cancel();
                        hasShowActivityProtocol = false;
                        dialog.dismiss();
                    }
                });
                baoming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        hasShowActivityProtocol = true;
                        showBaoMingDialog();

                    }
                });
                cdt.start();
                builder.setCancelable(false);
                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        dialog.show();
                        web.setVisibility(View.VISIBLE);

                    }


                });


            } else {
                showBaoMingDialog();
            }
        }
    }

    private void showBaoMingDialog() {
        if (isNeedToChooseTime) {//选择活动参与时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            Calendar end = Calendar.getInstance();
            end.set(calendar.get(Calendar.YEAR) + 100, 11, 31);
            TimePickerView timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    LogUtil.e("开始时间：：" + TimeUtils.getYMDTime(date, true));
                    startTime = TimeUtils.getYMDTime(date, true);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 29);
                    Calendar end = Calendar.getInstance();
                    end.set(calendar.get(Calendar.YEAR) + 100, 11, 31);
                    TimePickerView timePickerView = new TimePickerView.Builder(ActivityDetail.this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date endDate, View v) {
                            LogUtil.e("结束时间：：" + TimeUtils.getYMDTime(endDate, false));
                            endTime = TimeUtils.getYMDTime(endDate, false);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityDetail.this);
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setTitle(mApplication.ST("确定报名参加" + title.getText().toString() + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    BaoMing();
                                }


                            }).create().show();
                        }
                    })
                            .setSubmitText("确定")
                            .setCancelText("取消")
                            .setCancelColor(Color.GRAY)
                            .setSubmitColor(Color.BLACK)
                            .setTitleColor(ContextCompat.getColor(ActivityDetail.this, R.color.main_color))
                            .setTitleBgColor(Color.WHITE)
                            .setTitleSize(18)
                            .setTitleText("请选择结束时间")
                            .setContentSize(22)
                            .isCyclic(true)
                            .isCenterLabel(true)
                            .setDate(calendar)
                            .setType(new boolean[]{true, true, true, false, false, false})
                            .setLineSpacingMultiplier(1.5f)
                            .build();
                    timePickerView.show();
                }
            })
                    .setSubmitText("确定")
                    .setCancelText("取消")
                    .setCancelColor(Color.GRAY)
                    .setSubmitColor(Color.BLACK)
                    .setTitleColor(Color.BLACK)
                    .setTitleBgColor(Color.WHITE)
                    .setTitleSize(18)
                    .setTitleText("请选择开始时间")
                    .setContentSize(22)
                    .isCyclic(true)
                    .isCenterLabel(true)
                    .setRangDate(calendar, end)
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .setLineSpacingMultiplier(1.5f)
                    .build();
            timePickerView.show();

        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityDetail.this);
            builder1.setCancelable(true);
            builder1.setPositiveButton(mApplication.ST("取消"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setTitle(mApplication.ST("确定报名参加" + title.getText().toString() + "吗？")).setNegativeButton(mApplication.ST("确定"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BaoMing();
                }


            }).create().show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
        imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
    }

    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        if(shouldShowQuickChannel){
            quickChannel.setVisibility(View.GONE);
        }
        PLText.requestFocus();
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString(mApplication.ST("回复 ") + name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);

        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        PLText.requestFocus();
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        Id = AnalyticalJSON.getHashMap(map.get("msg")).get("id");
        LoadData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.qr_saoyisao).performClick();
        }
    }
}
