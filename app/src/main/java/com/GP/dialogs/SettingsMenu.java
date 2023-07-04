package com.GP.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.GP.crazylabyrinth.R;
import com.GP.mqtt.MQTTManager;

public class SettingsMenu extends DialogFragment {

    EditText playerText;
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

    /**
     * state of the settings-button
     */
    public interface ListenerSettings {
        void onSettingsButtonPressed(boolean settingsChangedParam);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ListenerSettings) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder _builder = new AlertDialog.Builder(getActivity());

        // build layout
        LayoutInflater _inflater = requireActivity().getLayoutInflater();
        View view = _inflater.inflate(R.layout.dialog_settings, null);
        _builder.setView(view);
        this.playerText = view.findViewById(R.id.playerNameTextView);
        this.ipText = view.findViewById(R.id.ipTextView);
        this.connectButton = view.findViewById(R.id.connectButton);
        this.soundButton = view.findViewById(R.id.soundButton);
        this.vibratorButton = view.findViewById(R.id.vibrateButton);
        this.closeButton = view.findViewById(R.id.closeButton);

        // Get settings from system
        this.sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();

        playerName = this.sharedPreferences.getString(getResources().getString(R.string.playerName), getResources().getString(R.string.playerNameDefaultValue));
        ip = this.sharedPreferences.getString(getActivity().getResources().getString(R.string.internetProtocol), getResources().getString(R.string.internetProtocolDefaultAddress));
        vibrator = this.sharedPreferences.getBoolean(getResources().getString(R.string.vibratorUsed), getResources().getBoolean(R.bool.vibratorUsedDefaultValue));
        sound = this.sharedPreferences.getBoolean(getResources().getString(R.string.soundUsed), getResources().getBoolean(R.bool.soundUsedDefaultValue));

        connectButton.setOnClickListener(_lambdaView -> MQTTManager.connect(ip));

        this.playerText.setText(playerName);
        this.ipText.setText(ip);

        InputFilter _nameFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequenceParam, int startParam, int endParam, Spanned spannedParam, int sstartParam, int dendParam) {
                StringBuilder _returnValue = new StringBuilder();

                for(int i = startParam; i < endParam; i++) {
                    if(i < 14) {
                        char _currentChar = charSequenceParam.charAt(i);

                        if ((int) _currentChar >= (int) 'a' && (int) _currentChar <= (int) 'z') {
                            _currentChar = (char) (_currentChar - 32);
                            _returnValue.append(_currentChar);

                        } else if ((int) _currentChar >= (int) '0' && (int) _currentChar <= (int) '9') {
                            _returnValue.append(_currentChar);

                        } else if ((int) _currentChar >= (int) 'A' && (int) _currentChar <= (int) 'Z') {
                            _returnValue.append(_currentChar);
                        }
                    }
                }

                return _returnValue;
            }
        };

        

        // Created by chatGPT
        this.playerText.setFilters(new InputFilter[] { _nameFilter });
        this.playerText.setOnFocusChangeListener((viewParam, hasFocusParam)  -> {
            if (!hasFocusParam) {
                this.playerName = this.playerText.getText().toString();
            }
        });
        this.soundButton.setText(sharedPreferences.getBoolean(getResources().getString(R.string.soundUsed), getResources().getBoolean(R.bool.soundUsedDefaultValue)) ? getResources().getString(R.string.soundOn) : getResources().getString(R.string.soundOff));
        this.vibratorButton.setText(sharedPreferences.getBoolean(getResources().getString(R.string.vibratorUsed), getResources().getBoolean(R.bool.vibratorUsedDefaultValue)) ? getResources().getString(R.string.vibratorOn) : getResources().getString(R.string.vibratorOff));

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
                boolean _sound = !sharedPreferences.getBoolean(getResources().getString(R.string.soundUsed), getResources().getBoolean(R.bool.soundUsedDefaultValue));
                soundButton.setText(_sound ? getResources().getString(R.string.soundOn) : getResources().getString(R.string.soundOff));
                editor.putBoolean(getResources().getString(R.string.soundUsed), _sound);

                editor.apply();
            }
        });

        this.vibratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean _vibrator = !sharedPreferences.getBoolean(getResources().getString(R.string.vibratorUsed), getResources().getBoolean(R.bool.vibratorUsedDefaultValue));
                vibratorButton.setText(_vibrator ? getResources().getString(R.string.vibratorOn) : getResources().getString(R.string.vibratorOff));
                editor.putBoolean(getResources().getString(R.string.vibratorUsed), _vibrator);

                editor.apply();
            }
        });

        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString(getResources().getString(R.string.playerName), playerText.getText().toString());
                editor.putString(getResources().getString(R.string.internetProtocol), ipText.getText().toString());
                editor.apply();

                if(listener != null) {
                    listener.onSettingsButtonPressed(true);
                }

                dismiss();
            }
        });


        return _builder.create();
    }


}
