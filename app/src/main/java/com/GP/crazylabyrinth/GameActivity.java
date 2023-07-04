package com.GP.crazylabyrinth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import com.GP.database.CrazyLabyrinthDatabaseAccess;
import com.GP.database.GameDataset;
import com.GP.dialogs.GameMenu;
import com.GP.dialogs.GameWon;
import com.GP.dialogs.NewGameMenu;
import com.GP.dialogs.SettingsMenu;
import com.GP.labyrinth.LabyrinthModel;
import com.GP.labyrinth.LabyrinthView;
import com.GP.mqtt.MQTTManager;

import java.time.LocalDate;

public class GameActivity extends AppCompatActivity implements SensorEventListener, LabyrinthModel.LabyrinthListener, MQTTManager.MQTTEventListener, SettingsMenu.ListenerSettings, GameMenu.ListenerGameMenuButton, GameWon.ListenerGameWonButtons, NewGameMenu.listenerNewGameButtons {
    private SharedPreferences sharedPreferences;
    private volatile boolean vibratorUsed;
    private boolean remoteUsed;
    private boolean soundUsed;

    private Button settingsButton;
    private Handler fpsHandler = new Handler();
    private Runnable fpsRunnable = new Runnable() {
        @Override
        public void run() {
            labyrinthView.invalidate();
            fpsHandler.postDelayed(this, 1000 / 24);
        }
    };

    /**
     * destructor for controlling inputs from esp32
     */
    private enum controls {
        MQTT,
        ACCELERATOR
    }

    int time = 0;
    private controls userControls;

    //private final Display display;
    private LabyrinthModel labyrinth;
    private MQTTManager mqttManager;
    private LabyrinthView labyrinthView;
    private SensorManager _sensorManager;
    private SensorEventListener sensorListener;
    private Sensor _accelerometer;
    private double xForceCalibrate;
    private double yForceCalibrate;
    private Vibrator vibrator;

    DisplayMetrics _displayMetrics;
    LabyrinthModel.Difficulty difficulty;
    Button menuButton;
    GameMenu gameMenu;

    @Override
    protected void onCreate(Bundle savedInstanceStateParam) {
        super.onCreate(savedInstanceStateParam);

        // Force display to not rotate and disable ugly action bar
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        // Handle system services
        _displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        windowManager.getDefaultDisplay().getMetrics(_displayMetrics);
        setContentView(R.layout.activity_game);
        this.labyrinthView = (LabyrinthView) findViewById(R.id.labyrinth_view);
        _sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        // Initialize Labyrinth

        // Handle activity view

        gameMenu = new GameMenu();

        menuButton = (Button)findViewById(R.id.menuButton);
        menuButton.setOnClickListener(view -> showGameMenu());

        this.userControls = controls.ACCELERATOR;

        startGame(getIntent().getIntExtra("level", 2));
        getSettings();
    }

    /**
     * Starts the game depending on the level of difficulty
     * @param levelParam Difficulty parameters
     */
    private void startGame(int levelParam) {
        labyrinth = new LabyrinthModel(this);
        difficulty = LabyrinthModel.Difficulty.fromInt(levelParam);
        labyrinth.create(10, 14, difficulty);
        labyrinthView.DrawLabyrinth(labyrinth, _displayMetrics.widthPixels, _displayMetrics.heightPixels);

        registerAll();
    }

    /**
     * shows the settings menu
     */
    private void showSettingsDialog() {
        SettingsMenu _settingsDialog = new SettingsMenu();
        _settingsDialog.show(getSupportFragmentManager(), null);

    }

    /**
     * shows the game menu
     */
    private void showGameMenu() {
        unregisterAll();
        gameMenu.setCancelable(false);
        gameMenu.show(getSupportFragmentManager(), null);
    }

