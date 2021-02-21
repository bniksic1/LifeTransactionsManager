package ba.unsa.etf.rma.rma20niksicbenjamin63.transaction;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransactionDELETEInteractor extends IntentService{
    public TransactionDELETEInteractor() {
        super("Transaction Delete Interactor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    try {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/d93ffb8f-dabc-4090-8cd3-7a430b5bd478/transactions/";
        url1 += intent.getIntExtra("id", 0);
        URL url = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
