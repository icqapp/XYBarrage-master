package example.com.xybarrage.demoone;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import example.com.xybarrage.R;

/**
 * http://blog.csdn.net/chuyouyinghe/article/details/49277461
 */
public class BarrageActivity extends Activity {
    private MyBarrageView ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrage);
        ll = (MyBarrageView) findViewById(R.id.ll);
    }
    int i = 1;

    public void addView(View v){
        ll.add("弹幕"+ i++);
    }
    public void stop(View v){
        ll.stop();
    }
    public void start(View v){
        ll.start();
    }
}