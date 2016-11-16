package com.rv150.notes.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static class Note implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static class NoteCategory implements BaseColumns {
        public static final String TABLE_NAME = "note_category";
        public static final String COLUMN_NAME_NOTE_ID = "note_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    }



    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Notes.db";

    private static final String SQL_CREATE_NOTE_TABLE  =
            "CREATE TABLE " + Note.TABLE_NAME + " (" +
                    Note._ID + " INTEGER PRIMARY KEY," +
                    Note.COLUMN_NAME_NAME + " VARCHAR(50) NOT NULL," +
                    Note.COLUMN_NAME_CONTENT + " TEXT NOT NULL," +
                    Note.COLUMN_NAME_CREATED_AT + " INTEGER NOT NULL)";

    private static final String SQL_CREATE_CATEGORY_TABLE  =
            "CREATE TABLE " + Category.TABLE_NAME + " (" +
                    Category._ID + " INTEGER PRIMARY KEY," +
                    Category.COLUMN_NAME_NAME + " VARCHAR(50) UNIQUE NOT NULL)";

    private static final String SQL_CREATE_NOTE_CATEGORY_TABLE  =
            "CREATE TABLE " + NoteCategory.TABLE_NAME + " (" +
                    NoteCategory._ID + " INTEGER PRIMARY KEY," +
                    NoteCategory.COLUMN_NAME_NOTE_ID + " INTEGER NOT NULL," +
                    NoteCategory.COLUMN_NAME_CATEGORY_ID + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + NoteCategory.COLUMN_NAME_NOTE_ID + ") REFERENCES " +
                    Note.TABLE_NAME + "(" + Note._ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY (" + NoteCategory.COLUMN_NAME_CATEGORY_ID + ") REFERENCES " +
                    Category.TABLE_NAME + "(" + Category._ID + ") ON DELETE CASCADE)";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTE_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_NOTE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
