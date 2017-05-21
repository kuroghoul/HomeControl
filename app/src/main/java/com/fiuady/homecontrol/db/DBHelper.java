package com.fiuady.homecontrol.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Kuro on 20/05/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mInstance=null;
    private static final String DATABASE_NAME = "inventory.db";
    private static final int SCHEMA_VERSION = 1;
    private Context context;

    public static DBHelper getDBHelper(Context ctx)
    {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {

        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        this.context = context;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        applySQLFile(db, "schema.sql");
        applySQLFile(db, "initial-data.sql");
    }

    public void applySQLFile(SQLiteDatabase db, String filename)
    {
        BufferedReader br=null;
        try{

            InputStream is = context.getAssets().open(filename);
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder statement = new StringBuilder();
            for(String line; (line = br.readLine()) != null; ){

                line = line.trim();
                if(!TextUtils.isEmpty(line) && !line.startsWith("--")){
                    statement.append(line);
                }

                if(line.endsWith(";")){
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null){
                try{
                    br.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
