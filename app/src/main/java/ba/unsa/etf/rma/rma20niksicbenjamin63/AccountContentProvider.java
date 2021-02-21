package ba.unsa.etf.rma.rma20niksicbenjamin63;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountContentProvider extends ContentProvider {
    public static final String AUTHORITY = "rma20niksicbenjamin63.accountProvider";
    public static final String CONTENT_PATH = ApplicationDBOpenHelper.ACCOUNT_TABLE;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    private static final String ONE_ROW_MIME_TYPE = "vnd.android.cursor.item/vnd.rma20niksicbenjamin63." + CONTENT_PATH;

    private ApplicationDBOpenHelper dbOpenHelper;
    private static final int ONEROW = 2;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new ApplicationDBOpenHelper(getContext(), ApplicationDBOpenHelper.ACCOUNT_TABLE, null, ApplicationDBOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database;
        try{
            database=dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=dbOpenHelper.getReadableDatabase();
        }
        SQLiteQueryBuilder squery = new SQLiteQueryBuilder();
        squery.setTables(ApplicationDBOpenHelper.ACCOUNT_TABLE);
        Cursor cursor = squery.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
            return ONE_ROW_MIME_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database;
        try{
            database=dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=dbOpenHelper.getReadableDatabase();
        }
        long id = database.insert(ApplicationDBOpenHelper.ACCOUNT_TABLE, null, values);
        Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(_uri, null);
        return Uri.parse(CONTENT_URI.toString() + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database;
        try{
            database=dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=dbOpenHelper.getReadableDatabase();
        }
        int br = database.delete(ApplicationDBOpenHelper.ACCOUNT_TABLE, null, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return br;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
