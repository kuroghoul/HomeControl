CREATE TABLE [users](
    [id] INTEGER PRIMARY KEY,
    [username] TEXT NOT NULL,
    [password] TEXT NOT NULL,
    [nip] TEXT NOT NULL);

CREATE TABLE [user_profiles](
    [id] INTEGER PRIMARY KEY,
    [user_id] INTEGER NOT NULL REFERENCES [users]([id]),
    [description] TEXT NOT NULL,
    [active] INTEGER);

CREATE TABLE [profile_devices](
    [id] INTEGER REFERENCES [user_profiles]([id]),
    [device_id] INTEGER REFERENCES [devices]([id]),
    [status1] INTEGER,
    [status2] INTEGER,
    [pwm1] INTEGER,
    [pwm2] INTEGER,
    [pwm3] INTEGER);

CREATE TABLE [devices](
       [id] INTEGER PRIMARY KEY,
       [description] TEXT NOT NULL,
       [category_id] INTEGER REFERENCES [categories]([id]));

CREATE TABLE [categories](
       [id] INTEGER PRIMARY KEY,
       [description] TEXT NOT NULL);