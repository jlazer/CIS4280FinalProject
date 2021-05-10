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

import java.util.List;

public class CategoryActivity extends AppCompatActivity
    implements CategoryDialogFragment.OnCategoryEnteredListener {

        private NoteDatabase mNoteDb;
        private CategoryAdapter mCategoryAdapter;
        private RecyclerView mRecyclerView;
        private int[] mCategoryColors;

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

            // Show the available subjects
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
                implements View.OnClickListener {

            private Category mCategory;
            private TextView mTextView;

            public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.recycler_view_items, parent, false));
                itemView.setOnClickListener(this);
                mTextView = itemView.findViewById(R.id.categoryTextView);
            }

            public void bind(Category category, int position) {
                mCategory = category;
                mTextView.setText(category.getText());

                // Make the background color dependent on the length of the subject string
                int colorIndex = category.getText().length() % mCategoryColors.length;
                mTextView.setBackgroundColor(mCategoryColors[colorIndex]);
            }

            @Override
            public void onClick(View view) {
                // Start QuestionActivity with the selected subject
                Intent intent = new Intent(CategoryActivity.this, NoteActivity.class);
                intent.putExtra(NoteActivity.EXTRA_CATEGORY_ID, mCategory.getId());
                startActivity(intent);
            }
        }

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
        }
}