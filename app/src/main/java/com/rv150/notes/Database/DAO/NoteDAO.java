package com.rv150.notes.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    private static final String TAG = "NodeDAO";

    public NoteDAO(Context context) {
        mDBHelper = DBHelper.getInstance(context);
        mCategoryDAO = new CategoryDAO(context);
    }

    public List<Note> getAll() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.Note.TABLE_NAME +
                " ORDER BY " + DBHelper.Note.COLUMN_NAME_CREATED_AT;
        Cursor cursor = db.rawQuery(query, null);
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = mapNote(cursor);
            List<Category> categories = mCategoryDAO.getNoteCategories(note.getId());
            note.setCategories(categories);
            notes.add(note);
        }
        cursor.close();
        Log.i(TAG, "Get all notes OK");
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
                " WHERE " + NOTE_CATEGORY_TABLE + '.' + NOTE_CATEGORY_CATEGORY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = mapNote(cursor);
            List<Category> categories = mCategoryDAO.getNoteCategories(note.getId());
            note.setCategories(categories);
            notes.add(note);
        }
        cursor.close();
        Log.i(TAG, "Get notes from category OK");
        return notes;
    }


    public long insertNote(Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues noteValues = new ContentValues();
        noteValues.put(DBHelper.Note.COLUMN_NAME_NAME, note.getName());
        noteValues.put(DBHelper.Note.COLUMN_NAME_CONTENT, note.getContent());
        noteValues.put(DBHelper.Note.COLUMN_NAME_CREATED_AT, note.getCreatedAtInMillis());
        long noteId = db.insert(DBHelper.Note.TABLE_NAME, null, noteValues);
        setNoteCategories(noteId, note.getCategories());
        Log.i(TAG, "Note was inserted");
        return noteId;
    }

    private void setNoteCategories(long noteId, List<Category> categories) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // Удалим все предыдущие категории этой заметки
        db.delete(DBHelper.NoteCategory.TABLE_NAME, DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID +
        "=?", new String[] {String.valueOf(noteId)});

        // Добавим новые
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID, noteId);
        for (Category category: categories) {
            contentValues.put(DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID, category.getId());
            db.insert(DBHelper.NoteCategory.TABLE_NAME, null, contentValues);
        }
    }


    public void updateNote(Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.Note.COLUMN_NAME_NAME, note.getName());
        values.put(DBHelper.Note.COLUMN_NAME_CONTENT, note.getContent());
        db.update(DBHelper.Note.TABLE_NAME, values,
                DBHelper.Note._ID + " = ?", new String[]{String.valueOf(note.getId())});
        setNoteCategories(note.getId(), note.getCategories());
        Log.i(TAG, "Note was updated");
    }


    public void removeNote(long id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Note.TABLE_NAME, DBHelper.Note._ID + "=?", new String[]{String.valueOf(id)});
        Log.i(TAG, "Note was removed");
    }

    public void removeNotesWithCategory(long categoryId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String NOTE_TABLE = DBHelper.Note.TABLE_NAME;
        String NOTE_ID = DBHelper.Note._ID;

        String NOTE_CATEGORY_TABLE = DBHelper.NoteCategory.TABLE_NAME;
        String NOTE_CATEGORY_NOTE_ID = DBHelper.NoteCategory.COLUMN_NAME_NOTE_ID;
        String NOTE_CATEGORY_CATEGORY_ID = DBHelper.NoteCategory.COLUMN_NAME_CATEGORY_ID;

        String query = "DELETE FROM " + NOTE_TABLE +
                " WHERE " + NOTE_ID + " IN (SELECT " +
                NOTE_CATEGORY_NOTE_ID + " FROM " + NOTE_CATEGORY_TABLE +
                " WHERE " + NOTE_CATEGORY_CATEGORY_ID + " = ?)";
        db.execSQL(query, new String[]{String.valueOf(categoryId)});
        Log.i(TAG, "Notes with specific category were removed");
    }



    public void removeAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBHelper.Note.TABLE_NAME, null, null);
        Log.i(TAG, "All notes were removed");
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
