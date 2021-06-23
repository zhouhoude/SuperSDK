package com.sxsdk.collection.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sxsdk.collection.callback.InfoCallBack;

import androidx.core.app.ActivityCompat;

public class SMSInfoUtil {
    /**
     * sms主要结构：
     *  _id：短信序号，如100
     *  thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
     *  address：发件人地址，即手机号，如+8613811810000
     *  person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
     *  date：日期，long型，如1256539465022，可以对日期显示格式进行设置
     *  protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
     *  read：是否阅读0未读，1已读
     *  status：短信状态-1接收，0complete,64pending,128failed
     *  type：短信类型1是接收到的，2是已发出
     *  body：短信具体内容
     *  service_center：短信服务中心号码编号，如+8613800755500
     */
    public static synchronized void smsInfoList(Context context, InfoCallBack callback) {
        JSONArray smsList = new JSONArray();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            callback.result(smsList.toJSONString());
            return;
        }
        Uri SMS_INBOX = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(SMS_INBOX, null, null, null, "_id asc");
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()){
                JSONObject entity = new JSONObject();
                entity.put("_id", cur.getLong(cur.getColumnIndex("_id")));
                entity.put("phone", cur.getString(cur.getColumnIndex("address")));
                entity.put("person", cur.getString(cur.getColumnIndex("person")));
                entity.put("content", cur.getString(cur.getColumnIndex("body")));
                entity.put("time", cur.getLong(cur.getColumnIndex("date")));
                entity.put("type", cur.getInt(cur.getColumnIndex("type")));
                entity.put("date_sent", cur.getLong(cur.getColumnIndex("date_sent")));
                entity.put("read", cur.getInt(cur.getColumnIndex("read")));
                entity.put("subject", cur.getString(cur.getColumnIndex("subject")));
                entity.put("seen", cur.getInt(cur.getColumnIndex("seen")));
                entity.put("status", cur.getInt(cur.getColumnIndex("status")));
                smsList.add(entity);
            }
        }
        callback.result(smsList.toJSONString());
    }
}
