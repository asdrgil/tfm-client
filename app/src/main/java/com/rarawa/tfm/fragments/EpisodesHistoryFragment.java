package com.rarawa.tfm.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.controllers.HistoryHandler;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.DayAxisValueFormatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.rarawa.tfm.utils.Constants.ONE_DAY_TIMESTAMP;

public class EpisodesHistoryFragment extends Fragment implements View.OnClickListener {

    public View rootView;

    private static final String ZERO = "0";
    private static final String TWO_DOTS = ":";
    private static final String SLASH = "/";

    public final Calendar calendar = Calendar.getInstance();
    public final Calendar calendar1 = Calendar.getInstance();
    public final Calendar calendar2 = Calendar.getInstance();

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

        btnQueryDate1 = (ImageButton) rootView.findViewById(R.id.btnQueryDate1);
        btnQueryDate2 = (ImageButton) rootView.findViewById(R.id.btnQueryDate2);
        btnFilterHistory = (Button) rootView.findViewById(R.id.btnFilterHistory);

        btnQueryDate1.setOnClickListener(this);
        btnQueryDate2.setOnClickListener(this);
        btnFilterHistory.setOnClickListener(this);


        //HashMap<Long, Integer> episodesPreviousWeek = getEpisodesPreviousWeek();

        //db.getNumberEpisodesPerDay(1595683560, 1595683850);

        //TODO: revisar a fondo con una semana en la que haya registros.
        //TODO: mirar el formato y basarse en setBarchart1
        //setBarchart2(rootView, episodesPreviousWeek);

        return rootView;
    }

    public HashMap<Long, Integer> getEpisodesPreviousWeek(){
        SqliteHandler db = new SqliteHandler(getContext());

        long currentTimestamp = System.currentTimeMillis()/1000;
        long previousWeekTimestamp = currentTimestamp - 7*24*60*60;

        HashMap<Long, Integer> result = db.getNumberEpisodesPerDay(currentTimestamp, previousWeekTimestamp);

        return result;
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
                HashMap<Long, Integer> search = performSearch();
                if(search != null){
                    setBarchart2(search);
                }
                break;
        }
    }

    private HashMap<Long, Integer> performSearch(){
        Log.d(Constants.LOG_TAG, "performSearch()");

        SqliteHandler db = new SqliteHandler(getContext());
        String queryDate1 = inputQueryDateText1.getText().toString();
        String queryDate2 = inputQueryDateText2.getText().toString();

        /* Multiple error checkings */

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
            return null;
        }

        if(queryDate2.length() == 0){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorEmptyField);
            return null;
        }

        //Invalid date format
        if (!queryDate1.matches(regexDateFormat)){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorInvalidFormat);
            return null;
        }

        //Invalid date format
        if (!queryDate2.matches(regexDateFormat)){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorInvalidFormat);
            return null;
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
        }

        if(timestamp1 > timestamp2){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorGreaterThan);
            return null;
        }

        if(timestamp1 > System.currentTimeMillis()/1000){
            inputQueryDateLayout1.requestFocus();
            inputQueryDateLayout1.setError(errorGreaterThanCurrent);
            return null;
        }

        if(timestamp2 > System.currentTimeMillis()/1000){
            inputQueryDateLayout2.requestFocus();
            inputQueryDateLayout2.setError(errorGreaterThanCurrent);
            return null;
        }

        Log.d(Constants.LOG_TAG, "timestamp1: " + timestamp1);
        Log.d(Constants.LOG_TAG, "timestamp2: " + timestamp2);

        return db.getNumberEpisodesPerDay(timestamp1,
                timestamp2);

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

    public void setBarchart2(HashMap<Long, Integer> dataRegisters){
        BarChart chart = rootView.findViewById(R.id.barchart1);

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

    /*public void setBarchart1(View rootView){
        BarChart chart = rootView.findViewById(R.id.barchart1);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(200f, 3f));
        entries.add(new BarEntry(100f, 8f));
        entries.add(new BarEntry(150f, 6f));
        entries.add(new BarEntry(151f, 1f));
        // gap of 2f
        entries.add(new BarEntry(152f, 7f));
        entries.add(new BarEntry(156f, 2f));

        BarDataSet set = new BarDataSet(entries, "Episodios/Día");

        set.setColor(ColorTemplate.rgb("#008000"));

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(Constants.MEASUREMENT_FREQUENCY/1000); // only intervals of 1 day
        xAxis.setLabelCount(7);
        //xAxis.setValueFormatter(xAxisFormatter);

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
    }*/

}
