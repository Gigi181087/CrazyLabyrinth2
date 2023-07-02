package com.GP.crazylabyrinth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.GP.dialogs.NewGameMenu;
import com.GP.dialogs.SettingsMenu;
import com.GP.dialogs.StartMenu;
import com.GP.mqtt.MQTTManager;

public class MainMenuActivity extends AppCompatActivity implements NewGameMenu.listenerNewGameButtons, StartMenu.listenerStartMenuButtons, SettingsMenu.ListenerSettings {
// TODO: draw background labyrinth
    private StartMenu dialogStartMenu;
    private NewGameMenu dialogNewGame;
    private SettingsMenu dialogSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        // Initialize the MQTT Manager
        MQTTManager.initialize(getApplicationContext());

        this.openDialogStartMenu();
    }

    private void openDialogStartMenu() {
        dialogStartMenu.show(getSupportFragmentManager(), null);
    }

    private void openDialogNewGame() {
        dialogNewGame.show(getSupportFragmentManager(), null);
    }

    private void openDialogSettings() {
        dialogSettings.show(getSupportFragmentManager(), null);
    }

    private void startGame(int levelParam) {
        Intent _intent = new Intent(this, GameActivity.class);
        Bundle _bundle = new Bundle();

        _bundle.putInt("level", levelParam);
        _intent.putExtras(_bundle);
        startActivity(_intent);
    }


    /*
     * StartMenu events
     */

    /**
     * Opens the NewGame dialog
     */
    @Override
    public void onButtonNewGame() {
        this.openDialogNewGame();
    }

    /**
     * Opens the Highscore activity
     */
    @Override
    public void onButtonHighscores() {
        startActivity(new Intent(this, HighscoreActivity.class));
    }

    /**
     * Opens the Settings dialog
     */
    @Override
    public void onButtonSettings() {
        this.openDialogSettings();
    }

    /**
     * Closes the app
     */
    @Override
    public void onButtonClose() {
        Log.d("Main Activity", "User closed App");
        this.finish();
        System.exit(0);
    }

    /*
     * NewGame events
     */

    /**
     * Starts game in easy mode
     */
    @Override
    public void onButtonEasy() {
        this.startGame(0);
    }

    /**
     * Starts game in medium mode
     */
    @Override
    public void onButtonMedium() {
        this.startGame(1);
    }

    /**
     * Starts game in hard mode
     */
    @Override
    public void onButtonHard() {
        this.startGame(2);
    }

    /**
     * Returns to the start menu
     */
    @Override
    public void onButtonBack() {
        this.openDialogStartMenu();
    }

    /*
     * Settings events
     */
    @Override
    public void onSettingsChanged() {

    }


}