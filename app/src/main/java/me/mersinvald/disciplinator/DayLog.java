package me.mersinvald.disciplinator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DayLog {
    public ArrayList<HourLog> hours;

    public DayLog(JSONArray array) throws JSONException {
        this.hours = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            JSONObject dayObj = array.getJSONObject(i);
            this.hours.add(new HourLog(dayObj));
        }
    }
}