package com.GP.crazylabyrinth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

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

import com.GP.dialogs.NewGameMenu;
import com.GP.dialogs.SettingsMenu;
import com.GP.labyrinth.LabyrinthModel;
import com.GP.labyrinth.LabyrinthView;
import com.GP.labyrinth.Ball;
import com.GP.mqtt.MQTTManager;
import com.google.android.material.internal.ContextUtils;

public class GameActivity extends AppCompatActivity implements SensorEventListener, LabyrinthModel.LabyrinthEventListener, MQTTManager.MQTTEventListener, SettingsMenu.ListenerSettings {
    private SharedPreferences sharedPreferences;
    private boolean vibratorUsed;
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

    private enum controls {
        MQTT,
        ACCELERATOR
    }

    private controls userControls;

    //private final Display display;
    private LabyrinthModel labyrinth;
    private MQTTManager mqttManager;
    private LabyrinthView labyrinthView;
    private SensorManager _sensorManager;
    private SensorEventListener sensorListener;
    private LabyrinthModel.LabyrinthEventListener labyrinthListener;
    private Sensor _accelerometer;
    private double xForceCalibrate;
    private double yForceCalibrate;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceStateParam) {
        super.onCreate(savedInstanceStateParam);

        LabyrinthModel.Difficulty _difficulty = LabyrinthModel.Difficulty.fromInt(getIntent().getIntExtra("level", 2));
        // Force display to not rotate and disable ugly action bar
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        // Handle system services
        DisplayMetrics _displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        windowManager.getDefaultDisplay().getMetrics(_displayMetrics);
        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);

        // Initialize Labyrinth
        labyrinth = new LabyrinthModel(10, 14, _difficulty);
        labyrinth.registerEventListener(this);

        // Handle activity view
        setContentView(R.layout.activity_game);
        this.labyrinthView = (LabyrinthView) findViewById(R.id.labyrinth_view);
        labyrinthView.DrawLabyrinth(labyrinth, _displayMetrics.widthPixels, _displayMetrics.heightPixels);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(view -> showSettingsDialog());

        onSettingsButtonPressed(true);
    }

    private void showSettingsDialog() {
        SettingsMenu _settingsDialog = new SettingsMenu();
        _settingsDialog.show(getSupportFragmentManager(), null);

    }

    private void registerAll() {
        fpsHandler.postDelayed(fpsRunnable, 1000 / 24);
        labyrinth.registerEventListener(this);
        if(userControls == controls.ACCELERATOR) {
            _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);

        } else {
            // TODO: subscribe MQTT

        }

    }

    private void unregisterAll() {
        fpsHandler.removeCallbacks(fpsRunnable);
        labyrinth.removeEventListener(this);

        if(this.userControls == controls.ACCELERATOR) {
            _sensorManager.unregisterListener(this);

        } else {
            // TODO: unsubscribe MQTT
        }
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
    @Override
    public void OnGameWon() {
        fpsHandler.removeCallbacks(fpsRunnable);
        _sensorManager.unregisterListener(this);
        labyrinth.removeEventListener(this);
    }

    /**
     * Lets the phone vibrate when the wall is touched
     * @param param Speed of the ball during impact
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void OnWallTouched(float param) {

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
                VibrationEffect effect = VibrationEffect.createOneShot(100, _intensity);
                this.vibrator.vibrate(effect);
            }
        }
    }

    @Override
    public void OnKeyCollected() {
        this.labyrinthView.RemoveKey();
    }

    @Override
    public void onSettingsButtonPressed(boolean settingsChanged) {
        sharedPreferences = this.getSharedPreferences("CrazyLabyrinthSettings", Context.MODE_PRIVATE);
        vibratorUsed = sharedPreferences.getBoolean("Vibrator", true);
        remoteUsed = sharedPreferences.getBoolean("Remote", false);
        soundUsed = sharedPreferences.getBoolean("Sound", true);
    }
}