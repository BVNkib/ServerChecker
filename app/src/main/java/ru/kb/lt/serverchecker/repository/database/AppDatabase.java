package ru.kb.lt.serverchecker.repository.database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.kb.lt.serverchecker.model.Server;

@Database(
        entities = {
                Server.class
        },
        exportSchema = false,
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServerDao serverDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "server_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
