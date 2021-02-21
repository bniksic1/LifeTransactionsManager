package ba.unsa.etf.rma.rma20niksicbenjamin63.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.R;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.TransactionTypeAdapter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

public class TransactionDetailFragment extends Fragment {
    private Transaction transaction, primaryTrans;
    private ImageView imageView;
    private EditText title, amount, description, interval;
    private Spinner typeSpinner;
    private DatePicker date, endDate;
    private Button delete, save;
    private TextView dateTV, endDateTV;
    private double monthL, totalL, budget;

    private TransactionTypeAdapter transactionTypeAdapter;
    private ArrayList<Transaction.TYPE> transactionsType;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        if(getArguments() == null || getArguments().get("trans") == null) return view;

        transaction = (Transaction) getArguments().getSerializable("trans");
        primaryTrans = new Transaction(transaction);

        monthL = ((MainActivity) getActivity()).getMonthLimit();
        totalL = ((MainActivity) getActivity()).getTotalLimit();
        budget = ((MainActivity) getActivity()).getBudget();

        transactionsType = new ArrayList<Transaction.TYPE>(){{
            add(Transaction.TYPE.PURCHASE);
            add(Transaction.TYPE.REGULARINCOME);
            add(Transaction.TYPE.INDIVIDUALINCOME);
            add(Transaction.TYPE.REGULARPAYMENT);
            add(Transaction.TYPE.INDIVIDUALPAYMENT);}};
        transactionTypeAdapter = new TransactionTypeAdapter(getActivity(), R.layout.custom_layout, R.id.textView, transactionsType);

        imageView = view.findViewById(R.id.ikonica);
        title = view.findViewById(R.id.title);
        amount = view.findViewById(R.id.amount);
        description = view.findViewById(R.id.description);
        interval = view.findViewById(R.id.interval);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        date = view.findViewById(R.id.date);
        endDate = view.findViewById(R.id.endDate);
        delete = view.findViewById(R.id.delete);
        save = view.findViewById(R.id.save);
        dateTV = view.findViewById(R.id.dateTextView);
        endDateTV = view.findViewById(R.id.endDateTextView);

        formSetupForNewTransaction();

