package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.GameActivity;
import com.GP.crazylabyrinth.R;

public class NewGameMenu extends DialogFragment {
    
    private ImageButton buttonEasy;
    private ImageButton buttonMedium;
    private ImageButton buttonHard;
    private ImageButton buttonBack;

    private listenerNewGameButtons listener;

    public interface listenerNewGameButtons {
        void onButtonEasy();
        void onButtonMedium();
        void onButtonHard();
        void onButtonBack();
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
        View view = inflater.inflate(R.layout.dialog_newgame, null);
        builder.setView(view);

        // Finde den Button im Dialogfenster
        buttonEasy = view.findViewById(R.id.buttonEasy);
        buttonMedium = view.findViewById(R.id.buttonEasy);
        buttonHard = view.findViewById(R.id.buttonEasy);
        buttonBack = view.findViewById(R.id.buttonBack);

        // Setze den OnClickListener für den Button
        buttonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonEasy();
                }
                dismiss();
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonMedium();
                }
                dismiss();
            }
        });

        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonHard();
                }
                dismiss();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                // Öffne das neue Dialogfragment
                if(listener != null) {
                    listener.onButtonBack();
                }
                dismiss();
            }
        });

        return builder.create();
    }


}
