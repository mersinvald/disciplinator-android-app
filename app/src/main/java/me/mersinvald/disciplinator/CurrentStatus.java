package me.mersinvald.disciplinator;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentStatus {
    public String state;
    public int activeMinutes;
    public int debt;

    public CurrentStatus(JSONObject object) throws JSONException {
        this.state = object.getString("type");
        this.activeMinutes = object.getInt("activeMinutes");
        this.debt = object.getInt("debt");
    }

    public int color() {
        if (state.equals("normal")) {
            return 0x8bc34a;
        } else if (state.equals("debtCollection")) {
            return 0xf44336;
        } else if (state.equals("debtCollectionPaused")) {
            return 0xffeb3b;
        } else {
            return 0xd32f2f;
        }
    }

    @Override
    public String toString() {
        return state.toUpperCase() +
                "\n" +
                "Active Minutes: " +
                activeMinutes +
                ", " +
                "Debt: " +
                debt;
    }
}
