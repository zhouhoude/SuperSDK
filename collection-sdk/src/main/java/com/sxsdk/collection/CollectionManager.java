package com.sxsdk.collection;

import android.content.Context;

import com.sxsdk.collection.callback.InfoCallBack;
import com.sxsdk.collection.util.AppInfoUtil;
import com.sxsdk.collection.util.ContactsUtil;
import com.sxsdk.collection.util.LoctionUtil;
import com.sxsdk.collection.util.SMSInfoUtil;
import com.sxsdk.collection.util.calendar.CalendarUtil;

public class CollectionManager {
    public static void getLocationInfo(Context context, InfoCallBack callBack){
        LoctionUtil.getLocation(context, callBack);
    }

    public static void getAppInfoList(final Context context, final InfoCallBack callback) {
        SXPoolExecutor.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppInfoUtil.appInfoList(context, callback);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getSmsInfoList(final Context context, final InfoCallBack callback) {
        SXPoolExecutor.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SMSInfoUtil.smsInfoList(context, callback);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getAllContacts(final Context context, final InfoCallBack callback) {
        SXPoolExecutor.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.result(new ContactsUtil(context).getContactsInfo().toJSONString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getCalendarInfo(final Context context, final InfoCallBack callback) {
        SXPoolExecutor.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.result(CalendarUtil.queryCalendarEvent(context).toJSONString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
