package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
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
    private Button buttonClose;

    private listenerStartMenuButtons listener;

    public interface listenerStartMenuButtons {
        void onButtonNewGame();
        void onButtonHighscores();
        void onButtonSettings();
        void onButtonClose();
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
        View view = inflater.inflate(R.layout.dialog_startmenu, null);
        builder.setView(view);

        // Finde den Button im Dialogfenster
        buttonNewGame = view.findViewById(R.id.buttonNewGame);
        buttonHighscores = view.findViewById(R.id.buttonHighscores);
        buttonSettings = view.findViewById(R.id.buttonSettings);
        buttonClose = view.findViewById(R.id.buttonClose);

        // Setze den OnClickListener für den Button
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonNewGame();
                }
                dismiss();
            }
        });

        buttonHighscores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonHighscores();
                }
                dismiss();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonSettings();
                }
                dismiss();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonClose();
                }
                dismiss();
            }
        });

        return builder.create();
    }
}
