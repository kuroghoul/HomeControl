package com.fiuady.homecontrol.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kuro on 20/05/2017.
 */

public class Inventory {
    private  DBHelper dbHelper;
    private SQLiteDatabase db;

    public enum LoginResponse {Success, InvalidUser, WrongPassword}

    public Inventory(Context context){

        //inventoryHelper = new InventoryHelper(context);
        dbHelper = DBHelper.getDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }







    class UserCursor extends CursorWrapper
    {
        public UserCursor(Cursor cursor) {
            super(cursor);
        }

        public User getUser()
        {
            Cursor cursor = getWrappedCursor();
            return new User (cursor.getInt(cursor.getColumnIndex(DBSchema.UsersTable.Columns.ID)),
                    cursor.getString(cursor.getColumnIndex(DBSchema.UsersTable.Columns.USER)),
                    cursor.getString(cursor.getColumnIndex(DBSchema.UsersTable.Columns.PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(DBSchema.UsersTable.Columns.NIP)));
        }
    }







    public LoginResponse attemptLogin (String username, String password)
    {

        UserCursor cursor = new UserCursor(db.query(DBSchema.UsersTable.NAME,
                null,
                DBSchema.UsersTable.Columns.USER + "= ?",
                new String[]{username},
                null, null, null));
        if(cursor.moveToNext())
        {
            User user = cursor.getUser();
            if (password.equals(user.getPassword()))
            {
                return LoginResponse.Success;
            }
            else
            {
                return LoginResponse.WrongPassword;
            }
        }

        else
        {
            return LoginResponse.InvalidUser;
        }


    }


}
