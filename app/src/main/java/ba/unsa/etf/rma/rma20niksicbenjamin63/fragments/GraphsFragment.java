package ba.unsa.etf.rma.rma20niksicbenjamin63.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma20niksicbenjamin63.MainActivity;
import ba.unsa.etf.rma.rma20niksicbenjamin63.R;
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

import static android.R.layout.simple_spinner_item;

public class GraphsFragment extends Fragment {
    private Spinner spinner;
    private BarChart consumptionChart, earningsChart, allChart;
    private ArrayAdapter adapter;
    private ArrayList<String> timeUnits;
    private ArrayList<Transaction> appMonthTransactions = MainActivity.getRealTransactions();
    private LocalDate appDate = MainActivity.getDate();

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graphs, container, false);
        spinner = view.findViewById(R.id.timeUnitSpinner);
        consumptionChart = view.findViewById(R.id.consumptionChart);
        earningsChart = view.findViewById(R.id.earningsChart);
        allChart = view.findViewById(R.id.allChart);

        //setup spinnera
        timeUnits = new ArrayList<String>(){{
            add("Months in year");
            add("Weeks in month");
            add("Days in month");
        }};
        adapter = new ArrayAdapter<>(getActivity(), simple_spinner_item, timeUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        ArrayList<String> months = new ArrayList<String>(){{
            add("January"); add("February"); add("March"); add("April"); add("May"); add("June");
            add("July"); add("August"); add("September"); add("October"); add("November"); add("December");
        }};

        ArrayList<String> weeks = new ArrayList<String>(){{
            add("Week 1"); add("Week 2"); add("Week 3"); add("Week 4"); add("Week 5");
        }};

        ArrayList<String> days = new ArrayList<String>(){{
            add("1"); add("2"); add("3"); add("4"); add("5"); add("6");
            add("7"); add("8"); add("9"); add("10"); add("11"); add("12");
            add("13"); add("14"); add("15"); add("16"); add("17"); add("18");
            add("19"); add("20"); add("21"); add("22"); add("23"); add("24");
            add("25"); add("26"); add("27"); add("28"); add("29"); add("30");
            add("31");
        }};

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Double> payments = getTransactions(position, true);
                ArrayList<Double> earnings = getTransactions(position, false);
                ArrayList<Double> summary = new ArrayList<>(payments);
                for(int i = 0; i < summary.size(); i++){
                    summary.set(i, payments.get(i) + earnings.get(i));
                }

                if(position == 0){
                    setConsumptionChart(payments, months, "Monthly consumption");
                    setEarningsChart(earnings, months, "Monthly earnings");
                    setAllChart(summary, months, "Monthly transactions");
                }
                else if(position == 1){
                    setConsumptionChart(payments, weeks, "Weekly consumption");
                    setEarningsChart(earnings, weeks, "Weekly earnings");
                    setAllChart(summary, weeks, "Weekly transactions");
                }
                else {
                    setConsumptionChart(payments, days, "Daily consumption");
                    setEarningsChart(earnings, days, "Daily earnings");
                    setAllChart(summary, days, "Daily transactions");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private boolean isPayment(Transaction t){
        return t.getType() == Transaction.TYPE.REGULARPAYMENT || t.getType() == Transaction.TYPE.INDIVIDUALPAYMENT || t.getType() == Transaction.TYPE.PURCHASE;
    }

    private boolean isRegularTransaction(Transaction t){
        return t.getType() == Transaction.TYPE.REGULARPAYMENT || t.getType() == Transaction.TYPE.REGULARINCOME;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Double> getTransactions(int timeUnit, boolean isPaymentTransactions){
        ArrayList<Double> result;
        if(timeUnit == 0){ //po mjesecima
            result  =new ArrayList<Double>(){{
            add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
            add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
            }};
            MainActivity.getAllTransactions().forEach(t -> {
                if(isPayment(t) == isPaymentTransactions){
                    if(isRegularTransaction(t) && t.getDate().isBefore(appDate) && t.getEndDate().isAfter(appDate)) {
                        // dodajemo jos zbog periodicnosti regularnih transakcija
                        LocalDate localDate = LocalDate.of(t.getDate().getYear(), t.getDate().getMonth(), t.getDate().getDayOfMonth());
                        while (t.getEndDate().isAfter(localDate)) {
                            if(localDate.getYear() == appDate.getYear()) result.set(localDate.getMonth().getValue() - 1, result.get(localDate.getMonth().getValue() - 1) + t.getAmount());
                            localDate = localDate.plusDays(t.getTransactionInterval());
                        }
                    }
                    else if(appDate.getYear() == t.getDate().getYear())
                        result.set(t.getDate().getMonth().getValue() - 1, result.get(t.getDate().getMonth().getValue() - 1) + t.getAmount());
                }
            });
        }
        else if(timeUnit == 1){ //po sedmicama u mjesecu
            result =new ArrayList<Double>(){{
                add(0d); add(0d); add(0d); add(0d); add(0d);
            }};
            appMonthTransactions.forEach(t -> {
                if(isPayment(t) == isPaymentTransactions){
                    if(isRegularTransaction(t) && t.getDate().isBefore(appDate) && t.getEndDate().isAfter(appDate)) {
                        // dodajemo jos zbog periodicnosti regularnih transakcija
                        LocalDate localDate = LocalDate.of(t.getDate().getYear(), t.getDate().getMonth(), t.getDate().getDayOfMonth());
                        while (t.getEndDate().isAfter(localDate)) {
                            if(localDate.getMonth().equals(appDate.getMonth())) result.set((t.getDate().getDayOfMonth() - 1)/ 7, result.get((t.getDate().getDayOfMonth() - 1)/ 7) + t.getAmount());
                            localDate = localDate.plusDays(t.getTransactionInterval());
                        }
                    }
                    else if(appDate.getMonth().equals(t.getDate().getMonth()))
                        result.set((t.getDate().getDayOfMonth() - 1)/ 7, result.get((t.getDate().getDayOfMonth() - 1)/ 7) + t.getAmount());
                }
            });
        }
        else{ //po danima u mjesecu
            result =new ArrayList<Double>(){{
                add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
                add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
                add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
                add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
                add(0d); add(0d); add(0d); add(0d); add(0d); add(0d);
                add(0d); // 31 dan
            }};
            appMonthTransactions.forEach(t -> {
                if(isPayment(t) == isPaymentTransactions){
                    if(isRegularTransaction(t) && t.getDate().isBefore(appDate) && t.getEndDate().isAfter(appDate)) {
                        // dodajemo jos zbog periodicnosti regularnih transakcija
                        LocalDate localDate = LocalDate.of(t.getDate().getYear(), t.getDate().getMonth(), t.getDate().getDayOfMonth());
                        while (t.getEndDate().isAfter(localDate)) {
                            if(localDate.getMonth().equals(appDate.getMonth())) result.set(t.getDate().getDayOfMonth() - 1, result.get(t.getDate().getDayOfMonth() - 1) + t.getAmount());
                            localDate = localDate.plusDays(t.getTransactionInterval());
                        }
                    }
                    else if(appDate.getMonth().equals(t.getDate().getMonth()))
                        result.set(t.getDate().getDayOfMonth() - 1, result.get(t.getDate().getDayOfMonth() - 1) + t.getAmount());
                }
            });
        }
        return result;
    }

    private void setConsumptionChart(ArrayList<Double> transactions, ArrayList<String> labels, String desc){
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++)
            barEntryArrayList.add(new BarEntry(i, transactions.get(i).floatValue()));
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, desc);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        consumptionChart.setData(barData);
        consumptionChart.getLegend().setEnabled(false);
        consumptionChart.getDescription().setText(desc);
        XAxis xAxis = consumptionChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(270);
        consumptionChart.animateY(1500);
        consumptionChart.invalidate();
    }

    private void setAllChart(ArrayList<Double> transactions, ArrayList<String> labels, String desc){
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++)
            barEntryArrayList.add(new BarEntry(i, transactions.get(i).floatValue()));
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, desc);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        allChart.setData(barData);
        allChart.getLegend().setEnabled(false);
        allChart.getDescription().setText(desc);
        XAxis xAxis = allChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(270);
        allChart.animateY(1500);
        allChart.invalidate();
    }

    private void setEarningsChart(ArrayList<Double> transactions, ArrayList<String> labels, String desc){
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++)
            barEntryArrayList.add(new BarEntry(i, transactions.get(i).floatValue()));
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, desc);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        earningsChart.setData(barData);
        earningsChart.getLegend().setEnabled(false);
        earningsChart.getDescription().setText(desc);
        XAxis xAxis = earningsChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(270);
        earningsChart.animateY(1500);
        earningsChart.invalidate();
    }

    public void update(){
        spinner.setSelection(0);
    }
}
