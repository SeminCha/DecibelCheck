package ensharp.decibelcheck;

/**
 * Created by Semin on 2017-02-22.
 */
public class WeeklyYearItem extends WeeklyInfoAdapterItem{

    public WeeklyYearItem(String year) {
        super(year);
    }

    @Override
    public int getType() {
        return TYPE_YEAR;
    }
}
