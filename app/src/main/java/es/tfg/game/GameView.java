package es.tfg.game;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import es.tfg.MainActivity;
import es.tfg.R;
import es.tfg.registration.SignIn;

public class GameView extends AppCompatActivity {
    private TextView nameGame;
    private TextView descriptionGame;
    private ImageView imageGame;
    private TextView genre;
    private Bundle bundle;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle("");
        top_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        top_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameView.this, Game.class));
            }
        });

        nameGame = (TextView) findViewById(R.id.name);
        descriptionGame = (TextView) findViewById(R.id.description);
        imageGame = (ImageView) findViewById(R.id.image);
        genre = (TextView) findViewById(R.id.genre);

        bundle = getIntent().getExtras();
        id = bundle.getString("id").replace("\"", "");

        new GetGame().execute(id);
    }

    public void goHome(View view) {
        startActivity(new Intent(GameView.this, MainActivity.class));
    }

    public void goSignIn(View view) {
        startActivity(new Intent(GameView.this, SignIn.class));
    }

    public void goGame(View view) {
        startActivity(new Intent(GameView.this, Game.class));
    }

    class GetGame extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_gogames) + "/" + strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                text = new Scanner(inputStream).useDelimiter("\\A").next();

            } catch (Exception e) {
                return e.toString();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            if (results != null) {
                try {
                    JSONObject jsonobject = new JSONObject(results);

                    nameGame.setText(jsonobject.getString("name"));
                    descriptionGame.setText(jsonobject.getString("description"));
                    genre.setText(jsonobject.getString("genre"));
                    byte[] decodedString = Base64.decode(jsonobject.getString("image"), Base64.DEFAULT);
                    imageGame.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}