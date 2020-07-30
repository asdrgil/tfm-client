package com.rarawa.tfm.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.DayAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EpisodesHistoryFragment extends Fragment implements View.OnClickListener {

    public View rootView;

    private static final String ZERO = "0";
    private static final String SLASH = "/";

    public final Calendar calendar = Calendar.getInstance();

    //Date
    final int day = calendar.get(Calendar.DAY_OF_MONTH);
    final int month = calendar.get(Calendar.MONTH);
    final int year = calendar.get(Calendar.YEAR);

    TextInputLayout inputQueryDateLayout1, inputQueryDateLayout2;
    TextInputEditText inputQueryDateText1, inputQueryDateText2;
    ImageButton btnQueryDate1, btnQueryDate2;
    Button btnFilterHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        inputQueryDateLayout1 = rootView.findViewById(R.id.inputQueryDate1Layout);
        inputQueryDateLayout2 = rootView.findViewById(R.id.inputQueryDate2Layout);

        inputQueryDateText1 = rootView.findViewById(R.id.inputQueryDate1Text);
        inputQueryDateText2 = rootView.findViewById(R.id.inputQueryDate2Text);

        btnQueryDate1 = rootView.findViewById(R.id.btnQueryDate1);
        btnQueryDate2 = rootView.findViewById(R.id.btnQueryDate2);
        btnFilterHistory = rootView.findViewById(R.id.btnFilterHistory);

        btnQueryDate1.setOnClickListener(this);
        btnQueryDate2.setOnClickListener(this);
        btnFilterHistory.setOnClickListener(this);

        BarChart chart1 = rootView.findViewById(R.id.barchart1);
        chart1.setNoDataText("No se ha encontrado ningún registro");
        BarChart chart2 = rootView.findViewById(R.id.barchart2);
        chart2.setNoDataText("No se ha encontrado ningún registro");

        //TODO: revisar a fondo cuando hay más de 15 registros que graficar
        // (seguramente haya que agrupar por meses)

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnQueryDate1:
                getDate(0);
                break;
            case R.id.btnQueryDate2:
                getDate(1);
                break;
            case R.id.btnFilterHistory:
                if(isInputDateCorrect()) {
                    Map<Integer, Long> timestamps = getInputTimestamp();

                    HashMap<Long, Integer> searchBarchar1 = searchBarchar1(timestamps);
                    HashMap<Long, Integer> searchBarchar2 = searchBarchar2(timestamps);
                    HashMap<Long, Integer> searchBarchar3 = searchBarchar3(timestamps);

                    if (searchBarchar1 != null) {
                        plotBarchart1(searchBarchar1);

                    }

                    if (searchBarchar2 != null) {
                        plotBarchart2(searchBarchar2);
                    }

                    if (searchBarchar3 != null) {
                        plotBarchart3(searchBarchar3);
                    }
                }
                break;
        }
    }

    private boolean isInputDateCorrect(){

        String queryDate1 = inputQueryDateText1.getText().toString();
        String queryDate2 = inputQueryDateText2.getText().toString();

        String errorEmptyField = "Es necesario rellenar este campo";
        String errorInvalidFormat = "Formato de fecha incorrecto";
        String errorGreaterThan = "La segunda fecha debe ser mayor que la primera";
        String errorGreaterThanCurrent = "La fecha no puede ser mayor que el día actual";

        String regexDateFormat = "([0-9]{2})/([0-9]{2})/([0-9]{4})";

        inputQueryDateLayout1.setError("");
        inputQueryDateLayout2.setError("");

        if(queryDate1.length() == 0){
            Log.d(Constants.LOG_TAG, "setError");
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorEmptyField);
            return false;
        }

        if(queryDate2.length() == 0){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorEmptyField);
            return false;
        }

        //Invalid date format
        if (!queryDate1.matches(regexDateFormat)){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorInvalidFormat);
            return false;
        }

        //Invalid date format
        if (!queryDate2.matches(regexDateFormat)){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorInvalidFormat);
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        long timestamp1 = 0;
        long timestamp2 = 0;

        try {
            Date date1=dateFormat.parse(queryDate1);
            timestamp1 = date1.getTime()/1000;

            Date date2=dateFormat.parse(queryDate2);
            timestamp2 = date2.getTime()/1000;
        } catch (ParseException e) {
            Log.d(Constants.LOG_TAG, "ParseException");
            e.printStackTrace();
            return false;
        }

        if(timestamp1 > timestamp2){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorGreaterThan);
            return false;
        }

        if(timestamp1 > System.currentTimeMillis()/1000){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorGreaterThanCurrent);
            return false;
        }

        if(timestamp2 > System.currentTimeMillis()/1000){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorGreaterThanCurrent);
            return false;
        }

        Log.d(Constants.LOG_TAG, "timestamp1: " + timestamp1);
        Log.d(Constants.LOG_TAG, "timestamp2: " + timestamp2);

        return true;

    }

    private Map<Integer, Long> getInputTimestamp(){
        String queryDate1 = inputQueryDateText1.getText().toString();
        String queryDate2 = inputQueryDateText2.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        long timestamp1 = 0;
        long timestamp2 = 0;

        try {
            Date date1=dateFormat.parse(queryDate1);
            timestamp1 = date1.getTime()/1000;

            Date date2=dateFormat.parse(queryDate2);
            timestamp2 = date2.getTime()/1000;
        } catch (ParseException e) {
            //
        }

        Map<Integer, Long> result = new HashMap<>();

        result.put(0, timestamp1);
        result.put(1, timestamp2);

        return result;
    }

    private HashMap<Long, Integer> searchBarchar1(Map<Integer, Long> timestamps){
        Log.d(Constants.LOG_TAG, "searchBarchar1()");
        SqliteHandler db = new SqliteHandler(getContext());

        return db.getNumberEpisodesPerDay(timestamps.get(0),
                timestamps.get(1));

    }

    private HashMap<Long, Integer> searchBarchar3(Map<Integer, Long> timestamps){
        Log.d(Constants.LOG_TAG, "searchBarchar2()");
        SqliteHandler db = new SqliteHandler(getContext());

        return db.getEpisodesDurationPerDay(timestamps.get(0),
                timestamps.get(1));

    }

    private HashMap<Long, Integer> searchBarchar2(Map<Integer, Long> timestamps){
        Log.d(Constants.LOG_TAG, "searchBarchar3()");
        SqliteHandler db = new SqliteHandler(getContext());

        return db.getEpisodesAverageDurationPerDay(timestamps.get(0),
                timestamps.get(1));

    }

    public void plotBarchart1(HashMap<Long, Integer> dataRegisters){
        BarChart chart = rootView.findViewById(R.id.barchart1);
        chart.setVisibility(View.VISIBLE);

        if(dataRegisters != null && !dataRegisters.isEmpty()) {

            ArrayList<BarEntry> barRegisters = new ArrayList<>();

            float minimumTimestamp = 0;
            float maximumTimestamp = 0;

            Iterator it = dataRegisters.entrySet().iterator();
            float i = 0;

            while (it.hasNext()) {
                Map.Entry element = (Map.Entry) it.next();

                if(minimumTimestamp == 0 ||
                        minimumTimestamp > Long.parseLong(element.getKey().toString())){
                    minimumTimestamp = Long.parseLong(element.getKey().toString());
                }

                if(maximumTimestamp == 0 ||
                        maximumTimestamp < Long.parseLong(element.getKey().toString())){
                    maximumTimestamp = Long.parseLong(element.getKey().toString());
                }

                barRegisters.add(new BarEntry(i++,
                        Float.parseFloat(element.getValue().toString())));
            }

            BarDataSet set = new BarDataSet(barRegisters, "Episodios/Día");
            set.setColor(ColorTemplate.rgb("#008000"));

            ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart, dataRegisters);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(dataRegisters.size());
            xAxis.mAxisMinimum = minimumTimestamp;
            xAxis.mAxisMaximum = maximumTimestamp;
            xAxis.setValueFormatter(xAxisFormatter);

            Description description = new Description();
            description.setText("");

            chart.setDescription(description);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.setHighlightFullBarEnabled(true);
            //chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.setDrawGridBackground(false);
            chart.setDrawBorders(true);
            chart.setContentDescription("Content description tryout");
            chart.setNoDataText("No se ha encontrado ningún registro");
            chart.animateXY(300, 700);
            chart.invalidate(); // refresh
        }
    }

    public void plotBarchart2(HashMap<Long, Integer> dataRegisters){
        BarChart chart = rootView.findViewById(R.id.barchart2);
        chart.setVisibility(View.VISIBLE);

        if(dataRegisters != null && !dataRegisters.isEmpty()) {

            ArrayList<BarEntry> barRegisters = new ArrayList<>();

            float minimumTimestamp = 0;
            float maximumTimestamp = 0;

            Iterator it = dataRegisters.entrySet().iterator();
            float i = 0;

            while (it.hasNext()) {
                Map.Entry element = (Map.Entry) it.next();

                if(minimumTimestamp == 0 ||
                        minimumTimestamp > Long.parseLong(element.getKey().toString())){
                    minimumTimestamp = Long.parseLong(element.getKey().toString());
                }

                if(maximumTimestamp == 0 ||
                        maximumTimestamp < Long.parseLong(element.getKey().toString())){
                    maximumTimestamp = Long.parseLong(element.getKey().toString());
                }

                barRegisters.add(new BarEntry(i++,
                        Float.parseFloat(element.getValue().toString())));
            }

            BarDataSet set = new BarDataSet(barRegisters, "Duración media de los episodios [segundos]/Día");
            set.setColor(ColorTemplate.rgb("#21618C"));

            ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart, dataRegisters);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(dataRegisters.size());
            xAxis.mAxisMinimum = minimumTimestamp;
            xAxis.mAxisMaximum = maximumTimestamp;
            xAxis.setValueFormatter(xAxisFormatter);

            Description description = new Description();
            description.setText("");

            chart.setDescription(description);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.setHighlightFullBarEnabled(true);
            //chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.setDrawGridBackground(false);
            chart.setDrawBorders(true);
            chart.setContentDescription("Content description tryout");
            chart.animateXY(300, 700);
            chart.invalidate(); // refresh
        }
    }

    public void plotBarchart3(HashMap<Long, Integer> dataRegisters){
        BarChart chart = rootView.findViewById(R.id.barchart3);
        chart.setVisibility(View.VISIBLE);

        if(dataRegisters != null && !dataRegisters.isEmpty()) {

            ArrayList<BarEntry> barRegisters = new ArrayList<>();

            float minimumTimestamp = 0;
            float maximumTimestamp = 0;

            Iterator it = dataRegisters.entrySet().iterator();
            float i = 0;

            while (it.hasNext()) {
                Map.Entry element = (Map.Entry) it.next();

                if(minimumTimestamp == 0 ||
                        minimumTimestamp > Long.parseLong(element.getKey().toString())){
                    minimumTimestamp = Long.parseLong(element.getKey().toString());
                }

                if(maximumTimestamp == 0 ||
                        maximumTimestamp < Long.parseLong(element.getKey().toString())){
                    maximumTimestamp = Long.parseLong(element.getKey().toString());
                }

                barRegisters.add(new BarEntry(i++,
                        Float.parseFloat(element.getValue().toString())));
            }

            BarDataSet set = new BarDataSet(barRegisters, "Duración total de los episodios [segundos]/Día");
            set.setColor(ColorTemplate.rgb("#FFA500"));

            ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart, dataRegisters);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(dataRegisters.size());
            xAxis.mAxisMinimum = minimumTimestamp;
            xAxis.mAxisMaximum = maximumTimestamp;
            xAxis.setValueFormatter(xAxisFormatter);

            Description description = new Description();
            description.setText("");

            chart.setDescription(description);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.setHighlightFullBarEnabled(true);
            //chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.setDrawGridBackground(false);
            chart.setDrawBorders(true);
            chart.setContentDescription("Content description tryout");
            chart.animateXY(300, 700);
            chart.invalidate(); // refresh
        }
    }

    private void getDate(int dateId){
        DatePickerDialog getDateVar = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int currentMonth = month + 1;

                String formattedDay = (dayOfMonth < 10)? ZERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String formattedMonth = (currentMonth < 10)? ZERO + String.valueOf(currentMonth):String.valueOf(currentMonth);
                String formattedText = formattedDay + SLASH + formattedMonth + SLASH + year;

                Log.d(Constants.LOG_TAG, formattedText);

                if(dateId == 0) {
                    inputQueryDateText1.setText(formattedText);
                } else {
                    inputQueryDateText2.setText(formattedText);
                }


            }
        },year, month, day);

        getDateVar.show();

    }

}
