package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.R;
import com.GP.database.DatabaseAccess;

import java.util.List;

public class Highscore extends DialogFragment {

    private Button buttonEasy;
    private Button buttonMedium;
    private Button buttonHard;
    private Button buttonBack;
    private DatabaseAccess databaseHelper;
    private SQLiteDatabase database;

    private LinearLayout linearLayoutStats;

    private listenerHighscoreButtons listener;

    public interface listenerHighscoreButtons {
        void onButtonBack();
    }

    @Override
    public void onAttach(@NonNull Context contextParam) {
        super.onAttach(contextParam);
        databaseHelper = new DatabaseAccess(contextParam, "CrazyLabyrinthDatabase", null);
        database = databaseHelper.getReadableDatabase();
        try {
            listener = (listenerHighscoreButtons) contextParam;
        } catch (ClassCastException e) {
            throw new ClassCastException(contextParam.toString() + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_highscore, null);
        builder.setView(view);

        linearLayoutStats = view.findViewById(R.id.statsList);
        buttonEasy = view.findViewById(R.id.buttonShowStatsEasy);
        buttonMedium = view.findViewById(R.id.buttonShowStatsEasy);
        buttonHard = view.findViewById(R.id.buttonShowStatsEasy);
        buttonBack = view.findViewById(R.id.buttonBack);

        buttonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                populateList(0);
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                populateList(1);
            }
        });

        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                populateList(2);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener != null) {
                    listener.onButtonBack();
                }
                dismiss();
            }
        });



        return builder.create();
    }

    private void populateList(int levelParam) {
        String _filter = "";
        linearLayoutStats.removeAllViews();

        if(levelParam == 0) {
            _filter = "Easy";
            buttonEasy.setSelected(true);
            buttonEasy.refreshDrawableState();
            buttonMedium.setSelected(false);
            buttonMedium.refreshDrawableState();
            buttonHard.setSelected(false);
            buttonHard.refreshDrawableState();

        } else if(levelParam == 1) {
            _filter = "Medium";
            buttonEasy.setSelected(false);
            buttonEasy.refreshDrawableState();
            buttonMedium.setSelected(true);
            buttonMedium.refreshDrawableState();
            buttonHard.setSelected(false);
            buttonHard.refreshDrawableState();

        } else if(levelParam == 2) {
            _filter = "Hard";
            buttonEasy.setSelected(false);
            buttonEasy.refreshDrawableState();
            buttonMedium.setSelected(false);
            buttonMedium.refreshDrawableState();
            buttonHard.setSelected(true);
            buttonHard.refreshDrawableState();

        } else {
            throw new IllegalArgumentException("Parameter of level is not supported!");
        }

        Cursor _cursor = databaseHelper.getData("Level", _filter);

        if(_cursor != null) {
            _cursor.moveToFirst();

            do {
                String _name = _cursor.getString(_cursor.getColumnIndexOrThrow("Name"));
                String _date = _cursor.getString(_cursor.getColumnIndexOrThrow("Date"));
                String _time = _cursor.getString(_cursor.getColumnIndexOrThrow("Time"));

                // TODO: implement listview
            } while(_cursor.moveToNext());
        }



    }

}
