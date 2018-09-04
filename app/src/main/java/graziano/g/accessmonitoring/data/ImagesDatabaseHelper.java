package graziano.g.accessmonitoring.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

//images db helper (save image for child and family name)
public class ImagesDatabaseHelper extends SQLiteOpenHelper {

    private static final int databaseVersion = 1;
    private static final String databaseName = "dbImages";
    private static final String TABLE_IMAGE = "ImageTable";

    // Image Table Columns names
    private static final String COL_ID = "col_id";
    private static final String FAMILY_NAME = "family_name";
    private static final String CHILD_NAME = "child_name";
    private static final String IMAGE_BITMAP = "image_bitmap";

    public static boolean toUpdate = false;

    public ImagesDatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
        //   this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        //   onCreate( this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + COL_ID + " INTEGER PRIMARY KEY ,"
                + FAMILY_NAME + " TEXT,"
                + CHILD_NAME + " TEXT,"
                + IMAGE_BITMAP + " BLOB )";
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(sqLiteDatabase);
    }

    public void insetImage(Drawable dbDrawable, String familyName, String childName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAMILY_NAME, familyName);
        values.put(CHILD_NAME, childName);
        Bitmap bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, stream);
        values.put(IMAGE_BITMAP, stream.toByteArray());
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public void insetImage(Bitmap bitmap, String familyName, String childName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAMILY_NAME, familyName);
        values.put(CHILD_NAME, childName);


        int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 1, stream);


        values.put(IMAGE_BITMAP, stream.toByteArray());
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public ImageHelper getImage(String familyName, String childName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_IMAGE, new String[] { FAMILY_NAME, CHILD_NAME, IMAGE_BITMAP},FAMILY_NAME +" LIKE '" + familyName + "' AND " + CHILD_NAME  + " LIKE '" + childName + "'", null, null, null, null);

        ImageHelper imageHelper = new ImageHelper();

        if (cursor.moveToFirst()) {
            do {
                imageHelper.setImageByteArray(cursor.getBlob(cursor.getColumnIndex(IMAGE_BITMAP)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return imageHelper;
    }
}