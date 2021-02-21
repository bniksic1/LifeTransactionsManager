package ba.unsa.etf.rma.rma20niksicbenjamin63.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Account;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.OnePaneFragment;

public class AccountDetailPresenter implements IAccountDetailPresenter {

    private Context context;
    private AccountDetailInteractor interactor;

    public AccountDetailPresenter(Context context) {
        this.context = context;
        interactor = new AccountDetailInteractor();
    }

    public void updateAPIAccount(double budget, double monthL, double totalL){
        Intent intent = new Intent(context, AccountPOSTInteractor.class);
        intent.putExtra("budget", budget);
        intent.putExtra("monthL", monthL);
        intent.putExtra("totalL", totalL);
        context.startService(intent);
    }

    public void getAccount(){
        Intent intent = new Intent(context, AccountDetailInteractor.class);
        intent.putExtra("receiver", new AccountReceiver());
        intent.putExtra("type", 1);
        context.startService(intent);

    }

    public Account getDBAccount(){
        return interactor.getAccount(context);
    }

    public void updateDBAccount(){
        interactor.updateAccount(context);
    }

    public void updateAPIviaDBAccount() {
        Account account = getDBAccount();
        if(account != null) updateAPIAccount(account.getBudget(), account.getMonthLimit(), account.getTotalLimit());
    }

    public void restartDB() {
        interactor.restartDB(context);
    }

    public void getDBAccountIntoApp() {
        Account account = getDBAccount();
        MainActivity.setBudget(account.getBudget());
        MainActivity.setMonthLimit(account.getMonthLimit());
        MainActivity.setTotalLimit(account.getTotalLimit());
        if(OnePaneFragment.getScreenSlideAdapter() != null)
            OnePaneFragment.refreshAdapter();
    }

    public void writeDBAccount() {
        interactor.writeDBAccount(context);
    }

    public class AccountReceiver extends ResultReceiver {
        public AccountReceiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode == 1)
                ((OnReceiveAccount) context).onReceive((Account) resultData.getSerializable("account"));
        }
    }

    public interface OnReceiveAccount{
        void onReceive(Account account);
    }
}
