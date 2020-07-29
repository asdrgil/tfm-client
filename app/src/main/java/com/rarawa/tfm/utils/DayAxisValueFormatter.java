package com.rarawa.tfm.utils;


import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DayAxisValueFormatter extends ValueFormatter {

    public final Calendar calendar = Calendar.getInstance();

    private final BarLineChartBase<?> chart;
    private final List<Long> keysList;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, HashMap<Long, Integer> dataRegisters) {
        this.chart = chart;

        Set<Long> keys = dataRegisters.keySet();

        // create an empty list
        this.keysList = new ArrayList<>();

        // push each element in the set into the list
        for (Long elem : keys) {
            keysList.add(elem);
        }
    }

    @Override
    public String getFormattedValue(float value) {

        calendar.setTimeInMillis((long) keysList.get((int) value) * 1000);

        return String.format(("%d/%d/%d"), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)%100);
    }
}
