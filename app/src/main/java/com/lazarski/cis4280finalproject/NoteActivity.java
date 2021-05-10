package com.lazarski.cis4280finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private final int REQUEST_CODE_NEW_NOTE = 0;
    private final int REQUEST_CODE_UPDATE_NOTE = 0;

    public static final String EXTRA_CATEGORY_ID = "com.lazarski.cis4280finalproject.category_id";

    private NoteDatabase mNoteDb;
    private long mCategoryId;
    private List<Note> mNoteList;
    private TextView mContentLabel;
    private TextView mContentText;
    private Button mContentButton;
    private TextView mNoteText;
    private int mCurrentNoteIndex;
    private ViewGroup mShowNotesLayout;
    private ViewGroup mNoNotesLayout;
    private View mNoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        // SubjectActivity should provide the subject ID of the questions to display
        Intent intent = getIntent();
        mCategoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);

        // Get all questions for this subject
        mNoteDb = NoteDatabase.getInstance(getApplicationContext());
        mNoteList = mNoteDb.noteDao().getNotes(mCategoryId);

        mNoteText = findViewById(R.id.noteText);
        mContentLabel = findViewById(R.id.contentLabel);
        mContentText = findViewById(R.id.contentText);
        mContentButton = findViewById(R.id.contentButton);
        mShowNotesLayout = findViewById(R.id.showNotesLayout);
        mShowNotesLayout.setBackgroundColor(Color.DKGRAY);
        mNoNotesLayout = findViewById(R.id.noNotesLayout);
        mNoteView = findViewById(R.id.noteView);

        mNoteView.setBackgroundColor(Color.DKGRAY);


        // Show first question
        showNote(0);
    }

    // Refactor from here down.
    @Override
    protected void onStart() {
        super.onStart();

        if (mNoteList.size() == 0) {
            updateAppBarTitle();
            displayNote(false);
        }
        else {
            displayNote(true);
            toggleContentVisibility();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //  Determine which app bar item was chosen
        if (item.getItemId() == R.id.previous) {
            showNote(mCurrentNoteIndex - 1);
            return true;
        }
        else if (item.getItemId() == R.id.next) {
            showNote(mCurrentNoteIndex + 1);
            return true;
        }
        else if (item.getItemId() == R.id.add) {
            addNote();
            return true;
        }
        else if (item.getItemId() == R.id.edit) {
            editNote();
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            deleteNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_NOTE) {
            // Get added question
            long noteId = data.getLongExtra(NoteEditActivity.EXTRA_NOTE_ID, -1);
            Note newNote = mNoteDb.noteDao().getNote(noteId);

            // Add newly created question to the question list and show it
            mNoteList.add(newNote);
            showNote(mNoteList.size() - 1);

            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_NOTE) {
            // Get updated question
            long noteId = data.getLongExtra(NoteEditActivity.EXTRA_NOTE_ID, -1);
            Note updatedNote = mNoteDb.noteDao().getNote(noteId);

            // Replace current question in question list with updated question
            Note currentNote = mNoteList.get(mCurrentNoteIndex);
            currentNote.setText(updatedNote.getText());
            currentNote.setNoteContent(updatedNote.getNoteContent());
            showNote(mCurrentNoteIndex);

            Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        }
    }

    public void addNoteButtonClick(View view) {
        addNote();
    }

    public void contentButtonClick(View view) {
        toggleContentVisibility();
    }

    private void displayNote(boolean display) {
        if (display) {
            mShowNotesLayout.setVisibility(View.VISIBLE);
            mNoNotesLayout.setVisibility(View.GONE);
        }
        else {
            mShowNotesLayout.setVisibility(View.GONE);
            mNoNotesLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateAppBarTitle() {

        // Display subject and number of questions in app bar
        Category category = mNoteDb.categoryDao().getCategory(mCategoryId);
        String title = getResources().getString(R.string.note_number,
                category.getText(), mCurrentNoteIndex + 1, mNoteList.size());
        setTitle(title);
    }

    private void addNote() {
        // TODO: Add question
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra(NoteEditActivity.EXTRA_CATEGORY_ID, mCategoryId);
        startActivityForResult(intent, REQUEST_CODE_NEW_NOTE);
    }

    private void editNote() {
        // TODO: Edit question
        if (mCurrentNoteIndex >= 0) {
            Intent intent = new Intent(this, NoteEditActivity.class);
            intent.putExtra(EXTRA_CATEGORY_ID, mCategoryId);
            long noteId = mNoteList.get(mCurrentNoteIndex).getId();
            intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, noteId);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
        }
    }

    private void deleteNote() {
        // TODO: Delete question
        if (mCurrentNoteIndex >= 0) {
            Note note = mNoteList.get(mCurrentNoteIndex);
            mNoteDb.noteDao().deleteNote(note);
            mNoteList.remove(mCurrentNoteIndex);

            if (mNoteList.size() == 0) {
                // No questions left to show
                mCurrentNoteIndex = -1;
                updateAppBarTitle();
                displayNote(false);
            }
            else {
                showNote(mCurrentNoteIndex);
            }

            Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void showNote(int noteIndex) {

        // Show question at the given index
        if (mNoteList.size() > 0) {
            if (noteIndex < 0) {
                noteIndex = mNoteList.size() - 1;
            }
            else if (noteIndex >= mNoteList.size()) {
                noteIndex = 0;
            }

            mCurrentNoteIndex = noteIndex;
            updateAppBarTitle();

            Note note = mNoteList.get(mCurrentNoteIndex);
            mNoteText.setText(note.getText());
            mContentText.setText(note.getNoteContent());
        }
        else {
            // No questions yet
            mCurrentNoteIndex = -1;
        }
    }

    private void toggleContentVisibility() {
        if (mContentText.getVisibility() == View.VISIBLE) {
            mContentButton.setVisibility(View.INVISIBLE);
            mContentButton.setText(R.string.show_content);
            mContentText.setVisibility(View.VISIBLE);
            mContentLabel.setVisibility(View.VISIBLE);
        }
        else {
            // mContentButton.setText(R.string.hide_content);
            //mContentText.setVisibility(View.VISIBLE);
            //mContentLabel.setVisibility(View.VISIBLE);
        }
    }
}