package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*
 * @author Anthony
 */
@Embeddable
public class VacationEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    @Temporal(TemporalType.DATE)
    private Calendar startDate;
    @Column(name = "DAYS")
    private int daysTaken;

    public int getDaysTaken() {
        return daysTaken;
    }

    public void setDaysTaken(int daysTaken) {
        this.daysTaken = daysTaken;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "VacationEntry{" + "startDate=" + startDate + ", daysTaken=" + daysTaken + '}';
    }
}
