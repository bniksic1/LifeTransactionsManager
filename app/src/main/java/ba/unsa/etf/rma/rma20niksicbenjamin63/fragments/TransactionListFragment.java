package ba.unsa.etf.rma.rma20niksicbenjamin63.fragments;

import android.annotation.SuppressLint;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.R;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.SortArrayAdapter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.TransactionListAdapter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.TransactionTypeAdapter;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TransactionListFragment extends Fragment{
    private TextView global, limit, date;
    private ListView transactionsView;
    private TransactionListAdapter adapter;
    private LocalDate localDate = MainActivity.getDate();
    private ImageButton leftButton, rightButton;
    private Spinner filterSpinner, sortSpinner;
    private TransactionTypeAdapter filterAdapter;
    private ArrayList<Transaction.TYPE> transactionsType;
    private ArrayList<String> sortType;
    private SortArrayAdapter sortAdapter;
    private Button addTransaction;

    public interface OnItemClick {
        void onItemClicked(Transaction transaction);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        adapter = ((MainActivity) getActivity()).getAdapter();

        transactionsType = new ArrayList<Transaction.TYPE>(){{
            add(Transaction.TYPE.ANYTYPE);
            add(Transaction.TYPE.REGULARPAYMENT);
            add(Transaction.TYPE.REGULARINCOME);
            add(Transaction.TYPE.PURCHASE);
            add(Transaction.TYPE.INDIVIDUALINCOME);
            add(Transaction.TYPE.INDIVIDUALPAYMENT);}};

        filterAdapter = new TransactionTypeAdapter(getActivity(), R.layout.custom_layout, R.id.title, transactionsType);
        sortType = new ArrayList<String>(){{add("Sort by:");
            add("Amount - Ascending");
            add("Amount - Descending");
            add("Title - Ascending");
            add("Title - Descending");
            add("Date - Ascending");
            add("Date - Descending");}};
        sortAdapter = new SortArrayAdapter(getActivity(), R.layout.custom_layout, R.id.title, sortType);

        global = view.findViewById(R.id.global);
        limit = view.findViewById(R.id.limit);
        transactionsView = view.findViewById(R.id.transactionsView);
        date = view.findViewById(R.id.dateText);
        leftButton = view.findViewById(R.id.left);
        rightButton = view.findViewById(R.id.right);
        filterSpinner = view.findViewById(R.id.typeSpinner);
        sortSpinner = view.findViewById(R.id.sortSpinner);
        addTransaction = view.findViewById(R.id.transactionButton);

        // za ovu spiralu koristimo samo prvi account iz modela
        global.setText(Double.toString(((MainActivity) getActivity()).getBudget()));
        limit.setText(Double.toString(((MainActivity) getActivity()).getTotalLimit()));

        filterSpinner.setAdapter(filterAdapter);
        sortSpinner.setAdapter(sortAdapter);

        // u slucaju promjene orijentacije ostati ce isto sortiranje, datum i filtriranje
        filterSpinner.setSelection(((MainActivity) getActivity()).getType());
        sortSpinner.setSelection(((MainActivity) getActivity()).getSortType());

        date.setText(localDate.getMonth().toString() + ", " + localDate.getYear());
        transactionsView.setAdapter(adapter);

        leftButton.setOnClickListener(v -> {
            System.out.println("left");
            localDate = localDate.minusMonths(1);
            ((MainActivity) getActivity()).setDate(localDate);
            date.setText(localDate.getMonth().toString() + ", " + localDate.getYear());
            adapter.dateFilterTransactions();
        });

        rightButton.setOnClickListener(v -> {
            System.out.println("right");
            localDate = localDate.plusMonths(1);
            ((MainActivity) getActivity()).setDate(localDate);
            date.setText(localDate.getMonth().toString() + ", " + localDate.getYear());
            adapter.dateFilterTransactions();
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(((MainActivity) getActivity()).getType() != position) {
                    ((MainActivity) getActivity()).setType(position);
                    adapter.filterTransactions();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }

        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(((MainActivity) getActivity()).getType() != position) {
                    ((MainActivity) getActivity()).setSortType(position);
                    adapter.sortTransactions();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        transactionsView.setOnItemClickListener((parent, v, position, id) ->{
            v.setSelected(true);
            ((OnItemClick) getActivity()).onItemClicked((Transaction) transactionsView.getItemAtPosition(position));
        });

        // ovo je da kada skrola list view, da se ne skrola fragment umjesto liste
        transactionsView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });

        global.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Double.parseDouble(limit.getText().toString()) > Double.parseDouble(global.getText().toString())) {
                    limit.setTextColor(Color.parseColor("#ff0000"));
                }
                else {
                    limit.setTextColor(Color.parseColor("#818181"));
                }

                if(Double.parseDouble(global.getText().toString()) < 0){
                    global.setTextColor(Color.parseColor("#ff0000"));
                }
                else{
                    global.setTextColor(Color.parseColor("#818181"));
                }
            }
        });

        addTransaction.setOnClickListener(v -> {
            ((OnItemClick) getActivity()).onItemClicked(new Transaction(-1, MainActivity.getDate(), 0, "", Transaction.TYPE.REGULARINCOME, "", 7, MainActivity.getDate().plusMonths(1)));
        });

        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override
            public void onChanged()
            {
                update();
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void update() {
        global.setText(Double.toString(MainActivity.getBudget()));
        limit.setText(Double.toString(MainActivity.getTotalLimit()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(((MainActivity) getActivity()).isConnected())
            ((MainActivity) getActivity()).setOnlineMode();
        else
            ((MainActivity) getActivity()).setOfflineMode("OFFLINE");
    }
}
