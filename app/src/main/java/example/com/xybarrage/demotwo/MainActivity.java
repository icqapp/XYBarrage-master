package example.com.xybarrage.demotwo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import example.com.xybarrage.R;
import example.com.xybarrage.demotwo.base.DanmakuActionManager;
import example.com.xybarrage.demotwo.base.DanmakuChannel;
import example.com.xybarrage.demotwo.base.DanmakuEntity;

public class MainActivity extends AppCompatActivity {
    DanmakuChannel danA, danB, danC;
    DanmakuActionManager danmakuActionManager;

    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo3);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        danA = (DanmakuChannel) findViewById(R.id.danA);
        danB = (DanmakuChannel) findViewById(R.id.danB);
        danC = (DanmakuChannel) findViewById(R.id.danC);
        Button addTanmu = (Button) findViewById(R.id.addTanmu);
        danmakuActionManager = new DanmakuActionManager();
        danA.setDanAction(danmakuActionManager);
        danB.setDanAction(danmakuActionManager);
        danC.setDanAction(danmakuActionManager);
        danmakuActionManager.addChannel(danA);
        danmakuActionManager.addChannel(danB);
        danmakuActionManager.addChannel(danC);


        addTanmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuActionManager.addDanmu(new DanmakuEntity(count++ + "弹幕  come on-->"));
            }
        });
    }
}
