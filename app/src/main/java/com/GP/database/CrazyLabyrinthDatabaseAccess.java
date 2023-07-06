// Quelle:

package com.GP.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.GP.crazylabyrinth.R;

public class CrazyLabyrinthDatabaseAccess extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private String databaseName;


    /**
     * Constructor
     * @param activity: aufrufende Activity
     */
    public CrazyLabyrinthDatabaseAccess(Context activity) {
        super(activity, activity.getString(R.string.database_name), null, 1);

        this.database = getWritableDatabase();
        this.databaseName = activity.getString(R.string.database_name);

        Log.d("CrazyLabyrinthDatabase", "Constructor: called!");
    }


    /**
     * Creates table
     * @param databaseParam
     */
    @Override
    public void onCreate(SQLiteDatabase databaseParam) {
        try {
            String _sqlCommand = "CREATE TABLE IF NOT EXISTS playerdata" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(14) NOT NULL," +
                    "date VARCHAR(8)," +
                    "time INTEGER NOT NULL," +
                    "level VARCHAR(10) NOT NULL)";
            databaseParam.execSQL(_sqlCommand);
            Log.d("CrazyLabyrinthDatabase", "Database created!");
        }
        catch(Exception excepionParam) {
            Log.d("CrazyLabyrinthDatabase", "onCreate: " + excepionParam.getMessage());
        }
    }

    /**
     * sets the player's data
     * @param dataSetParam parameter for the player's data
     * @return player's data
     */
    public long insertDataSet(GameDataset dataSetParam) {
        ContentValues _data = new ContentValues();

        _data.put("name", dataSetParam.Alias);
        _data.put("level", dataSetParam.Level);
        _data.put("date", dataSetParam.Date);
        _data.put("time", dataSetParam.Time);

        return database.insert("playerdata", null, _data);
    }

    /**
     * filters the data
     * @param levelParam value for the difficulty of the level
     * @return
     */
    public Cursor getFilteredData(String levelParam) {
        String[] _columns = new String[] {"name", "date", "time"};

        return database.query("playerdata", _columns, "level = '" + levelParam + "'", null, null, null, "time ASC");

    }

    /**
     *
     */
    public void debugPrintTable() {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"name", "date", "time", "level"};
        Cursor cursor = db.query("playerdata", columns, null, null, null, null, null);
        int length = cursor.getCount();
        if(length > 0) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String datum = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                long zeit = cursor.getLong(cursor.getColumnIndexOrThrow("time"));
                String schwierigkeit = cursor.getString(cursor.getColumnIndexOrThrow("level"));

                // Output in the console or in the logcat
                Log.d("DEBUG", "Name: " + name + ", Datum: " + datum + ", Zeit: " + zeit + ", Schwierigkeit: " + schwierigkeit);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "playerdata");
        onCreate(db);
    }

    /**
     * Hier noch evtl. eigene Aufr?umarbeiten durchf?hren
     */
    @Override
    public synchronized void close() {
        if(database != null) {
            database.close();
            database = null;
        }

        super.close();
    }
}
