package com.rv150.notes.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rv150.notes.Database.DBHelper;
import com.rv150.notes.Models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudnev on 18.11.2016.
 */

public class CategoryDAO {
    private DBHelper mDBHelper;
    private static final String TAG = "CategoryDAO";

    public CategoryDAO(Context context) {
        mDBHelper = DBHelper.getInstance(context);
    }

    public List<Category> getAll() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.Category.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        List<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()) {
            Category category = mapCategory(cursor);
            categories.add(category);
        }
        cursor.close();
        Log.i(TAG, "Get all categoryies OK");
        return categories;
    }


    public List<Category> getNoteCategories (long noteId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String CATEGORY_TABLE = DBHelper.Category.TABLE_NAME;
        String CATEGORY_ID = DBHelper.Category._ID;
        String CATEGORY_NAME = DBHelper.Category.COLUMN_NAME_NAME;
        String CATEGORY_COLOR = DBHelper.Category.COLUMN_NAME_COLOR;

        String NOTE_CATEGORY_TABLE = DBHelper.NoteCategory.TABLE_NAME;
        String NOTE_CATEGORY_CATEGORY_ID = DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID;
        String NOTE_CATEGORY_NOTE_ID = DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID;

        String query = "SELECT " +
                CATEGORY_TABLE + '.' + CATEGORY_ID + ',' +
                CATEGORY_TABLE + '.' + CATEGORY_NAME + ',' +
                CATEGORY_TABLE + '.' + CATEGORY_COLOR +
                " FROM " + CATEGORY_TABLE +
                " JOIN " + NOTE_CATEGORY_TABLE + " ON " +
                CATEGORY_TABLE + '.' + CATEGORY_ID + '=' +
                NOTE_CATEGORY_TABLE + '.' + NOTE_CATEGORY_CATEGORY_ID +
                " WHERE " + NOTE_CATEGORY_NOTE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(noteId)});
        List<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()) {
            Category category = mapCategory(cursor);
            categories.add(category);
        }
        cursor.close();
        Log.i(TAG, "All note's categories OK");
        return categories;
    }

    public long insertCategory (Category category) {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.Category.COLUMN_NAME_NAME, category.getName());
            contentValues.put(DBHelper.Category.COLUMN_NAME_COLOR, category.getColor());
            Log.i(TAG, "Category was added");
            return db.insertOrThrow(DBHelper.Category.TABLE_NAME, null, contentValues);
    }


    public void updateCategory(Category category) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.Category.COLUMN_NAME_NAME, category.getName());
        values.put(DBHelper.Category.COLUMN_NAME_COLOR, category.getColor());
        db.update(DBHelper.Category.TABLE_NAME, values,
                DBHelper.Category._ID + " = ?", new String[]{String.valueOf(category.getId())});
        Log.i(TAG, "Category was updated");
    }


    public void removeCategory(long id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Category.TABLE_NAME, DBHelper.Category._ID + "=?", new String[]{String.valueOf(id)});
        Log.i(TAG, "Category was removed");
    }



    public void removeAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Category.TABLE_NAME, null, null);
        Log.i(TAG, "All categoryies were removed");
    }



    private Category mapCategory(Cursor cursor) {
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Category.COLUMN_NAME_NAME));

        int color = cursor.getInt(
                cursor.getColumnIndexOrThrow(DBHelper.Category.COLUMN_NAME_COLOR));

        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(DBHelper.Category._ID));

        return new Category(name, id, color);
    }
}
