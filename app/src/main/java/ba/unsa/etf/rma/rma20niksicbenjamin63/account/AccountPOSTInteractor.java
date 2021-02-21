package ba.unsa.etf.rma.rma20niksicbenjamin63.account;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountPOSTInteractor extends IntentService {
   public AccountPOSTInteractor() {
        super("Account Post Interactor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/d93ffb8f-dabc-4090-8cd3-7a430b5bd478/";
            URL url = new URL(url1);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"budget\": " + intent.getDoubleExtra("budget", 0) + "," +
                                     " \"monthLimit\": " + intent.getDoubleExtra("monthL",0) + "," +
                                     " \"totalLimit\": " + intent.getDoubleExtra("totalL",0) + "}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            // ispis poslije upisa transakcije
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
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
