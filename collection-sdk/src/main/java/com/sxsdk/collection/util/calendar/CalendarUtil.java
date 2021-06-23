package com.sxsdk.collection.util.calendar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Calendar;

import androidx.core.app.ActivityCompat;


public class CalendarUtil {

    private static final String TAG = "SystemCalendarHandler";

    /**
     * 检查是否存在日历账户
     *
     * @param context
     * @return
     */
    public static long checkCalendarAccounts(Context context) {

        Cursor userCursor = context.getContentResolver()
                .query(CalendarConstantData.CALENDAR_URI, null, null, null, CalendarContract.Calendars._ID + " ASC ");
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 查询日历事件
     *
     * @param context
     * @return
     */
    public static JSONArray queryCalendarEvent(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return new JSONArray();
        }
        String[] REMINDERS_COLUMNS = new String[]{
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.EVENT_ID,
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.METHOD,
        };
//        Cursor cursor = context.getContentResolver().query(builder.build(), null, null, null, CalendarContract.Instances.BEGIN + " ASC ");
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), null,
                null, null, null);
        JSONArray jsonArray = new JSONArray();
        try {
            while (cursor.moveToNext()){
                long event_id = cursor.getLong(cursor.getColumnIndex("_id"));
                String event_title = cursor.getString(cursor.getColumnIndex("title"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long start_time = cursor.getLong(cursor.getColumnIndex("dtstart"));
                long end_time = cursor.getLong(cursor.getColumnIndex("dtend"));
                JSONObject eventJsonObject = new JSONObject();
                eventJsonObject.put("event_id", event_id);
                eventJsonObject.put("event_title", event_title);
                eventJsonObject.put("description", description);
                eventJsonObject.put("start_time", start_time);
                eventJsonObject.put("end_time", end_time);
                Cursor remindersCursor = context.getContentResolver().query(
                        CalendarContract.Reminders.CONTENT_URI,
                        REMINDERS_COLUMNS,
                        CalendarContract.Reminders.EVENT_ID + "=?",
                        new String[]{event_id + ""},
                        null);
                JSONArray reminders = new JSONArray();
                while (remindersCursor.moveToNext()) {
                    JSONObject reminder = new JSONObject();
                    String rid = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders._ID));
                    String event_Id = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID));
                    String minutes = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.MINUTES));
                    String method = remindersCursor.getString(remindersCursor.getColumnIndex(CalendarContract.Reminders.METHOD));
                    reminder.put("reminder_id", rid);
                    reminder.put("eventId", event_Id);
                    reminder.put("minutes", minutes);
                    reminder.put("method", method);
                    reminders.add(reminder);
                }
                eventJsonObject.put("reminders", reminders.toJSONString());
                remindersCursor.close();
                jsonArray.add(eventJsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return jsonArray;
    }

    /**
     * 处理全天事件开始时间
     * 系统全天时间举例：
     * 创建时间为5月19日
     * 开始时间变成了5月19日8点（猜测是东八区）
     * 结束时间变成了5月20日8点
     * 由于这种情况造成事件列表19日和20日都出现了这个事件，这是不合理的。
     * 所以对系统全天事件的开始时间和结束时间做处理
     * @param timeSecond
     */
    private static long dealAllDayStartTime(long timeSecond){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeSecond * 1000);
        return CalendarTools.getStartTimeOfDaySecond(calendar);
    }

    /**
     * 处理全天事件结束时间
     * 系统全天时间举例：
     * 创建时间为5月19日
     * 开始时间变成了5月19日8点（猜测是东八区）
     * 结束时间变成了5月20日8点
     * 由于这种情况造成事件列表19日和20日都出现了这个事件，这是不合理的。
     * 所以对系统全天事件的开始时间和结束时间做处理
     * @param timeSecond
     */
    private static long dealAllDaysEndTime(long timeSecond){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeSecond * 1000);
        return CalendarTools.getEndTimeOfLastDaySecond(calendar);
}

}
