package com.GP.crazylabyrinth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {
    private ImageButton _ButtonStartGame;
    private ImageButton _ButtonOpenHighscore;
    ImageButton _ButtonQuitApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        _ButtonStartGame = findViewById(R.id.buttonStartGame);
        _ButtonStartGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openActivity(_ButtonStartGame);
            }

        });

        _ButtonOpenHighscore = findViewById(R.id.buttonShowHighscore);
        _ButtonOpenHighscore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openActivity(_ButtonOpenHighscore);
            }

        });

        _ButtonQuitApp = findViewById(R.id.buttonQuitApp);
        _ButtonQuitApp.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        }));
    }

    public void openActivity(Object sender) {

        if (sender == _ButtonStartGame) {
            startActivity(new Intent(this, GameActivity.class));

        } else if(sender == _ButtonOpenHighscore) {
            startActivity(new Intent(this, Highscore.class));
        }
    }
}