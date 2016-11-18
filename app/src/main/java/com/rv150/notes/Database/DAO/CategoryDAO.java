package com.rv150.notes.Database.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rv150.notes.Database.DBHelper;
import com.rv150.notes.Models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudnev on 18.11.2016.
 */

public class CategoryDAO {
    private DBHelper mDBHelper;

    public CategoryDAO(Context context) {
        mDBHelper = new DBHelper(context);
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
        return categories;
    }


    public List<Category> getNoteCategories (long noteId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String CATEGORY_TABLE = DBHelper.Category.TABLE_NAME;
        String CATEGORY_NAME = DBHelper.Category.COLUMN_NAME_NAME;
        String CATEGORY_ID = DBHelper.Category._ID;
        String NOTE_CATEGORY_TABLE = DBHelper.NoteCategory.TABLE_NAME;
        String NOTE_CATEGORY_CATEGORY_ID = DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID;
        String NOTE_CATEGORY_NOTE_ID = DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID;
        String query = "SELECT " + CATEGORY_TABLE + '.' + CATEGORY_NAME + ',' +
               CATEGORY_TABLE + '.' + CATEGORY_ID + " FROM " + CATEGORY_TABLE +
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
        return categories;
    }



    private Category mapCategory(Cursor cursor) {
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Category.COLUMN_NAME_NAME));
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(DBHelper.Category._ID));

        return new Category(name, id);
    }
}
