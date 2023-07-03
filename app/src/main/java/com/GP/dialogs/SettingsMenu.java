package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.R;

public class SettingsMenu extends DialogFragment {

    private EditText playerText;
    private EditText ipText;
    private Button connectButton;
    private Button vibratorButton;
    private Button soundButton;
    private Button closeButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String playerName;
    private String ip;
    private boolean vibrator;
    private boolean sound;

    private ListenerSettings listener;

    public interface ListenerSettings {
        void onSettingsButtonPressed(boolean settingsChangedParam);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ListenerSettings) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        androidx.appcompat.app.AlertDialog.Builder _builder = new AlertDialog.Builder(getActivity());
        LayoutInflater _inflater = requireActivity().getLayoutInflater();
        View view = _inflater.inflate(R.layout.dialog_settings, null);
        _builder.setView(view);

        // Finde den Button im Dialogfenster
        this.playerText = view.findViewById(R.id.playerNameTextView);
        this.ipText = view.findViewById(R.id.ipTextView);
        this.connectButton = view.findViewById(R.id.connectButton);
        this.soundButton = view.findViewById(R.id.soundButton);
        this.vibratorButton = view.findViewById(R.id.vibrateButton);
        this.closeButton = view.findViewById(R.id.closeButton);

        // Get settings from system
        this.sharedPreferences = getActivity().getSharedPreferences("CrazyLabyrinthSettings", Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();

        playerName = this.sharedPreferences.getString("Playername", "PLAYER");
        ip = this.sharedPreferences.getString("IP", "127.0.0.1");
        vibrator = this.sharedPreferences.getBoolean("Vibrator", true);
        sound = this.sharedPreferences.getBoolean("Sound", true);

        this.playerText.setText(playerName);
        this.ipText.setText(ip);

        if(sound) {
            this.soundButton.setText("SOUND ON");
        } else {
            this.soundButton.setText("SOUND OFF");
        }

        if(vibrator) {
            this.vibratorButton.setText("VIBRATOR ON");
        } else {
            this.vibratorButton.setText("VIBRATOR OFF");
        }

        this.ipText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    boolean _valid = true;
                    // Try to parse ip address
                    String[] parts = ipText.getText().toString().split("\\.");

                    if (parts.length != 4) {
                        _valid = false;
                    }

                    for (String part : parts) {
                        try {
                            int value = Integer.parseInt(part);
                            if (value < 0 || value > 255) {
                                _valid = false;
                            }
                        } catch (NumberFormatException e) {
                            _valid = false;
                        }
                    }

                    if(!_valid) {
                        ipText.setBackgroundColor(Color.RED);
                        connectButton.setEnabled(false);

                    } else {
                        ipText.setBackgroundColor(Color.YELLOW);
                        connectButton.setEnabled(true);
                        playerName = ipText.getText().toString();
                    }
                }
            }
        });

        this.soundButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(sound) {
                    soundButton.setText("SOUND OFF");
                    sound = false;

                } else {
                    soundButton.setText("SOUND ON");
                    sound = true;
                }
            }
        });

        this.vibratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vibrator) {
                    vibratorButton.setText("VIBRATOR OFF");
                    vibrator = false;

                } else {
                    vibratorButton.setText("VIBRATOR ON");
                    vibrator = true;
                }
                editor.putBoolean("Vibrator", vibrator);
            }
        });

        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("Playername", playerName);
                editor.putString("IP", ip);
                editor.putBoolean("Sound", sound);
                editor.putBoolean("Vibrator", vibrator);

                if(listener != null) {
                    listener.onSettingsButtonPressed(true);
                }

                editor.apply();

                dismiss();
            }
        });


        return _builder.create();
    }


}
