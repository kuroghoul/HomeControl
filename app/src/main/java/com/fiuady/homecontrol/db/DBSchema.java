package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 20/05/2017.
 */

public final class DBSchema {

    public static final class CategoriesTable
    {
        public static final String NAME = "categories";
        public static final class Columns
        {
            public static final String ID = "id";
            public static final String DESCRIPTION ="description";
        }
    }


    public static final class DevicesTable
    {
        public static final String NAME = "devices";
        public static final class Columns
        {
            public static final String ID = "id";
            public static final String DESCRIPTION ="description";
            public static final String CATEGORY_ID ="category_id";
        }
    }

    public static final class ProfileDevicesTable
    {
        public static final String NAME = "profile_devices";
        public static final class Columns
        {
            public static final String ID = "id";
            public static final String DEVICE_ID = "device_id";
            public static final String STATUS1 ="status1";
            public static final String STATUS2 ="status2";
            public static final String PWM1 ="pwm1";
            public static final String PWM2 ="pwm2";
            public static final String PWM3 ="pwm3";
        }
    }

    public static final class UsersTable
    {
        public static final String NAME = "users";
        public static final class Columns
        {
            public static final String ID = "id";
            public static final String USER ="username";
            public static final String PASSWORD ="password";
            public static final String NIP = "nip";
        }
    }

    public static final class UserProfilesTable
    {
        public static final String NAME = "user_profiles";
        public static final class Columns
        {
            public static final String ID = "id";
            public static final String USER_ID ="user_id";
            public static final String DESCRIPTION ="description";
            public static final String ACTIVE = "active";
        }
    }

}
