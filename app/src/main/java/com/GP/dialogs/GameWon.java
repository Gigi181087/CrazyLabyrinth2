package com.GP.dialogs;

import static android.support.v4.widget.EdgeEffectCompatIcs.finish;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.R;
import com.GP.database.CrazyLabyrinthDatabaseAccess;

import java.util.Date;

public class GameWon extends DialogFragment {

    //layout elements
    private LayoutInflater inflater;
    private View view;
    private TextView name;
    private TextView time;
    private TextView level;
    private Button buttonQuit;
    private Button buttonNewGame;

    // shared preference elements
    private SharedPreferences sharedPreferences;

    // database elements
    private CrazyLabyrinthDatabaseAccess database;
    private Date date;

    // listener
    private ListenerGameWonButtons listener;

    public interface ListenerGameWonButtons {
        void onGameWonButtonPressed(String buttonPressedParam);
    }

    @Override
    public void onAttach(@NonNull Context contextParam) {
        super.onAttach(contextParam);


        try {
            listener = (ListenerGameWonButtons) contextParam;
        } catch (ClassCastException e) {
            throw new ClassCastException(contextParam.toString() + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //build layout
        this.inflater = requireActivity().getLayoutInflater();
        this.view = inflater.inflate(R.layout.dialog_highscore, null);
        builder.setView(view);
        this.name = view.findViewById(R.id.name);
        this.time = view.findViewById(R.id.time);
        this.level = view.findViewById(R.id.level);
        this.buttonNewGame = view.findViewById(R.id.newGame);
        this.buttonQuit = view.findViewById(R.id.quit);

        // get shared preferences
        SharedPreferences _sharedPreferences;
        _sharedPreferences = getActivity().getSharedPreferences(getActivity().getString(R.string.sharedPreferences_name), Context.MODE_PRIVATE);
        String _name = _sharedPreferences.getString(getActivity().getString(R.string.playerfieldname), getActivity().getString(R.string.playerDefaultValue));

        // add listeners to buttons
        this.buttonQuit.setOnClickListener(view -> finish());

}

}
