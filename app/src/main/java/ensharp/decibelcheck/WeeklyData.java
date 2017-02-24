package ensharp.decibelcheck;

/**
 * Created by Moon on 2015-03-02.
 */
public class WeeklyData extends WeeklyInfoAdapterItem {
    private String simpleWeek;
    private String detailWeek;

    public WeeklyData(String year, String simpleWeek, String detailWeek) {
        super(year);
        this.simpleWeek = simpleWeek;
        this.detailWeek=detailWeek;
    }

    @Override
    public int getType() {
        return TYPE_DATA;
    }

    public String getSimpleWeek() {
        return simpleWeek;
    }

    public void setSimpleWeek(String simpleWeek) {
        this.simpleWeek = simpleWeek;
    }

    public String getDetailWeek(){return detailWeek;}

    public void setDetailWeek(String detailWeek) {
        this.detailWeek = detailWeek;
    }
}
