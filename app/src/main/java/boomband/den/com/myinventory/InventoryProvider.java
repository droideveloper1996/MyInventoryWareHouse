package boomband.den.com.myinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import boomband.den.com.myinventory.InventoryContract.InventoryEntry;

/**
 * Created by Abhishek on 27/05/2017.
 */

public class InventoryProvider extends ContentProvider {


    public static final UriMatcher sUriMatcher = getMatch();
    public static final int MATCH_WITHOUT_ID = 100;
    public static final int MATCH_WITH_ID = 101;

    private static UriMatcher getMatch() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


        uriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH, MATCH_WITHOUT_ID);
        uriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH + "/#", MATCH_WITH_ID);


        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = new InventoryDbHelper(getContext()).getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match) {
            case MATCH_WITHOUT_ID:
                cursor = database.query(InventoryEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case MATCH_WITH_ID:
                String id = (uri.getPathSegments().get(1));
                String sele = "_id=?";
                String[] args = new String[]{id};
                cursor = database.query(InventoryEntry.TABLE_NAME, null, sele, args, null, null, null);
                break;

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {


        InventoryDbHelper inventory = new InventoryDbHelper(getContext());
        SQLiteDatabase db = inventory.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long row;
        Uri appendedUri = null;
        /*

        Sanity Checking...

         */
        String product_name = values.getAsString(InventoryEntry.PRODUCT_NAME);
        String product_price = values.getAsString(InventoryEntry.PRICE);
        String product_supplier = values.getAsString(InventoryEntry.SUPPLIER);
        byte[] product_image = values.getAsByteArray(InventoryEntry.PICTURE);
        String product_quantity = values.getAsString(InventoryEntry.QUANTITY);
        String product_sale = values.getAsString(InventoryEntry.TRACK_SALE);

        if (TextUtils.isEmpty(product_name) || TextUtils.isEmpty(product_price) || TextUtils.isEmpty(product_supplier)
                || TextUtils.isEmpty(product_quantity) || TextUtils.isEmpty(product_sale)) {
            Toast.makeText(getContext(), "Some Fields are empty", Toast.LENGTH_SHORT).show();
            return uri;
        }
        if (product_image == null || product_image.length == 0) {
            return uri;
        }

        switch (match) {
            case MATCH_WITHOUT_ID:
                row = db.insert(InventoryEntry.TABLE_NAME, null, values);
                if (row > 0) {
                    appendedUri = ContentUris.withAppendedId(uri, row);
                } else {
                    throw new android.database.SQLException("Failed to insert" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return appendedUri;

    }


    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = new InventoryDbHelper(getContext()).getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted = 0;
        switch (match) {
            case MATCH_WITHOUT_ID:
                tasksDeleted = database.delete(InventoryEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            case MATCH_WITH_ID:
                String id=uri.getPathSegments().get(1);
                String where="_id=?";
                String arg[]=new String[]{id};
                tasksDeleted=database.delete(InventoryEntry.TABLE_NAME,where,arg);
                break;
        }
        if (tasksDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = new InventoryDbHelper(getContext()).getWritableDatabase();
        int match = sUriMatcher.match(uri);
        String  id=(uri.getPathSegments().get(1));
        String where="_id=?";
        String args[]=new String[]{id};
        int row;
        switch (match) {
            case MATCH_WITH_ID:

                row = database.update(InventoryEntry.TABLE_NAME, values, where, args);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return row;

    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        SQLiteDatabase db = new InventoryDbHelper(getContext()).getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int row = 0;
        switch (match) {
            case MATCH_WITHOUT_ID:


                try {

                    db.beginTransaction();

                    for (ContentValues value : values
                            ) {

                        long _id = db.insert(InventoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            ++row;
                        }
                    }
                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }
                if (row > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return row;
            default:
                return super.bulkInsert(uri, values);

        }

    }

}
