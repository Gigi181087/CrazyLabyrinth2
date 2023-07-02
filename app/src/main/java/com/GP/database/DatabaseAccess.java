package com.GP.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.StringTokenizer;

public class DatabaseAccess extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private String tableSQL;
    private String table;

    /**
     * Constructor
     * @param activity: aufrufende Activity
     * @param databaseName: Name der Datenbank (wenn nicht vorhanden, dann wird sie neu erstellt)
     * @param tableSQL: SQL-Kommando zum Erzeugen der gew?nschten Tabelle (oder null bei ?ffnen
     *                      einer vorhandenen Datenbank)
     */
    public DatabaseAccess(Context activity, String databaseName, String tableSQL) {
        super(activity, databaseName, null, 1);
        this.tableSQL = tableSQL;
        identifyTable();
        database = this.getWritableDatabase();
    }

    /** Aus dem Tabellen-Anlage-SQL den Namen der Tabelle  extrahieren
     *
     */
    private void identifyTable() {
        String sql                = tableSQL.toUpperCase();
        StringTokenizer tokenizer = new StringTokenizer(sql);

        //  den Tabellennamen suchen
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if(token.equals("TABLE")) {
                table = tokenizer.nextToken();
                break;
            }
        }
    }

    /**
     * Gegebenen Datensatz in die  Tabelle eingeben
     * @param dataset
     * @return ID des neuen Datensatzes oder -1 bei Fehler

    public long datensatzEinfuegen(Dataset dataset) {
        try {
            ContentValues daten = erzeugeDatenObjekt(dataset);
            return database.insert(table, null, daten); // id wird automatisch von SQLite gef?llt
        }
        catch(Exception ex) {
            Log.d("CrazyLabyrinth", ex.getMessage());
            return -1;
        }
    } */

    /**
     * Liefert Cursor zum Zugriff auf alle Eintr?ge, alphabetisch geordnet nach Spalte "Name"
     * @return
     */
    public Cursor createListViewCursor() {
        String[] columns = new String[]{"_id", "Name", "Time", "H"};
        return  database.query(table, columns, null, null, null, null, "name");
    }

    public Cursor getData(String columnParam, String filterParam) {
        String[] _columns = {"Name", "Date", "Time"};
        String _selection = "Level = ?";
        String[] _selectionArguments = {"Hard"};
        String _orderBy = "Time ASC";

        return database.query("CrazyLabyrinth", _columns, _selection, _selectionArguments, null, null, _orderBy);
    }

    @Override
    /**
     * Wird nur aufgerufen, wenn eine Datenbank neu erzeugt wird
     */
    public void onCreate(SQLiteDatabase db) {
        try {
            // Tabelle anlegen
            db.execSQL(tableSQL);
            Log.d("CrazyLabyritnh", "database is being created");
        }
        catch(Exception ex) {
            Log.e("CrazyLabyrinth", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
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
