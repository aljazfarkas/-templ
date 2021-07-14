package com.pora.lib;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Person {
    private String name;
    private String pin;
    private ArrayList<CheckPair> checkedTimes;

    public Person(String name, String pin) {
        this.name = name;
        this.pin = pin;
        this.checkedTimes = new ArrayList<>();
    }

    public ArrayList<CheckPair> getCheckedTimes() {
        return checkedTimes;
    }

    public void setCheckedTimes(ArrayList<CheckPair> checkedTimes) {
        this.checkedTimes = checkedTimes;
    }

    public Person(String name, String pin, ArrayList<CheckPair[]> checkedTimes) {
        this.name = name;
        this.pin = pin;
        this.checkedTimes = new ArrayList<CheckPair>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCheckIn(LocalDateTime dateTime){
        this.checkedTimes.add(new CheckPair());
        this.checkedTimes.get(this.checkedTimes.size() - 1).setCheckIn(dateTime);
    }
    public void addCheckOut(LocalDateTime dateTime){
        this.checkedTimes.get(this.checkedTimes.size() - 1).setCheckOut(dateTime);
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
