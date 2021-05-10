package com.lazarski.cis4280finalproject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id",
        childColumns = "categoryId", onDelete = CASCADE))
public class Note {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "text")
    private String mText;

    @ColumnInfo(name = "noteContent")
    private String mNoteContent;

    @ColumnInfo(name = "categoryId")
    private long mCategoryId;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getNoteContent() {
        return mNoteContent;
    }

    public void setNoteContent(String noteContent) {
        mNoteContent = noteContent;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }
}

