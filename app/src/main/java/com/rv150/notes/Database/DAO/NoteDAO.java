package com.rv150.notes.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rv150.notes.Database.DBHelper;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class NoteDAO {
    private DBHelper mDBHelper;
    private CategoryDAO mCategoryDAO;

    public NoteDAO(Context context) {
        mDBHelper = new DBHelper(context);
        mCategoryDAO = new CategoryDAO(context);
    }

    public List<Note> getAll() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.Note.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = mapNote(cursor);
            List<Category> categories = mCategoryDAO.getNoteCategories(note.getId());
            note.setCategories(categories);
            notes.add(note);
        }
        cursor.close();
        return notes;
    }


    public List<Note> getFromCategory(long categoryId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String NOTE_TABLE = DBHelper.Note.TABLE_NAME;
        String NOTE_NAME = DBHelper.Note.COLUMN_NAME_NAME;
        String NOTE_CONTENT = DBHelper.Note.COLUMN_NAME_CONTENT;
        String NOTE_CREATED_AT = DBHelper.Note.COLUMN_NAME_CREATED_AT;
        String NOTE_ID = DBHelper.Note._ID;

        String NOTE_CATEGORY_TABLE = DBHelper.NoteCategory.TABLE_NAME;
        String NOTE_CATEGORY_NOTE_ID = DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID;
        String NOTE_CATEGORY_CATEGORY_ID = DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID;

        String query = "SELECT " + NOTE_TABLE + '.' + NOTE_NAME + ',' +
                NOTE_TABLE + '.' + NOTE_CONTENT + ',' +
                NOTE_TABLE + '.' + NOTE_CREATED_AT + ',' +
                NOTE_TABLE + '.' + NOTE_ID +
                " FROM " + NOTE_TABLE +
                " JOIN " + NOTE_CATEGORY_TABLE + " ON "
                + NOTE_TABLE + '.' + NOTE_ID + '=' +
                NOTE_CATEGORY_TABLE + '.' + NOTE_CATEGORY_NOTE_ID +
                " WHERE " + NOTE_CATEGORY_CATEGORY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = mapNote(cursor);
            List<Category> categories = mCategoryDAO.getNoteCategories(note.getId());
            note.setCategories(categories);
            notes.add(note);
        }
        cursor.close();
        return notes;
    }


    public long insertNote(Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues noteValues = new ContentValues();
        noteValues.put(DBHelper.Note.COLUMN_NAME_NAME, note.getName());
        noteValues.put(DBHelper.Note.COLUMN_NAME_CONTENT, note.getContent());
        noteValues.put(DBHelper.Note.COLUMN_NAME_CREATED_AT, note.getCreatedAtInMillis());
        long noteId = db.insert(DBHelper.Note.TABLE_NAME, null, noteValues);

        // Закрепление категорий за данной заметкой
        ContentValues categoryValues = new ContentValues();
        for (Category category: note.getCategories()) {
            categoryValues.put(DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID, noteId);
            categoryValues.put(DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID, category.getId());
            db.insert(DBHelper.NoteCategory.TABLE_NAME, null, categoryValues);
        }
        return noteId;
    }


    public void deleteNote(long id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Note.TABLE_NAME, DBHelper.Note._ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.Note.COLUMN_NAME_NAME, note.getName());
        values.put(DBHelper.Note.COLUMN_NAME_CONTENT, note.getContent());
        db.update(DBHelper.Note.TABLE_NAME, values,
                DBHelper.Note._ID + " = ?", new String[]{String.valueOf(note.getId())});

    }

    public void deleteAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Note.TABLE_NAME, null, null);
    }


    private Note mapNote(Cursor cursor) {
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Note.COLUMN_NAME_NAME));

        String content = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Note.COLUMN_NAME_CONTENT));

        long createdAt = cursor.getLong(
                cursor.getColumnIndexOrThrow(DBHelper.Note.COLUMN_NAME_CREATED_AT));

        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(DBHelper.Note._ID));

        Note note = new Note(name, content, createdAt);
        note.setId(id);
        return note;
    }
}
