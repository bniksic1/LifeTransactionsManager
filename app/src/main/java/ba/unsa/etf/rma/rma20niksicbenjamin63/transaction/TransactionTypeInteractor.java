package ba.unsa.etf.rma.rma20niksicbenjamin63.transaction;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

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
import java.util.ArrayList;

public class TransactionTypeInteractor extends IntentService {
    public TransactionTypeInteractor() {
        super("Transaction Type Interactor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String JSONres = convertStreamToString(in);
            JSONObject object = new JSONObject(JSONres);
            JSONArray typesArray = object.getJSONArray("rows");
            ArrayList<String> res = new ArrayList<>();
            for (int j = 0; j < typesArray.length(); j++) {
                JSONObject type = typesArray.getJSONObject(j);
                res.add(type.getString("name"));
            }
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            bundle.putSerializable("types", res);
            receiver.send(1, bundle);
        } catch (JSONException | IOException e) {
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
}
