package example.com.xybarrage.demoone;


import android.animation.Animator;


import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义弹幕控件
 *
 * @author 1
 */
public class MyBarrageView extends LinearLayout {
    /**
     * 每行有多少个弹幕item
     */
    private static final int ROW_ITEM_COUNT = 2;
    /**
     * 是否停止弹幕动画
     */
    private boolean stop = false;
    /**
     * 存放所有用到的动画
     */
    private HashSet<ObjectAnimator> animators = new HashSet<ObjectAnimator>();
    private HashSet<ObjectAnimator> pausedAnimators = new HashSet<ObjectAnimator>();

    public MyBarrageView(Context context) {
        super(context);
        init();
    }

    public MyBarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 停止弹幕滑动
     */
    public void stop() {
        stop = true;
        if (!animators.isEmpty()) {
            Iterator<ObjectAnimator> iterator = animators.iterator();
            while (iterator.hasNext()) {
                ObjectAnimator anim = iterator.next();
                if (anim.isRunning()) {
                    anim.cancel();
                    pausedAnimators.add(anim);
                }

            }
        }
    }

    /**
     * 在主线程将弹幕移动起来
     */
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ObjectAnimator animator = (ObjectAnimator) msg.obj;
            animator.start();
        }

        ;
    };

    /**
     * 开启弹幕移动
     */
    public void start() {
        stop = false;
        if (!pausedAnimators.isEmpty()) {
            new Thread() {
                public void run() {
                    Iterator<ObjectAnimator> iterator = pausedAnimators.iterator();
                    while (iterator.hasNext()) {
                        Message msg = handler.obtainMessage();
                        msg.obj = iterator.next();
                        msg.sendToTarget();
                        try {
                            //使动画启动时间分散不要太集中
                            sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    pausedAnimators.clear();
                }

                ;
            }.start();

        }
    }

    /**
     * 增加弹幕
     *
     * @param txt
     */
    public void add(String txt) {
        if (isFull()) {
            addViewItem(txt);
        } else {
            addGroupItem(txt);
        }
    }

    /**
     * 检查行Viewgroup是不是已加满
     *
     * @return
     */
    private boolean isFull() {
        if (getChildCount() < 1) {
            return false;
        }
        View lastViewGroup = getChildAt(getChildCount() - 1);
        int lastBottom = lastViewGroup.getTop() + lastViewGroup.getMeasuredHeight();
        boolean b1 = lastBottom >= getMeasuredHeight();
        boolean b2 = lastBottom + lastViewGroup.getMeasuredHeight() - getMeasuredHeight() >= lastViewGroup.getHeight() / 3;
        return b1 || b2;
    }

    /**
     * 根据指定的字符生成Textview
     *
     * @param txt
     * @return
     */
    private TextView initTextview(String txt) {
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(0xffffff);
        textView.setSingleLine(true);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //textView.setBackgroundColor(Color.YELLOW);
        textView.setText("---------------text " + txt + "------------");
        android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    /**
     * 根据指定的字符，增加相应弹幕
     *
     * @param txt
     */
    private void addViewItem(String txt) {
        int rowIndex = new Random().nextInt(getChildCount());
        addItemView2Viewgroup(rowIndex, txt);
    }

    /**
     * 根据指定的字符，增加相应弹幕，并新增新的弹幕行
     *
     * @param txt
     */
    private void addGroupItem(String txt) {
        RelativeLayout layout = new RelativeLayout(getContext());
        ArrayBlockingQueue<View> subViews = new ArrayBlockingQueue<View>(ROW_ITEM_COUNT);
        layout.setTag(subViews);
        addView(layout);
        int rowIndex = getChildCount() - 1;
        addItemView2Viewgroup(rowIndex, txt);
    }

    /**
     * 把指定的view加到指定的行viewGroup上
     *
     * @param rowIndex
     * @param txt
     */
    private void addItemView2Viewgroup(final int rowIndex, final String txt) {
        new Thread() {
            @Override
            public void run() {
                TextView textView = null;
                Holder holder;
                int width = getResources().getDisplayMetrics().widthPixels;
                ObjectAnimator animator = null;

                final RelativeLayout group = (RelativeLayout) getChildAt(rowIndex);
                ArrayBlockingQueue<View> queue = (ArrayBlockingQueue<View>) group.getTag();
                if (group.getChildCount() + queue.size() >= ROW_ITEM_COUNT) {
                    try {
                        textView = (TextView) queue.take();
                        group.post(new SetTextRunnable(txt, textView));

                        Holder holder2 = (Holder) textView.getTag();
                        animator = holder2.animator.get();
                        animator.setFloatValues(width, 0 - width);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    holder = new Holder();
                    holder.rowIndex = rowIndex;
                    holder.startX = width;
                    holder.endX = 0 - width;
                    textView = initTextview(txt);
                    textView.setTextColor(getColor());
                    textView.setTag(holder);
                    textView.setOnClickListener(new MyClickListener());


                    animator = ObjectAnimator.ofFloat(textView, "translationX", width, 0 - width);
                    animator.addListener(new MyAnimatorListener(textView));
                    animator.setInterpolator(new AccelerateInterpolator());
                    holder.animator = new SoftReference<ObjectAnimator>(animator);
                    animators.add(animator);
                    group.post(new AddViewRunnable(group, textView));
                }

                int random = new Random().nextInt(2500);
                int duration = 5000 + random;
                Log.d("duration", " random duration = " + duration);
                animator.setDuration(duration);
                animator.setInterpolator(getInterpolator());
                if (!stop) {
                    group.post(new StartAnimatorRunnable(animator));
                } else {
                    pausedAnimators.add(animator);
                }

            }
        }.start();
    }

    /**
     * 随机获取颜色值
     *
     * @return
     */
    protected int getColor() {
        int[] colors = new int[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.RED, Color.GREEN, Color.WHITE, Color.YELLOW};
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    /**
     * 随机获取弹幕动画加速器
     *
     * @return
     */
    private TimeInterpolator getInterpolator() {
        Random random = new Random();
        int id = random.nextInt(4);
        System.out.println("------id = " + id);
        switch (id) {
            case 0:
                return new AccelerateDecelerateInterpolator();
            case 1:
                return new AccelerateInterpolator();
            case 2:
                return new DecelerateInterpolator();
            case 3:
                return new LinearInterpolator();

		/*case 4:
            return new BounceInterpolator();
		case 5:
			return new CycleInterpolator(3);
		case 6:
			return new AnticipateInterpolator();
		case 7:
			return new AnticipateOvershootInterpolator();
		case 8:
			return new OvershootInterpolator();*/
            default:
                break;
        }
        return new LinearInterpolator();
    }

    /**
     * 用于向指定容器里添加指定的view
     *
     * @author 1
     */
    class AddViewRunnable implements Runnable {
        private ViewGroup group;
        private View view;

        public AddViewRunnable(ViewGroup group, View view) {
            super();
            this.group = group;
            this.view = view;
        }

        @Override
        public void run() {
            group.addView(view);
        }
    }

    /**
     * 启动相应动画
     *
     * @author 1
     */
    class StartAnimatorRunnable implements Runnable {
        private ObjectAnimator animator;

        public StartAnimatorRunnable(ObjectAnimator animator) {
            super();
            this.animator = animator;
        }

        @Override
        public void run() {
            if (!stop) {
                animator.start();
            }
        }
    }

    /**
     * 为指定的TextView设置指定的文本
     *
     * @author 1
     */
    class SetTextRunnable implements Runnable {
        private String txt;
        private TextView textView;

        public SetTextRunnable(String txt, TextView textView) {
            super();
            this.txt = txt;
            this.textView = textView;
        }

        @Override
        public void run() {
            textView.setText(txt);
            textView.setTextColor(getColor());
        }

    }

    /**
     * 帮助类
     *
     * @author 1
     */
    class Holder {
        /**
         * view所处的行号
         */
        public int rowIndex;
        /**
         * view所在的列号
         */
        public int columnIndex;
        /**
         * 动画持续时间
         */
        public int duation;
        /**
         * 动画开始坐标
         */
        public int startX;
        /**
         * 动画结束坐标
         */
        public float endX;
        /**
         * view所绑定的动画
         */
        public SoftReference<ObjectAnimator> animator;
    }

    class MyClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            TextView textView = (TextView) v;
            Toast.makeText(getContext(), textView.getText(), 0).show();
        }

    }

    /**
     * 动画监听器
     *
     * @author 1
     */
    class MyAnimatorListener implements AnimatorListener {
        private SoftReference<View> objectView;

        public MyAnimatorListener(View view) {
            this.objectView = new SoftReference<View>(view);
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (stop) {
                return;
            }
            final View view = objectView.get();
            Holder holder = (Holder) view.getTag();
            Float f = (Float) ((ValueAnimator) animation).getAnimatedValue();
            //((ObjectAnimator)animation).setFloatValues(f,holder.endX);

            ViewGroup group = (ViewGroup) getChildAt(holder.rowIndex);
            final ArrayBlockingQueue<View> queue = (ArrayBlockingQueue<View>) group.getTag();

            new Thread() {
                public void run() {
                    try {
                        queue.put(view);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();
            System.out.println("--------onAnimationEnd--------");
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            Float f = (Float) ((ValueAnimator) animation).getAnimatedValue();
            System.out.println("ssss   f=" + f);
            ((ObjectAnimator) animation).setFloatValues(f, 0 - getContext().getResources().getDisplayMetrics().widthPixels);
            System.out.println("--------onAnimationCancel---------");
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

    }
}