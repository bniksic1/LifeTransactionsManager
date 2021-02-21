package ba.unsa.etf.rma.rma20niksicbenjamin63;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class TransactionContentProvider extends ContentProvider {
    public static final String AUTHORITY = "rma20niksicbenjamin63.transactionProvider";
    public static final String CONTENT_PATH = ApplicationDBOpenHelper.TRANSACTION_TABLE;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    private static final String ONE_ROW_MIME_TYPE = "vnd.android.cursor.item/vnd.rma20niksicbenjamin63." + CONTENT_PATH;
    private static final String ALL_ROWS_MIME_TYPE = "vnd.android.cursor.dir/vnd.rma20niksicbenjamin63." + CONTENT_PATH;

    private ApplicationDBOpenHelper dbOpenHelper;
    private static final int ALLROWS =1;
    private static final int ONEROW = 2;
    private static final UriMatcher uM;
    static {
        uM = new UriMatcher(UriMatcher.NO_MATCH);
        uM.addURI(AUTHORITY, CONTENT_PATH, ALLROWS);
        uM.addURI(AUTHORITY, CONTENT_PATH + "/#", ONEROW);
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = new ApplicationDBOpenHelper(getContext(), ApplicationDBOpenHelper.TRANSACTION_TABLE, null, ApplicationDBOpenHelper.DATABASE_VERSION);
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
        switch (uM.match(uri)){
            case ONEROW:
                String id = uri.getPathSegments().get(1);
                squery.appendWhere(ApplicationDBOpenHelper.TRANSACTION_ID + "=" + id);
            default:
                break;
        }
        squery.setTables(ApplicationDBOpenHelper.TRANSACTION_TABLE);
        Cursor cursor = squery.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uM.match(uri)){
            case ALLROWS:
                return ALL_ROWS_MIME_TYPE;
            case ONEROW:
                return ONE_ROW_MIME_TYPE;
            default:
                throw new IllegalArgumentException(
                        "Unsuported uri: " + uri.toString());
        }
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
        long id = database.insert(ApplicationDBOpenHelper.TRANSACTION_TABLE, null, values);
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
        int br = database.delete(ApplicationDBOpenHelper.TRANSACTION_TABLE, selection, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return br;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database;
        try{
            database=dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=dbOpenHelper.getReadableDatabase();
        }
        database.update(ApplicationDBOpenHelper.TRANSACTION_TABLE, values, selection, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return 1;
    }
}
