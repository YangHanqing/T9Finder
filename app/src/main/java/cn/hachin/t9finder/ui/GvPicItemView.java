package cn.hachin.t9finder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hachin.t9finder.R;

/**
 * Created by yanghanqing on 15/11/22.
 */
public class GvPicItemView extends LinearLayout {


    ImageView ivPic;
    TextView tvAbc;

    private void iniView(Context context) {
        View.inflate(context, R.layout.item_gv_pic, this);
        ivPic = (ImageView) findViewById(R.id.iv_gv_pic);
        tvAbc = (TextView) findViewById(R.id.tv_gv_abc);
    }

    public GvPicItemView(Context context) {
        super(context);
        iniView(context);
    }


    public GvPicItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);

    }

    public GvPicItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);

    }


    public void setTvAbc(String abc) {
        tvAbc.setText(abc);
    }

    public void setIvPic(int pic){
        ivPic.setImageResource(pic);
    }
}
