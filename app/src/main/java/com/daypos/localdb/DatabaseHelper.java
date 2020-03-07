package com.daypos.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "daypos_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }


    private static final String TABLE_NAME = "printer";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "printer_name";
    private static final String COLUMN_PATH_NAME = "location_path_name";
    private static final String COLUMN_PATH = "location_path";
    private static final String COLUMN_IS_PRINT = "is_print";
    private static final String COLUMN_PAPER_WIDTH = "paper_width";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PATH_NAME + " TEXT,"
                    + COLUMN_PATH + " TEXT,"
                    + COLUMN_IS_PRINT + " TEXT,"
                    + COLUMN_PAPER_WIDTH + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public void insertPinter(PrinterData printerData){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, printerData.getPrinter_name());
        values.put(COLUMN_PATH_NAME, printerData.getLocation_path_name());
        values.put(COLUMN_PATH, printerData.getLocation_path());
        values.put(COLUMN_IS_PRINT, printerData.getIs_print());
        values.put(COLUMN_PAPER_WIDTH, printerData.getPaper_width());

        db.insert(TABLE_NAME, null, values);

    }

    public ArrayList<PrinterData> getAllPrinters() {
        ArrayList<PrinterData> list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME
                + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PrinterData printerData = new PrinterData();
                printerData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                printerData.setPrinter_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                printerData.setLocation_path_name(cursor.getString(cursor.getColumnIndex(COLUMN_PATH_NAME)));
                printerData.setLocation_path(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
                printerData.setIs_print(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PRINT)));
                printerData.setPaper_width(cursor.getString(cursor.getColumnIndex(COLUMN_PAPER_WIDTH)));

                list.add(printerData);
            } while (cursor.moveToNext());
        }

        db.close();

        return list;
    }

    public void updateIsPrintColumn(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_PRINT, "0");

        db.update(TABLE_NAME, values, null, null);
    }

    public PrinterData getActivePrinter(){
        PrinterData printerData = new PrinterData();
        SQLiteDatabase db = this.getReadableDatabase();

        String where = COLUMN_IS_PRINT +" =?";
        String[] whereArg = {"1"};
        String limit = "1";
        Cursor cursor = db.query(TABLE_NAME, null, where, whereArg,
                null,null,limit);

        if (cursor.moveToFirst()){

            printerData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            printerData.setPrinter_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            printerData.setLocation_path_name(cursor.getString(cursor.getColumnIndex(COLUMN_PATH_NAME)));
            printerData.setLocation_path(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
            printerData.setIs_print(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PRINT)));
            printerData.setPaper_width(cursor.getString(cursor.getColumnIndex(COLUMN_PAPER_WIDTH)));

        }

        cursor.close();

        return printerData;
    }

    public int getActivePrinterLength(){
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_IS_PRINT +" =?";
        String[] whereArg = {"1"};
        Cursor cursor = db.query(TABLE_NAME, null, where, whereArg,
                null,null,null);
        cursor.close();

        return cursor.getCount();
    }

    public void deletePrinter(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
