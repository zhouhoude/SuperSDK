//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxsdk.collection.util.lundu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract.Groups;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.Secure;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.aitime.android.deviceid.DeviceIdentifier;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;


public class DeviceInfoUtils {

    public static JSONObject getHardWareInfo(Context context) throws JSONException {
        JSONObject hardWareData = new JSONObject();
        hardWareData.put("device_name", DeviceCommonUtil.getNonNullText(getDriverBrand()));
        hardWareData.put("brand", DeviceCommonUtil.getNonNullText(getDriverBrand()));
        hardWareData.put("board", DeviceCommonUtil.getNonNullText(getBoard()));
        hardWareData.put("cores", getCPUCores());
        hardWareData.put("sdk_version", DeviceCommonUtil.getNonNullText(getDriverSDKVersion()));
        hardWareData.put("model", DeviceCommonUtil.getNonNullText(getDriverModel()));
        hardWareData.put("release", DeviceCommonUtil.getNonNullText(getDriverOsVersion()));
        hardWareData.put("serial_number", DeviceCommonUtil.getNonNullText(getSerialNumber()));
        hardWareData.put("physical_size", DeviceCommonUtil.getNonNullText(getScreenPhysicalSize(context)));
        hardWareData.put("production_date", DeviceCommonUtil.getNonNullText(getDriverTime()));
        hardWareData.put("device_height", DeviceCommonUtil.getNonNullText(getDisplayMetrics(context).heightPixels + ""));
        hardWareData.put("device_width", DeviceCommonUtil.getNonNullText(getDisplayMetrics(context).widthPixels + ""));
        return hardWareData;
    }

    public static JSONObject getGeneralData(Context context) throws JSONException {
        JSONObject generalData = new JSONObject();
        generalData.put("gaid", DeviceCommonUtil.getNonNullText(getGAID(context)));
        try {
            generalData.put("uuid", DeviceIdentifier.getUniqueIdentifier(context));
        } catch (Exception e){
        }
        generalData.put("and_id", DeviceCommonUtil.getNonNullText(getAndroidId(context)));
        generalData.put("phone_type", DeviceCommonUtil.getNonNullText(String.valueOf(getPhoneType(context))));
        generalData.put("mac", DeviceCommonUtil.getNonNullText(getMacAddress(context)));
        generalData.put("locale_iso_3_language", DeviceCommonUtil.getNonNullText(getISO3Language(context)));
        generalData.put("locale_display_language", DeviceCommonUtil.getNonNullText(getLocaleDisplayLanguage()));
        generalData.put("locale_iso_3_country", DeviceCommonUtil.getNonNullText(getISO3Country(context)));
        generalData.put("imei", DeviceCommonUtil.getNonNullText(getDeviceImeIValue(context)));
        generalData.put("phone_number", DeviceCommonUtil.getNonNullText(getCurrentPhoneNum(context)));
        generalData.put("network_operator_name", DeviceCommonUtil.getNonNullText(getNetWorkOperatorName(context)));
        generalData.put("network_type", DeviceCommonUtil.getNonNullText(getNetworkState(context)));
        generalData.put("network_type_new", DeviceCommonUtil.getNonNullText(getNetworkType(context)));
        generalData.put("time_zone_id", DeviceCommonUtil.getNonNullText(getCurrentTimeZone()));
        generalData.put("language", DeviceCommonUtil.getNonNullText(getLanguage(context)));
        generalData.put("is_using_proxy_port", isUsingProxyPort());
        generalData.put("is_using_vpn", isUsingVPN());
        generalData.put("is_usb_debug", isOpenUSBDebug(context));
        generalData.put("elapsedRealtime", getElapsedRealtime());
        generalData.put("sensor_list", getSensorList(context));
        generalData.put("currentSystemTime", System.currentTimeMillis());
        generalData.put("uptimeMillis", getUpdateMills());
        return generalData;
    }

    public static JSONObject getOtherData(Context context) throws JSONException {
        JSONObject otherData = new JSONObject();
        otherData.put("root_jailbreak", isRoot() ? "1" : "0");
        otherData.put("last_boot_time", bootTime() + "");
        otherData.put("keyboard", getKeyboard(context));
        otherData.put("simulator", isEmulator(context) ? "1" : "0");
        otherData.put("dbm", DeviceCommonUtil.getNonNullText(getMobileDbm(context)));
        return otherData;
    }

