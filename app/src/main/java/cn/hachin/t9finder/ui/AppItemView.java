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
public class AppItemView extends LinearLayout {

    TextView tvAppName;
    ImageView ivAppIcon;

    private void iniView(Context context) {
        View.inflate(context, R.layout.item_app, this);
        tvAppName = (TextView) findViewById(R.id.tv_appName);
        ivAppIcon = (ImageView) findViewById(R.id.iv_appIcon);
    }

    public AppItemView(Context context) {
        super(context);
        iniView(context);
    }


    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);

    }

    public AppItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);

    }

    public void setIvAppIcon(Drawable drawAble) {
        ivAppIcon.setImageDrawable(drawAble);
    }

    public void setAppName(String name) {
        tvAppName.setText(name);
    }

    public String getAppName() {
        return tvAppName.getText().toString().trim();
    }


}
