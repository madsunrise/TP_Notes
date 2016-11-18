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
    private DBHelper dbHelper;
    private NoteDAO noteDAO;

    public CategoryDAO(Context context) {
        dbHelper = new DBHelper(context);
        noteDAO = new NoteDAO(context);
    }

    public List<Category> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DBHelper.Category.COLUMN_NAME_NAME + ',' +
                DBHelper.Category._ID + " FROM " + DBHelper.Category.TABLE_NAME +
                " JOIN " + DBHelper.NoteCategory.TABLE_NAME + " ON " +
                DBHelper.Category._ID + '=' + DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID +
                " WHERE " + DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID + " = ?";

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
