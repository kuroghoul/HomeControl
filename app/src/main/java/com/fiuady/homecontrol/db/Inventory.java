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
    public enum RegisterResponse {Success, DuplicatedUser}

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



    public int getNewIdFrom(String Table)
    {

        Cursor cursor = db.query(Table, new String[]{"MAX(id) AS newId"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("newId"));
            cursor.close();
            return (id + 1);
        }
        else
        {
            return -1;
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
            cursor.close();
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

    public User searchUserByName (String username)
    {
        UserCursor cursor = new UserCursor(db.query(DBSchema.UsersTable.NAME,
                null,
                DBSchema.UsersTable.Columns.USER + "= ?",
                new String[]{username},
                null, null, null));
        if(cursor.moveToNext())
        {
            User user = cursor.getUser();
            cursor.close();
            return user;
        }
        else
        {
            return null;
        }
    }

    public RegisterResponse attemptRegister (String username, String password, String nip)
    {
        if(searchUserByName(username)!=null)
        {
            return RegisterResponse.DuplicatedUser;
        }
        else
        {
            int id = getNewIdFrom(DBSchema.UsersTable.NAME);
            db.execSQL("INSERT INTO users VALUES ("+String.valueOf(id)+", '"+username+"', '"+password+"', '"+nip+"');");
            generateDefaultUserProfile(id);
            return RegisterResponse.Success;
        }
    }

    public void generateDefaultUserProfile (int userid)
    {
        int newId = getNewIdFrom(DBSchema.UserProfilesTable.NAME);
        db.execSQL(
                "INSERT INTO user_profiles VALUES ("+String.valueOf(newId)+","+String.valueOf(userid)+",'Default',1);\n" +
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",0, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",1, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",2, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",3, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",4, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",5, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",6, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",7, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",8, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",9, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",10, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",11, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",12, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",13, 0, 0, 0, 0, 0);\n"+
                "INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",14, 0, 0, 0, 0, 0);\n");
    }

}
