package cn.hachin.t9finder.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hachin.t9finder.R;

/**
 * Created by yanghanqing on 15/11/22.
 */
public class GvItemView extends LinearLayout {


    TextView tvNum;
    TextView tvAbc;

    private void iniView(Context context) {
        View.inflate(context, R.layout.item_gv, this);
        tvNum = (TextView) findViewById(R.id.tv_gv_num);
        tvAbc = (TextView) findViewById(R.id.tv_gv_abc);
    }

    public GvItemView(Context context) {
        super(context);
        iniView(context);
    }


    public GvItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);

    }

    public GvItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);

    }

    public void setTvNum(String num) {
        tvNum.setText(num);
    }

    public void setTvAbc(String abc) {
        tvAbc.setText(abc);
    }
}
