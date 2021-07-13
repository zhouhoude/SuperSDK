//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxsdk.collection.util.lundu;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

public class DeviceCommonUtil {
    public DeviceCommonUtil() {
    }

    public static String getNonNullText(String text) {
        return TextUtils.isEmpty(text) ? "" : text;
    }

    public static boolean haveSelfPermission(Context paramContext, String paramString) {
        return ActivityCompat.checkSelfPermission(paramContext, paramString) == 0;
    }

    public static int getVersionCode(Context context) {
        PackageInfo info = null;
        int versionCode = 0;

        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return versionCode;
    }

    public static String getVersionName(Context context) {
        PackageInfo info = null;
        String versionName = "";

        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return versionName;
    }

    public static String getPackageName(Context context) {
        PackageInfo info = null;
        String packageName = "";

        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageName = info.packageName;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return packageName;
    }
}
