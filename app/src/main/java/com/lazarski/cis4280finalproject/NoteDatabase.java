package com.lazarski.cis4280finalproject;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class, Category.class}, version = 2)
public abstract class NoteDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "note2.db";

    private static NoteDatabase mNoteDatabase;

    // Singleton
    public static NoteDatabase getInstance(Context context) {
        if (mNoteDatabase == null) {
            mNoteDatabase = Room.databaseBuilder(context, NoteDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            mNoteDatabase.addStarterData();
        }

        return mNoteDatabase;
    }

    public abstract NoteDao noteDao();
    public abstract CategoryDao categoryDao();

    private void addStarterData() {

        // Add a few categories and notes if database is empty
        if (categoryDao().getCategories().size() == 0) {

            // Execute code on a background thread
            runInTransaction(new Runnable() {
                @Override
                public void run() {
                    Category category = new Category("Work");
                    long categoryId = categoryDao().insertCategory(category);

                    categoryId = categoryDao().insertCategory(category);
                }
            });
        }
    }
}