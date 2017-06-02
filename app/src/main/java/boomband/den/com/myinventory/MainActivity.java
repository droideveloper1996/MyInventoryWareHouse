package boomband.den.com.myinventory;


import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import boomband.den.com.myinventory.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Toast mToast;
    private int LOADER_CONSTANT = 100;
    InventoryAdapter mCurorAdapter;
    private ImageView mEmpeyImageView;
    private TextView mEmptyTextView;
    private RelativeLayout empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);
        mEmpeyImageView=(ImageView)findViewById(R.id.emptyImageView);
        mEmptyTextView=(TextView)findViewById(R.id.emptyTextView);
        SQLiteDatabase database = inventoryDbHelper.getReadableDatabase();
        ListView listView = (ListView) findViewById(R.id.listView);
        empty=(RelativeLayout)findViewById(R.id.emptyView);
        mCurorAdapter=new InventoryAdapter(this,null);
        listView.setAdapter(mCurorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                i.setData(uri);
                startActivity(i);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });

        getLoaderManager().initLoader(LOADER_CONSTANT, null,this);
       listView.setEmptyView(empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                makeToast("Deleting");
                delete();
               // query();
                break;
            case R.id.action_insert:
                insert();
               // query();
                makeToast("Inserted Mock Data");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeToast(String message) {
        Context context = getApplicationContext();
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void insert() {

        byte[] arr = getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.icecream));
        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        ContentValues contentValues3 = new ContentValues();
        ContentValues contentValues4 = new ContentValues();
        ContentValues contentValues5 = new ContentValues();


        contentValues1.put(InventoryEntry.PRODUCT_NAME, "IceCream");
        contentValues1.put(InventoryEntry.PRICE, "25");
        contentValues1.put(InventoryEntry.QUANTITY, "20");
        contentValues1.put(InventoryEntry.SUPPLIER, "Caterlink Supplies Pvt Ltd");
        contentValues1.put(InventoryEntry.PICTURE, arr);
        contentValues1.put(InventoryEntry.TRACK_SALE, "25");

        contentValues2.put(InventoryEntry.PRODUCT_NAME, "CupCake");
        contentValues2.put(InventoryEntry.PRICE, "30");
        contentValues2.put(InventoryEntry.QUANTITY, "40");
        contentValues2.put(InventoryEntry.SUPPLIER, "Caterlink Supplies Pvt Ltd");
        contentValues2.put(InventoryEntry.PICTURE, getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.cupcake)));
        contentValues2.put(InventoryEntry.TRACK_SALE, "8");

        contentValues3.put(InventoryEntry.PRODUCT_NAME, "Cake");
        contentValues3.put(InventoryEntry.PRICE, "10");
        contentValues3.put(InventoryEntry.QUANTITY, "10");
        contentValues3.put(InventoryEntry.SUPPLIER, "Delight Supplies Pvt Ltd");
        contentValues3.put(InventoryEntry.PICTURE, getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.cake)));
        contentValues3.put(InventoryEntry.TRACK_SALE, "8");

        contentValues4.put(InventoryEntry.PRODUCT_NAME, "Candy");
        contentValues4.put(InventoryEntry.PRICE, "1");
        contentValues4.put(InventoryEntry.QUANTITY, "100");
        contentValues4.put(InventoryEntry.SUPPLIER, "Farm Supplies Pvt Ltd");
        contentValues4.put(InventoryEntry.PICTURE, getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.candy)));
        contentValues4.put(InventoryEntry.TRACK_SALE, "65");

        contentValues5.put(InventoryEntry.PRODUCT_NAME, "Chocolates");
        contentValues5.put(InventoryEntry.PRICE, "5");
        contentValues5.put(InventoryEntry.QUANTITY, "18");
        contentValues5.put(InventoryEntry.SUPPLIER, "Farm Supplies Pvt Ltd");
        contentValues5.put(InventoryEntry.PICTURE, getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.chocolate)));
        contentValues5.put(InventoryEntry.TRACK_SALE, "80");

        ContentValues[] values = new ContentValues[]{contentValues1, contentValues2, contentValues3, contentValues4, contentValues5};

        ContentResolver resolver = getContentResolver();
        int row = resolver.bulkInsert(InventoryEntry.CONTENT_URI, values);
        if (row > 0) {
            makeToast("Inseted " + row + "rows");
        }

    }

    public void query() {
        ContentResolver contentResolver = getContentResolver();
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                Cursor cursor = contentResolver.query(InventoryEntry.CONTENT_URI, null, null, null, null, null);
                ListView listView = (ListView) findViewById(R.id.listView);

                InventoryAdapter inventoryAdapter = new InventoryAdapter(this, cursor);
                listView.setAdapter(inventoryAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {

        ContentResolver resolver = getContentResolver();
        int row = resolver.delete(InventoryEntry.CONTENT_URI, null, null);
        if (row > 0) {
            makeToast("deleted " + row + " rows");

        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("Called","LoaderManager");
        return new CursorLoader(this, InventoryEntry.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        System.out.println("called onLoadFinished()");
        mCurorAdapter.swapCursor(data);
        System.out.println(data.getCount());

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCurorAdapter.swapCursor(null);
    }

}

