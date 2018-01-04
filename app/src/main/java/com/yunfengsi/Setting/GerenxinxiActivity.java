package com.yunfengsi.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.ZhiFuShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

public class GerenxinxiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvxinbie;     //性别
    private TextView mtvnc;        //昵称
    private TextView mtvphone;   //手机号
    //    private TextView mtvid;     //身份证号
//    private TextView mtvsimiao;  //所属寺庙
    private TextView mtvyonghuleixing;   //用户类型
    //    private TextView mtvtime;      //注册时间
    private AvatarImageView mcircleview; //RoundedImageView头像
    private SharedPreferences sp;
    private String xb;
    private HashMap<String, String> map,moreMap=null;
    private int screenWidth;
    private TextView tvPerfect;

    private TextView more,trueName, faName, shenfenzheng, address, workPlace, xiuxingjingli, job, morePhone, carId;


    public void init() {
        ((TextView) findViewById(R.id.titletv)).setText(mApplication.ST("个人信息"));
        ((TextView) findViewById(R.id.nametv)).setText(mApplication.ST("昵称"));
        ((TextView) findViewById(R.id.phonetv)).setText(mApplication.ST("手机号码"));
        ((TextView) findViewById(R.id.sextv)).setText(mApplication.ST("性别"));
        ((TextView) findViewById(R.id.signtv)).setText(mApplication.ST("个人签名"));
        ((TextView) findViewById(R.id.tnametv)).setText(mApplication.ST("真实姓名"));
        ((TextView) findViewById(R.id.fnametv)).setText(mApplication.ST("法名"));
        ((TextView) findViewById(R.id.midtv)).setText(mApplication.ST("身份证号码"));
        ((TextView) findViewById(R.id.addresstv)).setText(mApplication.ST("家庭住址"));
        ((TextView) findViewById(R.id.worktv)).setText(mApplication.ST("工作单位"));
        ((TextView) findViewById(R.id.lasttv)).setText(mApplication.ST("修行经历"));
        ((TextView) findViewById(R.id.jobtv)).setText(mApplication.ST("职业"));
        ((TextView) findViewById(R.id.morePhone)).setText(mApplication.ST("紧急联系人手机"));
        ((TextView) findViewById(R.id.cartv)).setText(mApplication.ST("车牌登记"));
        mtvxinbie = (TextView) findViewById(R.id.gerenxinxi_xingbie_tv);
        mtvnc = (TextView) findViewById(R.id.gerenxinxi_nichengz_tv);
        mtvphone = (TextView) findViewById(R.id.gerenxinxi_phone_tv);
//    mtvid=(TextView) findViewById(R.id.gerenxinxi_shenfenz_tv);
//    mtvsimiao=(TextView) findViewById(R.id.gerenxinxi_shimiao_tv);
//    mtvyonghuleixing=(TextView) findViewById(R.id.gerenxinxi_yonghuleixing_tv);
//    mtvtime=(TextView) findViewById(R.id.gerenxinxi_time_tv);
        mcircleview = (AvatarImageView) findViewById(R.id.gerenxinxi_touxiang_cicleimageview);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        tvPerfect = (TextView) findViewById(R.id.perfect);
        tvPerfect.setText(mApplication.ST("完善资料"));
        tvPerfect.setOnClickListener(this);
        more= (TextView) findViewById(R.id.geren_more);
        more.setText(mApplication.ST("更多资料"));
        if (sp.getString("perfect", "1").equals("1")) {
            tvPerfect.setText(mApplication.ST("完善资料"));
        } else {
            tvPerfect.setVisibility(View.GONE);
            more.append(mApplication.ST("\n[修改个人信息请联系:15397639879或106889@qq.com]"));
            getMore();
        }

        trueName = (TextView) findViewById(R.id.tv_trueName);
        faName = (TextView) findViewById(R.id.tv_faMing);
        shenfenzheng = (TextView) findViewById(R.id.tv_shenfenzheng);
        address = (TextView) findViewById(R.id.tv_address);
        workPlace = (TextView) findViewById(R.id.tv_workPlace);
        xiuxingjingli = (TextView) findViewById(R.id.tv_xiuxingjingli);
        job = (TextView) findViewById(R.id.tv_job);
        morePhone = (TextView) findViewById(R.id.tv_morePhone);
        carId = (TextView) findViewById(R.id.tv_carId);

        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        final RelativeLayout r= (RelativeLayout) findViewById(R.id.back_bg);
        Glide.with(this).load(R.drawable.mine_banner)
                .asBitmap().override(screenWidth, DimenUtils.dip2px(this,150))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        r.setBackgroundDrawable(new BitmapDrawable(resource));
                    }
                });
    }
    private View.OnClickListener changeClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder=new AlertDialog.Builder(GerenxinxiActivity.this);


            String hint="";
            switch (view.getId()){
                case R.id.jiatingzhuzhi://家庭住址
                    hint="请输入您的家庭住址";
                    break;
                case R.id.faming://法名
                    hint="请输入您的法名";
                    break;
                case R.id.gongzuodanwei://工作单位
                    hint="请输入您的工作单位";
                    break;
                case R.id.xiuxingjingli://修行经历
                    hint="请输入您的修行经历";
                    break;
                case R.id.zhiye://职业
                    hint="请输入您的的职业";
                    break;
                case R.id.jinjilianxirenshouji://紧急联系人手机号码
                    hint="请输入您的紧急联系人手机号码";
                    break;
                case R.id.chepaidengji://车牌登记
                    hint="请输入您的车牌";
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenxinxi);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        init();
        getdata();  //数据交互方法




    }

    /*
    获取更多资料
     */
    private void getMore() {
        JSONObject js=new JSONObject();
        try {
            js.put("user_id",sp.getString("user_id",""));
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(Constants.getMoreInfo).params("key", ApisSeUtil.getKey())
                .params("msg",ApisSeUtil.getMsg(js)).execute(new AbsCallback<Object>() {
            @Override
            public Object convertSuccess(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                if(response!=null){
                    try {
                        String data=response.body().string();
                        if(!"".equals(data)){
                            moreMap=AnalyticalJSON.getHashMap(data);
                            if(moreMap!=null){
                                findViewById(R.id.moreInfo).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText("".equals(moreMap.get("signature"))?"未填写":moreMap.get("signature"));
                                trueName.setText("".equals(moreMap.get("name"))?"未填写":moreMap.get("name"));
                                faName.setText(mApplication.ST(moreMap.get("farmington")));
                                shenfenzheng.setText(moreMap.get("cid"));
                                address.setText(mApplication.ST(moreMap.get("address")));
                                workPlace.setText(mApplication.ST(moreMap.get("workunit")));
                                xiuxingjingli.setText(mApplication.ST(moreMap.get("practice")));
                                job.setText(mApplication.ST(moreMap.get("work")));
                                morePhone.setText(mApplication.ST(moreMap.get("contact")));
                                carId.setText(moreMap.get("plate"));
//                                Glide.with(GerenxinxiActivity.this).load(map.get("cidimage")).thumbnail(0.1f)
//                                        .into(moreImg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.gerenxinxi_back:
                finish();
                break;
            case R.id.perfect:
                if (sp.getString("perfect", "1").equals("1")) {
                    Intent intent =new Intent(this,ZhiFuShare.class);
                    intent.putExtra(ZhiFuShare.ISFORM,true);
                    startActivityForResult(intent,666);
//                    finish();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("resultCode:::"+resultCode);
        if(resultCode==66){//完善资料成功 刷新界面
            tvPerfect.setVisibility(View.GONE);
            more.append(mApplication.ST("\n[修改个人信息请联系:15397639879或106889@qq.com]"));
            getMore();
        }
    }

    private static final String TAG = "GerenxinxiActivity";

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data1 = OkGo.post(Constants.User_Info_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).execute()
                            .body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data1 != null && !data1.equals("")) {

                    map = AnalyticalJSON.getHashMap(data1);
                    if (map != null && map.get("code") == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtvnc.setText("".equals(map.get("pet_name"))?"未填写":map.get("pet_name"));
//                                mtvtime.setText(map.get("user_time"));
//                                mtvsimiao.setText(map.get("user_temple"));
//                                mtvid.setText(map.get("user_cid"));
                                mtvphone.setText(map.get("phone").equals("") ? mApplication.ST("暂未绑定手机号") : map.get("phone"));
                                Glide.with(GerenxinxiActivity.this).load(map.get("user_image")).into(mcircleview);
                                if (map.get("sex").equals("1")) {
                                    mtvxinbie.setText(mApplication.ST("男"));
                                } else if (map.get("sex").equals("2")) {
                                    mtvxinbie.setText(mApplication.ST("女"));
                                }else {
                                    mtvxinbie.setText(mApplication.ST("未填写"));
                                }
                                ((TextView) findViewById(R.id.gerenxinxi_sign_tv)).setText("".equals(map.get("signature"))?"未填写":map.get("signature"));

                            }
                        });
                    } else if (map != null && map.get("code") != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GerenxinxiActivity.this, mApplication.ST("用户信息获取失败"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

            }
        }).start();
    }


}

