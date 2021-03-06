package com.yunfengsi.Managers.ForManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

/**
 * 作者：luZheng on 2018/06/06 17:42
 */
public class ForManagers extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.for_manager);
        findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title_title)).setText("管理员中心");
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.comment_manage).setOnClickListener(this);
        findViewById(R.id.wish_manage).setOnClickListener(this);
        findViewById(R.id.auciton_manage).setOnClickListener(this);
        findViewById(R.id.wallpaper_manage).setOnClickListener(this);
        findViewById(R.id.user_manage).setOnClickListener(this);
        findViewById(R.id.suggest_manage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comment_manage:
                startActivity(new Intent(this,CommentManage.class));
                break;
            case R.id.wish_manage:
                startActivity(new Intent(this,WishManage.class));
                break;
            case R.id.auciton_manage:
                startActivity(new Intent(this,AuctionManage.class));
                break;
            case R.id.wallpaper_manage:
                startActivity(new Intent(this,WallPaperManage.class));
                break;
            case R.id.user_manage:
                startActivity(new Intent(this,UserManage.class));
                break;
            case R.id.suggest_manage:
                startActivity(new Intent(this,SuggestManage.class));
                break;
        }
    }
}
