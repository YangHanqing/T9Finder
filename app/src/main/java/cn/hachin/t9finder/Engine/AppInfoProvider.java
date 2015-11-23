package cn.hachin.t9finder.Engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.hachin.t9finder.Entity.AppInfo;

/**
 * Created by yanghanqing on 15/11/23.
 */
public class AppInfoProvider {
    static List<PackageInfo> packages;
    static PackageManager pm;

    public static List<PackageInfo> getPackages() {
        packages = pm.getInstalledPackages(0);
        return packages;
    }

    public static PackageManager getPm(Context context) {
        pm = context.getPackageManager();
        return pm;
    }

    public  static Set<AppInfo> getAppList() {


        Set<AppInfo> appList;
        appList = new HashSet<>();
        int location = 0;
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppInfo info = new AppInfo();
                info.appName = packageInfo.applicationInfo.loadLabel(pm)
                        .toString().trim();
                info.location = location;
                // info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                // 获取该应用安装包的Intent，用于启动该应用
                info.appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);

                appList.add(info);
            }
            location++;
        }
        return appList;
    }
}