        if(transaction.getTitle().equals("")) {
            delete.setText("Cancel");
        }
        if(!((MainActivity) getActivity()).isConnected()) {
            if(primaryTrans.getTitle().equals(""))
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE DODAVANJE");
            else if (MainActivity.getPresenter().isTransactionDeletedOffline(primaryTrans)) {
                delete.setText("Undo");
                delete.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.undo));
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE BRISANJE");
            }
            else {
                delete.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.offline));
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE IZMJENA");
            }
        }
        typeSpinner.setAdapter(transactionTypeAdapter);
        date.updateDate(transaction.getDate().getYear(), transaction.getDate().getMonth().getValue() - 1, transaction.getDate().getDayOfMonth());

        switch(transaction.getType()){
            case REGULARPAYMENT:
                imageView.setImageResource(R.drawable.regularpayment);
                typeSpinner.setSelection(3);
                break;
            case REGULARINCOME:
                imageView.setImageResource(R.drawable.regularincome);
                typeSpinner.setSelection(1);
                break;
            case PURCHASE:
                imageView.setImageResource(R.drawable.purchase);
                typeSpinner.setSelection(0);
                break;
            case INDIVIDUALINCOME:
                imageView.setImageResource(R.drawable.individualincome);
                typeSpinner.setSelection(2);
                break;
            case INDIVIDUALPAYMENT:
                imageView.setImageResource(R.drawable.individualpayment);
                typeSpinner.setSelection(4);
                break;
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Transaction.TYPE type = transactionsType.get(position);
                switch(type){
                    case REGULARPAYMENT:
                        imageView.setImageResource(R.drawable.regularpayment);
                        if(transaction.getType() != Transaction.TYPE.REGULARPAYMENT) typeSpinner.setBackgroundResource(R.drawable.spinner_validation);
                        else typeSpinner.setBackgroundResource(R.drawable.spinner_border);
                        description.setEnabled(true);
                        interval.setEnabled(true);
                        endDate.setEnabled(true);
                        break;
                    case REGULARINCOME:
                        imageView.setImageResource(R.drawable.regularincome);
                        if(transaction.getType() != Transaction.TYPE.REGULARINCOME) typeSpinner.setBackgroundResource(R.drawable.spinner_validation);
                        else typeSpinner.setBackgroundResource(R.drawable.spinner_border);
                        description.setEnabled(true);
                        interval.setEnabled(true);
                        endDate.setEnabled(true);
                        break;
                    case PURCHASE:
                        imageView.setImageResource(R.drawable.purchase);
                        if(transaction.getType() != Transaction.TYPE.PURCHASE) typeSpinner.setBackgroundResource(R.drawable.spinner_validation);
                        else typeSpinner.setBackgroundResource(R.drawable.spinner_border);
                        description.setEnabled(true);
                        interval.setEnabled(false);
                        endDate.setEnabled(false);
                        interval.setText("/");
                        break;
                    case INDIVIDUALINCOME:
                        imageView.setImageResource(R.drawable.individualincome);
                        if(transaction.getType() != Transaction.TYPE.INDIVIDUALINCOME) typeSpinner.setBackgroundResource(R.drawable.spinner_validation);
                        else typeSpinner.setBackgroundResource(R.drawable.spinner_border);
                        description.setEnabled(false);
                        interval.setEnabled(false);
                        endDate.setEnabled(false);
                        interval.setText("/");
                        description.setText("/");
                        break;
                    case INDIVIDUALPAYMENT:
                        imageView.setImageResource(R.drawable.individualpayment);
                        if(transaction.getType() != Transaction.TYPE.INDIVIDUALPAYMENT) typeSpinner.setBackgroundResource(R.drawable.spinner_validation);
                        else typeSpinner.setBackgroundResource(R.drawable.spinner_border);
                        description.setEnabled(false);
                        interval.setEnabled(false);
                        endDate.setEnabled(false);
                        interval.setText("/");
                        description.setText("/");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s){
                if(title.getText().length() < 4 || title.getText().length() > 14){
                    title.setError("Title must contain greater than 3 and less than 15 letters.");
                    title.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                            PorterDuff.Mode.SRC_ATOP);
                }
                else  title.getBackground().setColorFilter(getResources().getColor(R.drawable.validation_color),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!amount.getText().toString().matches("[0-9.]+")) {
                    amount.setError("Amount can contain only positive NUMBERS.");
                    amount.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                            PorterDuff.Mode.SRC_ATOP);
                }
                else amount.getBackground().setColorFilter(getResources().getColor(R.drawable.validation_color),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s){
                if(description.isEnabled()) description.getBackground().setColorFilter(getResources().getColor(R.drawable.validation_color),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });

        interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s){
                if(!interval.getText().toString().matches("[0-9]+") && interval.isEnabled()) {
                    interval.setError("Amount can contain only positive NUMBERS.");
                    interval.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                            PorterDuff.Mode.SRC_ATOP);
                }
                else if(interval.isEnabled()) interval.getBackground().setColorFilter(getResources().getColor(R.drawable.validation_color),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });

        date.setOnDateChangedListener((v, y, m, d) -> {
            if(LocalDate.of(endDate.getYear(), endDate.getMonth() + 1, endDate.getDayOfMonth()).isBefore(LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDayOfMonth()))) {
                dateTV.setTextColor(Color.parseColor("#ff0000"));
                endDateTV.setTextColor(Color.parseColor("#ff0000"));
                Toast.makeText(getActivity().getApplicationContext(),"Date can not be after end date of transaction",Toast.LENGTH_SHORT).show();
            }
            else{
                dateTV.setTextColor(Color.parseColor("#59ed9c"));
                endDateTV.setTextColor(Color.parseColor("#59ed9c"));
            }
        });

        endDate.setOnDateChangedListener((v, y, m, d) -> {
            if(LocalDate.of(endDate.getYear(), endDate.getMonth() + 1, endDate.getDayOfMonth()).isBefore(LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDayOfMonth()))) {
                dateTV.setTextColor(Color.parseColor("#ff0000"));
                endDateTV.setTextColor(Color.parseColor("#ff0000"));
                Toast.makeText(getActivity().getApplicationContext(),"Date can not be after end date of transaction",Toast.LENGTH_SHORT).show();
            }
            else{
                dateTV.setTextColor(Color.parseColor("#59ed9c"));
                endDateTV.setTextColor(Color.parseColor("#59ed9c"));
            }
        });

        delete.setOnClickListener(v -> {
            if(delete.getText().equals("Undo")){
                MainActivity.getPresenter().recoverDBTransaction(primaryTrans);
                delete.setText("Delete");
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE IZMJENA");
                delete.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.offline));
            }
            else if(primaryTrans.getTitle().equals("")){
                if(getActivity().findViewById(R.id.frameDetail) != null)
                    formSetupForNewTransaction();
                else{
                    TransactionListFragment transactionListFragment = new TransactionListFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameList, transactionListFragment).commit();
                }
            }
            else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete transaction")
                        .setMessage("Are you sure you want to delete this transaction?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            //obrisati preko API i lokalno
                            if(((MainActivity) getActivity()).isConnected()) {
                                ((MainActivity) getActivity()).getPresenter().delete(primaryTrans);
                                MainActivity.getPresenter().deleteDBOnline(primaryTrans);
                                ((MainActivity) getActivity()).remove(primaryTrans);
                                ((MainActivity) getActivity()).setOfflineMode("OFFLINE BRISANJE");
                            }
                            else
                                MainActivity.getPresenter().deleteDBOffline(primaryTrans);
                            //promijeniti budzet preko API i lokalno
                            double newBudget = MainActivity.getBudget() - primaryTrans.getAmount();
                            MainActivity.setBudget(newBudget);
                            OnePaneFragment.refreshAdapter();
                            if(((MainActivity) getActivity()).isConnected())
                                MainActivity.getAccPresenter().updateAPIAccount(newBudget, MainActivity.getMonthLimit(), MainActivity.getTotalLimit());
                            else
                                MainActivity.getAccPresenter().updateDBAccount();

                            if(((MainActivity) getActivity()).isConnected()) {
                                if (getActivity().findViewById(R.id.frameDetail) != null) {
                                    transaction = new Transaction(-1, LocalDate.now(), 0, "", Transaction.TYPE.REGULARINCOME, "", 7, LocalDate.now().plusMonths(1));
                                    primaryTrans = new Transaction(transaction);
                                    formSetupForNewTransaction();
                                }
                                Fragment fragment;
                                if (MainActivity.isTwoPaneMode()) {
                                    fragment = new TransactionListFragment();
                                } else
                                    fragment = new OnePaneFragment();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameList, fragment).commit();
                            }
                            else{
                                delete.setText("Undo");
                                delete.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.undo));
                                ((MainActivity) getActivity()).setOfflineMode("OFFLINE BRISANJE");
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        save.setOnClickListener(v -> {
            if(isErrorInValidation()) {
                Toast.makeText(getActivity().getApplicationContext(),"You must solve validation issue before saving.",Toast.LENGTH_SHORT).show();
                return;
            }

            title.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                    PorterDuff.Mode.SRC_ATOP);
            amount.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                    PorterDuff.Mode.SRC_ATOP);
            typeSpinner.setBackgroundResource(R.drawable.spinner_border);
            dateTV.setTextColor(Color.parseColor("#737373"));
            description.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                    PorterDuff.Mode.SRC_ATOP);
            interval.getBackground().setColorFilter(getResources().getColor(R.drawable.nonvalidation_color),
                    PorterDuff.Mode.SRC_ATOP);
            endDateTV.setTextColor(Color.parseColor("#737373"));

            if(typeSpinner.getSelectedItem() == Transaction.TYPE.REGULARPAYMENT || typeSpinner.getSelectedItem() == Transaction.TYPE.INDIVIDUALPAYMENT || typeSpinner.getSelectedItem() == Transaction.TYPE.PURCHASE){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Save transaction")
                        .setPositiveButton("Yes", (dialog, which) -> setValues())
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert);

                if(budget - transaction.getAmount() - Double.parseDouble(amount.getText().toString()) < 0){
                    alertDialog.setMessage("You don't have enough budget for this transaction. Are you sure you want to save this transaction?");
                    alertDialog.show();
                }
                else if(totalL > budget - transaction.getAmount() - Double.parseDouble(amount.getText().toString())) {
                    alertDialog.setMessage("You will cross total limit of account with this modification. Are you sure you want to save this transaction?");
                    alertDialog.show();
                }
                else if(monthL > budget - transaction.getAmount() - Double.parseDouble(amount.getText().toString())) {
                    alertDialog.setMessage("You will cross month limit of account with this modification. Are you sure you want to save this transaction?");
                    alertDialog.show();
                }
                else setValues();
            }
            else setValues();
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setValues(){
        if(primaryTrans.getTitle().equals("")){
            delete.setText("Delete");
            delete.setTextColor(Color.parseColor("#ff0000"));
        }

        transaction.setTitle(title.getText().toString());
        transaction.setAmount(Double.parseDouble(amount.getText().toString()));
        transaction.setType((Transaction.TYPE) typeSpinner.getSelectedItem());
        transaction.setDate(LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDayOfMonth()));

        if(transaction.getType() == Transaction.TYPE.REGULARPAYMENT || transaction.getType() == Transaction.TYPE.INDIVIDUALPAYMENT || transaction.getType() == Transaction.TYPE.PURCHASE)
            transaction.setAmount(transaction.getAmount() * -1);
        if(transaction.getType() != Transaction.TYPE.INDIVIDUALPAYMENT && transaction.getType() != Transaction.TYPE.INDIVIDUALINCOME)
            transaction.setItemDescription(description.getText().toString());
        if(transaction.getType() == Transaction.TYPE.REGULARPAYMENT || transaction.getType() == Transaction.TYPE.REGULARINCOME){
            transaction.setTransactionInterval(Integer.parseInt(interval.getText().toString()));
            transaction.setEndDate(LocalDate.of(endDate.getYear(), endDate.getMonth() + 1, endDate.getDayOfMonth()));
        }

        double newBudget = MainActivity.getBudget() + transaction.getAmount();
        if(primaryTrans.getTitle().equals("")){
            // dodavanje preko API i lokalno

            MainActivity.getAdapter().addTransaction(transaction);
            if(((MainActivity) getActivity()).isConnected())
                MainActivity.getPresenter().addTransaction(transaction);
            else
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE DODAVANJE");
            MainActivity.getPresenter().writeDBOffline(transaction);
        }
        else {
            // modifikacija preko API i lokalno
            if(((MainActivity) getActivity()).isConnected())
                MainActivity.getPresenter().modify(transaction);
            else
                ((MainActivity) getActivity()).setOfflineMode("OFFLINE IZMJENA");
            MainActivity.getAdapter().modifyTransaction(primaryTrans, transaction);
            MainActivity.getPresenter().modifyDBTransaction(primaryTrans, transaction);

            // promjena budzeta preko API i lokalno
            newBudget -= primaryTrans.getAmount();
        }
        MainActivity.setBudget(newBudget);
        OnePaneFragment.refreshAdapter();
        if(((MainActivity) getActivity()).isConnected())
            MainActivity.getAccPresenter().updateAPIAccount(newBudget, MainActivity.getMonthLimit(), MainActivity.getTotalLimit());
        MainActivity.getAccPresenter().updateDBAccount();

        primaryTrans = new Transaction(transaction);
    }

    private void resetVariables(){
        title.setText(title.getText());
        amount.setText(amount.getText());
        if(typeSpinner.getSelectedItemPosition() == 1 || typeSpinner.getSelectedItemPosition() == 3)
            interval.setText(interval.getText());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isErrorInValidation(){
        resetVariables();
        return title.getError() != null || amount.getError() != null || ((typeSpinner.getSelectedItemPosition() == 1 || typeSpinner.getSelectedItemPosition() == 3) && (interval.getError() != null || LocalDate.of(endDate.getYear(), endDate.getMonth() + 1, endDate.getDayOfMonth()).isBefore(LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDayOfMonth()))));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void formSetupForNewTransaction(){
        title.setText(transaction.getTitle());
        amount.setText(Double.toString(Math.abs(transaction.getAmount())));
        if(transaction.getItemDescription() != null) description.setText(transaction.getItemDescription());
        if(transaction.getTransactionInterval() != null) interval.setText(transaction.getTransactionInterval().toString());
        if(transaction.getEndDate() != null) endDate.updateDate(transaction.getEndDate().getYear(), transaction.getEndDate().getMonth().getValue() - 1, transaction.getEndDate().getDayOfMonth());
        typeSpinner.setSelection(1);
        typeSpinner.setBackgroundResource(R.drawable.spinner_border);
    }


}
