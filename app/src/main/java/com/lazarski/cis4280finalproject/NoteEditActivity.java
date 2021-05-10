package com.lazarski.cis4280finalproject;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NoteEditActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "com.lazarski.cis4280finalproject.note_id";
    public static final String EXTRA_CATEGORY_ID = "com.lazarski.cis4280finalproject.category_id";

    private EditText mNoteText;
    private EditText mContentText;

    private NoteDatabase mNoteDb;
    private long mNoteId;
    private Note mNote;
    private View mEditNoteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        mNoteText = findViewById(R.id.noteText);
        mContentText = findViewById(R.id.contentText);
        mEditNoteView = findViewById(R.id.editNoteView);
        mEditNoteView.setBackgroundColor(Color.DKGRAY);

        mNoteDb = NoteDatabase.getInstance(getApplicationContext());

        // Get question ID from QuestionActivity
        Intent intent = getIntent();
        mNoteId = intent.getLongExtra(EXTRA_NOTE_ID, -1);

        if (mNoteId == -1) {
            // Add new question
            mNote = new Note();
            setTitle(R.string.add_note);
        }
        else {
            // Update existing question
            mNote = mNoteDb.noteDao().getNote(mNoteId);
            mNoteText.setText(mNote.getText());
            mContentText.setText(mNote.getNoteContent());
            setTitle(R.string.update_note);
        }

        long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
        mNote.setCategoryId(categoryId);
    }

    public void saveButtonClick(View view) {

        mNote.setText(mNoteText.getText().toString());
        mNote.setNoteContent(mContentText.getText().toString());

        if (mNoteId == -1) {
            // New question
            long newId = mNoteDb.noteDao().insertNote(mNote);
            mNote.setId(newId);
        }
        else {
            // Existing question
            mNoteDb.noteDao().updateNote(mNote);
        }

        // Send back question ID
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOTE_ID, mNote.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}