package boomband.den.com.myinventory;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import boomband.den.com.myinventory.InventoryContract.InventoryEntry;

import static boomband.den.com.myinventory.R.id.price;
import static boomband.den.com.myinventory.R.id.quantity;
import static boomband.den.com.myinventory.R.id.sale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSale;
    private EditText mSupplier;
    private Button mImageButton;
    private Button mIncrement;
    Bitmap bitmap;
    private Button mDecrement;
    private ImageView mImagePreview;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int LOADER_MANGER_ID = 1;
    private Uri mUri;
    String product_quantity;
    private int mQuan;
    private TextView orderMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mIncrement = (Button) findViewById(R.id.increment);
        mDecrement = (Button) findViewById(R.id.decrement);
        bitmap = null;
        Intent i = getIntent();
        Uri uri = i.getData();
        if (uri == null) {
            mUri = null;
            mQuan = 0;
            setTitle("Insert Product");
        } else {
            setTitle("Edit Product");
            mUri = uri;
            getSupportLoaderManager().initLoader(LOADER_MANGER_ID, null, this);

        }

        mPrice = (EditText) findViewById(price);
        mProductName = (EditText) findViewById(R.id.name);
        mSale = (EditText) findViewById(sale);
        mQuantity = (EditText) findViewById(quantity);
        mSupplier = (EditText) findViewById(R.id.supplier);
        mImageButton = (Button) findViewById(R.id.addImage);
        mImagePreview = (ImageView) findViewById(R.id.preview);

        mImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();

            }
        });

        mIncrement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                incre();
            }
        });
        mDecrement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decre();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderMore = (TextView) findViewById(R.id.orderMore);
        orderMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });

    }

    private void updateData() {
        // System.out.println(mUri);
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.PRODUCT_NAME, mProductName.getText().toString().trim());
        values.put(InventoryEntry.PRICE, mPrice.getText().toString().trim());
        values.put(InventoryEntry.QUANTITY, mQuantity.getText().toString().trim());
        values.put(InventoryEntry.SUPPLIER, mSupplier.getText().toString().trim());
        int sale = Integer.parseInt(mQuantity.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantity.getText().toString().trim());
        byte arr[] = getBytes(bitmap);
        values.put(InventoryEntry.PICTURE, arr);
        values.put(InventoryEntry.TRACK_SALE, mSale.getText().toString().trim());
        if (TextUtils.isEmpty(mProductName.getText().toString().trim()) || TextUtils.isEmpty(mPrice.getText().toString().trim()) ||
                TextUtils.isEmpty(mQuantity.getText().toString().trim()) || TextUtils.isEmpty(mSupplier.getText().toString().trim()) || TextUtils.isEmpty(mSale.getText().toString().trim())

                ) {
            Toast.makeText(this, "Updated  Failed, Enter all Details",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (price < 0 || sale < 0 || quantity < 0) {
            Toast.makeText(this, "Updated  Failed,Negative values not accepted. ",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (mUri != null) {


            int rowsAffected = getContentResolver().update(mUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Updated  Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Data",
                        Toast.LENGTH_SHORT).show();
            }

        } else {

            ContentResolver contentResolver = getContentResolver();

            values.put(InventoryEntry.QUANTITY, mQuantity.getText().toString().trim());
            contentResolver.insert(InventoryEntry.CONTENT_URI, values);
        }
    }


    private void getImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mImagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                updateData();
                //Toast.makeText(getApplicationContext(), mUri.toString(), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.action_delete:
                dialogueBuilder();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (cursor.moveToFirst()) {
            String product_name = cursor.getString(cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME));

            String product_price = cursor.getString(cursor.getColumnIndex(InventoryEntry.PRICE));
            String product_supplier = cursor.getString(cursor.getColumnIndex(InventoryEntry.SUPPLIER));
            byte[] product_image = cursor.getBlob(cursor.getColumnIndex(InventoryEntry.PICTURE));
            product_quantity = cursor.getString(cursor.getColumnIndex(InventoryEntry.QUANTITY));
            String product_sale = cursor.getString(cursor.getColumnIndex(InventoryEntry.TRACK_SALE));

            mPrice.setText(product_price);
            mSupplier.setText(product_supplier);
            mProductName.setText(product_name);
            mQuantity.setText(product_quantity);
            mSale.setText(product_sale);
            mImagePreview.setImageBitmap(BitmapFactory.decodeByteArray(product_image, 0, product_image.length));
            bitmap = getImg(product_image);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void incre() {


        if (mUri != null) {
            mQuan = Integer.parseInt(mQuantity.getText().toString());
        }
        //     Toast.makeText(EditorActivity.this, String .valueOf(a), Toast.LENGTH_SHORT).show();

        ++mQuan;

        mQuantity.setText(String.valueOf(mQuan));

    }

    public void decre() {
        if (mUri != null) {
            mQuan = Integer.parseInt(mQuantity.getText().toString());
        }
        //     Toast.makeText(EditorActivity.this, String .valueOf(a), Toast.LENGTH_SHORT).show();

        --mQuan;
        if (mQuan < 0) {
            mQuan = 0;
            Toast.makeText(getApplicationContext(), "Quantity Cannot Be less than Zero", Toast.LENGTH_SHORT).show();
        }
        mQuantity.setText(String.valueOf(mQuan));

    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImg(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void discard() {

        ContentResolver resolver = getContentResolver();

        int row = resolver.delete(mUri, null, null);
        if (row > 0) {
            Toast.makeText(EditorActivity.this, "Deleted row " + row, Toast.LENGTH_SHORT).show();

        }
    }

    void dialogueBuilder() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this);
        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want delete this?");
        alertDialog.setIcon(R.drawable.delete_icon);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                discard();
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void composeEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"kushwaha.27@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Order Items");
        i.putExtra(Intent.EXTRA_TEXT, "Hello , This is Abhishek From FarmDelight Store..");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EditorActivity.this,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
