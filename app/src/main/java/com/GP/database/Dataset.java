package com.GP.database;

import java.sql.Time;
import java.util.Date;

public class Dataset {
    public long Id;
    public String _alias;
    public Time _time;
    public Date _date;

    /**
     * Constructors
     */
    public Dataset() {
        this.Id = -1;
    }

    public Dataset(String alias, Time time, Date date) {
        this._alias = alias;
        this._date = date;
        this._time = time;
        this.Id = -1;
    }
}

