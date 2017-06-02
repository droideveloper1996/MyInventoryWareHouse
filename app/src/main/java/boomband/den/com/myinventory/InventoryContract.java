package boomband.den.com.myinventory;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abhishek on 27/05/2017.
 */

public class InventoryContract {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "boomband.den.com.myinventory";
    public static final Uri BASE_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final String PATH = "inventory";

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();


        public static final String TABLE_NAME = "inventory";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String PRODUCT_NAME = "product_name";
        public static final String SUPPLIER = "supplier";
        public static final String PICTURE = "picture";
        public static final String TRACK_SALE = "track_sale";

    }
}
