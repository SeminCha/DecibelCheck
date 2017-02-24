package ensharp.decibelcheck;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by Semin on 2017-02-24.
 */
public class MyBarDataSet extends BarDataSet {


    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getY()<80) // less than 95 green
            return mColors.get(0);
        else // greater or equal than 100 red
            return mColors.get(1);
    }
}
