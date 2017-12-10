package com.example.joselm.yambaandroidtestjl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joselm on 8/12/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s int primary key, %s text, %s text, %s int)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.MESSAGE,
                StatusContract.Column.CREATED_AT);
        Log.d(TAG, "onCreate con SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Borramos la vieja base de datos
        db.execSQL("drop table if exists " + StatusContract.TABLE);

        // Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG, "onUpgrade");
    }
}
