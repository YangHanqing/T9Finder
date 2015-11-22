package cn.hachin.t9finder;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hachin.t9finder.Entity.AppInfo;
import cn.hachin.t9finder.tools.Tools;
import cn.hachin.t9finder.ui.AppItemView;
import cn.hachin.t9finder.ui.ContactItemView;
import cn.hachin.t9finder.ui.MyHorizontalScrollView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    GridView gvMain;
    ListView lvMain;
    Button btnMain;
    Button btnDel;
    TextView tvMain;
    TextView tv5;
    TextView tv9;
    RelativeLayout relativeLayout;
    Map<String, String> map;
    Map<String, String> map2 = new HashMap<>();
    // List<AppInfo> appList;
//    List<AppInfo> appList2 = new ArrayList<>();
    Set<AppInfo> appList;
    Set<AppInfo> appList2 = new HashSet<>();
    MyLvAdapter lvAdapter;
    MyAppAdapter appAdapter;
    MyHorizontalScrollView horizontalScrollView;
    StringBuffer queryNum = new StringBuffer();
    boolean up_down = true;//初始化T9键盘为打开状态
    boolean isApp = true;
    int width;     // 屏幕宽度（像素）

    @Override
    protected void onStart() {
        super.onStart();
        tvMain.setText("");
        queryNum = new StringBuffer();

        map2.putAll(map);//深拷贝
        appList2.addAll(appList);

        setListView();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvMain = (GridView) findViewById(R.id.gv_main);
        lvMain = (ListView) findViewById(R.id.lv_main);
        tvMain = (TextView) findViewById(R.id.tv_main);
        btnDel = (Button) findViewById(R.id.btn_del);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        btnMain = (Button) findViewById(R.id.btn_main);
        horizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.hSv_main);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);// 隐藏滚动条


        try {
            map = GetContact();

        } catch (Exception e) {
            e.printStackTrace();
        }

        appList = getAppList();

        setGridView();

        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                queryNum = new StringBuffer();
                tvMain.setText(queryNum);
                map2.putAll(map);//深拷贝
                appList2.addAll(appList);
                updateListView();


                return true;
            }
        });
    }


    private void setListView() {
        if (isApp) {
            appAdapter = new MyAppAdapter();
            lvMain.setAdapter(appAdapter);
            lvMain.setOnItemClickListener(new MyContactItemListener());

        } else {
            lvAdapter = new MyLvAdapter();
            lvMain.setAdapter(lvAdapter);
            lvMain.setOnItemClickListener(new MyListViewItemListener());
        }

    }

    private void setGridView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         //获取屏幕宽度px
        int itemWidth = width / 4;  //GridView列宽
        int gridviewWidth = itemWidth * 5;  //GirdView宽度

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gvMain.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gvMain.setColumnWidth(itemWidth); // 设置列表项宽
        //设置布局
        MyGridViewAdapter gvAdapter = new MyGridViewAdapter();
        gvMain.setAdapter(gvAdapter);
        //设置按钮事件
        gvMain.setOnItemClickListener(new MyGridViewItemListener());

    }

    /**
     * 底部按钮 收起打开
     *
     * @param v
     */
    public void showOrHide(View v) {

        ObjectAnimator ta = null;

        if (up_down) {
            up_down = false;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", 0, Tools.dp2px(this, 210));
        } else {
            up_down = true;
            ta = ObjectAnimator.ofFloat(horizontalScrollView, "translationY", Tools.dp2px(this, 210), 0);
        }
        ta.setDuration(100);
        ta.setRepeatCount(0);

        ta.start();
    }

    public void deleteNum(View v) {
        if (queryNum.length() == 0)
            return;
        queryNum.deleteCharAt(queryNum.length() - 1);
        tvMain.setText(queryNum);
        map2.putAll(map);//深拷贝
        appList2.addAll(appList);
        updateListView();
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
                    queryNum.append("1");
                    updateListView();
                    break;
                case 2:
                    queryNum.append("2");
                    updateListView();
                    break;
                case 3:
                    queryNum.append("3");
                    updateListView();
                    break;
                case 4:
                    ChangeRightToLeft(position);
                    return;
                case 5:
                    setAppOrContactMode();
                    break;
                case 6:
                    queryNum.append("4");
                    updateListView();
                    break;
                case 7:
                    queryNum.append("5");
                    updateListView();
                    break;
                case 8:
                    queryNum.append("6");
                    updateListView();
                    break;
                case 9:
                    setAppOrContactMode();
                    break;
                case 10:
                    break;
                case 11:
                    queryNum.append("7");
                    updateListView();
                    break;
                case 12:
                    queryNum.append("8");
                    updateListView();
                    break;
                case 13:
                    queryNum.append("9");
                    updateListView();
                    break;
                case 14:
                    break;
            }

        }
    }

    private void setAppOrContactMode() {
        if(isApp){
            isApp=false;
            tv5.setText("APP");
            tv9.setText("APP");
            setListView();
        }else
        {
            isApp=true;
            tv5.setText("联系人");
            tv9.setText("联系人");

        }
        setListView();
    }

    private void updateListView() {
        tvMain.setText(queryNum.toString());


        if (isApp) {
            List<AppInfo> alarmDelete = new ArrayList<>();
            for (AppInfo appInfo : appList2) {
                String appName = (appInfo.appName).trim();
                String[] pinyin = Tools.hz2py(appName);
                if (!pinyin[0].contains(queryNum.toString())&&!pinyin[0].startsWith(queryNum.toString()) && !pinyin[1].startsWith(queryNum.toString())) {
                    alarmDelete.add(appInfo);
                }
            }

            for (AppInfo a : alarmDelete) {
                appList2.remove(a);
            }
            for (AppInfo appInfo : appList2) {
                Log.i(TAG, appInfo.appName);
            }
            Log.i(TAG, "-----");

            appAdapter.notifyDataSetChanged();

        } else { //处理map2
            List<String> alarmDelete = new ArrayList<String>();

            Set<String> set = map2.keySet();
            for (String phoneNum : set) {
                String name = map2.get(phoneNum);
                String[] pinyin = Tools.hz2py(name);
                if (!pinyin[0].startsWith(queryNum.toString()) && !pinyin[1].startsWith(queryNum.toString())) {
                    alarmDelete.add(phoneNum);
                }

            }
            for (String s : alarmDelete) {
                map2.remove(s);
            }
            lvAdapter.notifyDataSetChanged();
        }
    }

    public void ChangeRightToLeft(int position) {
        if (position == 0) {//左边按钮
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);//滚动到右边
        } else {//右边按钮
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);//滚动到左边

        }


    }

    /**
     * listView适配器
     */

    private class MyLvAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return map2.size();
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

            Set<String> set = map2.keySet();
            int flag = 0;
            for (String phoneNum : set) {
                if (flag == position) {
                    String name = map2.get(phoneNum);
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

    private class MyListViewItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String phoneNum=((ContactItemView)view).getContentPhoneNum();
            Intent intent = new Intent();
            intent.setAction("android.intent.action.CALL");
            intent.setData(Uri.parse("tel:"+ phoneNum));
            startActivity(intent);
        }
    }

    /**
     * 联系人Item 按钮点击事件
     */
    private class MyContactItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for (AppInfo a : appList2) {
                if (((AppItemView) view).getAppName().equals(a.appName)) {
                    Intent intent = a.appIntent;
                    startActivity(intent);
                    return;
                }
            }


        }
    }

    /**
     * 查询所有联系人的姓名，电话，邮箱
     *
     * @return
     * @throws Exception
     */
    public Map<String, String> GetContact() throws Exception {
        Map<String, String> map = new HashMap<>();

        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        //获取一行数据
        while (cursor.moveToNext()) {

            String name = null;//姓名

            int contractID = cursor.getInt(0);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            //获取每一列的数据
            while (cursor1.moveToNext()) {
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //姓名列
                    name = data1; //获取姓名
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机列
                    map.put(data1, name);    //添加 手机号 姓名
                }
            }
            cursor1.close();
        }
        cursor.close();
        return map;
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
     * 获取应用列表
     *
     * @return
     */
    private Set<AppInfo> getAppList() {
        PackageManager pm = this.getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        Set<AppInfo> appList;
        appList = new HashSet<>();
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppInfo info = new AppInfo();
                info.appName = packageInfo.applicationInfo.loadLabel(pm)
                        .toString().trim();
                info.pkgName = packageInfo.packageName;
                info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                // 获取该应用安装包的Intent，用于启动该应用
                info.appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);

                appList.add(info);
            } else {
                // 系统应用　　　　　　　　
            }

        }
        return appList;
    }

    private class MyAppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appList2.size();
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
            for (AppInfo app : appList2) {
                if (flag == position) {
                    view.setAppName(app.appName);
                    view.setIvAppIcon(app.appIcon);
                    break;
                } else {
                    flag++;
                }
            }

            return view;
        }
    }
}
