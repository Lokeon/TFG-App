package es.tfg.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import es.tfg.R;


public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.home);

        Button btn_home = (Button) findViewById(R.id.button_home);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, UserActivity.class));
            }
        });

    }
}


