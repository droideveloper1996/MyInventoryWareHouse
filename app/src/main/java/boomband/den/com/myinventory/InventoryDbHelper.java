package boomband.den.com.myinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import boomband.den.com.myinventory.InventoryContract.InventoryEntry;

/**
 * Created by Abhishek on 27/05/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = " CREATE TABLE " + InventoryEntry.TABLE_NAME +
                " ( " + InventoryEntry._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.SUPPLIER + " TEXT NOT NULL, "
                + InventoryEntry.PICTURE + " BLOB NOT NULL, "
                + InventoryEntry.TRACK_SALE + " TEXT NOT NULL "
                + ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
