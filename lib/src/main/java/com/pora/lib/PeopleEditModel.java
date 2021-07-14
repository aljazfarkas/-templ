package com.pora.lib;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PeopleEditModel {
    private String name;
    private String pin;
    private ArrayList<CheckPair> checkedTimes;

    public PeopleEditModel() {
        checkedTimes = new ArrayList<CheckPair>();
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addCheckIn(LocalDateTime dateTime){
        this.checkedTimes.add(new CheckPair());
        this.checkedTimes.get(this.checkedTimes.size() - 1).setCheckIn(dateTime);
    }
    public void addCheckOut(LocalDateTime dateTime){
        this.checkedTimes.get(this.checkedTimes.size() - 1).setCheckOut(dateTime);
    }
    public boolean isCheckedIn(){
        if(checkedTimes.size() == 0)
            return true;
        if (checkedTimes.get(checkedTimes.size() - 1).getCheckOut() == LocalDateTime.MIN)
            return false;
        else return true;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}