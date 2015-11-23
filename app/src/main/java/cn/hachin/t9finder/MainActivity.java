package cn.hachin.t9finder;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hachin.t9finder.Engine.AppInfoProvider;
import cn.hachin.t9finder.Engine.ContactInfoProvider;
import cn.hachin.t9finder.Entity.AppInfo;
import cn.hachin.t9finder.tools.Tools;
import cn.hachin.t9finder.ui.AppItemView;
import cn.hachin.t9finder.ui.ContactItemView;
import cn.hachin.t9finder.ui.MyHorizontalScrollView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    GridView t9View;
    ListView listView;
    Button btnUpDown;
    Button btnDel;
    TextView tvSearchBox;
    TextView tv5;
    TextView tv9;
    RelativeLayout relativeLayout;
    Map<String, String> mapContact;
    Map<String, String> mapContact2 = new HashMap<>();
    Set<AppInfo> appSet;
    Set<AppInfo> appSet2 = new HashSet<>();
    MyContactAdapter contactAdapter;
    MyAppAdapter appAdapter;
    MyHorizontalScrollView horizontalScrollView;
    List<PackageInfo> packages;
    PackageManager pm;
    StringBuffer searchBoxNum = new StringBuffer();
    boolean isGvOpen = true;//初始化T9键盘为打开状态
    boolean isApp = true;
    boolean isFirst = true;
    int width;     // 屏幕宽度（像素）

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mapContact2.putAll(mapContact);//深拷贝联系人数据
                    break;
                case 1:
                    appSet2.addAll(appSet);//深拷贝App数据
                    isFirst = false;
                    setListView();
                    break;
            }
        }
    };

    /**
     * 当第一次启动或者再次回来App时
     */
    @Override
    protected void onStart() {
        super.onStart();
        clearSearchBox();
        if (!isFirst) {//如果数据已经初始化完毕 不是第一次启动
            appSet2.addAll(appSet);//深拷贝App数据
            mapContact2.putAll(mapContact);//深拷贝联系人数据
            setListView();//重新设置布局
        }
    }

    /**
     * 启动程序时 初始化数据
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t9View = (GridView) findViewById(R.id.gv_main);
        listView = (ListView) findViewById(R.id.lv_main);
        tvSearchBox = (TextView) findViewById(R.id.tv_main);
        btnDel = (Button) findViewById(R.id.btn_del);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        btnUpDown = (Button) findViewById(R.id.btn_main);
        horizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.hSv_main);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);// 隐藏滚动条

        //初始化App数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mapContact = ContactInfoProvider.GetContact(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();

        //初始化联系人数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                pm = AppInfoProvider.getPm(MainActivity.this);
                packages = AppInfoProvider.getPackages();
                appSet = AppInfoProvider.getAppList();
                handler.sendMessage(msg);
            }
        }).start();

        //设置T9布局
        setGridView();

        //Del按钮的常按事件
        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                clearSearchBox();
                mapContact2.putAll(mapContact);//深拷贝
                appSet2.addAll(appSet);
                updateListView();


                return true;
            }
        });
    }

    /**
     * 设置T9布局
     */
    private void setGridView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         //获取屏幕宽度px
        int itemWidth = width / 4;  //GridView列宽
        int gridViewWidth = itemWidth * 5;  //GirdView宽度

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        t9View.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        t9View.setColumnWidth(itemWidth); // 设置列表项宽

        //设置布局内容
        MyGridViewAdapter gvAdapter = new MyGridViewAdapter();
        t9View.setAdapter(gvAdapter);
        //设置按钮事件
        t9View.setOnItemClickListener(new MyGridViewItemListener());

    }

    /**
     * 设置ListView布局
     */
    private void setListView() {
        if (isApp) {//如果是app
            appAdapter = new MyAppAdapter();
            listView.setAdapter(appAdapter);
            listView.setOnItemClickListener(new MyAppItemListener());
        } else {
            contactAdapter = new MyContactAdapter();
            listView.setAdapter(contactAdapter);
            listView.setOnItemClickListener(new MyContactItemListener());
        }

    }


    /**
     * 底部按钮 收起打开
     *
     * @param v
     */
    public void showOrHide(View v) {
        ObjectAnimator ta = null;
        if (isGvOpen) {
            isGvOpen = false;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", 0, Tools.dp2px(this, 210));
        } else {
            isGvOpen = true;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", Tools.dp2px(this, 210), 0);
        }
        ta.setDuration(100);
        ta.setRepeatCount(0);
        ta.start();
    }

    /**
     * 删除按钮
     *
     * @param v
     */
    public void deleteNum(View v) {
        if (searchBoxNum.length() == 0)
            return;
        searchBoxNum.deleteCharAt(searchBoxNum.length() - 1);
        tvSearchBox.setText(searchBoxNum);
        mapContact2.putAll(mapContact);//深拷贝
        appSet2.addAll(appSet);
        updateListView();
    }

    /**
     * 模式切换按钮
     */
    private void switchAppOrContact() {
        if (isApp) {
            isApp = false;
            tv5.setText("APP");
            tv9.setText("APP");
        } else {
            isApp = true;
            tv5.setText("联系人");
            tv9.setText("联系人");
        }
        clearSearchBox();
        setListView();//切换模式后 重新布局
    }

    /**
     * 清空搜索框
     */
    private void clearSearchBox() {
        searchBoxNum = new StringBuffer();
        tvSearchBox.setText(searchBoxNum);
    }

    /**
     * 根据搜索的内容更新ListView
     */
    private void updateListView() {
        tvSearchBox.setText(searchBoxNum.toString());

        if (isApp) {
            List<AppInfo> alarmDelete = new ArrayList<>();
            for (AppInfo appInfo : appSet2) {
                String appName = (appInfo.appName).trim();
                String[] pinyin = Tools.hz2py(appName);
                if (!pinyin[0].contains(searchBoxNum.toString()) && !pinyin[0].startsWith(searchBoxNum.toString()) && !pinyin[1].startsWith(searchBoxNum.toString())) {
                    alarmDelete.add(appInfo);
                }
            }

            for (AppInfo a : alarmDelete) {
                appSet2.remove(a);
            }
            appAdapter.notifyDataSetChanged();

        } else { //处理map2
            List<String> alarmDelete = new ArrayList<String>();
            Set<String> set = mapContact2.keySet();
            for (String phoneNum : set) {
                String name = mapContact2.get(phoneNum);
                String[] pinyin = Tools.hz2py(name);
                if (!pinyin[0].startsWith(searchBoxNum.toString()) && !pinyin[1].startsWith(searchBoxNum.toString())) {
                    alarmDelete.add(phoneNum);
                }

            }
            for (String s : alarmDelete) {
                mapContact2.remove(s);
            }
            contactAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 切换左右手模式
     * @param position
     */
    public void ChangeRightToLeft(int position) {
        if (position == 0) {//左边按钮
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);//滚动到右边
        } else {//右边按钮
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);//滚动到左边

        }
    }

    /**
     * 联系人适配器
     */
    private class MyContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mapContact2.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContactItemView view;

            if (convertView == null) {
                view = new ContactItemView(MainActivity.this);
            } else {
                view = (ContactItemView) convertView;
            }

            Set<String> set = mapContact2.keySet();
            int flag = 0;
            for (String phoneNum : set) {
                if (flag == position) {
                    String name = mapContact2.get(phoneNum);
                    view.setContentPhoneNum(phoneNum);
                    view.setContentName(name);

                    break;
                } else {
                    flag++;
                }
            }
            return view;
        }
    }

    /**
     * 联系人按钮点击事件
     */
    private class MyContactItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String phoneNum = ((ContactItemView) view).getContentPhoneNum();
            Intent intent = new Intent();
            intent.setAction("android.intent.action.CALL");
            intent.setData(Uri.parse("tel:" + phoneNum));
            startActivity(intent);
        }
    }

    /**
     * app适配器
     */
    private class MyAppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appSet2.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppItemView view;
            if (convertView == null) {
                view = new AppItemView(MainActivity.this);
            } else {
                view = (AppItemView) convertView;
            }

            int flag = 0;
            for (AppInfo app : appSet2) {
                if (flag == position) {
                    view.setAppName(app.appName);
                    view.setIvAppIcon(packages.get(app.location).applicationInfo.loadIcon(pm));
                    break;
                } else {
                    flag++;
                }
            }

            return view;
        }
    }

    /**
     * app按钮点击事件
     */
    private class MyAppItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for (AppInfo a : appSet2) {
                if (((AppItemView) view).getAppName().equals(a.appName)) {
                    Intent intent = a.appIntent;
                    startActivity(intent);
                    return;
                }
            }


        }
    }


    /**
     * T9键盘布局
     */
    public class MyGridViewAdapter extends BaseAdapter {
        String[] name = new String[]{"切换", "1", "2", "3", "切换", "联系人", "4", "5", "6", "联系人", "网页", "7", "8", "9", "网页"};


        @Override
        public int getCount() {
            return name.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 5) {
                tv5 = new TextView(MainActivity.this);
                tv5.setBackgroundColor(Color.GREEN);
                tv5.setText(name[position]);
                tv5.setHeight(Tools.dp2px(MainActivity.this, 70));
                return tv5;
            } else if (position == 9) {
                tv9 = new TextView(MainActivity.this);
                tv9.setBackgroundColor(Color.GREEN);
                tv9.setText(name[position]);
                tv9.setHeight(Tools.dp2px(MainActivity.this, 70));
                return tv9;
            } else {
                TextView tv = new TextView(MainActivity.this);
                tv.setBackgroundColor(Color.GREEN);
                tv.setText(name[position]);
                tv.setHeight(Tools.dp2px(MainActivity.this, 70));
                return tv;
            }
        }
    }

    /**
     * T9布局按钮响应事件
     */
    private class MyGridViewItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    ChangeRightToLeft(position);
                    return;
                case 1:
                    searchBoxNum.append("1");
                    updateListView();
                    break;
                case 2:
                    searchBoxNum.append("2");
                    updateListView();
                    break;
                case 3:
                    searchBoxNum.append("3");
                    updateListView();
                    break;
                case 4:
                    ChangeRightToLeft(position);
                    return;
                case 5:
                    switchAppOrContact();
                    break;
                case 6:
                    searchBoxNum.append("4");
                    updateListView();
                    break;
                case 7:
                    searchBoxNum.append("5");
                    updateListView();
                    break;
                case 8:
                    searchBoxNum.append("6");
                    updateListView();
                    break;
                case 9:
                    switchAppOrContact();
                    break;
                case 10:
                    break;
                case 11:
                    searchBoxNum.append("7");
                    updateListView();
                    break;
                case 12:
                    searchBoxNum.append("8");
                    updateListView();
                    break;
                case 13:
                    searchBoxNum.append("9");
                    updateListView();
                    break;
                case 14:
                    break;
            }

        }
    }

}