    public static JSONObject getNetworkData(Context context) {
        JSONObject network = new JSONObject();
        JSONObject currentNetwork = new JSONObject();
        JSONArray configNetwork = new JSONArray();

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentNetwork.put("bssid", wifiInfo.getBSSID());
                currentNetwork.put("ssid", wifiInfo.getSSID());
                currentNetwork.put("mac", wifiInfo.getMacAddress());
                currentNetwork.put("name", getWifiName(context));
                network.put("current_wifi", currentNetwork);
                network.put("IP", getWifiIP(context));
                List<ScanResult> configs = wifiManager.getScanResults();
                Iterator var6 = configs.iterator();

                while (var6.hasNext()) {
                    ScanResult scanResult = (ScanResult) var6.next();
                    JSONObject config = new JSONObject();
                    config.put("bssid", scanResult.BSSID);
                    config.put("ssid", scanResult.SSID);
                    config.put("mac", scanResult.BSSID);
                    config.put("name", scanResult.SSID);
                    configNetwork.add(config);
                }

                network.put("wifi_count", configs.size() + 1);
                network.put("configured_wifi", configNetwork);
            }
        } catch (Exception var9) {
        }

        return network;
    }

    public static JSONObject getBatteryData(Context context) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        BatteryManager manager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (manager != null) {
            int dianliang = manager.getIntProperty(4);
            jSONObject.put("battery_pct", dianliang);
            jSONObject.put("battery_max", getBatteryCapacity(context));
            jSONObject.put("battery_level", getBatteryCapacity(context) * dianliang / 100);
        }

        Intent intent = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int k = intent.getIntExtra("plugged", -1);
        switch (k) {
            case 1:
                jSONObject.put("is_usb_charge", 0);
                jSONObject.put("is_ac_charge", 1);
                jSONObject.put("is_charging", 1);
                return jSONObject;
            case 2:
                jSONObject.put("is_usb_charge", 1);
                jSONObject.put("is_ac_charge", 0);
                jSONObject.put("is_charging", 1);
                return jSONObject;
            default:
                jSONObject.put("is_usb_charge", 0);
                jSONObject.put("is_ac_charge", 0);
                jSONObject.put("is_charging", 0);
                return jSONObject;
        }
    }


    public static double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    private static String getGAID(Context context) {
        try {
            DeviceGaidProvider.AdInfo adInfo = DeviceGaidProvider.getAdvertisingIdInfo(context);
            if (adInfo != null) {
                return adInfo.getId();
            }
        } catch (Exception var1) {
            var1.printStackTrace();
        }

        return "";
    }

    private static String getISO3Language(Context paramContext) {
        return paramContext.getResources().getConfiguration().locale.getISO3Language();
    }

    private static String getISO3Country(Context paramContext) {
        return paramContext.getResources().getConfiguration().locale.getISO3Country();
    }

    private static String getLocaleDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    @SuppressLint({"MissingPermission"})
    private static String getDeviceImeIValue(Context context) {
        if (DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_PHONE_STATE")) {
            try {
                if (VERSION.SDK_INT >= 26) {
                    return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
                }
                return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            } catch (Exception var2) {
            }
        }
        return "";
    }

    @SuppressLint({"MissingPermission"})
    private static String getCurrentPhoneNum(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String tel = tm.getLine1Number();
                return tel;
            }
        } catch (Exception var2) {
        }

        return "";
    }

    private static String getScreenPhysicalSize(Context paramContext) {
        Display display = ((WindowManager) paramContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return Double.toString(Math.sqrt(Math.pow((double) ((float) displayMetrics.heightPixels / displayMetrics.ydpi), 2.0D) + Math.pow((double) ((float) displayMetrics.widthPixels / displayMetrics.xdpi), 2.0D)));
    }

    public static String getAudioExternalNumber(Context context) {
        int result = 0;
        if (!DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE")) {
            return "";
        } else {
            Cursor cursor;
            for (cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return String.valueOf(result);
        }
    }

    public static String getAudioInternalNumber(Context context) {
        int result = 0;

        Cursor cursor;
        for (cursor = context.getContentResolver().query(Media.INTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, "title_key"); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getImagesExternalNumber(Context context) {
        int result = 0;
        if (!DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE")) {
            return "";
        } else {
            Cursor cursor;
            for (cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return String.valueOf(result);
        }
    }

    public static String getImagesInternalNumber(Context context) {
        int result = 0;

        Cursor cursor;
        for (cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getVideoExternalNumber(Context context) {
        int result = 0;
        if (!DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE")) {
            return "";
        } else {
            String[] arrayOfString = new String[]{"date_added"};

            Cursor cursor;
            for (cursor = context.getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return String.valueOf(result);
        }
    }

    public static String getVideoInternalNumber(Context context) {
        int result = 0;
        String[] arrayOfString = new String[]{"date_added"};

        Cursor cursor;
        for (cursor = context.getContentResolver().query(android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getDownloadFileNumber() {
        int result = 0;
        File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        if (files != null) {
            result = files.length;
        }

        return String.valueOf(result);
    }

    public static String getContactsGroupNumber(Context context) {
        int result = 0;
        if (!DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_CONTACTS")) {
            return "";
        } else {
            Uri uri = Groups.CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor;
            for (cursor = contentResolver.query(uri, (String[]) null, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return String.valueOf(result);
        }
    }

    private static int getPhoneType(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getPhoneType();
        } catch (Exception var1) {
            return 0;
        }
    }

    private static String getLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    private static String getNetWorkOperatorName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static JSONArray getAppList(Context context) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        JSONArray jsonArray = new JSONArray();
        if (packages != null && packages.size() > 0) {
            try {
                for (int i = 0; i < packages.size(); ++i) {
                    PackageInfo packageInfo = (PackageInfo) packages.get(i);
                    String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_name", name);
                    jsonObject.put("package", packageInfo.packageName);
                    jsonObject.put("version_name", packageInfo.versionName);
                    jsonObject.put("version_code", packageInfo.versionCode);
                    jsonObject.put("in_time", packageInfo.firstInstallTime);
                    jsonObject.put("up_time", packageInfo.lastUpdateTime);
                    jsonObject.put("flags", packageInfo.applicationInfo.flags);
                    jsonObject.put("app_type", (packageInfo.applicationInfo.flags & 1) == 0 ? "0" : "1");
                    jsonObject.put("create_time", System.currentTimeMillis());
                    jsonArray.add(jsonObject);
                }
            } catch (Exception var7) {
            }
        }

        return jsonArray;
    }

    private static String getKeyboard(Context paramContext) {
        Configuration configuration = paramContext.getResources().getConfiguration();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(configuration.keyboard);
        stringBuilder.append("");
        return stringBuilder.toString();
    }

    @SuppressLint({"MissingPermission"})
    private static String getMobileDbm(Context context) {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (DeviceCommonUtil.haveSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
                List<CellInfo> cellInfoList = tm.getAllCellInfo();
                if (null != cellInfoList) {
                    Iterator var3 = cellInfoList.iterator();

                    while (var3.hasNext()) {
                        CellInfo cellInfo = (CellInfo) var3.next();
                        if (cellInfo instanceof CellInfoGsm) {
                            CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthGsm.getDbm();
                        } else if (cellInfo instanceof CellInfoCdma) {
                            CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthCdma.getDbm();
                        } else if (cellInfo instanceof CellInfoWcdma) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                        } else if (cellInfo instanceof CellInfoLte) {
                            CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthLte.getDbm();
                        }
                    }
                }
            }
        } catch (Exception var6) {
        }
        return String.valueOf(dbm);
    }

    private static String getWifiIP(Context context) {
        String ip = null;

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                ip = (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
            }
        } catch (Exception var4) {
        }

        return ip;
    }

    private static String getWifiName(Context context) {
        if (isOnline(context) && getNetworkState(context).equals(Context.WIFI_SERVICE)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(ssid) && ssid.contains("\"")) {
                ssid = ssid.replaceAll("\"", "");
            }

            return ssid;
        } else {
            return "";
        }
    }

    private static String getMacAddress(Context context) {
        String mac = getMacAddress1(context);
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromHardware();
        }

        return mac;
    }

    private static String getMacAddress1(Context context) {
        try {
            WifiManager localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
            String macAddress = localWifiInfo.getMacAddress();
            if (TextUtils.isEmpty(macAddress) || "02:00:00:00:00:00".equals(macAddress)) {
                macAddress = getMacAddress2(context);
            }

            return macAddress;
        } catch (Exception var3) {
            return null;
        }
    }

    private static String getMacAddress2(Context context) {
        if (isOnline(context) && getNetworkState(context).equals(Context.WIFI_SERVICE)) {
            String macSerial = null;
            String str = "";

            try {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                while (null != str) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();
                        break;
                    }
                }
            } catch (Exception var5) {
            }

            return macSerial;
        } else {
            return "";
        }
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            Iterator var1 = all.iterator();

            while (var1.hasNext()) {
                NetworkInterface nif = (NetworkInterface) var1.next();
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return null;
                    }

                    StringBuilder mac = new StringBuilder();
                    byte[] var5 = macBytes;
                    int var6 = macBytes.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        byte b = var5[var7];
                        mac.append(String.format("%02X:", b));
                    }

                    if (mac.length() > 0) {
                        mac.deleteCharAt(mac.length() - 1);
                    }

                    return mac.toString();
                }
            }
        } catch (Exception var9) {
        }

        return null;
    }

    public static boolean getRootAuth() {
        Process process = null;
        DataOutputStream os = null;

        boolean var3;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                var3 = false;
                return var3;
            }

            var3 = true;
        } catch (Exception var14) {
            var3 = false;
            return var3;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                process.destroy();
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }

        return var3;
    }

    private static boolean isRoot() {
        boolean bool = false;

        try {
            if (!(new File("/system/bin/su")).exists() && !(new File("/system/xbin/su")).exists()) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception var2) {
        }

        return bool;
    }

    private static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000L;
    }

    @SuppressLint({"MissingPermission"})
    private static boolean isEmulator(Context context) {
        try {
            if (DeviceCommonUtil.haveSelfPermission(context, "android.permission.READ_PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = tm.getDeviceId();
                if (imei != null && imei.equals("000000000000000")) {
                    return true;
                } else {
                    return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
                }
            }
        } catch (Exception var2) {
        }
        return false;
    }

    @SuppressLint({"HardwareIds"})
    private static String getAndroidId(Context context) {
        try {
            return Secure.getString(context.getApplicationContext().getContentResolver(), "android_id");
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static String getNetworkType(Context context){
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connManager) {
            return "none";
        } else {
            int networkType = getCommonNetWorkType(context);
            // 这里对应的值为20 只有依赖为android 10.0才有此属性
            if(networkType == TelephonyManager.NETWORK_TYPE_NR){
                return "5G";
            }else if (networkType == ConnectivityManager.TYPE_WIFI){
                return "WIFI";
            }else {
                NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
                if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                    NetworkInfo wifiInfo = connManager.getNetworkInfo(1);
                    if (null != wifiInfo) {
                        State state = wifiInfo.getState();
                        if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                            return Context.WIFI_SERVICE;
                        }
                    }

                    NetworkInfo networkInfo = connManager.getNetworkInfo(0);
                    if (null != networkInfo) {
                        State state = networkInfo.getState();
                        String strSubTypeName = networkInfo.getSubtypeName();
                        if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                            switch (activeNetInfo.getSubtype()) {
                                case 1:
                                case 2:
                                case 4:
                                case 7:
                                case 11:
                                    return "2G";
                                case 3:
                                case 5:
                                case 6:
                                case 8:
                                case 9:
                                case 10:
                                case 12:
                                case 14:
                                case 15:
                                    return "3G";
                                case 13:
                                    return "4G";
                                default:
                                    if (!strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !strSubTypeName.equalsIgnoreCase("WCDMA") && !strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                        return "other";
                                    }
                                    return "3G";
                            }
                        }
                    }
                }
            }
        }
        return "none";
    }

    /**
     * get the network type
     *
     * @param ctx Context
     * @return networktype
     */
    public static int getCommonNetWorkType(Context ctx) {
        int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            int defaultDataSubId = getSubId();
            if (defaultDataSubId == -1) {
                networkType = tm.getNetworkType();
            } else {
                try {
                    Method dataNetworkType = TelephonyManager.class
                            .getDeclaredMethod("getDataNetworkType", new Class[]{int.class});
                    dataNetworkType.setAccessible(true);
                    networkType = (int) dataNetworkType.invoke(tm, defaultDataSubId);
                } catch (Throwable t) {
                    networkType = tm.getNetworkType();
                }
            }
        } catch (Throwable t) {
            // do nothing
        }
        if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            networkType = adjustNetworkType(ctx, networkType);
        }
        return networkType;
    }

    /**
     * get the 5G network type
     *
     * @param ctx Context
     * @param networkTypeFromSys this method can be call only when networkTypeFromSys = 13(LET)
     * @return correct network type
     */
    public static final int NETWORK_TYPE_NR = 20;
    public static final int SDK_VERSION_Q = 29;
    private static int adjustNetworkType(Context ctx, int networkTypeFromSys) {
        int networkType = networkTypeFromSys;
        if (VERSION.SDK_INT >= SDK_VERSION_Q
                && ctx.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                TelephonyManager tm = (TelephonyManager) ctx
                        .getSystemService(Context.TELEPHONY_SERVICE);
                ServiceState ss;
                int defaultDataSubId = getSubId();
                if (defaultDataSubId == -1) {
                    ss = tm.getServiceState();
                } else {
                    try {
                        Class<TelephonyManager> infTm = TelephonyManager.class;
                        Method method = infTm
                                .getDeclaredMethod("getServiceStateForSubscriber",
                                        new Class[]{int.class});
                        method.setAccessible(true);
                        ss = (ServiceState) method.invoke(tm, defaultDataSubId);
                    } catch (Throwable t) {
                        ss = tm.getServiceState();
                    }
                }
                if (ss != null && isServiceStateFiveGAvailable(ss.toString())) {
                    networkType = NETWORK_TYPE_NR;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return networkType;
    }

    /**
     * get data sub id
     *
     * @return subId
     */
    private static int getSubId() {
        int defaultDataSubId = -1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            defaultDataSubId = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        return defaultDataSubId;
    }

    /**
     * check the service state str is 5G
     *
     * @param ss services state str
     * @return true if is 5G
     */
    private static boolean isServiceStateFiveGAvailable(String ss) {
        boolean available = false;
        if (!TextUtils.isEmpty(ss)
                && (ss.contains("nrState=NOT_RESTRICTED")
                || ss.contains("nrState=CONNECTED"))) {
            available = true;
        }
        return available;
    }

    private static String getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connManager) {
            return "none";
        } else {
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                NetworkInfo wifiInfo = connManager.getNetworkInfo(1);
                if (null != wifiInfo) {
                    State state = wifiInfo.getState();
                    if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                        return Context.WIFI_SERVICE;
                    }
                }

                NetworkInfo networkInfo = connManager.getNetworkInfo(0);
                if (null != networkInfo) {
                    State state = networkInfo.getState();
                    String strSubTypeName = networkInfo.getSubtypeName();
                    if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                        switch(activeNetInfo.getSubtype()) {
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                        case 11:
                            return "2G";
                        case 3:
                        case 5:
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                            return "3G";
                        case 13:
                            return "4G";
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return  "5G";
                        default:
                            if (!strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !strSubTypeName.equalsIgnoreCase("WCDMA") && !strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return "other";
                            }

                            return "3G";
                        }
                    }
                }

                return "none";
            } else {
                return "none";
            }
        }
    }

    private static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, 0);
        return strTz;
    }

    private static String getDriverBrand() {
        try {
            return Build.BRAND;
        } catch (Exception var1) {
            return "";
        }
    }

    public static String getDriverTime() {
        try {
            long l = Build.TIME;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(l);
            stringBuilder.append("");
            return stringBuilder.toString();
        } catch (Exception var3) {
            return "";
        }
    }

    private static String getDriverSDKVersion() {
        try {
            return VERSION.SDK_INT + "";
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getDriverModel() {
        try {
            return Build.MODEL;
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getDriverOsVersion() {
        try {
            return VERSION.RELEASE;
        } catch (Exception var1) {
            return "";
        }
    }

    public static String getBoard() {
        try {
            return Build.BOARD;
        } catch (Exception var1) {
            return "";
        }
    }

    private static int getCPUCores()
    {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter
        {
            @Override
            public boolean accept(File pathname)
            {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName()))
                {
                    return true;
                }
                return false;
            }
        }

        try
        {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e)
        {
            // Default to return 1 core
            return 1;
        }
    }

    private static String getSerialNumber() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            return (String)clazz.getMethod("get", String.class).invoke(clazz, "ro.serialno");
        } catch (Exception var1) {
            return "";
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static long getElapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    public static long getUpdateMills() {
        return SystemClock.uptimeMillis();
    }

    public static boolean isUsingVPN() {
        if (VERSION.SDK_INT > 14) {
            String defaultHost = Proxy.getDefaultHost();
            return !TextUtils.isEmpty(defaultHost);
        } else {
            return false;
        }
    }

    public static boolean isOpenUSBDebug(Context context) {
        return Secure.getInt(context.getContentResolver(), "adb_enabled", 0) > 0;
    }

    public static boolean isUsingProxyPort() {
        if (VERSION.SDK_INT > 14) {
            int defaultPort = Proxy.getDefaultPort();
            return defaultPort != -1;
        } else {
            return false;
        }
    }

    public static JSONArray getSensorList(Context context) {
        JSONArray jsonArray = new JSONArray();
        SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        try {
            List<Sensor> sensors = sensorManager.getSensorList(-1);
            Iterator var4 = sensors.iterator();

            while(var4.hasNext()) {
                Sensor item = (Sensor)var4.next();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", String.valueOf(item.getType()));
                jsonObject.put("name", item.getName());
                jsonObject.put("version", String.valueOf(item.getVersion()));
                jsonObject.put("vendor", item.getVendor());
                jsonObject.put("maxRange", String.valueOf(item.getMaximumRange()));
                jsonObject.put("minDelay", String.valueOf(item.getMinDelay()));
                jsonObject.put("power", String.valueOf(item.getPower()));
                jsonObject.put("resolution", String.valueOf(item.getResolution()));
                jsonArray.add(jsonObject);
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return jsonArray;
    }
}
