package com.example.lucas.esapp.Data;

import android.provider.BaseColumns;

public class MySQLiteContract {
    public static abstract class Search implements BaseColumns {
        public static final String TABLE_NAME = "Search";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_SEARCH = "search";
    }

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String STRING_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    /**
     * CREATE TABLE Search SQL.
     */
    protected static final String SQL_CREATE_TABLE_SEARCH =
            "CREATE TABLE " + Search.TABLE_NAME + " (" +
                    Search.COLUMN_USER + STRING_TYPE  + COMMA_SEP +
                    Search.COLUMN_SEARCH + STRING_TYPE  + COMMA_SEP +
                    "PRIMARY KEY (" + Search.COLUMN_USER + COMMA_SEP + Search.COLUMN_SEARCH + "))";

    /**
     * SQL COUNT
     */
    protected static final String SQL_COUNT_SEARCH_ENTRIES =
            "SELECT count(*) FROM " + Search.TABLE_NAME;


    /**
     * SQL INSERT ENTRY
     */
    protected static final String SQL_INSERT_SEARCH_ENTRY =
            "INSERT INTO " + Search.TABLE_NAME + " (" +
                    Search.COLUMN_USER + COMMA_SEP +
                    Search.COLUMN_SEARCH + ")" +
                    " VALUES (?,?)";

    /**
     * SQL DELETE ENTRY
     */
    protected static final String SQL_DELETE_SEARCH_ENTRY =
            "DELETE FROM " + Search.TABLE_NAME + " WHERE " +
                    Search.COLUMN_USER + " = ? AND " +
                    Search.COLUMN_SEARCH + " = ?";

    protected static final String SQL_SELECT_SEARCH =
            "SELECT * FROM " + Search.TABLE_NAME + " WHERE " +
                    Search.COLUMN_USER + " = ?";
}