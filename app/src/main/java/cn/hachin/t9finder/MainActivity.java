package cn.hachin.t9finder;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.hachin.t9finder.ui.GvItemView;
import cn.hachin.t9finder.ui.GvPicItemView;
import cn.hachin.t9finder.ui.MyHorizontalScrollView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int T9HEIGHT = 210;
    GridView t9View;
    ListView listView;
    ImageButton btnUpDown;
    ImageButton btnDel;
    TextView tvSearchBox;
    GvPicItemView gvPicItemView5;
    GvPicItemView gvPicItemView9;
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
                case 3:
                    t9View.setVisibility(View.GONE);
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
        btnDel = (ImageButton) findViewById(R.id.btn_del);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        btnUpDown = (ImageButton) findViewById(R.id.btn_main);
        horizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.hSv_main);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);// 隐藏滚动条

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isGvOpen) {
                    showOrHide(t9View);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

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
        ObjectAnimator ta;
        final RotateAnimation animation;

        if (isGvOpen) {
            isGvOpen = false;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", 0, Tools.dp2px(this, T9HEIGHT));
            ta.setDuration(300);
            ta.setRepeatCount(0);
            ta.start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }).start();

        } else {
            t9View.setVisibility(View.VISIBLE);
            isGvOpen = true;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", Tools.dp2px(this, T9HEIGHT), 0);
            ta.setDuration(300);
            ta.setRepeatCount(0);
            ta.start();

        }

//        animation = new RotateAnimation(0f,180f, Animation.RELATIVE_TO_SELF,
//                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        animation.setDuration(500);//设置动画持续时间
//        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//        btnUpDown.setAnimation(animation);
//        animation.start();

    }

    /**
     * setting按钮
     *
     * @param v
     */
    public void setting(View v) {
        Toast.makeText(MainActivity.this, "测试版本,么么哒", Toast.LENGTH_SHORT).show();
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
            gvPicItemView5.setTvAbc("APP");
            gvPicItemView9.setTvAbc("APP");
            gvPicItemView5.setIvPic(R.drawable.turn2);
            gvPicItemView9.setIvPic(R.drawable.turn2);

        } else {
            isApp = true;
            gvPicItemView5.setTvAbc("联系人");
            gvPicItemView9.setTvAbc("联系人");
            gvPicItemView5.setIvPic(R.drawable.turn);
            gvPicItemView9.setIvPic(R.drawable.turn);

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
                if (!pinyin[1].contains(searchBoxNum.toString()) && !pinyin[0].contains(searchBoxNum.toString())) {
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

                if (!pinyin[1].contains(searchBoxNum.toString()) && !pinyin[0].contains(searchBoxNum.toString())) {
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
     *
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
        String[] name2 = new String[]{"切换", "测试版", "ABC", "DEF", "切换", "联系人", "GHI", "JKL", "MNO", "联系人", "网页", "PQRS", "TUV", "WXYZ", "网页"};


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
            GvPicItemView gvPicItemView;

            switch (position) {
                case 0://左右切换按钮
                    gvPicItemView = new GvPicItemView(MainActivity.this);
                    gvPicItemView.setTvAbc(name2[position]);
                    gvPicItemView.setIvPic(R.drawable.left2right);
                    return gvPicItemView;
                case 4:
                    gvPicItemView = new GvPicItemView(MainActivity.this);
                    gvPicItemView.setTvAbc(name2[position]);
                    gvPicItemView.setIvPic(R.drawable.left2right);
                    return gvPicItemView;
                case 5://app 联系人切换按钮
                    gvPicItemView5 = new GvPicItemView(MainActivity.this);
                    gvPicItemView5.setTvAbc(name2[position]);
                    gvPicItemView5.setIvPic(R.drawable.turn);
                    return gvPicItemView5;
                case 9:
                    gvPicItemView9 = new GvPicItemView(MainActivity.this);
                    gvPicItemView9.setTvAbc(name2[position]);
                    gvPicItemView9.setIvPic(R.drawable.turn);
                    return gvPicItemView9;

                case 10://启动浏览器按钮
                    gvPicItemView = new GvPicItemView(MainActivity.this);
                    gvPicItemView.setTvAbc(name2[position]);
                    gvPicItemView.setIvPic(R.drawable.web);

                    return gvPicItemView;
                case 14:
                    gvPicItemView = new GvPicItemView(MainActivity.this);
                    gvPicItemView.setTvAbc(name2[position]);
                    gvPicItemView.setIvPic(R.drawable.web);

                    return gvPicItemView;
                default:
                    GvItemView gvItemView = new GvItemView(MainActivity.this);
                    gvItemView.setTvNum(name[position]);
                    gvItemView.setTvAbc(name2[position]);
                    return gvItemView;

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
                    startExploer();
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
                    startExploer();
                    break;
            }

        }
    }

    private void startExploer() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse("http://");
        intent.setData(url);
        startActivity(intent);
    }

}
