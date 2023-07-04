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

public class GameMenu extends DialogFragment {

    private ListenerGameMenuButton listener;

    public interface ListenerGameMenuButton {
        public void onGameMenuButtonPressed(String buttonParam);
    }

    @Override
    public void onAttach(@NonNull Context contextParam) {
        super.onAttach(contextParam);
        try {
            listener = (ListenerGameMenuButton) contextParam;
        } catch (ClassCastException e) {
            throw new ClassCastException(contextParam.toString() + " must implement OnButtonClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStateParam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Build layout
        LayoutInflater _inflater = requireActivity().getLayoutInflater();
        View _view = _inflater.inflate(R.layout.dialog_gamemenu, null);
        builder.setView(_view);
        Button _resume = _view.findViewById(R.id.buttonResume);
        Button _settings = _view.findViewById(R.id.buttonSettings);
        Button _quit = _view.findViewById(R.id.buttonQuit);

        // Add onClickListeners
        _resume.setOnClickListener(_lambdaView -> {
            dismiss();
            listener.onGameMenuButtonPressed("RESUME");
        });
        _settings.setOnClickListener(_lambdaView -> {
            dismiss();
            SettingsMenu _settingsDialog = new SettingsMenu();
            _settingsDialog.setCancelable(false);
            _settingsDialog.show(getParentFragmentManager(), null);

        });
        _quit.setOnClickListener(_lambdaView -> getActivity().finish());

        return builder.create();
    }

}
