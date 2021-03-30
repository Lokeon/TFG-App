package es.tfg.game;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import es.tfg.MainActivity;
import es.tfg.R;
import es.tfg.adapter.CardGameAdapter;
import es.tfg.model.CardViewGames;
import es.tfg.registration.SignIn;


public class Game extends AppCompatActivity {
    private RecyclerView rvGames;
    private CardGameAdapter cardGameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.game);
        rvGames = (RecyclerView) findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new GridLayoutManager(Game.this, 3));

        new GetAllGames().execute();

    }

    public void goHome(View view) {
        startActivity(new Intent(Game.this, MainActivity.class));
    }

    public void goSignIn(View view) {
        startActivity(new Intent(Game.this, SignIn.class));
    }

    public void goGame(View view) {
        startActivity(new Intent(Game.this, Game.class));
    }

    class GetAllGames extends AsyncTask<Void, Void, String> {
        private ArrayList<CardViewGames> cardViewGamesArrayList = new ArrayList<>();

        @Override
        protected String doInBackground(Void... strings) {
            String text = null;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_ggames));
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
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        cardViewGamesArrayList.add(new CardViewGames(
                                jsonobject.getString("image"),
                                jsonobject.getString("name")));
                    }

                    cardGameAdapter = new CardGameAdapter(cardViewGamesArrayList);
                    rvGames.setAdapter(cardGameAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


