package com.pora.lib;

import java.time.LocalDateTime;

public class CheckPair {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public CheckPair(LocalDateTime checkIn, LocalDateTime checkOut) {
        super();
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    /**
     * Default checkIn/checkOut value je LocalDateTime.MIN
     */
    public CheckPair() {
        super();
        this.checkIn = LocalDateTime.MIN;
        this.checkOut = LocalDateTime.MIN;
    }

    public int hashCode() {
        int hashFirst = checkIn != null ? checkIn.hashCode() : 0;
        int hashSecond = checkOut != null ? checkOut.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof CheckPair) {
            CheckPair otherCheckPair = (CheckPair) other;
            return
                    ((  this.checkIn == otherCheckPair.checkIn ||
                            ( this.checkIn != null && otherCheckPair.checkIn != null &&
                                    this.checkIn.equals(otherCheckPair.checkIn))) &&
                            (  this.checkOut == otherCheckPair.checkOut ||
                                    ( this.checkOut != null && otherCheckPair.checkOut != null &&
                                            this.checkOut.equals(otherCheckPair.checkOut))) );
        }

        return false;
    }

    public String toString()
    {
        return "(" + checkIn + ", " + checkOut + ")";
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }
}