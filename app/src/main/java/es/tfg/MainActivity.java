package es.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import es.tfg.game.Game;
import es.tfg.registration.SignIn;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.home);

    }

    public void goHome(View view) {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    public void goSignIn(View view) {
        startActivity(new Intent(MainActivity.this, SignIn.class));
    }

    public void goGame(View view) {
        startActivity(new Intent(MainActivity.this, Game.class));
    }

}


