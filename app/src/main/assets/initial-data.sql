CREATE TABLE [users](
    [id] INTEGER PRIMARY KEY,
    [user] TEXT NOT NULL,
    [password] TEXT NOT NULL);

CREATE TABLE [profiles](
    [id] INTEGER PRIMARY KEY);

CREATE TABLE [user_profiles](
    [id] INTEGER NOT NULL REFERENCES [users]([id]),
    [id] INTEGER REFERENCES [profiles]([id]),
    [description] TEXT NOT NULL);