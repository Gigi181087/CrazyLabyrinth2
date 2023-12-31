package com.GP.crazylabyrinth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.GP.dialogs.Highscore;
import com.GP.dialogs.NewGameMenu;
import com.GP.dialogs.SettingsMenu;
import com.GP.dialogs.StartMenu;
import com.GP.mqtt.MQTTManager;
import com.GP.remote.Remote;

import java.io.File;

public class MainMenuActivity extends AppCompatActivity implements NewGameMenu.listenerNewGameButtons, StartMenu.listenerStartMenuButtons, SettingsMenu.ListenerSettings, Highscore.listenerHighscoreButtons {

    SharedPreferences sharedPreferences;
    private StartMenu dialogStart;
    private NewGameMenu dialogNewGame;
    private SettingsMenu dialogSettings;
    private Highscore dialogHighscore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        // Initialize Database
        File dbFile = this.getDatabasePath(this.getString(R.string.database_name));
        String _sqlCommand = null;

        // Initialize the Remote
        Remote.initialize(getApplicationContext());

        // Initialize shared Preferences
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPreferencesName), MODE_PRIVATE);
        this.openDialogStartMenu();
    }

    /**
     *
     */
    private void openDialogStartMenu() {
        this.dialogStart = new StartMenu();
        this.dialogStart.show(getSupportFragmentManager(), null);
    }

    /**
     *
     */
    private void openDialogNewGame() {
        this.dialogNewGame = new NewGameMenu();
        this.dialogNewGame.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.openDialogStartMenu();
    }

    /**
     * opens the settings menue
     */
    private void openDialogSettings() {
        this.dialogSettings = new SettingsMenu();
        this.dialogSettings.show(getSupportFragmentManager(), null);
    }

    /**
     * opens the high score board
     */
    private void openDialogHighscore() {
        this.dialogHighscore = new Highscore();
        this.dialogHighscore.show(getSupportFragmentManager(), null);
    }

    /**
     * destructor
     * @param levelParam parameter for the difficulty of the level
     */
    private void startGame(int levelParam) {
        Intent _intent = new Intent(this, GameActivity.class);
        Bundle _bundle = new Bundle();

        _bundle.putInt("level", levelParam);
        _intent.putExtras(_bundle);
        startActivity(_intent);
    }

    @Override
    public void onButtonConnectPressed(){
        //String _ip = sharedPreferences.getString(getResources().getString(R.string.internetProtocol), getResources().getString(R.string.internetProtocolDefaultAddress));
        String _ip = "broker.hivemq.com";
        Remote.getInstance().connectToBroker(_ip);
    }

    @Override
    public void onSettingsButtonPressed(boolean settingsChangedParam) {
        this.openDialogStartMenu();
    }
    @Override
    public void onStartMenuButtonPressed(@NonNull String buttonPressedParam) {

        switch(buttonPressedParam) {

            case "NEW GAME":
                this.openDialogNewGame();

                break;

            case "HIGHSCORES":
                this.openDialogHighscore();

                break;

            case "SETTINGS":
                this.openDialogSettings();

                break;

            case "QUIT":
                Log.d("Main Activity", "User closed App");
                this.finish();
                System.exit(0);

        }
    }

    @Override
    public void onNewGameButtonPressed(@NonNull String buttonPressedParam) {

        switch(buttonPressedParam) {

            case "EASY":
                this.startGame(0);

                break;

            case "MEDIUM":
                this.startGame(1);

                break;

            case "HARD":
                this.startGame(2);

                break;

            case "BACK":
                this.openDialogStartMenu();

        }
    }



    @Override
    public void onHighscoreButtonPressed(String buttonPressedParam) {
        this.openDialogStartMenu();
    }
}