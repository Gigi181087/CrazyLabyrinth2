package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.GameActivity;
import com.GP.crazylabyrinth.R;

public class NewGameMenu extends DialogFragment {
    
    private Button buttonEasy;
    private Button buttonMedium;
    private Button buttonHard;
    private Button buttonBack;

    private listenerNewGameButtons listener;

    public interface listenerNewGameButtons {
        void onNewGameButtonPressed(String buttonPressedParam);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (listenerNewGameButtons) context;
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
        View _view = inflater.inflate(R.layout.dialog_newgame, null);
        builder.setView(_view);

        buttonEasy = _view.findViewById(R.id.buttonEasy);
        buttonMedium = _view.findViewById(R.id.buttonMedium);
        buttonHard = _view.findViewById(R.id.buttonHard);
        buttonBack = _view.findViewById(R.id.buttonBack);

        // Setze den OnClickListener für den Button
        buttonEasy.setOnClickListener(view -> notifyButtonPressed("EASY"));

        buttonMedium.setOnClickListener(view -> notifyButtonPressed("MEDIUM"));

        buttonHard.setOnClickListener(view -> notifyButtonPressed("HARD"));

        buttonBack.setOnClickListener(view -> notifyButtonPressed("BACK"));

        return builder.create();
    }

    public void notifyButtonPressed(String buttonPressedParam) {
        listener.onNewGameButtonPressed(buttonPressedParam);

        dismiss();
    }

}
