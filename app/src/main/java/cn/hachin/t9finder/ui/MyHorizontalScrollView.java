package cn.hachin.t9finder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by yanghanqing on 15/11/21.
 */
public class MyHorizontalScrollView  extends HorizontalScrollView{
    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    float downX;//float DownX
    float moveX;//X轴距离

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        System.out.println(action);
        switch (action) {
            case 1:
                if(downX-moveX>0){
                    fullScroll(HorizontalScrollView.FOCUS_RIGHT);//滚动到右边
                }else{
                    fullScroll(HorizontalScrollView.FOCUS_LEFT);//滚动到左边
                }
                return true;
            case 2:
                downX=moveX;
                moveX = ev.getX();
                return super.onTouchEvent(ev);


        }
        return false;
    }
}
