package example.com.xybarrage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;


/**
 * 用来显示弹幕的View
 */
public class MyBarrageView extends TextView {

    private Paint paint;
    private int posX;           //x坐标
    private int posY;           //y坐标,需要加上字体的大小,不然不能完整的显示出来
    private Random random;      //随机数
    private static int TEXTSIZE = 50;     //字体大小
    private int color = Color.RED;      //字体颜色
    private RollThread rollThread;      //滚动线程
    private static final int MAXNUMBER = 100;       //设置当弹幕数量多于100的时候,才把弹幕显示位置调整为随机在全屏显示。否则值在屏幕上面显示。


    public MyBarrageView(Context context) {
        super(context);
        init();
    }

    public MyBarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        random = new Random();

        //设置x坐标为屏幕宽度,因为是要从右边滚动。
        posX = MainActivity.windowWidth;

        //如果超过我们设置的弹幕数量,那就在全屏随机显示
        if(MAXNUMBER < MainActivity.count_number){
            //设置Y坐标为屏幕内高度的随机值,需要注意要加上字体的大小,保证字体能完整显示
            posY = TEXTSIZE + random.nextInt(MainActivity.windowHeight - TEXTSIZE);
        }else{
            //否则值在屏幕上方显示
            posY = TEXTSIZE + random.nextInt(MainActivity.windowHeight / 2 - TEXTSIZE );

        }

        //设置随机颜色
        color = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256));
        paint.setColor(color);
        //设置弹幕字体大小
        paint.setTextSize(TEXTSIZE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(getShowText(),posX,posY,paint);
        if(rollThread == null){
            rollThread = new RollThread();
            rollThread.start();
        }
    }

    //获取要显示的文字
    private String getShowText(){
        if(getText() !=null && !getText().toString().isEmpty()){
            return getText().toString();
        }else{
            return getResources().getString(R.string.default_text);
        }
    }

    //设置x轴坐标,给线程调用,每次减少相应值,达到移动的效果
    public void setPosX(){
        posX -= 8;
    }



    /**
     * 滚动显示字幕的线程
     */
     class RollThread extends Thread {

        private Object mPauseLock;      //线程锁
        private boolean mPauseFlag;     //标签:是否暂停

        public RollThread(){
            mPauseLock = new Object();
            mPauseFlag = false;
        }

        @Override
        public void run() {
            while (true){
                checkIsPause();
                //更新x轴坐标
                setPosX();
                //绘制图像
                postInvalidate();
                //延迟一些时间,不然动画一飞而过。
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果弹幕已经移出屏幕,那就退出循环
                if(StopRollThread()){
                    System.out.println(getName() + " 线程停止");
                    //把下面这段信息发送到Ui线程内运行
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //从父类中移除该组件
                            ((ViewGroup) MyBarrageView.this.getParent()).removeView(MyBarrageView.this);
                        }
                    });

                     break;
                }
            }
        }

        //检查当前进程是否被挂起
        private void checkIsPause() {
            synchronized (mPauseLock){
                if(mPauseFlag){
                    try {
                        System.out.println(getName() + "线程已经被挂起");
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        //暂定当前动画
        public void onPause(){
            synchronized (mPauseLock){
                mPauseFlag = true;
            }
        }

        //恢复线程。
        public void onResume(){
            synchronized (mPauseLock){
                mPauseFlag = false;
                mPauseLock.notifyAll();
                mPauseLock.notify();
                System.out.println(getName() + "线程已恢复!");
            }
        }
    }

    //判断是否停止线程
    private boolean StopRollThread(){
        //如果x轴坐标小于等于字幕文字的宽度,那说明弹幕已经移动出屏幕了。
        if(posX <= -paint.measureText(getShowText())){
            return true;
        }
        return false;
    }

    /**
     * 该方法可以监听当前的屏幕是否可见
     * @param visibility
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(rollThread == null){
            return;
        }
        if(View.GONE == visibility){
            //如果不可见,那就把线程挂起
            rollThread.onPause();
        }else{
            rollThread.onResume();
        }
    }

    //设置字体大小
    public void SetFontSize(int size){
        TEXTSIZE = size;
    }



}
