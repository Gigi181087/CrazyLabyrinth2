package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.R;

public class StartMenu extends DialogFragment {
    private Button buttonNewGame;
    private Button buttonHighscores;
    private Button buttonSettings;
    private Button buttonQuit;

    private listenerStartMenuButtons listener;

    public interface listenerStartMenuButtons {
        void onStartMenuButtonPressed(String buttonPressedParam);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (listenerStartMenuButtons) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        // Erstelle den Dialog und setze das Layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View _view = inflater.inflate(R.layout.dialog_startmenu, null);
        builder.setView(_view);

        // Finde den Button im Dialogfenster
        buttonNewGame = _view.findViewById(R.id.buttonNewGame);
        buttonHighscores = _view.findViewById(R.id.buttonHighscores);
        buttonSettings = _view.findViewById(R.id.buttonSettings);
        buttonQuit = _view.findViewById(R.id.buttonQuit);

        // Setze den OnClickListener für den Button
        buttonNewGame.setOnClickListener(view -> goBack("NEW GAME"));

        buttonHighscores.setOnClickListener(view -> goBack("HIGHSCORES"));

        buttonSettings.setOnClickListener(view -> goBack("SETTINGS"));

        buttonQuit.setOnClickListener(view -> goBack("QUIT"));

        return builder.create();
    }

    private void goBack(String buttonPressedParam) {
        listener.onStartMenuButtonPressed(buttonPressedParam);

        dismiss();
    }
}
