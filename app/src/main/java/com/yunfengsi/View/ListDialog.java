package com.yunfengsi.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.AnimRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunfengsi.Utils.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：luZheng on 2018/06/20 15:13
 */
public class ListDialog {
    public static final int DEFAULT   = 0;
    public static final int WITH_LIST = 1;
    private WeakReference<Activity> weakReference;
    private Activity                activity;
    private List                    list;

    private HandleListCallBack handleListCallBack;
    private DialogCallBack     callBack;
    private View               view;
    private View               itemView;
    private RecyclerView       recyclerView;
    @LayoutRes
    private int     mRes       = -1;
    @LayoutRes
    private int     mitemRes   = -1;
    @AnimRes
    private int     mAnimRes   = -1;
    private boolean hasList    = false;
    private String  defaultKey = "";

    /**
     * 如果显示的调用过软键盘的化 edt则有值
     */
    private EditText                   edt;
    private InputMethodManager         imm;
    private HashMap<String, ImageView> imageViewHashMap;

    @IntDef({DEFAULT, WITH_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DIALOG_MODE {

    }

    @IntDef({android.view.Gravity.TOP, android.view.Gravity.BOTTOM, android.view.Gravity.LEFT
            , android.view.Gravity.RIGHT, android.view.Gravity.CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {

    }

    private int defaultTextsize = 18;//textSize for default list item ;

    public ListDialog setDialogHeight(int dialogHeight) {
        wl.height = dialogHeight;
        return this;
    }

    public ListDialog setDialogWidth(int dialogWidth) {
        wl.width = dialogWidth;
        return this;
    }


    public ListDialog setmDimAmount(@FloatRange(from = 0, to = 1.0) float mDimAmount) {
        wl.dimAmount = mDimAmount;
        return this;
    }

    public ListDialog setAnimResId(@StyleRes int animResId) {
        wl.windowAnimations = animResId;
        return this;
    }


    private int mode = DEFAULT;
    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog         dialog;
    private Window                                     window;
    private WindowManager.LayoutParams                 wl;

    private RecyclerView.Adapter adapter;

    private float denisty;

    public static ListDialog create(Activity activity) {
        ListDialog listDialog = new ListDialog();
        listDialog.weakReference = new WeakReference<>(activity);
        listDialog.activity = listDialog.weakReference.get();
        listDialog.builder = new android.support.v7.app.AlertDialog.Builder(listDialog.activity);
        listDialog.denisty = activity.getResources().getDisplayMetrics().density;
        return listDialog;
    }

    public ListDialog setView(@LayoutRes int resId) {
        mRes = resId;
        view = LayoutInflater.from(activity).inflate(resId, null);

        builder.setView(view);
        initDialog();
        return this;
    }

    private void initDialog() {
        dialog = builder.create();
        window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        wl = window.getAttributes();
        wl.width = activity.getResources().getDisplayMetrics().widthPixels;
        wl.gravity = android.view.Gravity.BOTTOM;

    }

    public ListDialog setGravity(@Gravity int gravity) {
        wl.gravity = gravity;
        return this;
    }

    public ListDialog setView(View view) {
        this.view = view;
        builder.setView(view);
        initDialog();
        return this;
    }

    public ListDialog setCancelViewId(@IdRes int cancelViewId) {
        if (view == null) {
            throw new NullPointerException("@@@@@@ListDialog \nCan not found the root resource !!! Please set the root view !!!");
        }
        view.findViewById(cancelViewId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果调起过软键盘  dialog消失时移除软键盘

                if (edt != null) {
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
                dialog.dismiss();
                if (callBack != null) {
                    callBack.onCancelClick(dialog);
                }
            }
        });
        return this;
    }

    public ListDialog setCommitId(@IdRes int commitId) {
        if (view == null) {
            throw new NullPointerException("@@@@@@ListDialog \nCan not found the root resource !!! Please set the root view !!!");
        }
        view.findViewById(commitId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果调起过软键盘  dialog消失时移除软键盘

                if (edt != null) {
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
                if (callBack != null) {
                    callBack.onCommit(dialog, view);
                }
            }
        });
        return this;
    }

    public ListDialog setCommitId(@IdRes int commitId, final DialogCallBack callBack) {
        this.callBack = callBack;
        setCommitId(commitId);
        return this;
    }


    /**
     * @param edtId 需要使用edttext实例调起软键盘
     */
    public ListDialog showSoftKeyboard(@IdRes int edtId) {
        dialog.setCanceledOnTouchOutside(false);

        edt = view.findViewById(edtId);
        edt.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        return this;
    }

    public ListDialog setCancelViewId(@IdRes int cancelViewId, final DialogCallBack callBack) {
        this.callBack = callBack;
        setCancelViewId(cancelViewId);
        return this;
    }

    public ListDialog setListViewId(@IdRes int listViewId) {
        if (view == null) {
            throw new NullPointerException("@@@@@@ListDialog \nCan not found the root resource !!! Please set the root view !!!");
        }
        recyclerView = view.findViewById(listViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        return this;
    }


    public ListDialog setText(@IdRes int resId, String text) {
        if (mRes == -1 || view == null) {
            throw new NullPointerException("@@@@@@ListDialog \nCan not found the root resource !!! Please set the root view !!!");
        }
        View subView = view.findViewById(resId);
        if (subView == null) {
            throw new Resources.NotFoundException("@@@@@@ListDialog \n Can not found the resource int the rootView");
        }
        if (!(subView instanceof TextView)) {
            throw new ClassCastException("@@@@@@ListDialog \nthe subView is not a textView !! ");
        } else {
            ((TextView) subView).setText(text);
        }
        return this;
    }

    public ListDialog setClickListener(@IdRes int resId, String text, View.OnClickListener listener) {
        View subView = view.findViewById(resId);
        if (subView instanceof TextView) {
            ((TextView) subView).setText(text);
        }
        subView.setOnClickListener(listener);
        return this;
    }

    public ListDialog setClickListener(@IdRes int resId, View.OnClickListener listener) {
        View subView = view.findViewById(resId);
        subView.setOnClickListener(listener);
        return this;
    }

    public ListDialog setImage(@IdRes int resId, String path) {
        if (imageViewHashMap == null) {
            imageViewHashMap = new HashMap<>();
        }
        imageViewHashMap.put(path, (ImageView) view.findViewById(resId));

        return this;
    }


    public void show() {

        window.setAttributes(wl);
        if (mode == WITH_LIST) {
            if (adapter == null) {
                adapter = new DeafultAdapter();
                if (recyclerView == null) {
                    throw new NullPointerException("@@@@@@ListDialog \n  Please set the listView by @setListViewId");
                } else {
                    recyclerView.setAdapter(adapter);
                }
            }

        } else {

        }

        dialog.show();
        if (imageViewHashMap != null) {
            for (String key : imageViewHashMap.keySet()) {
                Glide.with(activity).load(key).into(imageViewHashMap.get(key));
            }
        }
        if (edt != null) {
            edt.post(new Runnable() {
                @Override
                public void run() {
                    imm = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });

        }

    }

    /**
     * just be useful when {mode==WITH_LIST}
     *
     * @param resId resId in item layout
     * @param text
     */
    // TODO: 2018/6/20
    public void setItemText(@IdRes int resId, String text) {
        if (mode != WITH_LIST) {
            throw new IllegalStateException("@@@@@@ListDialog \nthe method @setItemText just be useful in WITH_LIST mode");
        }
        if (mRes == -1) {
            throw new NullPointerException("@@@@@@ListDialog \nCan not found the root resource !!! Please set the root resource !!!");
        }
        if (view == null) {
            throw new NullPointerException("@@@@@@ListDialog \nthe rootView is null !! Maybe there is something wrong with the library. Please timely feedback it to my email luzheng611@gmail.com ,Thank You");
        }
        View subView = view.findViewById(resId);
        if (!(subView instanceof TextView)) {
            throw new ClassCastException("@@@@@@ListDialog \nthe subView is not a textView !! ");
        } else {
            ((TextView) subView).setText(text);
        }

    }

    /**
     * @param resId 需要隐藏的id
     * @param flag  是否影藏
     * @return
     */
    public ListDialog setVisible(@IdRes int resId, boolean flag) {
        view.findViewById(resId).setVisibility(flag ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * just be useful when {mode==WITH_LIST}
     *
     * @param adapter custom adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mode == WITH_LIST) {
            this.adapter = adapter;
        } else {
            throw new IllegalStateException("@@@@@@ListDialog \nthe method @setAdapter just be useful in WITH_LIST mode");
        }

    }

    public ListDialog mode(@DIALOG_MODE int mode) {
        this.mode = mode;
        return this;
    }

    public ListDialog setHandleListCallBack(HandleListCallBack handleListCallBack) {
        this.handleListCallBack = handleListCallBack;
        return this;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public ListDialog setList(@Nullable List list) {
        if (mode == WITH_LIST) {
            this.list = list == null ? new ArrayList() : list;
        } else {
            throw new IllegalStateException("@@@@@@ListDialog \nthe method @setList just be useful in WITH_LIST mode");
        }

        return this;
    }

    public ListDialog setList(@Nullable String[] args) {
        if (mode == WITH_LIST) {
            this.list = args == null ? new ArrayList() : Arrays.asList(args);
        } else {
            throw new IllegalStateException("@@@@@@ListDialog \nthe method @setList just be useful in WITH_LIST mode");
        }

        return this;
    }

    /**
     * @param list
     * @param defaultKey 默认情况下  textview取defaultKey中的值
     * @return
     */
    public ListDialog setList(@Nullable List list, String defaultKey) {
        if (mode == WITH_LIST) {
            this.list = list == null ? new ArrayList() : list;
            this.defaultKey = defaultKey;
        } else {
            throw new IllegalStateException("@@@@@@ListDialog \nthe method @setList just be useful in WITH_LIST mode");
        }

        return this;
    }


    public interface DialogCallBack {
        void onCancelClick(android.support.v7.app.AlertDialog dialog);

        void onCommit(android.support.v7.app.AlertDialog dialog, View view);
    }


    public abstract static class HandleListCallBack<T> implements DialogCallBack {
        public abstract void onItemClick(T item, int pos, AlertDialog dialog);

        @Override
        public void onCancelClick(AlertDialog dialog) {

        }

        @Override
        public void onCommit(AlertDialog dialog, View view) {

        }
    }

    public ListDialog setItemDefaultTextsize(@IntRange(from = 12, to = 28) int defaultTextsize) {
        this.defaultTextsize = defaultTextsize;
        return this;
    }

    class DeafultAdapter extends RecyclerView.Adapter {
        private long downTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(Color.WHITE);
            textView.setGravity(android.view.Gravity.CENTER);
            textView.setTextSize(defaultTextsize);
            int                    defaultItemHeight = 45;
            ViewGroup.LayoutParams vl                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(denisty * defaultItemHeight));
            textView.setLayoutParams(vl);

            return new viewHolder(textView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final Object object = list.get(position);
            if (defaultKey != null && !defaultKey.equals("")) {
                if (object instanceof Map) {
                    ((TextView) holder.itemView).setText(String.valueOf(((Map) object).get(defaultKey)));
                }
            }
            if (object instanceof String) {
                ((TextView) holder.itemView).setText(String.valueOf(object));
            } else {
                LogUtil.e("Can not setText effectively");
            }
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        downTime = System.currentTimeMillis();
                        v.setBackgroundColor(Color.parseColor("#e6e6e6"));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        if (System.currentTimeMillis() - downTime <= ViewConfiguration.getTapTimeout()) {
                        handleListCallBack.onItemClick(object, holder.getAdapterPosition(), dialog);
//                        }
                        v.setBackgroundColor(Color.WHITE);
                        downTime = 0;
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setBackgroundColor(Color.WHITE);
                        downTime = 0;
                    }
                    return true;
                }

            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {

            public viewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
