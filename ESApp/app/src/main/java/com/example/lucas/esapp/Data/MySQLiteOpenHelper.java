package com.example.lucas.esapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "easytrade.db";

    private SQLiteDatabase mSQLiteDB;

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mSQLiteDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MySQLiteContract.SQL_CREATE_TABLE_SEARCH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    // PUBLIC API PARA MANIPULAR O BANCO DE DADOS

    public boolean inserirBusca(String user, String search) {
        List<String> buscas = Arrays.asList(recuperarbuscas(user));
        List<String> buscasUp = new ArrayList<>();
        String searchOriginal = search;

        for(String busca : buscas)
            buscasUp.add(busca.toUpperCase());

        if (!buscasUp.contains(search.toUpperCase())) {
            this.getWritableDatabase().execSQL(
                    MySQLiteContract.SQL_INSERT_SEARCH_ENTRY,
                    new Object[] {user, searchOriginal});
            return true;
        }
        return false;
    }

    public String[] recuperarbuscas( String user) {
        Cursor cursor = this.mSQLiteDB.rawQuery(MySQLiteContract.SQL_SELECT_SEARCH, new String[]{user});
        List<String> buscas = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            do {
                int searchColumnIndex = cursor.getColumnIndex(MySQLiteContract.Search.COLUMN_SEARCH);

                String search = cursor.getString(searchColumnIndex);

                buscas.add(search);
            } while (cursor.moveToNext());

        }
        return buscas.toArray(new String[buscas.size()]);
    }

    public boolean removerBusca (String user, String search) {
        this.getWritableDatabase().execSQL(
                MySQLiteContract.SQL_DELETE_SEARCH_ENTRY,
                new Object[] {user, search});
        return true;
    }
}