package cn.hachin.t9finder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hachin.t9finder.R;

/**
 * Created by yanghanqing on 15/11/22.
 */
public class ContactItemView extends LinearLayout {

    TextView tvName;
    TextView tvPhoneNum;

    private void iniView(Context context) {
        View.inflate(context, R.layout.item_contact, this);
        tvName = (TextView) findViewById(R.id.tv_ic_name);
        tvPhoneNum = (TextView) findViewById(R.id.tv_ic_phoneNum);
    }

    public ContactItemView(Context context) {
        super(context);
        iniView(context);
    }


    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);

    }

    public ContactItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);

    }

    public void setContentName(String name) {
        tvName.setText(name);
    }

    public void setContentPhoneNum(String phoneNum) {
        tvPhoneNum.setText(phoneNum);
    }

    public String getContentPhoneNum() {
        return tvPhoneNum.getText().toString().trim();
    }

}
