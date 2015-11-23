package cn.hachin.t9finder.Engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanghanqing on 15/11/23.
 */
public class ContactInfoProvider {
    /**
     * 获取联系人信息
     *
     * @return
     * @throws Exception
     */
    public static Map<String, String> GetContact(Context context) throws Exception {
        Map<String, String> map = new HashMap<>();

        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getContentResolver();
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

}
