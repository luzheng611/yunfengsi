package com.yunfengsi.Models.Auction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yunfengsi.R;
import com.yunfengsi.Utils.StatusBarCompat;

/**
 * 作者：luZheng on 2018/06/08 17:11
 */
public class AuctionDetail extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.auction_detail);
    }
}