package com.GP.database;

import java.sql.Time;
import java.util.Date;

public class GameDataset {
    public long Id;
    public String Level;
    public String Alias;
    public int Time;
    public String Date;

    /**
     * Constructors
     */
    public GameDataset() {
        this.Id = -1;
    }

    /**
     * contains data of the game
     * @param alias
     * @param time
     * @param date
     * @param levelParam value for the difficulty of the level
     */
    public GameDataset(String alias, int time, String date, String levelParam) {
        this.Alias = alias;
        this.Date = date;
        this.Time = time;
        this.Level = levelParam;
        this.Id = -1;
    }
}

