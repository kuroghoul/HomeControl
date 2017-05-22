package com.fiuady.homecontrol.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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







    private class UserCursor extends CursorWrapper
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

    private class ProfileDeviceCursor extends CursorWrapper
    {
        public ProfileDeviceCursor(Cursor cursor) {
            super(cursor);
        }

        public ProfileDevice getProfileDevice()
        {
            Cursor cursor = getWrappedCursor();
            return new ProfileDevice (cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.ID)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.DEVICE_ID)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.STATUS1))==1,
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.STATUS2))==1,
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.PWM1)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.PWM2)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.ProfileDevicesTable.Columns.PWM3)));
        }
    }
    private class UserProfileCursor extends CursorWrapper
    {
        public UserProfileCursor(Cursor cursor) {
            super(cursor);
        }

        public UserProfile getUserProfile()
        {
            Cursor cursor = getWrappedCursor();
            return new UserProfile(cursor.getInt(cursor.getColumnIndex(DBSchema.UserProfilesTable.Columns.ID)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.UserProfilesTable.Columns.USER_ID)),
                    cursor.getString(cursor.getColumnIndex(DBSchema.UserProfilesTable.Columns.DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(DBSchema.UserProfilesTable.Columns.ACTIVE ))==1);
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
                db.rawQuery("INSERT INTO user_profiles VALUES ("+String.valueOf(newId)+","+String.valueOf(userid)+",'Default',1)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",0, 0, 0, 25, 0, 100)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",1, 0, 0, 0, 35, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",2, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",3, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",4, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",5, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",6, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",7, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",8, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",9, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",10, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",11, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",12, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",13, 0, 0, 0, 0, 0)" , null).moveToNext();
                db.rawQuery("INSERT INTO profile_devices VALUES ("+String.valueOf(newId)+",14, 0, 0, 0, 0, 0)" , null).moveToNext();



    }


    public ProfileDevice getProfileDevice (int profileId, int deviceId)
    {
        ProfileDeviceCursor cursor = new ProfileDeviceCursor(db.rawQuery(

                        "select *\n" +
                        "from profile_devices\n" +
                        "where id = "+String.valueOf(profileId)+" and device_id ="+String.valueOf(deviceId)+";"
                ,null
        ));
        if (cursor.moveToNext())
        {
            ProfileDevice profileDevice = cursor.getProfileDevice();
            cursor.close();
            return profileDevice;
        }
        else
        {
            return null;
        }
    }

    public UserProfile getActiveProfile (int userId)
    {
        UserProfileCursor cursor = new UserProfileCursor(db.rawQuery(

                "select *\n" +
                        "from user_profiles\n" +
                        "where user_id="+String.valueOf(userId)+" and active=1"
                ,null
        ));
        if(cursor.moveToNext())
        {
            UserProfile userProfile = cursor.getUserProfile();
            cursor.close();
            return userProfile;
        }
        else
        {
            return null;
        }
    }

    public void saveProfileDevice(ProfileDevice profileDevice)
    {
        Cursor cursor = db.rawQuery(

            "UPDATE profile_devices\n" +
                    "SET status1="+(profileDevice.getStatus1()?"1":"0")+", status2="+(profileDevice.getStatus2()?"1":"0")+
                    ", pwm1="+profileDevice.getPwm1()+", pwm2="+profileDevice.getPwm2()+", pwm3="+profileDevice.getPwm3()+"\n" +
                    "where id="+String.valueOf(profileDevice.getId())+ " and device_id=" + String.valueOf(profileDevice.getDevice_id()), null

        );
        cursor.moveToNext();
        cursor.close();
    }

    //public ArrayList<UserProfile> getAllUserProfiles (int userId)
    //{
//
    //}


}
