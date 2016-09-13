package example.com.xybarrage.demotwo.base;

/**
 * Created by walkingMen on 2016/5/12.
 * 弹幕动作类
 */
public interface DanmakuActionInter {
    /**
     * 添加弹幕
     */
    void addDanmu(DanmakuEntity dan);

    /**
     * 移出弹幕
     */
    void pollDanmu();
}
