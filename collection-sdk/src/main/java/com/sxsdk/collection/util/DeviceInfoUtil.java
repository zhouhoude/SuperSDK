package com.sxsdk.collection.util;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.sxsdk.collection.SXPoolExecutor;
import com.sxsdk.collection.callback.InfoCallBack;
import com.sxsdk.collection.util.lundu.DeviceCommonUtil;
import com.sxsdk.collection.util.lundu.DeviceFileSizeUtil;
import com.sxsdk.collection.util.lundu.DeviceInfoUtils;


public class DeviceInfoUtil {
    /**
     * 获取手机安装的app列表
     */
    public static void getLunDuInfo(final Context context, final InfoCallBack callback) {
        SXPoolExecutor.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("hardware", DeviceInfoUtils.getHardWareInfo(context));
                    json.put("storage", DeviceFileSizeUtil.getStorageInfo(context));
                    json.put("general_data", DeviceInfoUtils.getGeneralData(context));
                    json.put("other_data", DeviceInfoUtils.getOtherData(context));
                    json.put("network", DeviceInfoUtils.getNetworkData(context));
                    json.put("battery_status", DeviceInfoUtils.getBatteryData(context));
                    json.put("audio_external", DeviceInfoUtils.getAudioExternalNumber(context));
                    json.put("audio_internal", DeviceInfoUtils.getAudioInternalNumber(context));
                    json.put("images_external", DeviceInfoUtils.getImagesExternalNumber(context));
                    json.put("images_internal", DeviceInfoUtils.getImagesInternalNumber(context));
                    json.put("video_external", DeviceInfoUtils.getVideoExternalNumber(context));
                    json.put("video_internal", DeviceInfoUtils.getVideoInternalNumber(context));
                    json.put("download_files", DeviceInfoUtils.getDownloadFileNumber());
                    json.put("contact_group", DeviceInfoUtils.getContactsGroupNumber(context));
                    json.put("albs", DeviceFileSizeUtil.getImagesMsg(context));
                    json.put("build_id", DeviceCommonUtil.getVersionCode(context));
                    json.put("build_name", DeviceCommonUtil.getVersionName(context));
                    json.put("package_name", DeviceCommonUtil.getPackageName(context));
                    json.put("create_time", System.currentTimeMillis());
                }catch (Exception e){

                }finally {
                    callback.result(json.toJSONString());
                }
            }
        });
    }
}
