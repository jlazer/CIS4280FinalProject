package com.lazarski.cis4280finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM Note WHERE id = :id")
    public Note getNote(long id);

    @Query("SELECT * FROM Note WHERE categoryId = :categoryId ORDER BY id")
    public List<Note> getNotes(long categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertNote(Note note);

    @Update
    public void updateNote(Note note);

    @Delete
    public void deleteNote(Note note);
}
