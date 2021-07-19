package com.pora.lib;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PersonInfoEditModel {
    private final CheckPair checkedTime;

    public PersonInfoEditModel() {
        this.checkedTime = new CheckPair();
    }

    public CheckPair getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedInTime(LocalDateTime checkedInTime) {
        this.checkedTime.setCheckIn(checkedInTime);
    }
    public void setCheckedOutTime(LocalDateTime checkedOutTime) {
        this.checkedTime.setCheckOut(checkedOutTime);
    }
}