    /**
     *
     */
    private void registerAll() {
        fpsHandler.postDelayed(fpsRunnable, 1000 / 24);
        if(userControls == controls.ACCELERATOR) {
            _sensorManager.registerListener(GameActivity.this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);

        } else {
            // TODO: subscribe MQTT

        }

    }

    /**
     *
     */
    private void unregisterAll() {
        Log.d("GameActivity", "unregisterAll() called");
        fpsHandler.removeCallbacks(fpsRunnable);

        if(this.userControls == controls.ACCELERATOR) {
            try {
                _sensorManager.unregisterListener(GameActivity.this);
            } catch(ClassCastException ex) {
                Log.d("GameActivity", ex.getMessage());
            }
        } else {
            // TODO: unsubscribe MQTT
        }
    }

    /**
     * sets default settings
     */
    private void getSettings() {
    sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
    vibratorUsed = sharedPreferences.getBoolean(getResources().getString(R.string.vibratorUsed), getResources().getBoolean(R.bool.vibratorUsedDefaultValue));
    remoteUsed = sharedPreferences.getBoolean("Remote", false);
    soundUsed = sharedPreferences.getBoolean(getResources().getString(R.string.soundUsed), getResources().getBoolean(R.bool.soundUsedDefaultValue));
}
    @Override
    public void onNewMessage(String topicParam, String messageParam) {

    }

    private void subscribeMQTT() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterAll();
    }

    /*
     * Sensor event handling
     */
    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            labyrinth.updateBallPosition(-event.values[0], event.values[1]);
            //labyrinthView.postInvalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensorParam, int accuracyParam) {

    }

    /*
     * Labyrinth event handling
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onGameWon() {
        Log.d("GameActivity", "Event onGameWon triggered!");
        try {
            unregisterAll();
            sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesName), Context.MODE_PRIVATE);
            LocalDate _date = LocalDate.now();
            CrazyLabyrinthDatabaseAccess database = new CrazyLabyrinthDatabaseAccess(this);
            String _name = sharedPreferences.getString(getString(R.string.playerName), getString(R.string.playerNameDefaultValue));
            String _dateString = String.format("%02d.%02d.%02d", _date.getDayOfMonth(), _date.getMonthValue(), _date.getYear() - 2000);
            String _difficulty = difficulty.getString();
            database.insertDataSet(new GameDataset(_name, time, _dateString, _difficulty));

            GameWon _dialog = new GameWon(_name, time, _difficulty);
            _dialog.setCancelable(false);
            _dialog.show(getSupportFragmentManager(), null);

        } catch(Exception ex) {
            Log.d("GameActivity", ex.getMessage());
        }
    }

    /**
     * Lets the phone vibrate when the wall is touched
     * @param param Speed of the ball during impact
     */
    @Override
    public void onWallTouched(float param) {

        if (vibratorUsed) {

            if (param < 0) {
                param *= -1;
            }
            int _intensity = 0;

            if (param > 10) {
                _intensity = 255;

            } else {
                _intensity = (int) (25 * param);
            }

            if (_intensity > 0) {
                VibrationEffect effect = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(100, _intensity);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.vibrator.vibrate(effect);
                }
            }
        }
    }

    @Override
    public void onKeyCollected() {
        this.labyrinthView.RemoveKey();
    }

    @Override
    public void onSettingsButtonPressed(boolean settingsChanged) {



        getSettings();

        registerAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onGameMenuButtonPressed(String buttonParam) {

        switch(buttonParam) {

            case "RESUME":
                registerAll();

                break;
        }
    }

    @Override
    public void onGameWonButtonPressed(String buttonParam) {
        switch(buttonParam) {

            case "QUIT":
                finish();

                break;

            case "NEW GAME":
                NewGameMenu _dialog = new NewGameMenu();
                _dialog.setCancelable(false);
                _dialog.show(getSupportFragmentManager(), null);

        }

    }

    @Override
    public void onNewGameButtonPressed(String buttonParam) {

        switch(buttonParam) {

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
                finish();

        }
    }
}