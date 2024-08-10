package com.example.favphilosophies;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {Quote.class}, version = 2)
public abstract class QuoteDatabase extends RoomDatabase {
    public abstract QuoteDao quoteDao();

    private static volatile QuoteDatabase INSTANCE;


    public static QuoteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuoteDatabase.class, "quote_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE quotes ADD COLUMN isFavorite INTEGER DEFAULT 0 NOT NULL");
        }
    };
}
