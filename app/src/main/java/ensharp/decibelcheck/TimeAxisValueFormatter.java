package ensharp.decibelcheck;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by Semin on 2017-02-27.
 */
public class TimeAxisValueFormatter implements IAxisValueFormatter {

    private BarLineChartBase<?> chart;
    private int count;

    public TimeAxisValueFormatter(BarLineChartBase<?> chart) {
        //Log.i("여기","public DayAxisValueFormatter(BarLineChartBase<?> chart)");
        this.chart = chart;
        count = 0;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        //Log.i("여기","public String getFormattedValue(float value, AxisBase axis)");
        int time = (int) value;
        //Log.i("값","count = " + count + " value : " + value);
        String result = "";

        if (time == 1) {
            result = "00";
        } else if (time == 2) {
            result = "01";
        } else if (time == 3) {
            result = "02";
        } else if (time == 4) {
            result = "03";
        } else if (time == 5) {
            result = "04";
        } else if (time == 6) {
            result = "05";
        } else if (time == 7) {
            result = "06";
        } else if (time == 8) {
            result = "07";
        } else if (time == 9) {
            result = "08";
        } else if (time == 10) {
            result = "09";
        } else if (time == 11) {
            result = "10";
        } else if (time == 12) {
            result = "11";
        } else if (time == 13) {
            result = "12";
        } else if (time == 14) {
            result = "13";
        } else if (time == 15) {
            result = "14";
        } else if (time == 16) {
            result = "15";
        } else if (time == 17) {
            result = "16";
        } else if (time == 18) {
            result = "17";
        } else if (time == 19) {
            result = "18";
        } else if (time == 20) {
            result = "19";
        } else if (time == 21) {
            result = "20";
        } else if (time == 22) {
            result = "21";
        } else if (time == 23) {
            result = "22";
        } else if (time == 24) {
            result = "23";
        }

        return result;
    }
}
