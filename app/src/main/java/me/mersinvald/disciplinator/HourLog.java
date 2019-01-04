package me.mersinvald.disciplinator;

import org.json.JSONException;
import org.json.JSONObject;

public class HourLog {
    public int hour;
    public int activeMinutes;
    public int debt;
    public boolean isTrackingDisabled;
    public boolean hourComplete;

    public HourLog(JSONObject object) throws JSONException {
        this.hour = object.getInt("hour");
        this.activeMinutes = object.getInt("activeMinutes");
        this.debt = object.getInt("debt");
        this.isTrackingDisabled = object.getBoolean("trackingDisabled");
        this.hourComplete = object.getBoolean("complete");
    }

    @Override
    public String toString() {
        return  hour + ":00 -> " + (hour + 1) + ":00\n" +
                "Active Minutes: " +
                activeMinutes +
                ", " +
                "Debt: " +
                debt +
                "\n" +
                "tracking " +
                (isTrackingDisabled ? "off" : "enabled") +
                ", " +
                (hourComplete ? "complete" : "current");
    }
}