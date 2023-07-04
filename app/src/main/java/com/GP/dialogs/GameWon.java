package com.GP.dialogs;


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

public class GameWon extends DialogFragment {

    //layout elements
    private LayoutInflater inflater;
    private View view;
    private TextView textViewName;
    private TextView textViewTime;
    private TextView textViewLevel;
    private Button buttonQuit;
    private Button buttonNewGame;

    String name;
    int time;
    String level;




    // listener
    private ListenerGameWonButtons listener;

    public interface ListenerGameWonButtons {
        void onGameWonButtonPressed(String buttonPressed);
    }

    public GameWon(String nameParam, int timeParam,  String levelParam) {
        name = nameParam;
        time = timeParam;
        level = levelParam;
    }

    @Override
    public void onAttach(@NonNull Context contextParam) {
        super.onAttach(contextParam);


        try {
            listener = (ListenerGameWonButtons) contextParam;
        } catch (ClassCastException e) {
            throw new ClassCastException(contextParam + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //build layout
        this.inflater = requireActivity().getLayoutInflater();
        this.view = inflater.inflate(R.layout.dialog_gamewon, null);
        builder.setView(view);
        this.textViewName = view.findViewById(R.id.name);
        this.textViewTime = view.findViewById(R.id.time);
        this.textViewLevel = view.findViewById(R.id.level);
        this.buttonNewGame = view.findViewById(R.id.newGame);
        this.buttonQuit = view.findViewById(R.id.quit);

        // get shared preferences
        textViewName.setText(name);
        textViewLevel.setText(level);
        textViewTime.setText(String.format("%02d:%02d.%03d", time / 60000, time % 60000 / 1000, time % 1000));

        // add listeners to buttons
        this.buttonQuit.setOnClickListener(view -> {
            dismiss();
            NotifyButtonPressed("QUIT");
        });
        this.buttonNewGame.setOnClickListener(view -> {
            dismiss();
            NotifyButtonPressed("NEW GAME");
        });

        return builder.create();
    }

    private void NotifyButtonPressed(String buttonParam) {
        listener.onGameWonButtonPressed(buttonParam);
    }
}
