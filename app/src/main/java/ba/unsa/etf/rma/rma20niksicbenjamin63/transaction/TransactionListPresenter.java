package ba.unsa.etf.rma.rma20niksicbenjamin63.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

public class TransactionListPresenter implements ITransactionListPresenter {
    private Context context;
    private TransactionListInteractor interactor;

    public TransactionListPresenter(Context context) {
        this.context = context;
        interactor = new TransactionListInteractor();
    }

    @Override
    public void getTransactions() {
        Intent intent = new Intent(context, TransactionListInteractor.class);
        intent.putExtra("receiver", new TransactionsReceiver());
        context.startService(intent);
    }

    public void addTransaction(Transaction transaction) {
        Intent intent = new Intent(context, TransactionPOSTInteractor.class);
        intent.putExtra("type", 1);
        intent.putExtra("trans", convertToJSON(transaction));
        intent.putExtra("transType", transaction.getType().toString());
        intent.putExtra("transaction", transaction);
        context.startService(intent);
    }

    public void getTransactionTypes(){
        Intent intent = new Intent(context, TransactionTypeInteractor.class);
        intent.putExtra("receiver", new TransactionTypeReceiver());
        context.startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDBTransactionsIntoApp(){
        ArrayList<Transaction> transactions = interactor.getAllDBTransactions(context);
        MainActivity.getAdapter().setTransactions(transactions);
        MainActivity.setAllTransactions(transactions);
        MainActivity.getAdapter().dateFilterTransactions();
    }

    public void writeDBTransactions(){
        interactor.writeDBTransactions(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Transaction> getDBTransactions() {
        return interactor.getDBTransactions(context, null);
    }

    public void modify(Transaction transaction) {
        Intent intent = new Intent(context, TransactionPOSTInteractor.class);
        intent.putExtra("type", 2);
        intent.putExtra("id", transaction.getId());
        intent.putExtra("trans", convertToJSON(transaction));
        intent.putExtra("transType", transaction.getType().toString());
        context.startService(intent);
    }

    public void delete(Transaction transaction) {
        Intent intent = new Intent(context, TransactionDELETEInteractor.class);
        intent.putExtra("id", transaction.getId());
        deleteDBOnline(transaction);
        context.startService(intent);
    }

    private String convertToJSON(Transaction t){
        String json = "{" +
                "\"date\": \"" + t.getDate() +"T14:13:00.000Z\"," +
                "\"title\": \""+ t.getTitle() +"\"," +
                "\"amount\": " + t.getAmount() + ",";
        if(t.getItemDescription() != null && !t.getItemDescription().isEmpty())
            json += "\"itemDescription\": \"" + t.getItemDescription() + "\",";
        else
            json += "\"itemDescription\": null,";
        if(t.getEndDate() != null)
            json += "\"endDate\": \"" + t.getEndDate() + "T14:13:00.000Z\"," +
                    "\"transactionInterval\": " + t.getTransactionInterval() + ",";
        else
            json += "\"transactionInterval\": null," +
                "\"endDate\": null,";
        json += "\"TransactionTypeId\": ";
        return json;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean updateAPIviaDBTransactions() {
        boolean rez = false;
        ArrayList<Transaction> ad = getDBAddTransactions();
        ArrayList<Transaction> mod = getDBModTransactions();
        ArrayList<Transaction> del = getDBDelTransactions();

        if(!ad.isEmpty() || !mod.isEmpty() || !del.isEmpty()) {
            Toast.makeText(context, "Please wait few seconds to update your modifications on internet.", Toast.LENGTH_LONG).show();
            rez = true;
        }

        for(Transaction t : mod)
            modify(t);
        for(Transaction t : del) {
            ((MainActivity) context).remove(t);
            delete(t);
        }
        for(Transaction t : ad)
            add(t);

        refreshAllDeleteColumnTransactions();
        return rez;
    }

    private void refreshAllDeleteColumnTransactions() {
        interactor.refreshAllDeleteColumnTransactions(context);
    }

    private void add(Transaction t) {
        Intent intent = new Intent(context, TransactionPOSTInteractor.class);
        intent.putExtra("type", 1);
        intent.putExtra("trans", convertToJSON(t));
        intent.putExtra("transType", t.getType().toString());
        context.startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Transaction> getDBAddTransactions() {
        return interactor.getDBTransactions(context, null);
    }

    public void restartDB(){
        interactor.restartDB(context);
    }

    public void deleteDBOnline(Transaction primaryTrans){
        interactor.deleteDBOnline(context, primaryTrans);
    }

    public void deleteDBOffline(Transaction primaryTrans) {
        interactor.deleteDBOffline(context, primaryTrans);
    }

    public boolean isTransactionDeletedOffline(Transaction primaryTrans) {
        return interactor.isTransactionDeletedOffline(context, primaryTrans);
    }

    public void recoverDBTransaction(Transaction primaryTrans) {
        interactor.recoverDBTransaction(context, primaryTrans);
    }

    public void modifyDBTransaction(Transaction primaryTrans, Transaction transaction) {
        interactor.modifyDBTransaction(context, primaryTrans, transaction);
    }

    public void setIDFromApiToDB(int parseInt) {
        interactor.setIDFromApiToDB(context, parseInt);
    }

    public void writeDBOffline(Transaction transaction) {
        interactor.writeDBOffline(context, transaction);
    }

    public void refreshDeleteColumnTransactions(int parseInt) {
        interactor.refreshDeleteColumnTransactions(context, parseInt);
    }

    public class TransactionsReceiver extends ResultReceiver{
        public TransactionsReceiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ArrayList<Transaction> trans = (ArrayList<Transaction>) resultData.getSerializable("transactions");
            ((OnReceiveTransactions) context).onReceive(trans);
        }
    }

    public class TransactionTypeReceiver extends ResultReceiver{
        public TransactionTypeReceiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ArrayList<Transaction.TYPE> types = new ArrayList<>();
            ArrayList<String> result = resultData.getStringArrayList("types");
            for(String s : result){
                switch(s) {
                    case "Regular payment":
                        types.add(Transaction.TYPE.REGULARPAYMENT);
                        break;
                    case "Individual payment":
                        types.add(Transaction.TYPE.INDIVIDUALPAYMENT);
                        break;
                    case "Purchase":
                        types.add(Transaction.TYPE.PURCHASE);
                        break;
                    case "Regular income":
                        types.add(Transaction.TYPE.REGULARINCOME);
                        break;
                    case "Individual income":
                        types.add(Transaction.TYPE.INDIVIDUALINCOME);
                        break;
                }
            }
            MainActivity.setTransactionTypes(types);
            ((MainActivity) context).onReceive();
        }
    }

    public interface OnReceiveTransactions {
        void onReceive(ArrayList<Transaction> transactions);
    }

    public interface OnReceiveTypesStartTransactions{
        void onReceive();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Transaction> getDBModTransactions(){
        return interactor.getDBTransactions(context, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Transaction> getDBDelTransactions(){
        return interactor.getDBTransactions(context, 1);
    }
}
