package com.vicom.frontend.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class DBManger {

    private Context context;
    private static DBManger instance;
    // 操作表的对象，进行增删改查
    private static SQLiteDatabase writableDatabase;

    private DBManger(Context context) {
        this.context = context;
        DBHelper dbHelper = new DBHelper(context, 1);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    public static DBManger getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManger.class) {
                if (instance == null) {
                    instance = new DBManger(context);
                }
            }
        }
        return instance;
    }

    public void addCookie(long uid, String cookie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("cookie", cookie);

        writableDatabase.insert("cookie", null, contentValues);
    }

    public void deleteCookie(long uid) {
        writableDatabase.delete("cookie", "uid=?", new String[]{uid + ""});
    }

    public Map<String, Object> selectCookie() {

        Cursor cursor = writableDatabase.query("cookie", null, null, null, null, null, null, null);

        cursor.moveToFirst();
        long uid = -1;
        String cookie = "null";

        if (cursor.getCount() > 0) {
            uid = cursor.getLong(cursor.getColumnIndex("uid"));
            cookie = cursor.getString(cursor.getColumnIndex("cookie"));
        }

        Map<String, Object> result = new HashMap<String, Object>();

        result.put("uid", uid);
        result.put("cookie", cookie);

        return result;

    }
}

