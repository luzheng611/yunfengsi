package com.yunfengsi.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunfengsi.Managers.MineManager;
import com.yunfengsi.R;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.mApplication;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class Mine_GridAdapter extends BaseItemDraggableAdapter<HashMap<String, Object>,BaseViewHolder> {
    public Mine_GridAdapter(List<HashMap<String, Object>> data) {
        super(R.layout.item_mine, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, HashMap<String, Object> map) {

        holder.setText(R.id.text, mApplication.ST(map.get(MineManager.text).toString()));
        Glide.with(mApplication.getInstance()).load(map.get(MineManager.img))
                .override(DimenUtils.dip2px(mApplication.getInstance(), 40), DimenUtils.dip2px(mApplication.getInstance(), 40))
                .fitCenter().into((ImageView) holder.getView(R.id.image));

    }


}
