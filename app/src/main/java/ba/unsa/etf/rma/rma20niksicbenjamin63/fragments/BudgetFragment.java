package ba.unsa.etf.rma.rma20niksicbenjamin63.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.R;

public class BudgetFragment extends Fragment{
    private EditText budget, monthL, totalL;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        budget = view.findViewById(R.id.budget);
        monthL = view.findViewById(R.id.monthL);
        totalL = view.findViewById(R.id.totalL);

        budget.setText(Double.toString(MainActivity.getBudget()));
        monthL.setText(Double.toString(MainActivity.getMonthLimit()));
        totalL.setText(Double.toString(MainActivity.getTotalLimit()));

        budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(budget.hasFocus()){
                    if(!budget.getText().toString().matches("[0-9.-]+"))
                        budget.setError("Budget can contain only NUMBERS.");
                    else
                        MainActivity.setBudget(Double.parseDouble(budget.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        monthL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(monthL.hasFocus()){
                    if(!monthL.getText().toString().matches("[0-9.-]+"))
                        monthL.setError("Month limit can contain only NUMBERS.");
                    else
                        MainActivity.setMonthLimit(Double.parseDouble(monthL.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        totalL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(totalL.hasFocus()){
                    if(!totalL.getText().toString().matches("[0-9.-]+"))
                        totalL.setError("Total limit can contain only NUMBERS.");
                    else
                        MainActivity.setTotalLimit(Double.parseDouble(totalL.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        budget.setOnFocusChangeListener((v, hasFocus) -> {
            updateGlobalVariables();
        });
        monthL.setOnFocusChangeListener((v, hasFocus) -> {
            updateGlobalVariables();
        });
        totalL.setOnFocusChangeListener((v, hasFocus) -> {
            updateGlobalVariables();
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void update() {
        budget.setText(Double.toString(MainActivity.getBudget()));
        totalL.setText(Double.toString(MainActivity.getTotalLimit()));
        monthL.setText(Double.toString(MainActivity.getMonthLimit()));
    }

    private void updateGlobalVariables(){
        if(budget.getError() == null && monthL.getError() == null && totalL.getError() == null) {
            if(((MainActivity) getActivity()).isConnected())
                MainActivity.getAccPresenter().updateAPIAccount(MainActivity.getBudget(), MainActivity.getMonthLimit(), MainActivity.getTotalLimit());
            else
                MainActivity.getAccPresenter().updateDBAccount();
            OnePaneFragment.refreshAdapter();
        }
    }
}
