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

import es.tfg.R;
import es.tfg.user.Profile;
import es.tfg.user.UserActivity;


public class GameViewUser extends AppCompatActivity {
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id_user;
    private String id_game;
    private TextView nameGame;
    private TextView descriptionGame;
    private ImageView imageGame;
    private TextView genre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_user);

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar_user);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle("");
        top_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id_user = bundle.getString("id").replace("\"", "");
        id_game = bundle.getString("id_game").replace("\"", "");
        nameGame = (TextView) findViewById(R.id.name);
        descriptionGame = (TextView) findViewById(R.id.description);
        imageGame = (ImageView) findViewById(R.id.image);
        genre = (TextView) findViewById(R.id.genre);

        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id_user);


        top_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameViewUser.this, GameUser.class).putExtras(bundleSend));
            }
        });

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        new GetGame().execute(new GameInfo(token, id_game));
    }

    public void goHome(View view) {
        startActivity(new Intent(GameViewUser.this, UserActivity.class).putExtras(bundleSend));
    }

    public void goProfile(View view) {
        startActivity(new Intent(GameViewUser.this, Profile.class).putExtras(bundleSend));
    }

    public void goGame(View view) {
        startActivity(new Intent(GameViewUser.this, GameUser.class).putExtras(bundleSend));
    }

    private static class GameInfo {
        String id;
        String token;

        GameInfo(String token, String id) {
            this.id = id;
            this.token = token;
        }
    }

    class GetGame extends AsyncTask<GameInfo, Void, String> {

        @Override
        protected String doInBackground(GameInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_uogames) + "/" + strings[0].id);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
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


