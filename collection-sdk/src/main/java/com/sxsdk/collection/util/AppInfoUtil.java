package com.sxsdk.collection.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sxsdk.collection.callback.InfoCallBack;

import java.util.List;

public class AppInfoUtil {
    public static synchronized void appInfoList(Context context, InfoCallBack callback){
        // 获取手机中所有已安装的应用，并判断是否系统应用
        JSONArray appList = new JSONArray(); //用来存储获取的应用信息数据，手机上安装的应用数据都存在appList里
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if(packages != null) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                JSONObject item = new JSONObject();
                item.put("appName", packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
                item.put("packageName", packageInfo.packageName);
                item.put("versionName", packageInfo.versionName);
                item.put("versionCode", packageInfo.versionCode);
                item.put("inTime", packageInfo.firstInstallTime);
                item.put("upTime", packageInfo.lastUpdateTime);
                item.put("appType", (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? 0 : 1);
                item.put("flags", packageInfo.applicationInfo.flags);
                appList.add(item);
            }
        }
        callback.result(appList.toJSONString());
    }
}
