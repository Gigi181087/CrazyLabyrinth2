package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
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
import com.GP.database.CrazyLabyrinthDatabaseAccess;

public class Highscore extends DialogFragment {

    Button buttonEasy;
    Button buttonMedium;
    Button buttonHard;
    Button buttonBack;
    CrazyLabyrinthDatabaseAccess database;

    private LayoutInflater inflater;
    View view;

    private LinearLayout linearLayoutStats;

    private listenerHighscoreButtons listener;

    /**
     * state of the highscore-button
     */
    public interface listenerHighscoreButtons {
        void onHighscoreButtonPressed(String buttonPressedParam);
    }

    @Override
    public void onAttach(@NonNull Context contextParam) {
        super.onAttach(contextParam);


        try {
            listener = (listenerHighscoreButtons) contextParam;
        } catch (ClassCastException e) {
            throw new ClassCastException(contextParam + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.inflater = requireActivity().getLayoutInflater();
        this.view = inflater.inflate(R.layout.dialog_highscore, null);
        builder.setView(view);

        database = new CrazyLabyrinthDatabaseAccess(getActivity());

        linearLayoutStats = view.findViewById(R.id.statsList);
        buttonEasy = view.findViewById(R.id.buttonShowStatsEasy);
        buttonMedium = view.findViewById(R.id.buttonShowStatsMedium);
        buttonHard = view.findViewById(R.id.buttonShowStatsHard);
        buttonBack = view.findViewById(R.id.buttonBack);

        buttonEasy.setOnClickListener(view -> populateList(0));
        buttonMedium.setOnClickListener(view -> populateList(1));
        buttonHard.setOnClickListener(view -> populateList(2));
        buttonBack.setOnClickListener(view -> buttonPressed("BACK"));

        return builder.create();
    }

    /**
     * sets the difficulty based on the levelParam
     * @param levelParam value for difficulty of the level
     */
    private void populateList(int levelParam) {
        String _filter;
        linearLayoutStats.removeAllViews();

        if(levelParam == 0) {
            _filter = "EASY";

        } else if(levelParam == 1) {
            _filter = "MEDIUM";

        } else if(levelParam == 2) {
            _filter = "HARD";

        } else {
            throw new IllegalArgumentException("Parameter of level is not supported!");
        }

        Cursor _cursor = database.getFilteredData(_filter);

        if(_cursor != null) {
            int _length = _cursor.getCount();

            if (_length > 0) {
                int _counter = 0;
                _cursor.moveToFirst();
                do {
                    _counter++;
                    String _name = _cursor.getString(_cursor.getColumnIndexOrThrow("name"));
                    String _date = _cursor.getString(_cursor.getColumnIndexOrThrow("date"));
                    int _time = _cursor.getInt(_cursor.getColumnIndexOrThrow("time"));

                    View _view = inflater.inflate(R.layout.listentries, linearLayoutStats,false);

                    ((TextView)_view.findViewById(R.id.number)).setText(String.valueOf(_counter));
                    ((TextView)_view.findViewById(R.id.name)).setText(_name);
                    ((TextView)_view.findViewById(R.id.date)).setText(_date);
                    String _timeText = String.valueOf(_time / 60000) + ":" + String.valueOf(_time % 60000 / 1000) + "." + String.valueOf(_time % 1000);
                    ((TextView)_view.findViewById(R.id.time)).setText(_timeText);

                    linearLayoutStats.addView(_view);
                } while (_cursor.moveToNext());
            }
        }
    }

    /**
     * constructor
     * @param buttonPressedParam state of the Highscore-button
     */
    public void buttonPressed(String buttonPressedParam) {
        listener.onHighscoreButtonPressed(buttonPressedParam);

        dismiss();
    }

}
