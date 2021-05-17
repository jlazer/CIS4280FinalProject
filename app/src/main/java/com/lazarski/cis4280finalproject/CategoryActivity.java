package com.lazarski.cis4280finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.graphics.Color;

import java.util.List;

public class CategoryActivity extends AppCompatActivity
    implements CategoryDialogFragment.OnCategoryEnteredListener {

        private NoteDatabase mNoteDb;
        private CategoryAdapter mCategoryAdapter;
        private RecyclerView mRecyclerView;
        private int[] mCategoryColors;

        private Category mSelectedCategory;
        private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
        private ActionMode mActionMode = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_category);


            // Singleton
            mNoteDb = NoteDatabase.getInstance(getApplicationContext());

            mCategoryColors = getResources().getIntArray(R.array.categoryColors);

            // Create 2 grid layout columns
            mRecyclerView = findViewById(R.id.categoryRecyclerView);
            RecyclerView.LayoutManager gridLayoutManager =
                    new GridLayoutManager(getApplicationContext(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setBackgroundColor(Color.DKGRAY);

            // Show the available categories
            mCategoryAdapter = new CategoryAdapter(loadCategories());
            mRecyclerView.setAdapter(mCategoryAdapter);
        }

        public void onCategoryEntered(String categoryText) {
            if (categoryText.length() > 0) {
                Category category = new Category(categoryText);
                long categoryId = mNoteDb.categoryDao().insertCategory(category);
                category.setId(categoryId);

                //mNoteDb.addCategory(category);

                // TODO: add subject to RecyclerView
                mCategoryAdapter.addCategory(category);
                Toast.makeText(this, "Added " + categoryText, Toast.LENGTH_SHORT).show();
            }
        }

        public void addCategoryClick(View view) {
            FragmentManager manager = getSupportFragmentManager();
            CategoryDialogFragment dialog = new CategoryDialogFragment();
            dialog.show(manager, "categoryDialog");
        }

        private List<Category> loadCategories() {
            return mNoteDb.categoryDao().getCategoriesNewerFirst();
        }

        private class CategoryHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private Category mCategory;
            private TextView mTextView;

            public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.recycler_view_items, parent, false));
                itemView.setOnClickListener(this);
                mTextView = itemView.findViewById(R.id.categoryTextView);
                itemView.setOnLongClickListener(this);
            }

            public void bind(Category category, int position) {
                mCategory = category;
                mTextView.setText(category.getText());

                if (mSelectedCategoryPosition == position) {
                    // Make selected category stand out
                    mTextView.setBackgroundColor(Color.GRAY);
                }
                else {
                    //int colorIndex = category.getText().length() % mCategoryColors.length;
                    mTextView.setBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onClick(View view) {
                // Start QuestionActivity with the selected category
                Intent intent = new Intent(CategoryActivity.this, NoteActivity.class);
                intent.putExtra(NoteActivity.EXTRA_CATEGORY_ID, mCategory.getId());
                startActivity(intent);
            }


            @Override
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }

                mSelectedCategory = mCategory;
                mSelectedCategoryPosition = getAdapterPosition();

                // Re-bind the selected item
                mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);

                // Show the CAB
                mActionMode = CategoryActivity.this.startActionMode(mActionModeCallback);

                return true;
            }
        }

        private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Provide context menu for CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Process action item selection
                if (item.getItemId() == R.id.delete) {
                    // Delete from the database and remove from the RecyclerView
                    mNoteDb.categoryDao().deleteCategory(mSelectedCategory);
                    mCategoryAdapter.removeCategory(mSelectedCategory);
                    // Close the CAB
                    mode.finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;

                // CAB closing, need to deselect item if not deleted
                mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);
                mSelectedCategoryPosition = RecyclerView.NO_POSITION;
            }
        };

        private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

            private List<Category> mCategoryList;

            public CategoryAdapter(List<Category> categories) {
                mCategoryList = categories;
            }

            @NonNull
            @Override
            public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                return new CategoryHolder(layoutInflater, parent);
            }

            @Override
            public void onBindViewHolder(CategoryHolder holder, int position){
                holder.bind(mCategoryList.get(position), position);
            }

            @Override
            public int getItemCount() {
                return mCategoryList.size();
            }

            public void addCategory(Category category) {
                // Add the new category at the beginning of the list
                mCategoryList.add(0, category);

                // Notify the adapter that item was added to the beginning of the list
                notifyItemInserted(0);

                // Scroll to the top
                mRecyclerView.scrollToPosition(0);
            }

            public void removeCategory(Category category) {
                // Find category in the list
                int index = mCategoryList.indexOf(category);
                if (index >= 0) {
                    // Remove the category
                    mCategoryList.remove(index);

                    // Notify adapter of category removal
                    notifyItemRemoved(index);
                }
            }
        }
}