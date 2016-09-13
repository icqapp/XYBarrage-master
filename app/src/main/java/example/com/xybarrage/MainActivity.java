package example.com.xybarrage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends Activity {

    public static int windowWidth;      //屏幕宽度
    public static int windowHeight;     //屏幕高度
    private Handler handler;
    private boolean isStop = true;     //判断字幕数据是否已发送完,或者是否需要暂定发送。
    private ViewGroup.LayoutParams lp;  //设置宽高全屏
    private static final int SLEEP_TIME = 800;    //设置两条信息之间发送的时间间隔。
    private int value = 0;             //字幕文件数组下标。
    public static int count_number = 0;  //保存有多少条字幕

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getShowText();
    }

    private void init() {
        //获取到屏幕宽高
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        windowHeight = rect.height();
        windowWidth = rect.width();

        //设置宽高全屏
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        handler = new Handler();
    }


    public void getShowText(){
        ReaderBarrage rb = new ReaderBarrage(this);
        //读取弹幕数据
        final Vector<String> data = rb.readerAssetsFolder("BarrageMessage");
        if(data != null) {
            count_number = data.size();
            Runnable createBarrageView = new Runnable() {
                @Override
                public void run() {
                    if (isStop) {
                        MyBarrageView myBarrageView = new MyBarrageView(MainActivity.this);
                        //传递字幕文字
                        myBarrageView.setText(data.get(value++));
                        //把控件添加到主页面上
                        addContentView(myBarrageView, lp);
                        //如果显示完数组内的数据,那就停止。
                        if (value >= data.size()) {
                            isStop = false;
                        }
                    }
                    //发送下一条消息等待时间。
                    handler.postDelayed(this, SLEEP_TIME);
                }
            };
            //将消息发到ui线程中
            handler.post(createBarrageView);
        }
    }
}