package boomband.den.com.myinventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import boomband.den.com.myinventory.InventoryContract.InventoryEntry;

import static boomband.den.com.myinventory.MainActivity.getImage;

/**
 * Created by Abhishek on 28/05/2017.
 */

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView mName;
        ImageView imageView;
        TextView mPrice;
        TextView mTrack;
        mName = (TextView) view.findViewById(R.id.name);
        imageView = (ImageView) view.findViewById(R.id.image);
        mPrice = (TextView) view.findViewById(R.id.price);
        mTrack = (TextView) view.findViewById(R.id.track);
        int name = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME);
        int image = cursor.getColumnIndex(InventoryEntry.PICTURE);
        int price = cursor.getColumnIndex(InventoryEntry.PRICE);
        int track = cursor.getColumnIndex(InventoryEntry.TRACK_SALE);

        String nName = cursor.getString(name);
        String nPrice = cursor.getString(price);
        String nTrack = cursor.getString(track);
        byte[] arr = cursor.getBlob(image);
        Bitmap bitmap = getImage(arr);
        mName.setText(nName);
        mPrice.setText("$"+nPrice);
        mTrack.setText(nTrack+"sold");
        imageView.setImageBitmap(bitmap);
    }
}
