package ba.unsa.etf.rma.rma20niksicbenjamin63;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.account.AccountDetailPresenter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.TransactionListAdapter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Account;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.OnePaneFragment;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.TransactionDetailFragment;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.TransactionListFragment;
import ba.unsa.etf.rma.rma20niksicbenjamin63.transaction.TransactionListPresenter;

public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick,
                                                               TransactionListPresenter.OnReceiveTransactions,
                                                               AccountDetailPresenter.OnReceiveAccount,
                                                               TransactionListPresenter.OnReceiveTypesStartTransactions {
    private static boolean twoPaneMode = false;
    private static TransactionListAdapter adapter = null;
    private static LocalDate date = null;
    private static int type;
    private static int sortType;
    private static double budget;
    private static double totalLimit;
    private static double monthLimit;
    private static TransactionListPresenter presenter;
    private static AccountDetailPresenter accPresenter;
    private static ArrayList<Transaction> allTransactions = new ArrayList<>();
    private static ArrayList<Transaction> realTransactions = new ArrayList<>();
    private static ArrayList<Transaction> regularTransactions = new ArrayList<>();
    private static ArrayList<Transaction.TYPE> transactionTypes = new ArrayList<>();
    private static OnePaneFragment onePaneFragment;
    private static TextView textView;
    private boolean prviUlazak = true;

    private ConnectivityBroadcastReceiver receiver = new ConnectivityBroadcastReceiver();
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");


    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(isConnected())
            Toast.makeText(this,"Please wait few seconds because transactions are loading from internet.",Toast.LENGTH_LONG).show();

        if(adapter == null) {
            adapter = new TransactionListAdapter(this, R.layout.list_transaction, realTransactions);
            date = LocalDate.now();
            type = 0;
            sortType = 0;
            budget = 0;
            totalLimit = 0;
            monthLimit = 0;
        }

        presenter = new TransactionListPresenter(this);
        accPresenter = new AccountDetailPresenter(this);

        textView = findViewById(R.id.connText);
        if(textView != null) {
            if (isConnected())
                setOnlineMode();
            else
                setOfflineMode("OFFLINE");
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout details = findViewById(R.id.frameDetail);
        if (details != null) {
            twoPaneMode = true;
            onItemClicked(new Transaction(-1, LocalDate.now(), 0, "", Transaction.TYPE.REGULARINCOME, "", 0, LocalDate.now().plusMonths(1)));
            TransactionListFragment listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction().replace(R.id.frameList, listFragment).commit();
        }
        else {
            twoPaneMode = false;
            onePaneFragment = new OnePaneFragment();
            fragmentManager.beginTransaction().replace(R.id.frameList, onePaneFragment).commit();
        }
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("trans", transaction);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode)
            getSupportFragmentManager().beginTransaction().replace(R.id.frameDetail, detailFragment).commit();
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameList, detailFragment).addToBackStack(null).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void modify(Transaction oldT, Transaction newT) {
        adapter.modifyTransaction(oldT, newT);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void remove(Transaction transaction) {
        adapter.removeTransaction(transaction);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void add(Transaction transaction) {
        adapter.addTransaction(transaction);
    }

    public static TransactionListAdapter getAdapter(){
        return adapter;
    }

    public static LocalDate getDate() {
        return date;
    }

    public static void setDate(LocalDate date) {
        MainActivity.date = date;
    }

    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        MainActivity.type = type;
    }

    public static int getSortType() {
        return sortType;
    }

    public static void setSortType(int sortType) {
        MainActivity.sortType = sortType;
    }

    public static double getBudget() {
        return budget;
    }

    public static void setBudget(double budget) {
        MainActivity.budget = budget;
    }

    public static double getTotalLimit() {
        return totalLimit;
    }

    public static void setTotalLimit(double totalLimit) {
        MainActivity.totalLimit = totalLimit;
    }

    public static double getMonthLimit() {
        return monthLimit;
    }

    public static void setMonthLimit(double monthLimit) {
        MainActivity.monthLimit = monthLimit;
    }

    public static TransactionListPresenter getPresenter() {return presenter;}

    public static ArrayList<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public static void setAllTransactions(ArrayList<Transaction> allTransactions) {
        MainActivity.allTransactions = allTransactions;
    }

    public static ArrayList<Transaction> getRealTransactions() {
        return realTransactions;
    }

    public static AccountDetailPresenter getAccPresenter() {
        return accPresenter;
    }

    public static ArrayList<Transaction.TYPE> getTransactionTypes() {
        return transactionTypes;
    }

    public static void setTransactionTypes(ArrayList<Transaction.TYPE> transactionTypes) {
        MainActivity.transactionTypes = transactionTypes;
    }

    public static boolean isTwoPaneMode() {
        return twoPaneMode;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Account account) {
        budget = account.getBudget();
        monthLimit = account.getMonthLimit();
        totalLimit = account.getTotalLimit();
        accPresenter.restartDB();
        accPresenter.writeDBAccount();
        OnePaneFragment.refreshAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(ArrayList<Transaction> transactions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allTransactions = transactions;
            adapter.setTransactions(transactions);
            adapter.dateFilterTransactions();
            presenter.restartDB();
            presenter.writeDBTransactions();
        }
    }

    @Override
    public void onReceive() {
        // primanje tipova, kada se dobave tipovi krece dobavljanje transakcija
        presenter.getTransactions();
    }

    public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent){
            System.out.println("IDENTIFIER INTENTA JE : " + intent.getFlags());

            if (!isConnected()) {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                if(!twoPaneMode) setOfflineMode("OFFLINE");
                if(prviUlazak) {
                    presenter.getDBTransactionsIntoApp();
                    accPresenter.getDBAccountIntoApp();
                }
            }
            else {
               Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
               if (!twoPaneMode) setOnlineMode();
               if (!prviUlazak) {
                   accPresenter.updateAPIviaDBAccount();
                   presenter.updateAPIviaDBTransactions();
               }
               else {
                   if(!presenter.updateAPIviaDBTransactions()){
                       presenter.getTransactionTypes();
                       accPresenter.getAccount();
                   }
                   else{
                       accPresenter.updateAPIviaDBAccount();
                       presenter.getDBTransactionsIntoApp();
                       accPresenter.getDBAccountIntoApp();
                   }
               }
            }
            prviUlazak = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public void setOnlineMode(){
        if(!twoPaneMode) {
            textView.setText("ONLINE");
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.online));
        }
    }

    public void setOfflineMode(String message){
        if(!twoPaneMode) {
            textView.setText(message);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.offline));
        }
    }

    public boolean isConnected(){
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
