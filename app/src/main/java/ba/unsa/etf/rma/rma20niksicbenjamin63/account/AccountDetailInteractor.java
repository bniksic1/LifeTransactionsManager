package ba.unsa.etf.rma.rma20niksicbenjamin63.account;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ba.unsa.etf.rma.rma20niksicbenjamin63.AccountContentProvider;
import ba.unsa.etf.rma.rma20niksicbenjamin63.ApplicationDBOpenHelper;
import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Account;

public class AccountDetailInteractor extends IntentService implements IAccountDetailInteractor {
    private Account account;

    public AccountDetailInteractor() {
        super("Account Detail Interactor");
    }

    @Override
    public Account get() {
        return account;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/d93ffb8f-dabc-4090-8cd3-7a430b5bd478/";
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String JSONres = convertStreamToString(in);

            JSONObject object = new JSONObject(JSONres);
            Double budget = object.getDouble("budget");
            Double totalLimit = object.getDouble("totalLimit");
            Double monthLimit = object.getDouble("monthLimit");
            int id = object.getInt("id");
            account = new Account(id, budget, totalLimit, monthLimit);
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            bundle.putSerializable("account", account);
            if(intent.getIntExtra("type", 0) == 1)
                receiver.send(1, bundle);
            else if(intent.getIntExtra("type", 0) == 2)
                receiver.send(2, bundle);
        }catch (IOException | JSONException ex) {
            ex.printStackTrace();
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

    public Account getAccount(Context context) {
        Account account = null;
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String[] kolone = {ApplicationDBOpenHelper.ACCOUNT_BUDGET,
                ApplicationDBOpenHelper.ACCOUNT_MONTH_LIMIT,
                ApplicationDBOpenHelper.ACCOUNT_TOTAL_LIMIT};
        System.out.println("URI JE: " + AccountContentProvider.CONTENT_URI);
        Cursor cursor = resolver.query(AccountContentProvider.CONTENT_URI, kolone, null,null,null);
        if(cursor.moveToFirst()) {
            do{
                account = new Account(27, 0, 0,0);
                int budgetPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.ACCOUNT_BUDGET);
                int monthLimitPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.ACCOUNT_MONTH_LIMIT);
                int totalLimitPos = cursor.getColumnIndexOrThrow(ApplicationDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
                account.setBudget(cursor.getDouble(budgetPos));
                account.setMonthLimit(cursor.getDouble(monthLimitPos));
                account.setTotalLimit(cursor.getDouble(totalLimitPos));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return account;
    }

    public void updateAccount(Context context){
        restartDB(context);
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ApplicationDBOpenHelper.ACCOUNT_BUDGET, MainActivity.getBudget());
        values.put(ApplicationDBOpenHelper.ACCOUNT_MONTH_LIMIT, MainActivity.getMonthLimit());
        values.put(ApplicationDBOpenHelper.ACCOUNT_TOTAL_LIMIT, MainActivity.getTotalLimit());
        resolver.insert(AccountContentProvider.CONTENT_URI, values);
    }

    public void restartDB(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        resolver.delete(AccountContentProvider.CONTENT_URI,null, null);
    }

    public void writeDBAccount(Context context) {
        restartDB(context);
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ApplicationDBOpenHelper.ACCOUNT_BUDGET, MainActivity.getBudget());
        values.put(ApplicationDBOpenHelper.ACCOUNT_MONTH_LIMIT, MainActivity.getMonthLimit());
        values.put(ApplicationDBOpenHelper.ACCOUNT_TOTAL_LIMIT, MainActivity.getTotalLimit());
        resolver.insert(AccountContentProvider.CONTENT_URI, values);
    }
}
