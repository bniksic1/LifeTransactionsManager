package ba.unsa.etf.rma.rma20niksicbenjamin63.transaction;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.ApplicationDBOpenHelper;
import ba.unsa.etf.rma.rma20niksicbenjamin63.TransactionContentProvider;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

import static ba.unsa.etf.rma.rma20niksicbenjamin63.ApplicationDBOpenHelper.TRANSACTION_DATE;
import static ba.unsa.etf.rma.rma20niksicbenjamin63.ApplicationDBOpenHelper.TRANSACTION_ID;
import static java.sql.Types.NULL;

public class TransactionListInteractor extends IntentService implements ITransactionListInteractor{
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionListInteractor() {
        super("Transaction List Interactor");
    }

    @Override
    public ArrayList<Transaction> get() {
        return transactions;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        try {
            transactions = new ArrayList<>();
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/d93ffb8f-dabc-4090-8cd3-7a430b5bd478/transactions/filter?page=0";
            for (int i = 0; ; i++) {
                url1 = url1.substring(0, url1.length() - 1);
                url1 += i;
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String JSONres = convertStreamToString(in);

                JSONObject object = new JSONObject(JSONres);
                JSONArray transactionsArray = object.getJSONArray("transactions");
                if (transactionsArray.length() == 0) break;
                for (int j = 0; j < transactionsArray.length(); j++) {
                    JSONObject t = transactionsArray.getJSONObject(j);
                    LocalDate date = LocalDate.of(Integer.parseInt(t.getString("date").substring(0, 4)), Integer.parseInt(t.getString("date").substring(5, 7)), Integer.parseInt(t.getString("date").substring(8, 10)));
                    LocalDate endDate = null;
                    if (!t.getString("endDate").equals("null"))
                        endDate = LocalDate.of(Integer.parseInt(t.getString("endDate").substring(0, 4)), Integer.parseInt(t.getString("endDate").substring(5, 7)), Integer.parseInt(t.getString("endDate").substring(8, 10)));
                    Integer interval = null;
                    if (!t.getString("transactionInterval").equals("null"))
                        interval = t.getInt("transactionInterval");
                    Transaction.TYPE type = MainActivity.getTransactionTypes().get(t.getInt("TransactionTypeId") - 1);
                    transactions.add(
                            new Transaction(t.getInt("id"), date, t.getDouble("amount"), t.getString("title"), type, t.getString("itemDescription"), interval, endDate)
                    );
                }
            }
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            bundle.putSerializable("transactions", transactions);
            receiver.send(1, bundle);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    private String getSortType(int type){
        switch(type){
            case 1:
                return "&sort=amount.asc";
            case 2:
                return "&sort=amount.desc";
            case 3:
                return "&sort=title.asc";
            case 4:
                return "&sort=title.desc";
            case 5:
                return "&sort=date.asc";
            case 6:
                return "&sort=date.desc";
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Transaction> getDBTransactions(Context context, Integer delete) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ArrayList<Transaction> transactions = new ArrayList<>();
        String[] kolone = {TRANSACTION_ID,
                           TRANSACTION_DATE,
                           ApplicationDBOpenHelper.TRANSACTION_TITLE,
                           ApplicationDBOpenHelper.TRANSACTION_AMOUNT,
                           ApplicationDBOpenHelper.TRANSACTION_TYPE,
                           ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION,
                           ApplicationDBOpenHelper.TRANSACTION_INTERVAL,
                           ApplicationDBOpenHelper.TRANSACTION_ENDDATE,
                           ApplicationDBOpenHelper.TRANSACTION_DELETE};
        Cursor cursor = resolver.query(TransactionContentProvider.CONTENT_URI, kolone, null, null, null);
        if(cursor.moveToFirst()) {
            do{
                int idPos = cursor.getColumnIndexOrThrow(TRANSACTION_ID);
                int titlePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_TITLE);
                int datePos = cursor.getColumnIndexOrThrow(TRANSACTION_DATE);
                int amountPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_AMOUNT);
                int typePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_TYPE);
                int descriptionPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION);
                int intervalPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_INTERVAL);
                int enddatePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_ENDDATE);
                int deletePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_DELETE);


                if(delete == null && cursor.isNull(deletePos) && cursor.getInt(idPos) == -1 || !cursor.isNull(deletePos) && delete != null && delete == cursor.getInt(deletePos))
                    transactions.add(new Transaction(cursor.getInt(idPos), toLocalDate(cursor.getString(datePos)),
                            cursor.getDouble(amountPos), cursor.getString(titlePos), toTransactionTYPE(cursor.getString(typePos)),
                            cursor.getString(descriptionPos), cursor.getInt(intervalPos), toLocalDate(cursor.getString(enddatePos))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate toLocalDate(String date){
        // output LocalDate string: 2020-05-01
        return date != null ? LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    private Transaction.TYPE toTransactionTYPE(String type){
        switch(type) {
            case "Regular payment":
                return Transaction.TYPE.REGULARPAYMENT;
            case "Individual payment":
                return Transaction.TYPE.INDIVIDUALPAYMENT;
            case "Purchase":
                return Transaction.TYPE.PURCHASE;
            case "Regular income":
                return Transaction.TYPE.REGULARINCOME;
            case "Individual income":
                return Transaction.TYPE.INDIVIDUALINCOME;
        }
        return  null;
    }

    public void restartDB(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        resolver.delete(TransactionContentProvider.CONTENT_URI,null, null);
    }

    public void writeDBTransactions(Context context) {
        restartDB(context);
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        for(Transaction t : MainActivity.getAllTransactions()) {
            ContentValues values = new ContentValues();
            values.put(TRANSACTION_ID, t.getId());
            values.put(ApplicationDBOpenHelper.TRANSACTION_TITLE, t.getTitle());
            values.put(TRANSACTION_DATE, t.getDate().toString());
            values.put(ApplicationDBOpenHelper.TRANSACTION_AMOUNT, t.getAmount());
            values.put(ApplicationDBOpenHelper.TRANSACTION_TYPE, t.getType().toString());
            if(t.getItemDescription() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION, t.getItemDescription());
            if(t.getTransactionInterval() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_INTERVAL, t.getTransactionInterval());
            if(t.getEndDate() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_ENDDATE, t.getEndDate().toString());
            resolver.insert(TransactionContentProvider.CONTENT_URI, values);
        }
    }

    public void deleteDBOnline(Context context, Transaction primaryTrans) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        resolver.delete(TransactionContentProvider.CONTENT_URI, "id = " + primaryTrans.getId(), null);
    }

    public void deleteDBOffline(Context context, Transaction primaryTrans) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ApplicationDBOpenHelper.TRANSACTION_DELETE, 1);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, "id = " + primaryTrans.getId(), null);
    }

    public boolean isTransactionDeletedOffline(Context context, Transaction primaryTrans) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String[] kolone = {ApplicationDBOpenHelper.TRANSACTION_DELETE};
        Cursor cursor = resolver.query(TransactionContentProvider.CONTENT_URI, kolone, "id = " + primaryTrans.getId(), null, null);
        if(cursor.moveToFirst()) {
            int deletePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_DELETE);
            return cursor.getInt(deletePos) == 1;
        }
        return false;
    }

    public void recoverDBTransaction(Context context, Transaction primaryTrans) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ApplicationDBOpenHelper.TRANSACTION_DELETE, 0);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, "id = " + primaryTrans.getId(), null);
    }

    public void modifyDBTransaction(Context context, Transaction primaryTrans, Transaction t) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_ID, t.getId());
        values.put(ApplicationDBOpenHelper.TRANSACTION_TITLE, t.getTitle());
        values.put(TRANSACTION_DATE, t.getDate().toString());
        values.put(ApplicationDBOpenHelper.TRANSACTION_AMOUNT, t.getAmount());
        values.put(ApplicationDBOpenHelper.TRANSACTION_TYPE, t.getType().toString());
        if(t.getItemDescription() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION, t.getItemDescription());
        if(t.getTransactionInterval() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_INTERVAL, t.getTransactionInterval());
        if(t.getEndDate() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_ENDDATE, t.getEndDate().toString());
        if(primaryTrans.getId() != -1) values.put(ApplicationDBOpenHelper.TRANSACTION_DELETE, 0);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, "id = " + primaryTrans.getId(), null);
    }

    private String ifNullObject(Object o){
        return o == null ? "null" : o.toString();
    }

    public void setIDFromApiToDB(Context context, int parseInt){
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_ID, parseInt);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, "id = -1", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Transaction> getAllDBTransactions(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ArrayList<Transaction> transactions = new ArrayList<>();
        String[] kolone = {TRANSACTION_ID,
                TRANSACTION_DATE,
                ApplicationDBOpenHelper.TRANSACTION_TITLE,
                ApplicationDBOpenHelper.TRANSACTION_AMOUNT,
                ApplicationDBOpenHelper.TRANSACTION_TYPE,
                ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION,
                ApplicationDBOpenHelper.TRANSACTION_INTERVAL,
                ApplicationDBOpenHelper.TRANSACTION_ENDDATE};
        Cursor cursor = resolver.query(TransactionContentProvider.CONTENT_URI, kolone, null, null, null);
        if(cursor.moveToFirst()) {
            do{
                int idPos = cursor.getColumnIndexOrThrow(TRANSACTION_ID);
                int titlePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_TITLE);
                int datePos = cursor.getColumnIndexOrThrow(TRANSACTION_DATE);
                int amountPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_AMOUNT);
                int typePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_TYPE);
                int descriptionPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION);
                int intervalPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_INTERVAL);
                int enddatePos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.TRANSACTION_ENDDATE);
                transactions.add(new Transaction(cursor.getInt(idPos), toLocalDate(cursor.getString(datePos)),
                        cursor.getDouble(amountPos), cursor.getString(titlePos), toTransactionTYPE(cursor.getString(typePos)),
                        cursor.getString(descriptionPos), cursor.getInt(intervalPos), toLocalDate(cursor.getString(enddatePos))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public void writeDBOffline(Context context, Transaction t) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_ID, t.getId());
        values.put(ApplicationDBOpenHelper.TRANSACTION_TITLE, t.getTitle());
        values.put(TRANSACTION_DATE, t.getDate().toString());
        values.put(ApplicationDBOpenHelper.TRANSACTION_AMOUNT, t.getAmount());
        values.put(ApplicationDBOpenHelper.TRANSACTION_TYPE, t.getType().toString());
        if(t.getItemDescription() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_DESCRIPTION, t.getItemDescription());
        if(t.getTransactionInterval() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_INTERVAL, t.getTransactionInterval());
        if(t.getEndDate() != null) values.put(ApplicationDBOpenHelper.TRANSACTION_ENDDATE, t.getEndDate().toString());
        resolver.insert(TransactionContentProvider.CONTENT_URI, values);
    }

    public void refreshDeleteColumnTransactions(Context context, int parseInt) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.putNull(ApplicationDBOpenHelper.TRANSACTION_DELETE);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, "id = " + parseInt, null);
    }

    public void refreshAllDeleteColumnTransactions(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.putNull(ApplicationDBOpenHelper.TRANSACTION_DELETE);
        resolver.update(TransactionContentProvider.CONTENT_URI, values, null, null);
    }
}
