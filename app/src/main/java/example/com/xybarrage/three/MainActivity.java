package example.com.xybarrage.three;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;


import java.util.ArrayList;
import java.util.List;

import example.com.xybarrage.R;
import example.com.xybarrage.three.lib.BarrageView;
import example.com.xybarrage.three.lib.BaseBarrageItem;
import example.com.xybarrage.three.lib.NormalBarrageItem;

public class MainActivity extends Activity {
    BarrageView barrageView;
    List<BaseBarrageItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_three);
        barrageView = (BarrageView) findViewById(R.id.barrageview);
    }

    public void start(View view) {
        barrageView.start();
    }

    public void pause(View view) {
        barrageView.pause();
    }

    public void resume(View view) {
        barrageView.resume();
    }

    public void stop(View view) {
        barrageView.stop();
    }

    public void addItem(View view) {
        items.clear();
        for (int i = 0; i < 30; i++) {
            items.add(new NormalBarrageItem.BarrageItemBuilder().
                    contentStr("你好").
                    speed(4).create(this));

            items.add(new NormalBarrageItem.BarrageItemBuilder()
                    .textSize(40)
                    .contentStr("带图片message"+i)
                    .imageRsd(R.drawable.favourite_love_yellow)
                    .color(Color.WHITE)
                    .speed(4)
                    .paddingSize(40)
                    .create(this));

            items.add(new NormalBarrageItem.BarrageItemBuilder()
                    .textSize(40)
                    .contentStr("带背景message"+i)
                    .bgRsd(R.drawable.bg)
                    .color(Color.GREEN)
                    .speed(5)
                    .paddingSize(20)
                    .interpolator(new DecelerateInterpolator())
                    .create(this));

            items.add(new NormalBarrageItem.BarrageItemBuilder()
                    .textSize(40)
                    .contentStr("即带背景也带图片的message" +i)
                    .bgRsd(R.drawable.bg)
                    .imageRsd(R.drawable.favourite_love_yellow)
                    .color(Color.RED)
                    .speed(5)
                    .paddingSize(20)
                    .interpolator(new DecelerateInterpolator())
                    .create(this));

        }
        barrageView.addItemList(items);
    }

    public void reset(View view) {
        barrageView.reset();
    }
}
