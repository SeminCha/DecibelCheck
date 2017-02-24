package ensharp.decibelcheck;

public abstract class WeeklyInfoAdapterItem {

    public static final int TYPE_YEAR = 1;
    public static final int TYPE_DATA = 2;

    private String year;

    public WeeklyInfoAdapterItem(String year) {
        this.year = year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public String getYearToString() {
        return getYear();
    }

    public abstract int getType();

}
