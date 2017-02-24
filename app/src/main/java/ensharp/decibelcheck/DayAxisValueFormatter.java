package ensharp.decibelcheck;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter implements IAxisValueFormatter{

    private BarLineChartBase<?> chart;
    private int count;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        //Log.i("여기","public DayAxisValueFormatter(BarLineChartBase<?> chart)");
        this.chart = chart;
        count=0;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        //Log.i("여기","public String getFormattedValue(float value, AxisBase axis)");
        int days = (int) value;
        //Log.i("값","count = " + count + " value : " + value);
                    String result="";
            if(days==1){
               result="일";
            } else if(days==2) {
                result="월";
            } else if(days==3) {
                result="화";
            }else if(days==4) {
                result="수";
            }else if(days==5) {
                result="목";
            }else if(days==6) {
                result="금";
            }else if(days==7) {
                result="토";
            }

            return result;
        }
}
