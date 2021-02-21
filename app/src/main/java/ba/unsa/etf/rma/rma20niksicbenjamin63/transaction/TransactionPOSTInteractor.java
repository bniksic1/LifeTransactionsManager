package ba.unsa.etf.rma.rma20niksicbenjamin63.transaction;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;

public class TransactionPOSTInteractor extends IntentService{
    public TransactionPOSTInteractor() {
        super("Transaction Post Class");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/d93ffb8f-dabc-4090-8cd3-7a430b5bd478/transactions/";
            if(intent.getIntExtra("type", 0) == 2)
                url1 += intent.getIntExtra("id", 0);
            URL url = new URL(url1);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = intent.getStringExtra("trans");
            jsonInputString += getTypeId(intent.getStringExtra("transType")) + "}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            String id = "";
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());

                // ovim dobijam ID koji je zapisan na API
                for(char s : response.toString().substring(6).toCharArray()) {
                    if (s != ',') id += s;
                    else break;
                }
            }
            // update lokalnog ID transakcije odmah
            for(int i = 0 ; i < MainActivity.getAllTransactions().size(); i++)
                if(MainActivity.getAllTransactions().get(i).getId() == -1)
                    MainActivity.getAllTransactions().get(i).setId(Integer.parseInt(id));
            for(int i = 0 ; i < MainActivity.getRealTransactions().size(); i++)
                if(MainActivity.getRealTransactions().get(i).getId() == -1)
                    MainActivity.getRealTransactions().get(i).setId(Integer.parseInt(id));
            // update DB ID/delete transakcije odmah
            if(intent.getIntExtra("type", 0) == 2)
                MainActivity.getPresenter().refreshDeleteColumnTransactions(Integer.parseInt(id));
            else
                MainActivity.getPresenter().setIDFromApiToDB(Integer.parseInt(id));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private int getTypeId(String t) throws IOException, JSONException {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        URL url = new URL(url1);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String JSONres = convertStreamToString(in);

        JSONObject object = new JSONObject(JSONres);
        JSONArray typesArray = object.getJSONArray("rows");
        for (int j = 0; j < typesArray.length(); j++) {
            JSONObject type = typesArray.getJSONObject(j);
            if(t.equals(type.getString("name")))
                return type.getInt("id");
        }
        return 0;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
