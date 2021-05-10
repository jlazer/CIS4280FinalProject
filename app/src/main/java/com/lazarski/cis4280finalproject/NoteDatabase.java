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

        // Add a few subjects and questions if database is empty
        if (categoryDao().getCategories().size() == 0) {

            // Execute code on a background thread
            runInTransaction(new Runnable() {
                @Override
                public void run() {
                    Category category = new Category("Math");
                    long categoryId = categoryDao().insertCategory(category);

                    Note note = new Note();
                    note.setText("What is 2 + 3?");
                    note.setNoteContent("2 + 3 = 5");
                    note.setCategoryId(categoryId);
                    noteDao().insertNote(note);

                    note = new Note();
                    note.setText("What is pi?");
                    note.setNoteContent("Pi is the ratio of a circle's circumference to its diameter.");
                    note.setCategoryId(categoryId);
                    noteDao().insertNote(note);

                    category = new Category("History");
                    categoryId = categoryDao().insertCategory(category);

                    note = new Note();
                    note.setText("On what date was the U.S. Declaration of Independence adopted?");
                    note.setNoteContent("July 4, 1776.");
                    note.setCategoryId(categoryId);
                    noteDao().insertNote(note);

                    category = new Category("Computing");
                    categoryId = categoryDao().insertCategory(category);
                }
            });
        }
    }
}