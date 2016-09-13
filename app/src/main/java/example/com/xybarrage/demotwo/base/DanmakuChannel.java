package example.com.xybarrage.demotwo.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import example.com.xybarrage.R;
import example.com.xybarrage.demotwo.AnimationHelper;
import example.com.xybarrage.demotwo.ScreenUtils;


/**
 * Created by walkingMen on 2016/5/12.
 */
public class DanmakuChannel extends RelativeLayout {

    public boolean isRunning = false;
    public DanmakuEntity mEntity;
    private DanmakuActionInter danAction;

    public DanmakuActionInter getDanAction() {
        return danAction;
    }

    public void setDanAction(DanmakuActionInter danAction) {
        this.danAction = danAction;
    }

    public DanmakuChannel(Context context) {
        super(context);
        init();
    }


    public DanmakuChannel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmakuChannel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DanmakuChannel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.danmaku_channel_layout, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setClipToOutline(false);
        }
    }


    public void setDanmakuEntity(DanmakuEntity entity) {
        mEntity = entity;
    }

    public void mStartAnimation(DanmakuEntity entity) {
        isRunning = true;
        setDanmakuEntity(entity);
        if (mEntity != null) {
            final View view = View.inflate(getContext(), R.layout.item_live_danmu, null);
            TextView contentView = (TextView) view.findViewById(R.id.content);
            contentView.setText(entity.text);
            view.measure(-1, -1);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            int leftMargin = ScreenUtils.getScreenW(getContext());
            Animation anim = AnimationHelper.createTranslateAnim(getContext(), leftMargin, -measuredWidth);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!((Activity) getContext()).isDestroyed()) {//防止内存溢出
                        new Handler().post(new Runnable() {
                            public void run() {
                                view.clearAnimation();
                                DanmakuChannel.this.removeView(view);
                                if (danAction != null) {
                                    danAction.pollDanmu();
                                }
                            }
                        });
                    }
                    isRunning = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(anim);
            this.addView(view);
        }
    }
}
