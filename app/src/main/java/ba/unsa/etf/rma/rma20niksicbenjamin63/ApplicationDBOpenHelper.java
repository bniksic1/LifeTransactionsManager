package ba.unsa.etf.rma.rma20niksicbenjamin63;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ApplicationDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "rma20niksicbenjamin63.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_TITLE = "title";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_DESCRIPTION = "description";
    public static final String TRANSACTION_INTERVAL = "interval";
    public static final String TRANSACTION_ENDDATE = "end_date";
    public static final String TRANSACTION_DELETE = "delete_transaction";
    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE
                    + " (" + TRANSACTION_ID + " INTEGER UNIQUE, "
                    + TRANSACTION_TITLE + " TEXT NOT NULL, "
                    + TRANSACTION_DATE + " TEXT NOT NULL, "
                    + TRANSACTION_AMOUNT + " REAL NOT NULL, "
                    + TRANSACTION_TYPE + " TEXT NOT NULL, "
                    + TRANSACTION_DESCRIPTION + " TEXT, "
                    + TRANSACTION_INTERVAL + " INTEGER, "
                    + TRANSACTION_ENDDATE + " TEXT, "
                    + TRANSACTION_DELETE + " INTEGER);";
    private static final String TRANSACTION_DROP = "DROP TABLE IF EXISTS "
            + TRANSACTION_TABLE;

    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_BUDGET = "budget";
    public static final String ACCOUNT_MONTH_LIMIT = "month_limit";
    public static final String ACCOUNT_TOTAL_LIMIT = "total_limit";
    private static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE
                    + " (" + ACCOUNT_BUDGET + " REAL NOT NULL, "
                    + ACCOUNT_MONTH_LIMIT + " REAL NOT NULL, "
                    + ACCOUNT_TOTAL_LIMIT + " REAL NOT NULL);";
    private static final String ACCOUNT_TABLE_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;

    public ApplicationDBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ApplicationDBOpenHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(TRANSACTION_TABLE_CREATE);
        System.out.println(ACCOUNT_TABLE_CREATE);
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_DROP);
        db.execSQL(ACCOUNT_TABLE_DROP);
        onCreate(db);
    }

    public void resetAccountTable(){
        SQLiteDatabase database;
        try{
            database=getWritableDatabase();
        }catch (SQLiteException e){
            database=getReadableDatabase();
        }
        database.execSQL(ACCOUNT_TABLE_DROP);
        database.execSQL(ACCOUNT_TABLE_CREATE);
    }

    public void resetTransactionsTable(){
        SQLiteDatabase database;
        try{
            database=getWritableDatabase();
        }catch (SQLiteException e){
            database=getReadableDatabase();
        }
        database.execSQL(TRANSACTION_DROP);
        database.execSQL(TRANSACTION_TABLE_CREATE);
    }
}
