package ba.unsa.etf.rma.rma20niksicbenjamin63.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.R;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

public class TransactionListAdapter extends ArrayAdapter<Transaction>{
    int resource;
    private ImageView ikonica;
    private TextView title;
    private TextView iznos;
    private ArrayList<Transaction> realTransactions;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TransactionListAdapter(Context context, int _resource, ArrayList<Transaction> items) {
        super(context, _resource, items);
        resource = _resource; //resource je id layout-a list item-a
        realTransactions = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().
                    getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }

        Transaction transaction = getItem(position);


        title = newView.findViewById(R.id.title);
        iznos = newView.findViewById(R.id.iznos);
        ikonica = newView.findViewById(R.id.ikonica);
        title.setText(transaction.getTitle());
        iznos.setText(transaction.getAmount() + " BAM");

        switch(transaction.getType()){
            case REGULARPAYMENT:
                ikonica.setImageResource(R.drawable.regularpayment);
                break;
            case REGULARINCOME:
                ikonica.setImageResource(R.drawable.regularincome);
                break;
            case PURCHASE:
                ikonica.setImageResource(R.drawable.purchase);
                break;
            case INDIVIDUALINCOME:
                ikonica.setImageResource(R.drawable.individualincome);
                break;
            case INDIVIDUALPAYMENT:
                ikonica.setImageResource(R.drawable.individualpayment);
                break;
        }
        return newView;
    }

    public void setTransactions(ArrayList<Transaction> trans){
        realTransactions.clear();
        realTransactions.addAll(trans);
        this.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Transaction> filtering(){
        return dateFiltering().stream().filter(t ->
                MainActivity.getType() == 0 || MainActivity.getType() == getAppType(t.getType())
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterTransactions(){
        realTransactions.clear();
        realTransactions.addAll(filtering());
        this.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sortTransactions(){
        switch (MainActivity.getSortType()){
            case 1:
                Collections.sort(realTransactions, (t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount()));
                break;
            case 2:
                Collections.sort(realTransactions, (t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount()));
                Collections.reverse(realTransactions);
                break;
            case 3:
                Collections.sort(realTransactions, (t1, t2) -> t1.getTitle().compareTo(t2.getTitle()));
                break;
            case 4:
                Collections.sort(realTransactions, (t1, t2) -> t1.getTitle().compareTo(t2.getTitle()));
                Collections.reverse(realTransactions);
                break;
            case 5:
                Collections.sort(realTransactions, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                break;
            case 6:
                Collections.sort(realTransactions, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                Collections.reverse(realTransactions);
                break;
            default:
                break;
        }
        this.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Transaction> dateFiltering(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ArrayList<Transaction> result = MainActivity.getAllTransactions().stream().filter(t -> {
                if (t.getType() == Transaction.TYPE.REGULARINCOME || t.getType() == Transaction.TYPE.REGULARPAYMENT)
                    return (isEqualDates(MainActivity.getDate(), t.getDate()) || MainActivity.getDate().isAfter(t.getDate())) && (MainActivity.getDate().isBefore(t.getEndDate()) || isEqualDates(MainActivity.getDate(), t.getEndDate()));
                else return isEqualDates(MainActivity.getDate(), t.getDate());
            }).collect(Collectors.toCollection(ArrayList::new));
            // ponavljanje regularnih transakcija
            for(Transaction t : MainActivity.getAllTransactions()) {
                if (t.getType() == Transaction.TYPE.REGULARINCOME || t.getType() == Transaction.TYPE.REGULARPAYMENT){
                    if ((isEqualDates(MainActivity.getDate(), t.getDate()) || MainActivity.getDate().isAfter(t.getDate())) && (MainActivity.getDate().isBefore(t.getEndDate()) || isEqualDates(MainActivity.getDate(), t.getEndDate())))
                        for (int i = 0; i < (30 / t.getTransactionInterval()) - 1; i++)
                            result.add(new Transaction(t));
                }
            }
            return result;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void dateFilterTransactions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            filterTransactions();
            sortTransactions();
            this.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeTransaction(Transaction t){
        MainActivity.getAllTransactions().remove(t);
        this.dateFilterTransactions();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void modifyTransaction(Transaction oldT, Transaction newT){
        for(int i = 0; i < MainActivity.getAllTransactions().size(); i++)
            if(MainActivity.getAllTransactions().get(i).equals(oldT)){
                MainActivity.getAllTransactions().get(i).setTitle(newT.getTitle());
                MainActivity.getAllTransactions().get(i).setAmount(newT.getAmount());
                MainActivity.getAllTransactions().get(i).setType(newT.getType());
                MainActivity.getAllTransactions().get(i).setDate(newT.getDate());
                MainActivity.getAllTransactions().get(i).setItemDescription(newT.getItemDescription());
                MainActivity.getAllTransactions().get(i).setTransactionInterval(newT.getTransactionInterval());
                MainActivity.getAllTransactions().get(i).setEndDate(newT.getEndDate());
            }
        this.dateFilterTransactions();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addTransaction(Transaction t) {
        MainActivity.getAllTransactions().add(t);
        this.dateFilterTransactions();
    }

    private int getAppType(Transaction.TYPE type){
        switch (type){
            case REGULARPAYMENT:
                return 1;
            case REGULARINCOME:
                return 2;
            case PURCHASE:
                return 3;
            case INDIVIDUALINCOME:
                return 4;
            case INDIVIDUALPAYMENT:
                return 5;
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isEqualDates(LocalDate ld1, LocalDate ld2){
        return ld1.getMonth() == ld2.getMonth() && ld1.getYear() == ld2.getYear();
    }
}
