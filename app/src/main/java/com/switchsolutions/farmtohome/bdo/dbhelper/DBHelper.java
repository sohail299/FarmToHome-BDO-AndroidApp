package com.switchsolutions.farmtohome.bdo.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper helper = null;


    private static final String DB_NAME = "FarmToHome_DB";
    private static final int DB_VERSION = 2;

    // CART TABLE
    private static final String CART_TABLE = "Cart_Table";
    private static final String PRODUCT_UNIQUE_ID_CART = "Id";
    private static final String PRODUCT_ID_CART = "Product_Id";
    private static final String PRODUCT_NAME_CART = "Product_Name";
    private static final String PRODUCT_UNIT_CART = "Product_Unit";
    private static final String PRODUCT_QUANTITY_CART = "Product_Quantity";
    private static final String PRODUCT_PRICE_CART = "Product_Price";
    private static final String PRODUCT_REMARKS_CART = "Product_Remarks";
    private static final String USER_ID_CART = "User_Id";
    private static final String PRODUCT_ID_SYNCED_CART = "Is_Synced";
    private static final String PRODUCT_IMAGE_CART = "Image_URL";
    private static final String CITY_ID_CART = "City_ID";
    private static final String PRODUCT_IS_AVAILABLE = "Is_Available";
    private static final String PRODUCT_IS_UPDATED = "Is_Updated";
    private static final String PRODUCT_IS_DECIMAL_STATUS = "Is_Decimal_Status";


    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (helper == null)
            helper = new DBHelper(context);
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CART_TABLE + "(" + PRODUCT_UNIQUE_ID_CART + " INTEGER PRIMARY KEY AUTOINCREMENT," + PRODUCT_ID_CART + " TEXT, " + PRODUCT_NAME_CART + " TEXT, " + PRODUCT_UNIT_CART +
                " TEXT, " + PRODUCT_QUANTITY_CART + " TEXT, " + PRODUCT_PRICE_CART + " TEXT, " + PRODUCT_REMARKS_CART + " TEXT, "
                + USER_ID_CART + " TEXT, " + PRODUCT_ID_SYNCED_CART + " INTEGER, " + PRODUCT_IMAGE_CART + " TEXT , " + CITY_ID_CART + " TEXT, "
                + PRODUCT_IS_AVAILABLE + " INTEGER, " + PRODUCT_IS_UPDATED + " INTEGER, " + PRODUCT_IS_DECIMAL_STATUS + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion && newVersion == 2) {
            db.execSQL("ALTER TABLE " +CART_TABLE + " ADD COLUMN " + PRODUCT_IS_DECIMAL_STATUS );
        }
    }


    //    BLOCK FOR INSERTION

    public boolean setCart(Integer productId, String productName) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_ID_CART, productId);
        contentValues.put(PRODUCT_NAME_CART, productName);
        contentValues.put(PRODUCT_ID_SYNCED_CART, 0);
        contentValues.put(PRODUCT_IS_AVAILABLE, 1);
        contentValues.put(PRODUCT_IS_UPDATED, 0);
        long result = db.insert(CART_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;
    }

    //    BLOCK FOR SELECTION

//    public Cursor getCart(String userId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE " + USER_ID_CART + " = '"
//                + userId + "' OR " + USER_ID_CART + " = 'guest'", null);
//    }

    public Cursor getCart(String userId, String cityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE (" + USER_ID_CART + " = '"
                + userId + "' OR " + USER_ID_CART + " = 'guest') AND " + CITY_ID_CART + " = '" + cityId + "'" +
                " GROUP BY Product_Id ORDER BY " + PRODUCT_IS_AVAILABLE + "," + PRODUCT_IS_UPDATED + " DESC", null);
    }

    public Cursor getCartForDelivery(String userId, String cityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE (" + USER_ID_CART + " = '"
                + userId + "' OR " + USER_ID_CART + " = 'guest') AND " + CITY_ID_CART + " = '" + cityId + "' AND " +
                PRODUCT_IS_AVAILABLE + " = '1' GROUP BY Product_Id ORDER BY " + PRODUCT_IS_AVAILABLE + "," + PRODUCT_IS_UPDATED + " DESC", null);
    }

    public Cursor getCartProductID(String userId, int productId, String cityID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE (" + USER_ID_CART + " = '" + userId + "' OR " + USER_ID_CART + " = 'guest') AND " + PRODUCT_ID_CART + " = '" + productId + "' AND " + CITY_ID_CART + " = '" + cityID +"'", null);
    }

//    public Cursor getCartCount(String userId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.rawQuery("SELECT COUNT(*) FROM " + CART_TABLE + " WHERE " + USER_ID_CART + " = '" + userId + "' OR "
//                + USER_ID_CART + " = 'guest'", null);
//    }

    public Cursor getCartCount(String userId, String cityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT COUNT(*) FROM " + CART_TABLE + " WHERE (" + USER_ID_CART + " = '" + userId + "' OR "
                + USER_ID_CART + " = 'guest' ) AND " + CITY_ID_CART + " = '" + cityId + "' AND " + PRODUCT_IS_AVAILABLE + " = '1' GROUP BY Product_Id", null);
    }


    //    BLOCK FOR UPDATING

    public boolean updateCart(String productId, String productQuantity, String cityId, String productRemarks, String productPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_QUANTITY_CART, productQuantity);
        contentValues.put(PRODUCT_REMARKS_CART, productRemarks);
        contentValues.put(PRODUCT_PRICE_CART,productPrice);
        long result = db.update(CART_TABLE, contentValues, PRODUCT_ID_CART + " = " + productId + " AND " + CITY_ID_CART + " = '"+cityId+"'", null);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean updateCartUponVerification(String productId, String productQuantity, String productPrice,
                                              int isAvailable, int isUpdated) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_QUANTITY_CART, productQuantity);
        contentValues.put(PRODUCT_PRICE_CART, productPrice);
        contentValues.put(PRODUCT_IS_AVAILABLE, isAvailable);
        contentValues.put(PRODUCT_IS_UPDATED, isUpdated);
        long result = db.update(CART_TABLE, contentValues, PRODUCT_ID_CART + " = " + productId, null);
        if (result == -1)
            return false;
        else
            return true;
    }

    //    BLOCK FOR DELETION

    public Integer deleteCartProduct(String productId, String cityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CART_TABLE, PRODUCT_ID_CART + " = ? AND " + CITY_ID_CART + " = ?", new String[]{productId, cityId});
    }

    public Integer deleteCart(String userId, String cityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CART_TABLE, USER_ID_CART + " = ? OR " + USER_ID_CART + " = ? AND " + CITY_ID_CART + " = ?", new String[]{"guest", userId, cityId});
    }

}
